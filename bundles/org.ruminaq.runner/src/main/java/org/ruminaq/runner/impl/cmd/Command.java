package org.ruminaq.runner.impl.cmd;

import org.ruminaq.runner.impl.InternalInputPortI;

public interface Command {
    void execute(InternalInputPortI internalInputPortI);
}
