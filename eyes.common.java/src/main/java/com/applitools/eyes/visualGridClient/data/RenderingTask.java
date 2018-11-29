package com.applitools.eyes.visualGridClient.data;

import com.applitools.eyes.EyesException;
import com.applitools.eyes.Logger;
import com.applitools.eyes.visualGridClient.IEyesConnector;
import com.applitools.eyes.visualGridClient.IResourceFuture;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
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


        boolean stillRunning;

        List<RunningRender> runningRenders;
        do {
            runningRenders = this.eyesConnector.render(requests);

            RenderStatus worstStatus = runningRenders.get(0).getRenderStatus();

            worstStatus = calcWorstStatus(runningRenders, worstStatus);

            stillRunning = worstStatus == RenderStatus.NEED_MORE_RESOURCES;
            if (stillRunning) {
                sendMissingResources(runningRenders);
            }

        } while (stillRunning);

        Map<RunningRender, RenderRequest> mapping = mapRequestToRunningRender(runningRenders, requests);

        startPollingStatus(mapping);

        return null;
    }

    private Map<RunningRender, RenderRequest> mapRequestToRunningRender(List<RunningRender> runningRenders, RenderRequest[] requests) {
        Map<RunningRender, RenderRequest> mapping = new HashMap<>();
        for (int i = 0; i < requests.length; i++) {
            RenderRequest request = requests[i];
            RunningRender runningRender = runningRenders.get(i);
            mapping.put(runningRender,request);
        }
        return mapping;
    }

    private RenderStatus calcWorstStatus(List<RunningRender> runningRenders, RenderStatus worstStatus) {
        LOOP:
        for (RunningRender runningRender : runningRenders) {
            switch (runningRender.getRenderStatus()) {
                case NEED_MORE_RESOURCES:
                    if (worstStatus == RenderStatus.RENDERED || worstStatus == RenderStatus.RENDERING) {
                        worstStatus = RenderStatus.NEED_MORE_RESOURCES;
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

        Iterator<Map.Entry<RunningRender, RenderRequest>> iterator = runningRenders.entrySet().iterator();

        do {

            while (iterator.hasNext()) {
                Map.Entry<RunningRender, RenderRequest> entry = iterator.next();
                RunningRender runningRender = entry.getKey();
                RenderRequest request = entry.getValue();
                RenderStatus renderStatus = runningRender.getRenderStatus();
                if(renderStatus == RenderStatus.ERROR){
                    iterator.remove();
                    throw new EyesException("RENDERING FAILED");
                }

                if(renderStatus == RenderStatus.RENDERED){
                    String[] ids = getRenderIds(runningRenders.keySet());
                    this.eyesConnector.renderStatusById(ids);
                    iterator.remove();

                }
            }

        }while (runningRenders.size() > 0);
    }

    private String[] getRenderIds(Set<RunningRender> runningRenders) {
        return new String[0];
    }

    private void sendMissingResources(List<RunningRender> runningRenders) {
        for (RunningRender runningRender : runningRenders) {
            List<String> needMoreResources = runningRender.getNeedMoreResources();
            for (String hash : needMoreResources) {

                if (putResourceCache.containsKey(hash)) continue;

                String url = hashToUrl.get(hash);
                try {

                    RGridResource resource = fetchedCacheMap.get(url).get();
                    Future<Boolean> future = this.eyesConnector.renderPutResource(runningRender, resource);
                    putResourceCache.put(hash, future);

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

        //Create RenderingRequest
        List<RenderRequest> allRequestsForRG = buildRenderRequests(result, settings, allBlobs);

        RenderRequest[] asArray = allRequestsForRG.toArray(new RenderRequest[allRequestsForRG.size()]);

        return asArray;
    }


    private List<RenderRequest> buildRenderRequests(HashMap<String, Object> result, CheckRGSettings settings, List<RGridResource> allBlobs) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        String cdt;
        RGridDom dom = null;
        Map<String, RGridResource> resourceMapping = new HashMap<>();
        try {
            cdt = objectMapper.writeValueAsString(result.get("cdt"));
            dom = new RGridDom();
            dom.setCdt(cdt);
            for (RGridResource blob : allBlobs) {
                resourceMapping.put(blob.getUrl(), blob);
            }
        } catch (JsonProcessingException e) {
            GeneralUtils.logExceptionStackTrace(logger, e);
        }
        dom.setResources(resourceMapping);

        //Create RG requests
        List<RenderRequest> allRequestsForRG = new ArrayList<>();

        double randomRequestId = Math.random();

        for (Task task : this.taskList) {

            RenderingConfiguration.RenderBrowserInfo browserInfo = task.getBrowserInfo();
            RenderInfo renderInfo = new RenderInfo(browserInfo.getWidth(), browserInfo.getHeight(), browserInfo.getSizeMode(), settings.getRegion(), browserInfo.getEmulationInfo());

            RenderRequest request = new RenderRequest(randomRequestId, this.renderingInfo.getResultsUrl(), (String) result.get("url"), dom,
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
                this.hashToUrl.put(resource.getSha256hash(), resource.getUrl());

            } catch (InterruptedException | ExecutionException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }
    }

}

