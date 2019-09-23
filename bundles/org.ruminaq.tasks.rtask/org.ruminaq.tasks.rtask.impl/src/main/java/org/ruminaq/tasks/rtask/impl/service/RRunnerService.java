package org.ruminaq.tasks.rtask.impl.service;

import java.util.List;

import org.ruminaq.runner.impl.data.DataI;

import de.walware.rj.data.RObject;

public interface RRunnerService {
  RObject toRData(DataI dataI, RObject dims);

  DataI fromRData(RObject data, RObject[] rValues, List<Integer> dims);
}
