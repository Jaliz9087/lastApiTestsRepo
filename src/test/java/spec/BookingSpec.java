package spec;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static helpers.CustomAllureListener.withCustomTemplates;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;

public class BookingSpec {

    private static final String BASE_URL = "https://restful-booker.herokuapp.com";

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(withCustomTemplates()) // Allure filter
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getRequestSpec(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addHeader("Cookie", "token=" + token)
                .addFilter(withCustomTemplates()) // Allure filter
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getRequestSpecWithToken(String token) {
        return getRequestSpec(token); // Объединено
    }

    public static RequestSpecification getRequestSpecif() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(withCustomTemplates()) // Allure filter
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getRequestSpec2() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType("application/json")
                .addFilter(withCustomTemplates()) // Allure filter
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification getBookingListSpec4() {
        return getRequestSpec2(); // Переиспользуем
    }

    public static RequestSpecification getBookingCreateSpec3() {
        return getRequestSpec2(); // Переиспользуем
    }

    public static RequestSpecification getReq(String token) {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setBasePath("/booking")
                .setContentType(ContentType.JSON)
                .addHeader("Authorization", "Basic " + token)
                .addFilter(withCustomTemplates()) // Allure filter
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

    public static ResponseSpecification getBookingCreateSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .expectBody("bookingid", notNullValue())
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
                .expectBody("bookingdates.checkin", notNullValue())
                .expectBody("bookingdates.checkout", notNullValue())
                .expectBody("additionalneeds", notNullValue())
                .build();
    }
}
