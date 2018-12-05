package com.applitools.eyes.visualGridClient.data;

import com.applitools.ICheckRGSettings;
import com.applitools.ICheckRGSettingsInternal;
import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.IEyesConnector;
import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.utils.GeneralUtils;
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

public class RenderingTask implements Callable<RenderStatusResults> {

    private final RenderTaskListener listener;
    private IEyesConnector eyesConnector;
    private String script;
    private ICheckRGSettings renderingConfiguration;
    private List<Task> taskList;
    private RenderingInfo renderingInfo;
    private Map<String, IResourceFuture> fetchedCacheMap;
    private Map<String, Future<Boolean>> putResourceCache;
    private Logger logger;

    public interface RenderTaskListener {
        void onRenderSuccess();

        void onRenderFailed(Exception e);
    }

    public RenderingTask(IEyesConnector eyesConnector, String script, ICheckRGSettings renderingConfiguration, List<Task> taskList, RenderingInfo renderingInfo, Map<String, IResourceFuture> fetchedCacheMap, Map<String, Future<Boolean>> putResourceCache, Logger logger, RenderTaskListener listener) {
        this.eyesConnector = eyesConnector;
        this.script = script;
        this.renderingConfiguration = renderingConfiguration;
        this.taskList = taskList;
        this.renderingInfo = renderingInfo;
        this.fetchedCacheMap = fetchedCacheMap;
        this.putResourceCache = putResourceCache;
        this.logger = logger;
        this.listener = listener;
    }

    @Override
    public RenderStatusResults call() {

        try {

            HashMap<String, Object> result;
            RenderRequest[] requests = new RenderRequest[0];

            try {
                //Parse to JSON
                result = GeneralUtils.parseJsonToObject(script);

                //Build RenderRequests
                requests = prepareDataForRG(result);

            } catch (IOException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }

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
            listener.onRenderFailed(e);
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
                    } catch (InterruptedException e) {
                        GeneralUtils.logExceptionStackTrace(logger, e);
                    }
                    continue;
                }

                logger.verbose("render status result received ");

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
            listener.onRenderSuccess();
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
                    logger.log("trying to get url from map - " + url);
                    IResourceFuture resourceFuture = fetchedCacheMap.get(url);
                    if (resourceFuture == null) {
                        logger.log("fetchedCacheMap.get(url) == null - " + url);
                    } else {
                        RGridResource resource = resourceFuture.get();
                        Future<Boolean> future = this.eyesConnector.renderPutResource(runningRender, resource);
                        if (!putResourceCache.containsKey(url)) {
                            putResourceCache.put(url, future);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }

        }

        for (Future<Boolean> future : putResourceCache.values()) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private RenderRequest[] prepareDataForRG(HashMap<String, Object> result) {

        final List<RGridResource> allBlobs = Collections.synchronizedList(new ArrayList<RGridResource>());
        List<URL> resourceUrls = new ArrayList<>();

        parseScriptResult(result, allBlobs, resourceUrls);

        //Fetch all resources
        while (!resourceUrls.isEmpty()) {
            fetchAllResources(allBlobs, resourceUrls);
        }
        addBlobsToCache(allBlobs);

        //Create RenderingRequest
        List<RenderRequest> allRequestsForRG = buildRenderRequests(result, allBlobs);

        @SuppressWarnings("UnnecessaryLocalVariable")
        RenderRequest[] asArray = allRequestsForRG.toArray(new RenderRequest[0]);
        return asArray;
    }

