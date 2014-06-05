package com.groupon.seleniumgridextras.tasks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class StopAll extends ExecuteOSTask {
    private static Logger logger = Logger.getLogger(StopAll.class);

    public StopAll() {
        setEndpoint("/stop_all");
        setDescription("Stop all hub and nodes and shuts down Grid Extras service");
        JsonObject params = new JsonObject();
        params.addProperty("confirm", "(Required) Will ignore request unless true is passed here");
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName());
        setButtonText("Shut Down All");
        setEnabledInGui(true);
    }

    @Override
    public JsonObject execute() {
        return execute("");
    }

    @Override
    public JsonObject execute(String confirm) {
        if (confirm.equals("true")) {
            Config config = RuntimeConfig.getConfig();
            if (config.getDefaultRole().equals("hub")) {
                logger.debug("stop hub.");
                StopGrid stopGrid = new StopGrid();
                JsonObject result = stopGrid.execute(config.getHub().getPort());
                boolean noError = result.get("error").isJsonArray() && result.get("error").getAsJsonArray().size() == 0;
                if (!noError) {
                    logger.info("failed to stop hub: " + result.get("error").toString());
                }
            }
            for (GridNode node : config.getNodes()) {
                int port = node.getConfiguration().getPort();
                logger.debug("stop node: " + port);
                StopGrid stopGrid = new StopGrid();
                JsonObject result = stopGrid.execute(Integer.toString(port));
                boolean noError = result.get("error").isJsonArray() && result.get("error").getAsJsonArray().size() == 0;
                if (!noError) {
                    logger.info("failed to stop node (port=" + port + "): " + result.get("error").toString());
                }
            }
            logger.debug("stop extras.");
            StopGridExtras stopGridExtras = new StopGridExtras();
            Map<String, String> parameter = new HashMap<>();
            parameter.put("confirm", "true");
            return stopGridExtras.execute(parameter);
        }

        getJsonResponse().addKeyValues("error", "Pass in confirm=true");
        return getJsonResponse().getJson();
    }


    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (!parameter.isEmpty() && parameter.containsKey("confirm")) {
            return execute(parameter.get("confirm"));
        }

        return execute();
    }

    @Override
    public List<String> getDependencies() {
        List<String> dependencies = new LinkedList<>();
        dependencies.add("com.groupon.seleniumgridextras.tasks.StopGrid");
        dependencies.add("com.groupon.seleniumgridextras.tasks.StopGridExtras");
        return dependencies;
    }
}
