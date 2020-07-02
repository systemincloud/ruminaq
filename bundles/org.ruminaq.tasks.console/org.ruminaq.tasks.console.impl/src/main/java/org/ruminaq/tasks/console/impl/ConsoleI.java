/*******************************************************************************
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package org.ruminaq.tasks.console.impl;

import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ruminaq.model.ruminaq.Task;
import org.ruminaq.runner.RunnerLoggerFactory;
import org.ruminaq.runner.impl.BasicTaskI;
import org.ruminaq.runner.impl.EmbeddedTaskI;
import org.ruminaq.runner.impl.PortMap;
import org.ruminaq.runner.impl.data.DataI;
import org.ruminaq.runner.impl.data.TextI;
import org.ruminaq.tasks.console.model.Port;
import org.ruminaq.tasks.console.model.console.Console;

import ch.qos.logback.classic.Logger;

public class ConsoleI extends BasicTaskI {

  private final Logger logger = RunnerLoggerFactory.getLogger(ConsoleI.class);

  private static final String NEW_LINE = System.lineSeparator();

  private volatile List<String> cmds = new LinkedList<>();
  private volatile List<ConsoleViewService> listeners = new LinkedList<>();

  private Console console;
  private int maxNbOfLines;

  private StringBuilder history = new StringBuilder();

  private int nbOfLines = 0;

  public ConsoleI(EmbeddedTaskI parent, Task task) {
    super(parent, task);
    this.console = (Console) task;
    this.maxNbOfLines = console.getNbOfLines();
    logger.trace("Max number of lines: {} ", maxNbOfLines);

    if (!task.getOutputPort().isEmpty()) {
      setExternalSource(true);
    }

//    DirmiClient.INSTANCE.register(
//        console.getBundleName() + ":" + console.getVersion(),
//        Util.getUniqueId(console, parent.getBasePath()), new ConsoleIService() {
//          @Override
//          public void newCommand(String cmd) {
//            logger.trace("New command: {} ", cmd);
//            ConsoleI.this.cmds.add(cmd);
//            addExternalSrcExecNb(ConsoleI.this.cmds.size());
//            setReadyWithParents(true);
//          }
//
//          @Override
//          public void addListener(ConsoleViewService viewApi) {
//            if (!listeners.contains(viewApi)) {
//              logger.trace("Add Console View: {} ", viewApi);
//              listeners.add(viewApi);
//            }
//          }
//
//          @Override
//          public void removeListener(ConsoleViewService viewApi) {
//            logger.trace("Remove Console View: {} ", viewApi);
//            listeners.remove(viewApi);
//          }
//
//          @Override
//          public String getHistory() throws RemoteException {
//            logger.trace("Console View ask for history");
//            return history.toString();
//          }
//
//          @Override
//          public void clearHistory() throws RemoteException {
//            logger.trace("Clear history");
//            history.setLength(0);
//            nbOfLines = 0;
//          }
//        });
  }

  @Override
  public void runnerStart() {
    clearScreen();
  }

  @Override
  protected void executeExternalSrc() {
    if (cmds.size() > 0) {
      putData(Port.OUT, new TextI(cmds.remove(0)));
    }
  }

  @Override
  protected void execute(PortMap portIdData, int grp) {
    DataI data = portIdData.get(Port.IN);
    TextI text = data.get(TextI.class);

    // TODO communicate to client about error
    if (text == null)
      return;

    String string = text.toString(console.isMatricesPretty(), -1);
    int clearSign = string.indexOf("\0");
    if (clearSign != -1) {
      string = string.substring(clearSign + 1);
      clearScreen();
    }

    history.append(string);
    if (console.getNbOfLines() > 0) {
      nbOfLines += StringUtils.countMatches(string, "\n");
    }
    if (console.isNewLine()) {
      history.append(NEW_LINE);
      if (maxNbOfLines > 0)
        nbOfLines++;
    }

    logger.trace("Number of lines : {}", nbOfLines);

    if (nbOfLines > maxNbOfLines && maxNbOfLines > 0) {
      int i = nbOfLines - maxNbOfLines;
      logger.trace("{} lines to delete", i);
      while (i-- > 0) {
        history.delete(0, history.indexOf("\n") + 1);
        for (ConsoleViewService view : listeners) {
          try {
            view.deleteFirstLine();
          } catch (RemoteException e) {
            e.printStackTrace();
          }
        }
      }
      nbOfLines = maxNbOfLines;
    }

    for (ConsoleViewService view : listeners) {
      try {
        String tmp = string;
        if (console.isNewLine())
          tmp += NEW_LINE;
        view.newOutput(tmp);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }

  private void clearScreen() {
    for (ConsoleViewService view : listeners) {
      try {
        view.clearScreen();
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
    history.setLength(0);
    nbOfLines = 0;
  }
}
