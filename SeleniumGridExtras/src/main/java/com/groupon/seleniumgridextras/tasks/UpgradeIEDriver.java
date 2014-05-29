package com.groupon.seleniumgridextras.tasks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class UpgradeIEDriver extends ExecuteOSTask {

    private String bit = "Win32";

    public UpgradeIEDriver() {
        setEndpoint("/upgrade_iedriver");
        setDescription("Downloads a version of IEDriver executable to node, and upgrades the setting to use new version on restart");
        JsonObject params = new JsonObject();
        params.addProperty("version", "(Required) - Version of IEDriver to download, such as 2.40.0");
        params.addProperty("bit", "Bit Version of IEDriver Win32/x64 - (default: configured or Win32)");
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName());
        setCssClass("btn-warning");
        setButtonText("Upgrade IEDriver");
        setEnabledInGui(true);

        addResponseDescription("old_version", "Old version of the executable that got replaced");
        addResponseDescription("new_version", "New version downloaded and reconfigured");
        addResponseDescription("old_bit", "Old bit of the executable that got replaced");
        addResponseDescription("new_bit", "New bit downloaded and reconfigured");

        getJsonResponse().addKeyValues("old_version", RuntimeConfig.getConfig().getIEdriver().getVersion());
        getJsonResponse().addKeyValues("old_bit", RuntimeConfig.getConfig().getIEdriver().getBit());

        bit = RuntimeConfig.getConfig().getIEdriver().getBit();
    }

    @Override
    public JsonObject execute() {
        getJsonResponse().addKeyValues("error", "version parameter is required");
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(String version) {

        DownloadIEDriver downloader = new DownloadIEDriver();
        Map<String, String> parameter = new HashMap<>();
        parameter.put("version", version);
        parameter.put("bit", bit);
        JsonObject result = downloader.execute(parameter);

        boolean noError = result.get("error").isJsonArray() && result.get("error").getAsJsonArray().size() == 0;
        if (noError) {
            RuntimeConfig.getConfig().getIEdriver().setVersion(version);
            RuntimeConfig.getConfig().getIEdriver().setBit(bit);
            RuntimeConfig.getConfig().writeToDisk(RuntimeConfig.getConfigFile());
            getJsonResponse().addKeyValues("new_version", version);
            getJsonResponse().addKeyValues("new_bit", bit);
            return getJsonResponse().getJson();
        } else {
            getJsonResponse().addKeyValues("error", result.get("error").toString());
            return getJsonResponse().getJson();
        }
    }

    @Override
    public JsonObject execute(Map<String, String> parameter) {
        if (!parameter.isEmpty() && parameter.containsKey("version")) {
            if (parameter.containsKey("bit")) {
                bit = parameter.get("bit");
            }
            return execute(parameter.get("version"));
        } else {
            return execute();
        }
    }

    @Override
    public List<String> getDependencies() {
        List<String> dependencies = new LinkedList<>();
        dependencies.add("com.groupon.seleniumgridextras.tasks.DownloadIEDriver");
        return dependencies;
    }

}
