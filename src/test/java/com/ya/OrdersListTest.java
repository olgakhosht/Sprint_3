package com.ya;

import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Story("Getting a list of orders")
public class OrdersListTest {

    OrdersClient ordersClient;
    Order order;
    int orderTrack;

    @Before
    public void setUpOrder() {
        ordersClient = new OrdersClient();
        order = new Order(
                "Алевтина",
                "Лютикова",
                "ул. Прекрасная, д.2, кв.6",
                "4",
                "+7 888 888 88 88",
                "5",
                "2022-04-04",
                "Стучать громко",
                null);
        ordersClient.createOrder(order);
        ValidatableResponse orderCreatedResponse = ordersClient.createOrder(order);
        orderTrack = orderCreatedResponse.extract().path("track");
    }


    @After
    public void cancel() {
        ordersClient.cancelOrder(orderTrack);
    }


    @Test
    @DisplayName("Getting a list of orders without parameters")
    public void checkGetListOrdersWithoutParameters() {
        ValidatableResponse listOrdersResponse = ordersClient.getListOrdersWithoutParameters();

        int statusCode = listOrdersResponse.extract().statusCode();
        assertThat("При получении списка заказов без параметров не получен ожидаемый статус кода", statusCode, equalTo(SC_OK));

        List Ids = listOrdersResponse.extract().jsonPath().getList("orders.id");
        for (int i = 0; i < Ids.size(); i++) {
            assertThat("При получении списка заказов без параметров у заказа некорректный Id", Ids.get(i), is(not(0)));
        }

        List courierIds = listOrdersResponse.extract().jsonPath().getList("orders.courierId");
        for (int i = 0; i < courierIds.size(); i++) {
            assertThat("При получении списка заказа без параметров у заказа courierId не null", courierIds.get(i), equalTo(null));
        }
    }
}
