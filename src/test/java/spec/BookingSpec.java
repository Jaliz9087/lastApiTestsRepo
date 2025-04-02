package spec;

import helpers.CustomAllureListener;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class BookingSpec {

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType(ContentType.JSON)
                .addFilter(CustomAllureListener.withCustomTemplates()) // Добавляем Allure-фильтр
                .log(LogDetail.ALL)
                .build();
    }

    public static ResponseSpecification getBookingListSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("bookingid.size()", greaterThan(0))
                .build();
    }
    public static RequestSpecification getRequestSpecif() {
        return new RequestSpecBuilder()
                .setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification getBookingCreateSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("bookingid", notNullValue())
                .build();
    }
    public static RequestSpecification getRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri("https://restful-booker.herokuapp.com")
                .setContentType(ContentType.JSON)
                .addHeader("Cookie", "token=" + token)
                .build();
    }

    public static ResponseSpecification getBookingUpdateSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("firstname", notNullValue())
                .expectBody("lastname", notNullValue())
                .expectBody("totalprice", notNullValue())
                .expectBody("depositpaid", notNullValue())
                .expectBody("bookingdates.checkin", notNullValue()) // Исправлено
                .expectBody("bookingdates.checkout", notNullValue()) // Исправлено
                .expectBody("additionalneeds", notNullValue())
                .build();
    }

    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    public static RequestSpecification getRequestSpec2() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json")
                .build();
    }

    public static RequestSpecification getRequestSpecWithToken(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json")
                .addHeader("Cookie", "token=" + token)
                .addFilter(CustomAllureListener.withCustomTemplates()) // Добавляем Allure-фильтр
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getBookingListSpec4() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json")
                .build();
    }

    public static RequestSpecification getBookingCreateSpec3() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json")
                .build();
    }

//    public static RequestSpecification getBookingUpdateSpec2() {
//        return new RequestSpecBuilder()
//                .setBaseUri(BASE_URL)
//                .setContentType("application/json")
//                .build();
//    }
public static RequestSpecification getReq(String token) {
    return given()
            .baseUri("https://restful-booker.herokuapp.com")
            .basePath("/booking")
            .contentType(ContentType.JSON)
            .header("Authorization", "Basic " + token); // передаем токен для авторизации
}
}
