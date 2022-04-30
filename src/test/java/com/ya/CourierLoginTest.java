package com.ya;

import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Story("Courier authorization")
public class CourierLoginTest {

    CourierClient courierClient;
    Courier courier;
    int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandom();
        courierClient.createCourier(courier);
    }

    @After
    public void tearDown() {
        courierClient.deleteCourier(courierId);
    }


    @Test
    @DisplayName("Courier authorization with the correct login and password")
    public void courierCanLoginWithCorrectCredentials() {
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("При авторизации курьера с корректными данными не получен ожидаемый статус кода", statusCode, equalTo(SC_OK));

        courierId = loginResponse.extract().path("id");
        assertThat("При авторизации курьера с корректными данными в ответе некорректный ID", courierId, is(not(0)));
    }


    @Test
    @DisplayName("Courier authorization with an incorrect login")
    public void courierCannotLoginWithIncorrectLogin() {
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials("loginlogin", courier.getPassword()));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("При авторизации курьера c неправильным логином не получен ожидаемый статус кода", statusCode, equalTo(SC_NOT_FOUND));

        String actualMessage = loginResponse.extract().path("message");
        assertThat("При авторизации курьера с неправильным логином не получено ожидаемое сообщение", actualMessage, equalTo("Учетная запись не найдена") );
    }

    @Test
    @DisplayName("Courier authorization with an incorrect password")
    public void courierCannotLoginWithIncorrectPassword() {
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), "parolparol"));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("При авторизации курьера с неправильным паролем не получен ожидаемый статус кода", statusCode, equalTo(SC_NOT_FOUND));

        String actualMessage = loginResponse.extract().path("message");
        assertThat("При авторизации курьера с неправильным паролем не получено ожидаемое сообщение", actualMessage, equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Courier authorization without login")
    public void courierCannotLoginWithoutLogin() {
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials(null, courier.getPassword()));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("При авторизации курьера без логина не получен ожидаемый статус кода", statusCode, equalTo(SC_BAD_REQUEST));

        String actualMessage = loginResponse.extract().path("message");
        assertThat("При авторизации курьера без логина не получено ожидаемое сообщение", actualMessage, equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Courier authorization without password")
    public void courierCannotLoginWithoutPassword() {
        ValidatableResponse loginResponse = courierClient.loginCourier(new CourierCredentials(courier.getLogin(), null));
        int statusCode = loginResponse.extract().statusCode();
        assertThat("При авторизации курьера без пароля не получен ожидаемый статус кода", statusCode, equalTo(SC_BAD_REQUEST));

        String actualMessage = loginResponse.extract().path("message");
        assertThat("При авторизации курьера без пароля не получено ожидаемое сообщение", actualMessage, equalTo("Недостаточно данных для входа"));
    }
}
