package org.ruminaq.tasks.pythontask.impl.service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.ruminaq.runner.impl.data.DataI;

public abstract class AbstractPythonRunnerService implements PythonRunnerService {

	protected Properties prop = new Properties();

	{
		try {
			prop.load(this.getClass().getResourceAsStream("bundle-info.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void importDatas(PythonInterpreter pi) {
	}

	@Override
	public PyObject toJythonData(PythonInterpreter pi, DataI dataI, PyList pyDims) {
		return null;
	}

	@Override
	public DataI fromJythonData(PythonInterpreter pi, PyObject data, PyObject[] pyValues, List<Integer> dims) {
		return null;
	}
}
