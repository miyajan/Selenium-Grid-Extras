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

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.JsonResponseBuilder;

public class StopGridExtras extends ExecuteOSTask {

  private static Logger logger = Logger.getLogger(StopGridExtras.class);
  public StopGridExtras(){
    setEndpoint("/stop_extras");
    setDescription("Shuts down Grid Extras service");
    JsonObject params = new JsonObject();
    params.addProperty("confirm", "(Required) Will ignore request unless true is passed here");
    setAcceptedParams(params);
    setRequestType("GET");
    setResponseType("json");
    setClassname(this.getClass().getCanonicalName().toString());
    setButtonText("Shut Down Grid Extras");
    setEnabledInGui(true);
  }

  @Override
  public JsonObject getAcceptedParams() {
    JsonObject params = new JsonObject();
    params.addProperty("confirm", "(Required) Will ignore request unless true is passed here");
    return params;
  }

  @Override
  public JsonObject execute() {
    return execute("");
  }

  @Override
  public JsonObject execute(String port) {
    getJsonResponse().addKeyValues("error", "Pass in confirm=true");
    return getJsonResponse().getJson();
  }



    @Override
  public JsonObject execute(Map<String, String> parameter) {
    if (!parameter.isEmpty() && parameter.containsKey("confirm") && parameter.get("confirm").equals("true")) {
      logger.info("Shutdown command received, shutting down.");
      setShouldStopServer(true);
      return new JsonResponseBuilder().getJson();
    }

    return execute();
  }



}
