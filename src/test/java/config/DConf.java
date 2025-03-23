
package config;

import org.aeonbits.owner.Config;

import static com.codeborne.selenide.Browsers.CHROME;

@Config.Sources({
        "classpath:local-config.properties",
        "classpath:remote-config.properties"
})
public interface DConf extends Config {
    @Key("baseUrl")
    @DefaultValue("https://restful-booker.herokuapp.com")
    String getBaseUrl();

    @Key("browser")
    @DefaultValue(CHROME)
    String getBrowser();

    @Key("browserSize")
    @DefaultValue("1920x1080")
    String getBrowserSize();

    @Key("browserVersion")
    @DefaultValue("latest")
    String getBrowserVersion();

    @Key("remote")
    @DefaultValue("false")
    boolean remote();

    @Key("remoteUrl")
    String remoteUrl();
}