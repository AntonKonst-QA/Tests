package iteration2;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;

public class ChangeUserNameTest {
    @Test
    public void successChangeUserName() {
        given()
                .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "name": "John Smith"
                        }
                        """)
                .put("http://localhost:4111/api/v1/customer/profile")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(strings = {
                "kate", // Имя содержит одно слово
                "kateNew", // Имя содержит два слова без пробела
                "Kate1998", // Имя содержит одно слово с цифрами
                "Kate 1998", // Имя содержит одно слово и цифры через пробел
                "" // Вместо имени пустое поле
        })

        public void failChangeName(String invalidName) {
            given()
                    .auth().preemptive().basic("kate1998", "verysTRongPassword33$")
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body("""
                        {
                            "name": "%s"
                        }
                        """.formatted(invalidName))
                    .put("http://localhost:4111/api/v1/customer/profile")
                    .then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_BAD_REQUEST);
        }
    }
}
