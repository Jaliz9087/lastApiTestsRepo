package config;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;

public class WDConf {
    private final DConf dataConfig;


    public WDConf(DConf dataConfig) {
        this.dataConfig = dataConfig;
    }


    public void configure () {
        Configuration.pageLoadStrategy = "eager";
        RestAssured.baseURI = dataConfig.getBaseURI();
        Configuration.browser = dataConfig.getBrowser();
        Configuration.browserSize = dataConfig.getBrowserSize();
        Configuration.browserVersion = dataConfig.getBrowserVersion();


        if (dataConfig.remote()) {
            Configuration.remote = dataConfig.remoteUrl();
        }
    }
}
