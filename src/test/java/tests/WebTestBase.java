package tests;


import config.DConf;
import config.WDConf;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;

public class WebTestBase {

    private boolean isRemote = Boolean.parseBoolean(System.getProperty("isRemote", "false"));
    private String environment = System.getProperty("env");
    private WDConf wdConf;




    @BeforeAll
    static void configParams() {
        DConf dConf = ConfigFactory.create(DConf.class);
        WDConf wdConf = new WDConf(dConf);
        wdConf.configure();
    }
}
