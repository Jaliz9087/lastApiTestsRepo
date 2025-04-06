package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spec.BookingSpec;
import java.util.List;


import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Booking API Tests")
@Feature("CRUD operations")
public class Sm {
    private static final Logger log = LoggerFactory.getLogger(Sm.class);
    private static String token;
    private static int bookingId;

    @BeforeAll
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Get Auth Token")
    static void setToken() {
        RequestCreateToken request = new RequestCreateToken();
        request.setUsername("admin");
        request.setPassword("password123");

        Token authResponse = given()
                .spec(BookingSpec.getRequestSpec())
                .body(request)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .extract()
                .as(Token.class);

        token = authResponse.getToken();
        assertNotNull(token, "Token should not be null");
        log.info("Token received: {}", token);
    }

    @Test
    @Tag("booking")
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Bookings by Firstname")
    void getBookingsByFirstname() {

        Response response = given()
                .spec(BookingSpec.getRequestSpec())
                .queryParam("firstname", "Jim")
                .queryParam("lastname", "Brown")
                .when()
                .get("/booking")
                .then()
                .spec(BookingSpec.getBookingListSpec())
                .extract()
                .response();


        List<BookingId> bookings = response.jsonPath().getList("", BookingId.class);


        if (bookings.isEmpty()) {
            log.warn("Список бронирований пуст. Возможно, нет активных бронирований для указанных параметров.");
        } else {

            bookings.forEach(b -> assertNotNull(b.getBookingid(), "Booking ID не должен быть null"));
        }
    }




    @Test
    @Epic("API Tests")
    @Feature("Create Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Create a new booking")
    @Description("Проверяем, что можно создать новое бронирование")
    @DisplayName("Создание бронирования")
    void createBooking() {
        BookingDates bookingDates = new BookingDates("2018-01-01", "2019-01-01");
        Booking booking = new Booking("Jim", "Brown", 111, true, bookingDates, "Breakfast");

        bookingId = given()
                .spec(BookingSpec.getRequestSpec())
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .spec(BookingSpec.getBookingCreateSpec())
                .extract()
                .as(BookingResponse.class)
                .getBookingid();

        assertNotNull(bookingId, "Booking ID should not be null");
        log.info("Booking created with ID: {}", bookingId);
    }

    @Test
    @Tag("booking")
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Update Booking")
    void updateBooking() {
        BookingDates newDates = new BookingDates("2022-01-01", "2022-01-10");
        BookingUpdate updatedBooking = new BookingUpdate("James", "Brown", 150, false, newDates, "Late checkout");

        BookingUpdate response = given()
                .spec(BookingSpec.getRequestSpecWithToken(token))
                .body(updatedBooking)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .spec(BookingSpec.getBookingUpdateSpec())
                .extract()
                .as(BookingUpdate.class);

        assertThat(response.getFirstname()).isEqualTo("James");
        assertThat(response.getLastname()).isEqualTo("Brown");
        assertThat(response.getTotalprice()).isEqualTo(150);
        assertThat(response.isDepositpaid()).isFalse();
        assertThat(response.getBookingdates().getCheckin()).isEqualTo("2022-01-01");
        assertThat(response.getBookingdates().getCheckout()).isEqualTo("2022-01-10");
        assertThat(response.getAdditionalneeds()).isEqualTo("Late checkout");

        log.info("Booking ID {} successfully updated!", bookingId);

    }
    @Test
    @DisplayName("Удаление бронирования по ID")
    void deleteBookingTest() {
        int bookingId = createBookingAndGetId(); // 👈 вызываем создание

        String responseMessage = given()
                .spec(BookingSpec.getRequestSpecWithToken(token))
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .statusCode(201)
                .extract()
                .asString();

        assertEquals("Created", responseMessage, "Удаление бронирования не было успешным");
    }

    int createBookingAndGetId() {
        BookingDates bookingDates = new BookingDates("2018-01-01", "2019-01-01");
        Booking booking = new Booking("Jim", "Brown", 111, true, bookingDates, "Breakfast");

        return given()
                .spec(BookingSpec.getRequestSpec())
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .extract()
                .as(BookingResponse.class)
                .getBookingid();
    }

}