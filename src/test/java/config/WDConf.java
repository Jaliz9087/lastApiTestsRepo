package config;

import com.codeborne.selenide.Configuration;

public class WDConf {
    private final DConf dataConfig;


    public WDConf(DConf dataConfig) {
        this.dataConfig = dataConfig;
    }


    public void configure() {
        Configuration.holdBrowserOpen = true;
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
