package helpers;

import io.qameta.allure.restassured.AllureRestAssured;

public class AllureListener {
    public static AllureRestAssured withCustomTemplates() {
        return new AllureRestAssured()
                .setRequestTemplate("request.ftl")
                .setResponseTemplate("response.ftl");
    }
}
