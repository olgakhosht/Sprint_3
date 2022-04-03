package com.ya;

import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Story("Creating an order")
@RunWith(Parameterized.class)
public class OrderCreateTest {

    static OrdersClient ordersClient;
    static Order order;
    static int orderTrack;

    private static List<String> colorsScooter;

    public OrderCreateTest(List<String> colorsScooter) {
        this.colorsScooter = colorsScooter;
    }

    @Parameterized.Parameters(name = "Тестовые данные Цвета самоката: {0} {1}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {null},
        };
    }

    @After
    public void cancel() {
        ordersClient.cancelOrder(orderTrack);
    }

    @Test
    @DisplayName("Creating an order with correct data with different scooter color options")
    public void orderCreateWithCorrectData() {
        ordersClient = new OrdersClient();
        order = new Order(
                "Сергей",
                "Арбузов",
                "ул. Замечательная, д.1, кв.4",
                "4",
                "+7 999 333 44 55",
                "5",
                "2022-04-03",
                "Предварительно позвонить",
                colorsScooter);
        ordersClient.createOrder(order);

        ValidatableResponse orderCreatedResponse = ordersClient.createOrder(order);
        int statusCode = orderCreatedResponse.extract().statusCode();
        assertThat("При создании заказа с корректными данными не получен ожидаемый статус кода", statusCode, equalTo(SC_CREATED));

        orderTrack = orderCreatedResponse.extract().path("track");
        assertThat("При создании заказа с корректными данными в ответе некорректный track", orderTrack, is(not(0)));
    }
}
