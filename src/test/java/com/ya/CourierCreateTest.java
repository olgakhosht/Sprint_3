package com.ya;

import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Story("Creating a courier")
public class CourierCreateTest {

    CourierClient courierClient;
    Courier courier;
    int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandom();
    }

    @After
    public void tearDown() {
        courierClient.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Creating a courier with all the correct data")
    public void courierCreateWithValidCredentials() {
        ValidatableResponse createResponse = courierClient.createCourier(courier);

        int statusCode = createResponse.extract().statusCode();
        assertThat("При создании курьера со всеми корректными данными не получен ожидаемый статус кода", statusCode, equalTo(SC_CREATED));

        boolean ok = createResponse.extract().path("ok");
        assertThat("При создании курьера со всеми корректными данными в ответе 'ok' не true", ok, is(true));

        // Авторизация курьера для получения ID курьера. ID курьера нужен для удаления курьера в @After
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.extract().path("id");
    }


    @Test
    @DisplayName("Creating a courier with an existing login")
    public void courierNotCreateWithReplayLogin() {
        courierClient.createCourier(courier);

        // Авторизация курьера для получения ID курьера. ID курьера нужен для удаления курьера в @After
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        courierId = loginResponse.extract().path("id");

        ValidatableResponse createResponse2 = courierClient.createCourier(courier);
        int statusCode = createResponse2.extract().statusCode();
        assertThat("При создании курьера с повторяющимся логином не получен ожидаемый статус кода", statusCode, equalTo(SC_CONFLICT));

        String actualMessage = createResponse2.extract().path("message");
        assertThat("При создании курьера с повторяющимся логином не получено ожидаемое сообщение", actualMessage, equalTo("Этот логин уже используется"));

    }

    @Test
    @DisplayName("Creating a courier without a login")
    public void courierNotCreateWithoutLogin() {
        courier.forgetLogin();
        ValidatableResponse createResponse = courierClient.createCourier(courier);

        int statusCode = createResponse.extract().statusCode();
        assertThat("При создании курьера без логина не получен ожидаемый статус кода", statusCode, equalTo(SC_BAD_REQUEST));

        String actualMessage = createResponse.extract().path("message");
        assertThat("При создании курьера без логина не получено ожидаемое сообщение", actualMessage, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Creating a courier without a password")
    public void courierNotCreateWithoutPassword() {
        courier.forgetPassword();
        ValidatableResponse createResponse = courierClient.createCourier(courier);

        int statusCode = createResponse.extract().statusCode();
        assertThat("При создании курьера без пароля не получен ожидаемый статус кода", statusCode, equalTo(SC_BAD_REQUEST));

        String actualMessage = createResponse.extract().path("message");
        assertThat("При создании курьера без пароля не получено ожидаемое сообщение", actualMessage, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Creating a courier without a firstname")
    public void courierNotCreateWithoutFirstName() {
        courier.forgetFirstName();
        ValidatableResponse createResponse = courierClient.createCourier(courier);

        int statusCode = createResponse.extract().statusCode();
        // В документации отсутствует информация об ответе при запросе без имени. Сделано по аналогии с запросами без логина или пароля
        assertThat("При создании курьера без имени не получен ожидаемый статус кода", statusCode, equalTo(SC_BAD_REQUEST));

        String actualMessage = createResponse.extract().path("message");
        // В документации отсутствует информация об ответе при запросе без имени. Сделано по аналогии с запросами без логина или пароля
        assertThat("При создании курьера без имени не получено ожидаемое сообщение", actualMessage, equalTo("Недостаточно данных для создания учетной записи"));
    }
}
