package tests.web;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.DConf;
import config.WDConf;
import helpers.Attachments2;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class WebTestBase {

    private boolean isRemote = Boolean.parseBoolean(System.getProperty("isRemote", "false"));
    private String environment = System.getProperty("env");
    private WDConf wdConf;


    // Инициализация конфигурации перед всеми тестами
    @BeforeAll
    static void configParams() {
        DConf dConf = ConfigFactory.create(DConf.class); // Используем ConfigFactory для создания экземпляра конфигурации
        WDConf wdConf = new WDConf(dConf); // Передаем полученную конфигурацию в WDConf
        wdConf.configure();  // Теперь вызываем configure(), а не dataConfig()
    }

    // Добавляем слушатель Allure для Selenide
    @BeforeEach
    void addSelenideListener() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    // Добавляем аттачменты после каждого теста
    @AfterEach
    void addAttachments() {
        if (isRemote || "remote".equals(environment)) {
            if (!Configuration.browser.equals("firefox")) {
                Attachments2.screenshotAs("Test screenshot");
                Attachments2.pageSource();
                Attachments2.browserConsoleLogs();
                Attachments2.addVideo();
            }
        } else {
            Attachments2.screenshotAs("Test screenshot");
            Attachments2.pageSource();
            Attachments2.browserConsoleLogs();
        }
        Selenide.closeWebDriver();
    }
}
