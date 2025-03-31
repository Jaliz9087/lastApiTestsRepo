package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spec.BookingSpec;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@Owner("-whey-")
@Epic("Booking API Tests")
@Feature("CRUD operations")
public class Sm {
    private static final Logger log = LoggerFactory.getLogger(Sm.class);
    private static String token;
    private static int bookingId;

    @BeforeEach
    @DisplayName("Добавляем AllureSelenide Listener")
    void setupAllureListener() {
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));
    }
    @BeforeAll
    static void setupAllure() {
        AllureLifecycle lifecycle = Allure.getLifecycle();
    }

    @BeforeAll
    @Description("Получение токена перед тестами")
    @DisplayName("Запрашиваем токен авторизации")
    public static void setToken() {
        RequestCreateToken request = step("Создаём объект запроса на токен", () -> {
            RequestCreateToken req = new RequestCreateToken();
            req.setUsername("admin");
            req.setPassword("password123");
            return req;
        });

        Token authResponse = step("Отправляем запрос на получение токена", () ->
                given()
                        .spec(BookingSpec.getRequestSpec())
                        .body(request)
                        .when()
                        .post("/auth")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(Token.class)
        );

        step("Сохраняем и проверяем токен", () -> {
            token = authResponse.getToken();
            assertNotNull(token, "Токен не должен быть null!");
            log.info("Токен успешно получен: {}", token);
        });
    }

    @Test
    @Description("Получение списка бронирований по имени")
    @DisplayName("Получаем список бронирований по имени 'Jim'")
    public void testGetBookingsByFirstname() {
        BookingIds bookingResponse = step("Запрашиваем список бронирований по имени 'Jim'", () ->
                given()
                        .spec(BookingSpec.getRequestSpec())
                        .queryParam("firstname", "Jim")
                        .when()
                        .get("/booking")
                        .then()
                        .spec(BookingSpec.getBookingListSpec())
                        .extract()
                        .as(BookingIds.class)
        );

        step("Проверяем полученные данные", () -> {
            List<Integer> bookingIds = bookingResponse.getBookingid();
            log.info("Найденные booking IDs: {}", bookingIds);

            assertNotNull(bookingIds, "Список бронирований не должен быть пустым!");
            assertFalse(bookingIds.isEmpty(), "Список бронирований должен содержать элементы.");
        });
    }

    @Test
    @Description("Создание нового бронирования")
    @DisplayName("Создаём новое бронирование")
    public void testCreateBooking() {
        BookingDates bookingDates = new BookingDates("2018-01-01", "2019-01-01");
        Booking booking = new Booking("Jim", "Brown", 111, true, bookingDates, "Breakfast");

        bookingId = step("Отправляем запрос на создание бронирования", () ->
                given()
                        .spec(BookingSpec.getRequestSpec())
                        .body(booking)
                        .when()
                        .post("/booking")
                        .then()
                        .spec(BookingSpec.getBookingCreateSpec())
                        .extract()
                        .as(BookingResponse.class)
                        .getBookingid()
        );

        step("Проверяем созданное бронирование", () -> {
            assertNotNull(bookingId, "Booking ID не должен быть null!");
            log.info("Бронирование успешно создано. ID: {}", bookingId);
        });
    }

    @Test
    @Description("Обновление существующего бронирования")
    @DisplayName("Обновляем бронирование по ID")
    public void testUpdateBooking() {
        BookingDates newDates = new BookingDates("2022-01-01", "2022-01-10");
        BookingUpdate updatedBooking = new BookingUpdate("James", "Brown", 150, false, newDates, "Late checkout");

        BookingUpdate response = step("Отправляем запрос на обновление бронирования", () ->
                given()
                        .spec(BookingSpec.getRequestSpecWithToken(token))
                        .body(updatedBooking)
                        .when()
                        .put("/booking/" + bookingId)
                        .then()
                        .spec(BookingSpec.getBookingUpdateSpec())
                        .extract()
                        .as(BookingUpdate.class)
        );

        validateUpdatedBooking(response);
    }

    @Step("Проверяем обновленные данные бронирования")
    private void validateUpdatedBooking(BookingUpdate response) {
        step("Проверяем изменения бронирования", () -> {
            assertEquals("James", response.getFirstname(), "Имя не обновлено!");
            assertEquals("Brown", response.getLastname(), "Фамилия не обновлена!");
            assertEquals(150, response.getTotalprice(), "Цена не обновлена!");
            assertFalse(response.isDepositpaid(), "Статус депозита не обновлён!");
            assertEquals("2022-01-01", response.getBookingdates().getCheckin(), "Дата заезда не обновлена!");
            assertEquals("2022-01-10", response.getBookingdates().getCheckout(), "Дата выезда не обновлена!");
            assertEquals("Late checkout", response.getAdditionalneeds(), "Дополнительные услуги не обновлены!");

            log.info("Бронирование ID {} успешно обновлено!", bookingId);
        });
    }
}
