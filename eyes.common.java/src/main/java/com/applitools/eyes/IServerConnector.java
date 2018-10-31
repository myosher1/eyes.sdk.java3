package com.applitools.eyes;

import com.applitools.IResourceUploadListener;
import com.applitools.renderingGrid.*;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Defines the interface which should be implemented by a ServerConnector.
 */
public interface IServerConnector {

    String API_SESSIONS = "api/sessions";
    String RUNNING_DATA_PATH = API_SESSIONS + "/running/data";

    //Rendering Grid
    String RENDER_INFO_PATH = API_SESSIONS + "/renderinfo";
    String RESOURCES_SHA_256 = "/resources/sha256/";
    String RENDER_STATUS = "/render-status";

    int TIMEOUT = 1000 * 60 * 5; // 5 Minutes
    String API_PATH = "/" + API_SESSIONS + "/running";
    String DEFAULT_CHARSET_NAME = "UTF-8";
    int THREAD_SLEEP_MILLIS = 3;
    int NUM_OF_RETRIES = 100;

    void setApiKey(String apiKey);

    String getApiKey();

    void setServerUrl(URI serverUrl);

    URI getServerUrl();

    void setLogger(Logger logger);

    Logger getLogger();


    void setProxy(ProxySettings proxySettings);

    ProxySettings getProxy();

    /**
     *
     * @return The server timeout. (Seconds).
     */
    int getTimeout();

    /**
     * Starts a new running session in the agent. Based on the given parameters,
     * this running session will either be linked to an existing session, or to
     * a completely new session.
     *
     * @param sessionStartInfo The start parameters for the session.
     * @return RunningSession object which represents the current running
     *         session
     * @throws EyesException
     */
    RunningSession startSession(SessionStartInfo sessionStartInfo);

    /**
     * Stops the running session.
     *
     * @param runningSession The running session to be stopped.
     * @return TestResults object for the stopped running session
     * @throws EyesException
     */
    TestResults stopSession(final RunningSession runningSession,
                                   final boolean isAborted, final boolean save);

    /**
     * Deletes the given test result
     *
     * @param testResults The session to delete by test results.
     * @throws EyesException
     */
    void deleteSession(TestResults testResults);

    /**
     * Matches the current window (held by the WebDriver) to the expected
     * window.
     *
     * @param runningSession The current agent's running session.
     * @param matchData Encapsulation of a capture taken from the application.
     * @return The results of the window matching.
     * @throws EyesException
     */
    MatchResult matchWindow(RunningSession runningSession,
                            MatchWindowData matchData);

    /**
     * Downloads string from a given Url
     *
     * @param uri The URI from which the IServerConnector will download the string
     * @param isSecondRetry Indicates if a retry is mandatory onFailed - 2 retries per request
     * @return the string that was return from the request to the Url
     */
    void downloadString(URL uri, boolean isSecondRetry, IDownloadListener listener);


    /**
     * Posting the DOM snapshot to the server and returns
     * @param domJson JSON as String.
     * @return URL to the JSON that is stored by the server.
     */
    String postDomSnapshot(String domJson);


    /**
     * Gets the render info from the server to be used later on.
     */
    void getRenderInfo();

    /**
     * Initiate a rendering using RenderingGrid API
     *
     * @param renderRequests renderRequest The current agent's running session.
     * @return List<RunningRender> The results of the render request
     */
    List<RunningRender> render(RenderRequest... renderRequests);

    /**
     * Check if resource exists on the server
     *
     * @param runningRender The running render (for second request only)
     * @param resource The resource to use
     * @return Whether resource exists on the server or not
     */
    boolean renderCheckResource(RunningRender runningRender, RGridResource resource);

    /**
     * Upload resource to the server
     *
     * @param runningRender The running render (for second request only)
     * @param resource The resource to upload
     * @return true if resource was uploaded
     */
    void renderPutRequest(RunningRender runningRender, RGridResource resource,boolean isRetryOn, IResourceUploadListener listener);

    /**
     * Get the rendering status for current render
     *
     * @param runningRender The running render
     * @return RenderStatusResults The render's status
     */
    RenderStatusResults renderStatus(RunningRender runningRender);

    /**
     * Get the rendering status for current render
     *
     * @param renderIds The running renderId
     * @return The render's status
     */
    List<RenderStatusResults> renderStatusById(String... renderIds);

}
