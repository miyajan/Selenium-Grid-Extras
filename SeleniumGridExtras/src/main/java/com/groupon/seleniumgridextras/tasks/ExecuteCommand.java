package com.groupon.seleniumgridextras.tasks;

import java.util.Map;

import com.google.gson.JsonObject;

public class ExecuteCommand extends ExecuteOSTask {
    public ExecuteCommand() {
        setEndpoint("/execute_command");
        setDescription("Executes a command");
        JsonObject params = new JsonObject();
        params.addProperty("command", "(Required) - Command string.");
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName());
        setCssClass("btn-danget");
        setButtonText("Execute Command");
        setEnabledInGui(false);
    }

    @Override
    public JsonObject execute() {
        getJsonResponse().addKeyValues("error", "command is a required parameter");
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (parameter.isEmpty() || !parameter.containsKey("command")) {
            return execute();
        }
        String command = parameter.get("command");
        return execute(command);
    }

    @Override
    public String getWindowsCommand(String parameter) {
        return parameter;
    }

    @Override
    public String getLinuxCommand(String parameter) {
        return parameter;
    }
}
