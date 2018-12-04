package com.applitools.eyes.rendering;

import com.applitools.eyes.*;
import com.applitools.eyes.capture.AppOutputWithScreenshot;
import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MatchRGWindowTask extends MatchWindowTask {

    public MatchRGWindowTask(Logger logger, IServerConnector serverConnector, EyesBase eyes) {
        ArgumentGuard.notNull(serverConnector, "serverConnector");
        this.logger = logger;
        this.serverConnector = serverConnector;
        this.eyes = eyes;
    }

    /**
     * Creates the match data and calls the server connector matchWindow method.
     * @param userInputs         The user inputs related to the current appOutput.
     * @param appOutput          The application output to be matched.
     * @param tag                Optional tag to be associated with the match (can be {@code null}).
     * @param ignoreMismatch     Whether to instruct the server to ignore the match attempt in case of a mismatch.
     * @param imageMatchSettings The settings to use.
     * @return The match result.
     */
    public MatchResult performMatch(Trigger[] userInputs,
                                    AppOutputWithScreenshot appOutput,
                                    String tag, boolean ignoreMismatch,
                                    ImageMatchSettings imageMatchSettings) {

        String agentSetupStr = "";
        if (eyes != null) {
            Object agentSetup = eyes.getAgentSetup();
            ObjectMapper jsonMapper = new ObjectMapper();
            try {
                agentSetupStr = jsonMapper.writeValueAsString(agentSetup);
            } catch (JsonProcessingException e) {
                GeneralUtils.logExceptionStackTrace(logger, e);
            }
        }

        // Prepare match data.
        MatchWindowData data = new MatchWindowData(
                userInputs,
                appOutput.getAppOutput(),
                tag,
                ignoreMismatch,
                new MatchWindowData.Options(tag, userInputs, ignoreMismatch,
                        false, false, false,
                        imageMatchSettings),
                agentSetupStr);

        // Perform match.
        return serverConnector.matchWindow(runningSession, data);
    }
}
