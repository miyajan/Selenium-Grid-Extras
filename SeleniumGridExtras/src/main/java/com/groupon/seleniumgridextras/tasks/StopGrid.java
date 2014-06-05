/**
 * Copyright (c) 2013, Groupon, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of GROUPON nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 * Created with IntelliJ IDEA.
 * User: Dima Kovalenko (@dimacus) && Darko Marinov
 * Date: 5/10/13
 * Time: 4:06 PM
 */


package com.groupon.seleniumgridextras.tasks;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.JsonResponseBuilder;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.Hub;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class StopGrid extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(StopGrid.class);

  public StopGrid() {
    setEndpoint("/stop_grid");
    setDescription("Stops grid or node process");
    JsonObject params = new JsonObject();
    params.addProperty("port", "(Required) Port on which the node/hub is running.");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setCssClass("btn-info");
    setButtonText("Stop Grid");
    setEnabledInGui(false);
  }

  @Override
  public JsonObject getAcceptedParams() {
    JsonObject params = new JsonObject();
    params.addProperty("port", "(Required) Port on which the node/hub is running");
    return params;
  }

  @Override
  public JsonObject execute() {
    getJsonResponse().addKeyValues("error", "Port parameter is required");
    return getJsonResponse().getJson();
  }


  /**
   * Get all the tasks currently running with verbose description. Search in the task description
   * for execution of the start_&lt;port>.bat and kill the corresponding process.
   */
  @Override
  public String getWindowsCommand(String port) {
    return "FOR /F \"usebackq tokens=2\" %i IN (`tasklist /V ^| findstr \"" + port
           + ".bat\"`) DO taskkill /PID %i";
  }

  public String getWindowsCommand(int port) {
    return getWindowsCommand(String.valueOf(port));
  }

  public String getLinuxCommand(int port) {
    return getLinuxCommand(String.valueOf(port));
  }

  @Override
  public String getLinuxCommand(String port) {
    JsonObject status = PortChecker.getParsedPortInfo(port);

    if (status.has("pid")){
      KillPid killer = new KillPid();
      return killer.getLinuxCommand(status.get("pid").getAsString());
    }

      return "";
//    return "lsof -sTCP:LISTEN -i TCP:" + port + " | grep -v PID | awk '{print $2}' | xargs kill";
  }

  @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (parameter.isEmpty() || !parameter.containsKey("port")) {
      return execute();
    } else {
      return this.execute(parameter.get("port").toString());
    }
  }

  @Override
  public JsonObject execute(String parameter) {
      Config config = RuntimeConfig.getConfig();
      if (config.getDefaultRole().equals("hub") && parameter.equals(config.getHub().getPort())) {
          return stopHub(parameter);
      } else {
          return stopNode(parameter);
      }
  }

  private JsonObject stopHub(String port) {
      JsonResponseBuilder builder = new JsonResponseBuilder();
      Hub hub = RuntimeConfig.getConfig().getHub();
      try {
          URL url = new URL("http://" + hub.getHost() + ":" + hub.getPort() + "/lifecycle-manager?action=shutdown");
          logger.debug(url.toString());
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          conn.setRequestMethod("GET");
          String out = ExecuteCommand.inputStreamToString(conn.getInputStream());
          logger.debug(out);
          builder.addKeyValues("out", out);
      } catch (IOException e) {
          builder.addKeyValues("exit_code", 1);
          builder.addKeyValues("error", "IOException: " + e.getMessage());
          return builder.getJson();
      }
      return builder.getJson();
  }

  private JsonObject stopNode(String port) {
      String command;
      if (RuntimeConfig.getOS().isWindows()){
          command = getWindowsCommand(port);
      } else if (RuntimeConfig.getOS().isMac()){
          command = getMacCommand(port);
      } else {
          command = getLinuxCommand(port);
      }

      return ExecuteCommand.execRuntime(command, waitToFinishTask);
  }

}
