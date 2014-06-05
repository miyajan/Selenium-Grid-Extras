package com.groupon.seleniumgridextras.grid;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;
import com.groupon.seleniumgridextras.ExecuteCommand;
import com.groupon.seleniumgridextras.PortChecker;
import com.groupon.seleniumgridextras.config.Config;
import com.groupon.seleniumgridextras.config.GridNode;
import com.groupon.seleniumgridextras.config.RuntimeConfig;

public class SelfHealingGrid extends GridStarter {
  private static Logger logger = Logger.getLogger(SelfHealingGrid.class);

  public static void checkStatus(int gridExtrasPort, Config config) {
    if (portOccupied(gridExtrasPort)) {
      logger.info("Already running on port " + gridExtrasPort + " with pid");
      healHubIfNeeded(config);
      healNodesIfNeeded(config);
      System.exit(0);
    } else {
      logger.info("GridExtras is not running will boot normally");
    }

  }

  private static void healHubIfNeeded(Config config) {
      if (config.getDefaultRole().equals("hub")) {
          int port = Integer.parseInt(config.getHub().getPort());
          if (portOccupied(port)) {
              logger.debug("Hub on port " + port + " is running");
          } else {
              logger.debug("Hub on port " + port + " is NOT running, attempting to start");

              Boolean isWindows = RuntimeConfig.getOS().isWindows();
              logger.debug("isWindows: " + isWindows);
              String command = getOsSpecificHubStartCommand(isWindows);
              logger.debug("command: " + command);
              logger.debug(ExecuteCommand.execRuntime(command, false));
          }
      }
  }

  private static void healNodesIfNeeded(Config config) {
    logger.info("Checking if all nodes are running");
    for (GridNode node : config.getNodes()) {
      int port = node.getConfiguration().getPort();
      if (portOccupied(port)) {
        logger.debug("Node on port " + port + " is running");
      } else {
        logger.debug("Node on port " + port + " is NOT running, attempting to start");

        Boolean isWindows = RuntimeConfig.getOS().isWindows();
        logger.debug(isWindows);
        String logFile = node.getLoadedFromFile().replace("json", "log");
        logger.debug(logFile);
        String configFile = node.getLoadedFromFile();
        logger.debug(configFile);
        String startCommand = getNodeStartCommand(configFile, isWindows);
        logger.debug(startCommand);
        String backgroundCommand = getBackgroundStartCommandForNode(startCommand,logFile, isWindows);
        logger.debug(backgroundCommand);

        logger.debug(startOneNode(backgroundCommand));

      }
    }
  }



  private static Boolean portOccupied(int port) {
    JsonObject foo = PortChecker.getParsedPortInfo(port);
    if (foo.has("pid")) {
      return true;
    }
    return false;
  }


}
