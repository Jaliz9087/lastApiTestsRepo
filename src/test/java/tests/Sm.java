package tests;

import io.qameta.allure.*;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spec.BookingSpec;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Owner("-whey-")
@Epic("Booking API Tests")
@Feature("CRUD operations")
public class Sm {
    private static final Logger log = LoggerFactory.getLogger(Sm.class);
    private static String token;
    private static int bookingId;

    @BeforeAll
    @Description("Получение токена перед тестами")
    @Step("Запрашиваем токен авторизации")
    public static void setToken() {
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
        assertNotNull(token, "Токен не должен быть null!");
        log.info("Токен успешно получен: {}", token);
    }

    @Test
    @Description("Получение списка бронирований по имени")
    @Step("Получаем список бронирований по имени")
    public void testGetBookingsByFirstname() {
        BookingIds bookingResponse = given()
                .spec(BookingSpec.getRequestSpec())
                .queryParam("firstname", "Jim")
                .when()
                .get("/booking")
                .then()
                .spec(BookingSpec.getBookingListSpec())
                .extract()
                .as(BookingIds.class);

        List<Integer> bookingIds = bookingResponse.getBookingid();
        log.info("Найденные booking IDs: {}", bookingIds);

        assertNotNull(bookingIds, "Список бронирований не должен быть пустым!");
        assertTrue(bookingIds.size() > 0, "Список бронирований должен содержать элементы.");
    }

    @Test
    @Description("Создание нового бронирования")
    @Step("Создаём новое бронирование")
    public void testCreateBooking() {
        BookingDates bookingDates = new BookingDates("2018-01-01", "2019-01-01");
        Booking booking = new Booking("Jim", "Brown", 111, true, bookingDates, "Breakfast");

        BookingResponse response = given()
                .spec(BookingSpec.getRequestSpec())
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .spec(BookingSpec.getBookingCreateSpec())
                .extract()
                .as(BookingResponse.class);

        bookingId = response.getBookingid();
        assertNotNull(bookingId, "Booking ID не должен быть null!");
        log.info("Бронирование успешно создано. ID: {}", bookingId);
    }

    @Test
    @Description("Обновление существующего бронирования")
    @Step("Обновляем бронирование по ID")
    public void testUpdateBooking() {
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

        validateUpdatedBooking(response);
    }

    @Step("Проверяем обновленные данные бронирования")
    private void validateUpdatedBooking(BookingUpdate response) {
        assertEquals("James", response.getFirstname(), "Имя не обновлено!");
        assertEquals("Brown", response.getLastname(), "Фамилия не обновлена!");
        assertEquals(150, response.getTotalprice(), "Цена не обновлена!");
        assertFalse(response.isDepositpaid(), "Статус депозита не обновлён!");
        assertEquals("2022-01-01", response.getBookingdates().getCheckin(), "Дата заезда не обновлена!");
        assertEquals("2022-01-10", response.getBookingdates().getCheckout(), "Дата выезда не обновлена!");
        assertEquals("Late checkout", response.getAdditionalneeds(), "Дополнительные услуги не обновлены!");

        log.info("Бронирование ID {} успешно обновлено!", bookingId);
    }
}