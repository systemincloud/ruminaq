package org.ruminaq.runner.impl.debug;

import org.ruminaq.runner.impl.InternalInputPortI;
import org.ruminaq.runner.impl.InternalOutputPortI;

public interface DebugVisitor {
  void visit(InternalInputPortI elem);

  void visit(InternalOutputPortI elem);

  void visit(Debug elem);
}