    @SuppressWarnings("unchecked")
    private void parseScriptResult(Map<String, Object> result, List<RGridResource> allBlobs, List<URL> resourceUrls) {
        org.apache.commons.codec.binary.Base64 codec = new Base64();
        URL baseUrl = null;
        try {
            baseUrl = new URL((String) result.get("url"));
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        for (String key : result.keySet()) {
            Object value = result.get(key);
            switch (key) {
                case "blobs":
                    List listOfBlobs = (List) value;
                    for (Object blob : listOfBlobs) {
                        RGridResource resource = parseBlobToGridResource(codec, baseUrl, (Map) blob);
                        if (!allBlobs.contains(resource)) {
                            allBlobs.add(resource);
                        }
                    }
                    break;

                case "resourceUrls":
                    List<String> list = (List<String>) value;
                    for (String url : list) {
                        try {
                            if (this.fetchedCacheMap.containsKey(url)) continue;
                            resourceUrls.add(new URL(baseUrl, url));
                        } catch (MalformedURLException e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }
                    }
                    break;

                case "frames":
                    List<Map<String, Object>> allObjects = (List) value;
                    for (Map<String, Object> frameObj : allObjects) {
                        RGridDom frame = new RGridDom();
                        try {
                            frame.setUrl(new URL((String) frameObj.get("url")));
                            frame.setDomNodes((List) frameObj.get("cdt"));
                            List blobs = (List<String>) frameObj.get("blobs");
                            for (Object blob : blobs) {
                                RGridResource resource = parseBlobToGridResource(codec, baseUrl, (Map) blob);
                                frame.addResource(resource);
                            }
                            List<String> frameResourceUrlsAsStrings = (List<String>) frameObj.get("resourceUrls");
                            List<URL> frameResourceUrls = new ArrayList<>();

                            URL frameBaseUrl = new URL((String) frameObj.get("url"));
                            for (String frameResourceUrl : frameResourceUrlsAsStrings) {
                                frameResourceUrls.add(new URL(frameBaseUrl, frameResourceUrl));
                            }
                            ArrayList<RGridResource> resourceArrayList = new ArrayList<>();
                            fetchAllResources(resourceArrayList, frameResourceUrls);
                            allBlobs.addAll(resourceArrayList);
                            frame.addResources(resourceArrayList);
                            allBlobs.add(frame.asResource());
                        } catch (MalformedURLException e) {
                            GeneralUtils.logExceptionStackTrace(logger, e);
                        }

                        parseScriptResult(frameObj, allBlobs, resourceUrls);
                    }
                    break;
            }
        }
        addBlobsToCache(allBlobs);

        parseAndCollectCSSResources(allBlobs, baseUrl, resourceUrls);
    }

    private RGridResource parseBlobToGridResource(Base64 codec, URL baseUrl, Map blob) {
        String contentAsString = (String) blob.get("value");
        byte[] decode = codec.decode(contentAsString);
        Byte[] content = ArrayUtils.toObject(decode);
        String url = (String) blob.get("url");
        url = url.replaceAll("blob:", "");
        RGridResource resource = null;
        try {
            resource = new RGridResource(new URL(baseUrl, url), (String) blob.get("type"), content);
            resource.setBaseUrl(baseUrl);
        } catch (MalformedURLException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        return resource;
    }

    private void parseAndCollectCSSResources(List<RGridResource> allBlobs, URL baseUrl, List<URL> resourceUrls) {
        for (RGridResource blob : allBlobs) {
            String contentTypeStr = blob.getContentType();
            String css = getCss(blob.getContent(), contentTypeStr);
            if (css == null) continue;
            parseCSS(css, baseUrl, resourceUrls);
        }
    }

    private String getCss(Byte[] contentBytes, String contentTypeStr) {
        String[] parts = contentTypeStr.split(";");
        String charset = "UTF-8";
        for (String part : parts) {
            part = part.trim();
            if (part.equalsIgnoreCase("text/css")) {
                charset = null;
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

        String css = null;
        if (charset != null) {
            try {
                css = new String(ArrayUtils.toPrimitive(contentBytes), charset);
            } catch (UnsupportedEncodingException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
        return css;
    }

    private void parseCSS(String css, URL baseUrl, List<URL> resourceUrls) {
        final CascadingStyleSheet cascadingStyleSheet = CSSReader.readFromString(css, ECSSVersion.CSS30);
        if (cascadingStyleSheet == null) {
            return;
        }
        collectAllImportUris(cascadingStyleSheet, resourceUrls, baseUrl);
        collectAllFontFaceUris(cascadingStyleSheet, resourceUrls, baseUrl);
        collectAllBackgroundImageUris(cascadingStyleSheet, resourceUrls, baseUrl);
    }

    private void collectAllFontFaceUris(CascadingStyleSheet cascadingStyleSheet, List<URL> allResourceUris, URL baseUrl) {
        ICommonsList<CSSFontFaceRule> allFontFaceRules = cascadingStyleSheet.getAllFontFaceRules();
        for (CSSFontFaceRule fontFaceRule : allFontFaceRules) {
            getAllResourcesUrisFromDeclarations(allResourceUris, fontFaceRule, "src", baseUrl);
        }
    }

    private void collectAllBackgroundImageUris(CascadingStyleSheet cascadingStyleSheet, List<URL> allResourceUris, URL baseUrl) {
        ICommonsList<CSSStyleRule> allStyleRules = cascadingStyleSheet.getAllStyleRules();
        for (CSSStyleRule styleRule : allStyleRules) {
            getAllResourcesUrisFromDeclarations(allResourceUris, styleRule, "background", baseUrl);
            getAllResourcesUrisFromDeclarations(allResourceUris, styleRule, "background-image", baseUrl);
        }
    }

    private void collectAllImportUris(CascadingStyleSheet cascadingStyleSheet, List<URL> allResourceUris, URL baseUrl) {
        ICommonsList<CSSImportRule> allImportRules = cascadingStyleSheet.getAllImportRules();
        for (CSSImportRule importRule : allImportRules) {
            String uri = importRule.getLocation().getURI();
            try {
                URL url = new URL(baseUrl, uri);
                allResourceUris.add(url);
            } catch (MalformedURLException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private <T extends IHasCSSDeclarations<T>> void getAllResourcesUrisFromDeclarations(List<URL> allResourceUris, IHasCSSDeclarations<T> rule, String propertyName, URL baseUrl) {
        ICommonsList<CSSDeclaration> sourcesList = rule.getAllDeclarationsOfPropertyName(propertyName);
        for (CSSDeclaration cssDeclaration : sourcesList) {
            CSSExpression cssDeclarationExpression = cssDeclaration.getExpression();
            ICommonsList<ICSSExpressionMember> allExpressionMembers = cssDeclarationExpression.getAllMembers();
            ICommonsList<CSSExpressionMemberTermURI> allUriExpressions = allExpressionMembers.getAllInstanceOf(CSSExpressionMemberTermURI.class);
            for (CSSExpressionMemberTermURI uriExpression : allUriExpressions) {
                try {
                    URL url = new URL(baseUrl, uriExpression.getURIString());
                    allResourceUris.add(url);
                } catch (MalformedURLException e) {
                    GeneralUtils.logExceptionStackTrace(logger, e);
                }
            }
        }
    }

    private void addBlobsToCache(List<RGridResource> allBlobs) {
        for (RGridResource blob : allBlobs) {
            URL url = blob.getUrl();
            if (!this.fetchedCacheMap.containsKey(url.toString())) {
                this.fetchedCacheMap.put(url.toString(), this.eyesConnector.createResourceFuture(blob));
            }
        }
    }

    private List<RenderRequest> buildRenderRequests(HashMap<String, Object> result, List<RGridResource> allBlobs) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        Object cdt;
        Map<String, RGridResource> resourceMapping = new HashMap<>();
        cdt = result.get("cdt");
        RGridDom dom = new RGridDom();
        dom.setDomNodes((List) cdt);
        for (RGridResource blob : allBlobs) {
            URL url = blob.getUrl();
            resourceMapping.put(url.toString(), blob);
        }
        dom.setResources(resourceMapping);

        //Create RG requests
        List<RenderRequest> allRequestsForRG = new ArrayList<>();
        ICheckRGSettingsInternal rcInternal = (ICheckRGSettingsInternal) renderingConfiguration;

        for (Task task : this.taskList) {

            RenderingConfiguration.RenderBrowserInfo browserInfo = task.getBrowserInfo();
            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(), browserInfo.getSizeMode(), rcInternal.getRegion(), browserInfo.getEmulationInfo());

            RenderRequest request = new RenderRequest(this.renderingInfo.getResultsUrl(), (String) result.get("url"), dom,
                    resourceMapping, renderInfo, browserInfo.getPlatform(), browserInfo.getBrowserType(), rcInternal.getScriptHooks(), null, rcInternal.isSendDom(), task);

            allRequestsForRG.add(request);
        }
        return allRequestsForRG;
    }

    @SuppressWarnings("WhileLoopReplaceableByForEach")
    private void fetchAllResources(final List<RGridResource> allBlobs, List<URL> resourceUrls) {

        List<IResourceFuture> allFetches = new ArrayList<>();

        final Iterator<URL> iterator = resourceUrls.iterator();
        while (iterator.hasNext()) {
            URL link = iterator.next();
            if (fetchedCacheMap.containsKey(link.toString())) continue;

            IEyesConnector eyesConnector = this.taskList.get(0).getEyesConnector();
            IResourceFuture future = eyesConnector.getResource(link, null);
            allFetches.add(future);
            this.fetchedCacheMap.put(link.toString(), future);
        }

        for (IResourceFuture future : allFetches) {

            try {

                RGridResource resource = future.get();
                removeUrlFromList(resource.getUrl(), resourceUrls);
                allBlobs.add(resource);
                String contentType = resource.getContentType();
                String css = getCss(resource.getContent(), contentType);
                if (css == null || css.isEmpty() || !contentType.contains("text/css")) continue;

                parseCSS(css, resource.getUrl(), resourceUrls);

            } catch (InterruptedException | ExecutionException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

    private void removeUrlFromList(URL url, List<URL> resourceUrls) {
        Iterator<URL> iterator = resourceUrls.iterator();
        while (iterator.hasNext()) {
            URL resourceUrl = iterator.next();
            if (resourceUrl.toString().equalsIgnoreCase(url.toString())) {
                iterator.remove();
            }
        }
    }

}

