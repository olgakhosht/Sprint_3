package com.ya;

import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrdersClient extends ScooterRestClient {
    private static final String ORDERS_PATH = "api/v1/orders";

    public ValidatableResponse createOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDERS_PATH)
                .then();
    }

    public ValidatableResponse getListOrdersWithoutParameters() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDERS_PATH)
                .then();
    }

    public ValidatableResponse cancelOrder(int orderTrack) {
        return given()
                .spec(getBaseSpec())
                .body(orderTrack)
                .when()
                .put(ORDERS_PATH + "/cancel")
                .then();
    }
}
