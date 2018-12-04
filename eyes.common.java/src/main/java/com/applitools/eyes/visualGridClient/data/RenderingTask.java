package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.IEyesConnector;
import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.*;
import com.helger.css.reader.CSSReader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenderingTask implements Callable<RenderStatusResults> {


    private static AtomicBoolean isThrown = new AtomicBoolean(false);

    private final Map<String, String> hashToUrl;
    private IEyesConnector eyesConnector;
    private String script;
    private CheckRGSettings renderingConfiguration;
    private List<Task> taskList;
    private RenderingInfo renderingInfo;
    private Map<String, IResourceFuture> fetchedCacheMap;
    private Map<String, Future<Boolean>> putResourceCache;
    private Logger logger;


    public RenderingTask(Map<String, String> hashToUrl, IEyesConnector eyesConnector, String script, CheckRGSettings renderingConfiguration, List<Task> taskList, RenderingInfo renderingInfo, Map<String, IResourceFuture> fetchedCacheMap, Map<String, Future<Boolean>> putResourceCache, Logger logger) {
        this.hashToUrl = hashToUrl;
        this.eyesConnector = eyesConnector;
        this.script = script;
        this.renderingConfiguration = renderingConfiguration;
        this.taskList = taskList;
        this.renderingInfo = renderingInfo;
        this.fetchedCacheMap = fetchedCacheMap;
        this.putResourceCache = putResourceCache;
        this.logger = logger;
    }

    @Override
    public RenderStatusResults call() {

        try {
            HashMap<String, Object> result;
            RenderRequest[] requests = null;
            Map<Task, RenderRequest> testToRenderRequestMapping = new HashMap<>();
            try {

                //Parse to JSON
                result = GeneralUtils.parseJsonToObject(script);

                //Build RenderRequests
                requests = prepareDataForRG(result, renderingConfiguration);

            } catch (IOException e) {
                e.printStackTrace();
            }
            matchRequestsToTests(requests, testToRenderRequestMapping);


            boolean stillRunning = true;

            List<RunningRender> runningRenders = null;
            do {

                try {

                    runningRenders = this.eyesConnector.render(requests);

                } catch (Exception e) {
                    Thread.sleep(1500);
                    logger.verbose("/render throws exception... sleeping for 1.5s");
                    continue;
                }

                for (int i = 0; i < requests.length; i++) {
                    RenderRequest request = requests[i];
                    request.setRenderId(runningRenders.get(i).getRenderId());
                }

                RunningRender runningRender = runningRenders.get(0);
                RenderStatus worstStatus = runningRender.getRenderStatus();

                worstStatus = calcWorstStatus(runningRenders, worstStatus);

                boolean isNeedMoreDom = runningRender.isNeedMoreDom();

                stillRunning = worstStatus == RenderStatus.NEED_MORE_RESOURCE || isNeedMoreDom;
                if (stillRunning) {
                    sendMissingResources(runningRenders, requests[0].getDom(), isNeedMoreDom);
                }

            } while (stillRunning);

            Map<RunningRender, RenderRequest> mapping = mapRequestToRunningRender(runningRenders, requests);

            startPollingStatus(mapping);

        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return null;
    }

    private Map<RunningRender, RenderRequest> mapRequestToRunningRender(List<RunningRender> runningRenders, RenderRequest[] requests) {
        Map<RunningRender, RenderRequest> mapping = new HashMap<>();
        for (int i = 0; i < requests.length; i++) {
            RenderRequest request = requests[i];
            RunningRender runningRender = runningRenders.get(i);
            mapping.put(runningRender, request);
        }
        return mapping;
    }

    private RenderStatus calcWorstStatus(List<RunningRender> runningRenders, RenderStatus worstStatus) {
        LOOP:
        for (RunningRender runningRender : runningRenders) {
            switch (runningRender.getRenderStatus()) {
                case NEED_MORE_RESOURCE:
                    if (worstStatus == RenderStatus.RENDERED || worstStatus == RenderStatus.RENDERING) {
                        worstStatus = RenderStatus.NEED_MORE_RESOURCE;
                    }
                    break;
                case ERROR:
                    worstStatus = RenderStatus.ERROR;
                    break LOOP;
            }
        }
        return worstStatus;
    }

    private void startPollingStatus(Map<RunningRender, RenderRequest> runningRenders) {

        List<String> ids = getRenderIds(runningRenders.keySet());

        try {
            do {

                List<RenderStatusResults> renderStatusResultsList = this.eyesConnector.renderStatusById(ids.toArray(new String[0]));
                if (renderStatusResultsList == null || renderStatusResultsList.isEmpty() || renderStatusResultsList.get(0) == null) {
                    try {
                        Thread.sleep(500);
                        continue;
                    } catch (InterruptedException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                    }
                } else {
                    logger.verbose("render status result received ");
                }

                for (int i = 0; i < renderStatusResultsList.size(); i++) {
                    RenderStatusResults renderStatusResults = renderStatusResultsList.get(i);
                    if (renderStatusResults.getStatus() == RenderStatus.RENDERED) {

                        String removed = ids.remove(i);

                        for (RunningRender renderedRender : runningRenders.keySet()) {
                            if (renderedRender.getRenderId().equalsIgnoreCase(removed)) {
                                Task task = runningRenders.get(renderedRender).getTask();
                                task.setRenderResult(renderStatusResults);
                                break;
                            }
                        }
                    }
                }

                if (ids.size() > 0) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                    }
                }


            } while (ids.size() > 0);
        } catch (Exception e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
    }

    private List<String> getRenderIds(Collection<RunningRender> runningRenders) {
        List<String> ids = new ArrayList<>();
        for (RunningRender runningRender : runningRenders) {
            ids.add(runningRender.getRenderId());
        }
        return ids;
    }

    private void sendMissingResources(List<RunningRender> runningRenders, RGridDom dom, boolean isNeedMoreDom) {
        for (RunningRender runningRender : runningRenders) {
            if (isNeedMoreDom) {
                Future<Boolean> future = this.eyesConnector.renderPutResource(runningRender, dom.asResource());
                putResourceCache.put("dom", future);
            }
            List<String> needMoreResources = runningRender.getNeedMoreResources();
            for (String url : needMoreResources) {

                if (putResourceCache.containsKey(url)) continue;

                try {

                    RGridResource resource = fetchedCacheMap.get(url).get();
                    Future<Boolean> future = this.eyesConnector.renderPutResource(runningRender, resource);
                    if (!putResourceCache.containsKey(url)) {
                        putResourceCache.put(url, future);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }

        }

        for (Future<Boolean> future : putResourceCache.values()) {
            try {
                Boolean aBoolean = future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void matchRequestsToTests(RenderRequest[] requests, Map<Task, RenderRequest> testToRenderRequestMapping) {
        for (Task task : taskList) {
            RenderingConfiguration.RenderBrowserInfo browserInfo = task.getBrowserInfo();
            for (RenderRequest request : requests) {

                RenderInfo renderInfo = request.getRenderInfo();

                boolean isSameBrowser = request.getBrowserName().equalsIgnoreCase(browserInfo.getBrowserType());
                boolean isSameViewport = renderInfo.getHeight() == browserInfo.getHeight() && renderInfo.getWidth() == browserInfo.getWidth();

                if (isSameBrowser && isSameViewport) {
                    testToRenderRequestMapping.put(task, request);
                }
            }

        }
    }

    private void sendResourcesToRG() {


    }

    private static boolean isThrown() {
        return RenderingTask.isThrown.get();
    }

    private RenderRequest[] prepareDataForRG(HashMap<String, Object> result, CheckRGSettings settings) {

        final List<RGridResource> allBlobs = Collections.synchronizedList(new ArrayList<RGridResource>());
        List<String> resourceUrls = null;

        org.apache.commons.codec.binary.Base64 codec = new Base64();

        for (String key : result.keySet()) {
            Object value = result.get(key);
            switch (key) {
                case "blobs":
                    List listOfBlobs = (List) value;
                    for (Object blob : listOfBlobs) {
                        Map blobAsMap = (Map) blob;
                        String contentAsString = (String) blobAsMap.get("value");
                        Byte[] content = ArrayUtils.toObject(codec.decode(contentAsString));
                        RGridResource resource = new RGridResource((String) blobAsMap.get("url"), (String) blobAsMap.get("type"), content);
                        allBlobs.add(resource);
                    }
                    break;
                case "resourceUrls":

                    List<String> list = (List<String>) value;
//                    list.add("https://nikita-andreev.github.io/applitools/style0.css");
                    resourceUrls = Collections.synchronizedList(list);
                    break;
            }
        }

        //Fetch all resources
        fetchAllResources(allBlobs, resourceUrls);

        addBlobsToCache(allBlobs);

        parseAndFetchCSSResources(allBlobs);

        //Create RenderingRequest
        List<RenderRequest> allRequestsForRG = buildRenderRequests(result, settings, allBlobs);

        RenderRequest[] asArray = allRequestsForRG.toArray(new RenderRequest[allRequestsForRG.size()]);

        return asArray;
    }

    private void parseAndFetchCSSResources(List<RGridResource> allBlobs) {
        for (RGridResource blob : allBlobs) {
            String contentTypeStr = blob.getContentType();
            String[] parts = contentTypeStr.split(";");
            boolean cont = true;
            String charset = "UTF-8";
            for (String part : parts) {
                part = part.trim();
                if (part.equalsIgnoreCase("text/css")) {
                    cont = false;
                } else {
                    String[] keyVal = part.split("=");
                    if (keyVal.length == 2) {
                        String key = keyVal[0].trim();
                        String val = keyVal[1].trim();
                        if (key.equalsIgnoreCase("charset")) {
                            charset = val.toUpperCase();
                        }
                    }
                }
            }
            if (cont) continue;
            try {
                String css = new String(ArrayUtils.toPrimitive(blob.getContent()), charset);
                parseCSS(css);
            } catch (UnsupportedEncodingException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private void parseCSS(String css) {
        final CascadingStyleSheet cascadingStyleSheet = CSSReader.readFromString(css, ECSSVersion.CSS30);
        if (cascadingStyleSheet == null) {
            return;
        }
        List<String> allResourceUris = new ArrayList<>();
        collectAllFontFaceUris(cascadingStyleSheet, allResourceUris);
        int x = allResourceUris.size();
    }

    private void collectAllFontFaceUris(CascadingStyleSheet cascadingStyleSheet, List<String> allResourceUris) {
        ICommonsList<CSSFontFaceRule> allFontFaceRules = cascadingStyleSheet.getAllFontFaceRules();
        for (CSSFontFaceRule fontFaceRule : allFontFaceRules) {
            ICommonsList<CSSDeclaration> sourcesList = fontFaceRule.getAllDeclarationsOfPropertyName("src");
            for (CSSDeclaration cssDeclaration : sourcesList) {
                CSSExpression cssDeclarationExpression = cssDeclaration.getExpression();
                ICommonsList<ICSSExpressionMember> allExpressionMembers = cssDeclarationExpression.getAllMembers();
                ICommonsList<CSSExpressionMemberTermURI> allUriExpressions = allExpressionMembers.getAllInstanceOf(CSSExpressionMemberTermURI.class);
                for (CSSExpressionMemberTermURI uriExpression : allUriExpressions) {
                    CSSURI uri = uriExpression.getURI();
                    allResourceUris.add(uri.getURI());
                }
            }
        }
    }

    private void addBlobsToCache(List<RGridResource> allBlobs) {
        for (RGridResource blob : allBlobs) {
            String url = blob.getUrl();
            if (!this.fetchedCacheMap.containsKey(url)) {
                this.fetchedCacheMap.put(url, this.eyesConnector.createResourceFuture(blob));
            }
        }
    }


    private List<RenderRequest> buildRenderRequests(HashMap<String, Object> result, CheckRGSettings settings, List<RGridResource> allBlobs) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        Object cdt;
        RGridDom dom = null;
        Map<String, RGridResource> resourceMapping = new HashMap<>();
        cdt = result.get("cdt");
        dom = new RGridDom();
        dom.setDomNodes((List) cdt);
        for (RGridResource blob : allBlobs) {
            resourceMapping.put(blob.getUrl(), blob);
        }
        dom.setResources(resourceMapping);

        //Create RG requests
        List<RenderRequest> allRequestsForRG = new ArrayList<>();

        double randomRequestId = Math.random();

        for (Task task : this.taskList) {

            RenderingConfiguration.RenderBrowserInfo browserInfo = task.getBrowserInfo();
            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(), browserInfo.getSizeMode(), settings.getRegion(), browserInfo.getEmulationInfo());

            RenderRequest request = new RenderRequest(this.renderingInfo.getResultsUrl(), (String) result.get("url"), dom,
                    resourceMapping, renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType(), settings.getScriptHooks(), null, settings.isSendDom(), task);

            try {
                String value = objectMapper.writeValueAsString(request);
                System.out.println(value);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            allRequestsForRG.add(request);
        }
        return allRequestsForRG;
    }

    private void fetchAllResources(final List<RGridResource> allBlobs, List<String> resourceUrls) {

        List<IResourceFuture> allFetches = new ArrayList<>();
        for (String link : resourceUrls) {

            if (fetchedCacheMap.containsKey(link)) continue;

            IEyesConnector eyesConnector = this.taskList.get(0).getEyesConnector();
            try {
                final URL url = new URL(link);
                IResourceFuture future = eyesConnector.getResource(url, null);
                allFetches.add(future);
                this.fetchedCacheMap.put(url.toString(), future);

            } catch (MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }

        for (IResourceFuture future : allFetches) {

            try {

                RGridResource resource = future.get();
                allBlobs.add(resource);
                this.hashToUrl.put(resource.getSha256(), resource.getUrl());

            } catch (InterruptedException | ExecutionException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

}

