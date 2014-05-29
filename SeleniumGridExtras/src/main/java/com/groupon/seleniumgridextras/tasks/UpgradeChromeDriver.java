package com.groupon.seleniumgridextras.tasks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class UpgradeChromeDriver extends ExecuteOSTask {

    private String bit = "32";

    public UpgradeChromeDriver() {
        setEndpoint("/upgrade_chromedriver");
        setDescription("Downloads a version of ChromeDriver executable to node, and upgrades the setting to use new version on restart");
        JsonObject params = new JsonObject();
        params.addProperty("version", "(Required) - Version of ChromeDriver to download, such as 2.10");
        params.addProperty("bit", "Bit Version of ChromeDriver 32/64 - (default: configured or 32)");
        setAcceptedParams(params);
        setRequestType("GET");
        setResponseType("json");
        setClassname(this.getClass().getCanonicalName());
        setCssClass("btn-warning");
        setButtonText("Upgrade ChromeDriver");
        setEnabledInGui(true);

        addResponseDescription("old_version", "Old version of the executable that got replaced");
        addResponseDescription("new_version", "New version downloaded and reconfigured");
        addResponseDescription("old_bit", "Old bit of the executable that got replaced");
        addResponseDescription("new_bit", "New bit downloaded and reconfigured");

        getJsonResponse().addKeyValues("old_version", RuntimeConfig.getConfig().getChromeDriver().getVersion());
        getJsonResponse().addKeyValues("old_bit", RuntimeConfig.getConfig().getChromeDriver().getBit());

        bit = RuntimeConfig.getConfig().getChromeDriver().getBit();
    }

    @Override
    public JsonObject execute() {
        getJsonResponse().addKeyValues("error", "version parameter is required");
        return getJsonResponse().getJson();
    }

    @Override
    public JsonObject execute(String version) {

        DownloadChromeDriver downloader = new DownloadChromeDriver();
        Map<String, String> parameter = new HashMap<>();
        parameter.put("version", version);
        parameter.put("bit", bit);
        JsonObject result = downloader.execute(parameter);

        boolean noError = result.get("error").isJsonArray() && result.get("error").getAsJsonArray().size() == 0;
        if (noError) {
            RuntimeConfig.getConfig().getChromeDriver().setVersion(version);
            RuntimeConfig.getConfig().getChromeDriver().setBit(bit);
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
        dependencies.add("com.groupon.seleniumgridextras.tasks.DownloadChromeDriver");
        return dependencies;
    }

}
