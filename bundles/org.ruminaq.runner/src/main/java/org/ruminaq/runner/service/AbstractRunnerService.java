package org.ruminaq.runner.service;

import java.io.IOException;
import java.util.Properties;

public abstract class AbstractRunnerService implements RunnerService {

    protected Properties prop = new Properties();

    {
        try {
            prop.load(this.getClass().getResourceAsStream("bundle-info.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBundleName() {
        return prop.getProperty("bundleName");
    }

    @Override
    public String getVersion() {
        return prop.getProperty("version");
    }
}
