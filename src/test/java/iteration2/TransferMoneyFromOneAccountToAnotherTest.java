package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

public class TransferMoneyFromOneAccountToAnotherTest {

    // Перевод на свой аккаунт
    @Test
    public void successTransferMoneyBetweenMyAccountTest(){
        given()
                .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 2,
                          "amount": 250.75
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        // Проверяем результаты (GET)
        given()
                .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                .pathParams("id", 1)
                .when()
                .get("http://localhost:4111/api/v1/accounts/{id}/transactions")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .log().all();
    }

    // Перевод на чужой аккаунт
    @Test
    public void successTransferMoneyToAnotherTest(){
        given()
                .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "senderAccountId": 1,
                          "receiverAccountId": 3,
                          "amount": 0.56
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "senderAccountId": 1,
                              "receiverAccountId": 2,
                              "amount": 10000.01
                            }
                            """)
                    .post("http://localhost:4111/api/v1/accounts/transfer")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);

            // Проверяем результаты (GET)
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .pathParams("id", 1)
                    .when()
                    .get("http://localhost:4111/api/v1/accounts/{id}/transactions")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .log().all();
        }

        @Test
        public void lessThanPermissibleAmountTest() {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "senderAccountId": 1,
                              "receiverAccountId": 2,
                              "amount": 9999.99
                            }
                            """)
                    .post("http://localhost:4111/api/v1/accounts/transfer")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);

            // Проверяем результаты (GET)
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .pathParams("id", 1)
                    .when()
                    .get("http://localhost:4111/api/v1/accounts/{id}/transactions")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .log().all();
        }

        @Test
        public void moreThanZeroTest() {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "senderAccountId": 1,
                              "receiverAccountId": 2,
                              "amount": 0.01
                            }
                            """)
                    .post("http://localhost:4111/api/v1/accounts/transfer")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK);

            // Проверяем результаты (GET)
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .pathParams("id", 1)
                    .when()
                    .get("http://localhost:4111/api/v1/accounts/{id}/transactions")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_OK)
                    .log().all();
        }
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(doubles = {
                0.0, // Нет суммы на депозите
                10000.1, // Превышение максимальную сумму перевода
                5001.0, // Превышение максимального баланс на депозите
                -0.1 // Отрицательная сумма не депозите
        })

        public  void failedTransferMoneyTest(double invalidDeposit) {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "senderAccountId": 1,
                              "receiverAccountId": 2,
                              "amount": %s
                            }
                            """.formatted(invalidDeposit))
                    .post("http://localhost:4111/api/v1/accounts/transfer")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
