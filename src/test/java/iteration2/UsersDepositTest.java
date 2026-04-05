package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

public class UsersDepositTest {
    @Test
    public void successGenerateDepositTest() {
        given()
                .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "id": 1,
                          "balance": 5000
                        }
                        """)
                .post("http://localhost:4111/api/v1/accounts/deposit")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(ints = {
                 0, // Нет суммы на депозите
                5001, // Превышение максимальной суммы на депозите
                -5000 // Отрицательная сумма не депозите
        })

        public  void ailedGenerateDepositTest(int invalidDeposit) {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "id": 1,
                              "balance": %d
                            }
                            """.formatted(invalidDeposit))
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }

    @Nested
    class DepositeToNonAccountOrSomeOneTests {

        @Test
        public  void depositToNonAccountTest() {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "id": 0,
                              "balance": 5000
                            }
                            """)
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_FORBIDDEN);
        }

        @Test
        public  void depositToSomeOneElseAccountTest() {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                            {
                              "id": 2, // ID2 принадлежит другому пользователю
                              "balance": 5000
                            }
                            """)
                    .post("http://localhost:4111/api/v1/accounts/deposit")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
