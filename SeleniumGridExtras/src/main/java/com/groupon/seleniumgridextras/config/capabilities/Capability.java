package com.groupon.seleniumgridextras.config.capabilities;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Capability extends HashMap {

  public Capability() {
    this.put("maxInstances", 1);
    this.put("seleniumProtocol", "WebDriver");
    setBrowser(getWDStyleName());
  }

  public void setBrowserVersion(String version) {
    this.put("version", version);
  }

  public void setPlatform(String platform) {
    this.put("platform", platform);
  }

  protected void setBrowser(String browser) {
    this.put("browserName", browser);
  }

  public String getBrowserName() {
    return this.getClass().getSimpleName();
  }

  public static Capability getCapabilityFor(String browserName) {

    for (Map.Entry<Class, String> entry : Capability.getSupportedCapabilities().entrySet()) {

      Class<Capability> key = entry.getKey();
      String value = entry.getValue();

      if (value.equals(browserName)) {
        try {
          return key.newInstance();
        } catch (Exception e) {
          e.printStackTrace();
          System.exit(1);
        }
      }

    }

    return null;
  }

  public static Map<Class, String> getSupportedCapabilities() {
    Map<Class, String> capabilityHash = new LinkedHashMap<>();

    capabilityHash.put(Firefox.class, "firefox");
    capabilityHash.put(InternetExplorer.class, "internet explorer");
    capabilityHash.put(Chrome.class, "chrome");
    capabilityHash.put(Safari.class, "safari");

    return capabilityHash;
  }

  public String getWDStyleName() {
    return Capability.getSupportedCapabilities().get(this.getClass());
  }
}


