package iteration2;

import models.GenerateChangeUserNameRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.ChangeUserNameRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class ChangeUserNameTest {
    @Test
    public void successChangeUserName() {
        GenerateChangeUserNameRequest body = GenerateChangeUserNameRequest.builder()
                .name("John smith")
                .build();

        ChangeUserNameRequest changeUserNameAction = new ChangeUserNameRequest(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        changeUserNameAction.put(body);

        // Проверяем результаты (GET)
        changeUserNameAction.get("/api/v1/customer/profile");
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
            GenerateChangeUserNameRequest body = GenerateChangeUserNameRequest.builder()
                    .name(invalidName)
                    .build();

            ChangeUserNameRequest nameAction = new ChangeUserNameRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );
            nameAction.put(body);
        }
    }
}
