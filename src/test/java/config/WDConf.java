package config;

import com.codeborne.selenide.Configuration;

public class WDConf {
    private final DConf dataConfig;

    public WDConf(DConf dtaConfig) {
        this.dataConfig = dtaConfig;
    }

    public void dataConfig() {
        //Configuration.holdBrowserOpen = true;
        Configuration.pageLoadStrategy = "eager";
        Configuration.baseUrl = dataConfig.getBaseUrl();
        Configuration.browser = dataConfig.getBrowser();
        Configuration.browserSize = dataConfig.getBrowserSize();
        Configuration.browserVersion = dataConfig.getBrowserVersion();
        if (dataConfig.remote()) {
            Configuration.remote = dataConfig.remoteUrl();
        }
    }
}