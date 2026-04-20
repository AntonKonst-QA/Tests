package iteration2;

import generators.RandomData;
import models.GenerateChangeUserNameRequest;
import models.GenerateChangeUserNameResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.ChangeUserNameRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class ChangeUserNameTest extends BaseTest{
    @Test
    public void successChangeUserName() {
        String expectedName = RandomData.getUsername();

        GenerateChangeUserNameRequest body = GenerateChangeUserNameRequest.builder()
                .name(expectedName)
                .build();

        ChangeUserNameRequester changeUserNameAction = new ChangeUserNameRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateChangeUserNameResponse response = changeUserNameAction.put(body)
                .extract()
                .as(GenerateChangeUserNameResponse.class);

        org.junit.jupiter.api.Assertions.assertEquals(expectedName, response.getCustomer().getName());
        org.junit.jupiter.api.Assertions.assertEquals("Profile updated successfully", response.getMessage());

        // Проверяем результаты (GET). Но это не обязательно, т.к. выше выполнили проверку.
        changeUserNameAction.get(body);
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

            ChangeUserNameRequester nameAction = new ChangeUserNameRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );
            nameAction.put(body);
        }
    }
}
