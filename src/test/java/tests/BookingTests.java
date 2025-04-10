package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import models.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spec.BookingSpec;
import static org.hamcrest.Matchers.equalTo;
import java.util.List;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Booking API Tests")
@Feature("CRUD operations")
@Tag("AllTests")
public class BookingTests extends TestBase {
    private static final Logger log = LoggerFactory.getLogger(BookingTests.class);
    private static String token;
    private static int bookingId;

    @BeforeAll
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Получаем токен авторизации")
    static void setTokenTest() {
        step("Получаем токен авторизации", () -> {
            RequestCreateToken request = new RequestCreateToken();
            request.setUsername("admin");
            request.setPassword("password123");

            Token authResponse = given()
                    .spec(BookingSpec.request)
                    .body(request)
                    .when()
                    .post("/auth")
                    .then()
                    .spec(BookingSpec.response200)
                    .extract()
                    .as(Token.class);

            token = authResponse.getToken();
            assertNotNull(token, "Token should not be null");
            log.info("Token received: {}", token);
        });
    }

    @Test
    @Tag("booking")
    @Tag("positive")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Получаем бронирование по имени  и фамилии")
    void getBookingsByFirstnameTest() {
        step("Получаем список бронирований по имени и фамилии", () -> {
            Response response = given()
                    .spec(BookingSpec.request)
                    .queryParam("firstname", "Jim")
                    .queryParam("lastname", "Brown")
                    .when()
                    .get("/booking")
                    .then()
                    .spec(BookingSpec.response200)
                    .extract()
                    .response();

            List<BookingId> bookings = response.jsonPath().getList("", BookingId.class);

            if (bookings.isEmpty()) {
                log.warn("Список бронирований пуст.");
            } else {
                bookings.forEach(b -> assertNotNull(b.getBookingid(), "Booking ID не должен быть null"));
            }
        });
    }

    @Test
    @Epic("API Tests")
    @Feature("Create Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Create a new booking")
    @Description("Проверяем, что можно создать новое бронирование")
    @DisplayName("Создание бронирования")
    void createBookingTest() {
        step("Создаём бронирование", () -> {
            BookingDates bookingDates = new BookingDates("2018-01-01", "2019-01-01");
            Booking booking = new Booking("Jim", "Brown", 111, true, bookingDates, "Breakfast");

            bookingId = given()
                    .spec(BookingSpec.request)
                    .body(booking)
                    .when()
                    .post("/booking")
                    .then()
                    .spec(BookingSpec.response200)
                    .extract()
                    .as(BookingResponse.class)
                    .getBookingid();

            assertNotNull(bookingId, "Booking ID should not be null");
            log.info("Booking created with ID: {}", bookingId);
        });
    }

    @Test
    @Tag("booking")
    @Tag("positive")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Обновление бронирования")
    void updateBookingTest() {
        step("Обновляем бронирование", () -> {
            BookingDates newDates = new BookingDates("2022-01-01", "2022-01-10");
            BookingUpdate updatedBooking = new BookingUpdate("James", "Brown", 150, false, newDates, "Late checkout");

            BookingUpdate response = given()
                    .spec(BookingSpec.request)
                    .header("Cookie", "token=" + token)
                    .body(updatedBooking)
                    .when()
                    .put("/booking/" + bookingId)
                    .then()
                    .spec(BookingSpec.response200)
                    .extract()
                    .as(BookingUpdate.class);

            assertThat(response.getFirstname()).isEqualTo("James");
            assertThat(response.getLastname()).isEqualTo("Brown");
            assertThat(response.getTotalprice()).isEqualTo(150);
            assertThat(response.isDepositpaid()).isFalse();
            assertThat(response.getBookingdates().getCheckin()).isEqualTo("2022-01-01");
            assertThat(response.getBookingdates().getCheckout()).isEqualTo("2022-01-10");
            assertThat(response.getAdditionalneeds()).isEqualTo("Late checkout");

            log.info("Booking ID {} успешно обновлён!", bookingId);
        });
    }

    @Test
    @DisplayName("Удаление бронирования по ID")
    void deleteBookingTest() {
        step("Создаём бронирование для удаления", () -> {
            bookingId = createBookingAndGetIdTest();
        });

        step("Удаляем бронирование по ID", () -> {
            String responseMessage = given()
                    .spec(BookingSpec.request)
                    .header("Cookie", "token=" + token)
                    .when()
                    .delete("/booking/" + bookingId)
                    .then()
                    .spec(BookingSpec.response201)
                    .extract()
                    .asString();

            assertEquals("Created", responseMessage, "Удаление бронирования не было успешным");
        });
    }

    @Step("Создание бронирования через метод")
    int createBookingAndGetIdTest() {
        BookingDates bookingDates = new BookingDates("2018-01-01", "2019-01-01");
        Booking booking = new Booking("Jim", "Brown", 111, true, bookingDates, "Breakfast");

        return given()
                .spec(BookingSpec.request)
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .spec(BookingSpec.response200)
                .extract()
                .as(BookingResponse.class)
                .getBookingid();
    }

    @Test
    @DisplayName("Частичное обновление бронирования по ID")
    void partialUpdateBookingTest() {
        step("Создаём бронирование для обновления", () -> {
            bookingId = createBookingAndGetIdTest();
        });

        step("Частично обновляем бронирование по ID", () -> {
            models.booking.PartialUpdateRequest payload = models.booking.PartialUpdateRequest.builder()
                    .firstname("James")
                    .lastname("Brown")
                    .build();

            given()
                    .spec(BookingSpec.request)
                    .header("Cookie", "token=" + token)
                    .body(payload)
                    .when()
                    .patch("/booking/" + bookingId)
                    .then()
                    .spec(BookingSpec.response200)
                    .body("firstname", equalTo(payload.getFirstname()))
                    .body("lastname", equalTo(payload.getLastname()));
        });
    }
}
