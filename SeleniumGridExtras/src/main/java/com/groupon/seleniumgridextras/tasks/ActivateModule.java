package com.groupon.seleniumgridextras.tasks;

import java.util.Map;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class ActivateModule extends ExecuteOSTask {
    public ActivateModule() {
        setEndpoint("/activate_module");
        setDescription("Activates a module");
        JsonObject params = new JsonObject();
        params.addProperty("module", "(Required) - Module name.");
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName());
        setCssClass("btn-danget");
        setButtonText("Activate Module");
        setEnabledInGui(false);

        addResponseDescription("activated_module", "Activated module name");
    }

    @Override
    public JsonObject execute() {
        getJsonResponse().addKeyValues("error", "module parameter is required");
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(String parameter) {
        Config config = RuntimeConfig.getConfig();
        if (!config.getActivatedModules().contains(parameter)) {
            config.addActivatedModules(parameter);
            config.writeToDisk(RuntimeConfig.getConfigFile());
        }
        getJsonResponse().addKeyValues("activated_module", parameter);
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (parameter.isEmpty() || !parameter.containsKey("module")) {
            return execute();
        }
        return execute(parameter.get("module"));
    }
}
