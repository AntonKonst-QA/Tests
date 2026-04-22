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

        ChangeUserNameRequester changeUserNameAction = new ChangeUserNameRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateChangeUserNameRequest body = GenerateChangeUserNameRequest.builder()
                .name(expectedName)
                .build();

        GenerateChangeUserNameResponse response = changeUserNameAction.put(body)
                .extract()
                .as(GenerateChangeUserNameResponse.class);

        softly.assertThat(response.getCustomer().getName())
                .as("Имя в ответе совпадает с ожидаемым")
                .isEqualTo(expectedName);

        softly.assertThat(response.getMessage())
                .as("Сообщение об успешном изменении User Name")
                .isEqualTo("Profile updated successfully");
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
            //  Для получения имени использую спецификацию с ответом 200.
            ChangeUserNameRequester checkAction = new ChangeUserNameRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            // Для проверки смены имени использую негативную спецификацию
            ChangeUserNameRequester nameAction = new ChangeUserNameRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            String nameBefore = checkAction.get()
                    .extract()
                    .jsonPath()
                    .getString("name");

            GenerateChangeUserNameRequest body = GenerateChangeUserNameRequest.builder()
                    .name(invalidName)
                    .build();

            String responseBody = nameAction.put(body).extract().asString();

            String nameAfter = checkAction.get(GenerateChangeUserNameRequest.builder().build())
                    .extract()
                    .jsonPath()
                    .getString("name");

            softly.assertThat(responseBody)
                    .as("Ошибка смены имени " + invalidName)
                    .isNotBlank();

            softly.assertThat(nameAfter)
                    .as("Имя не должно измениться")
                    .isEqualTo(nameBefore);
        }
    }
}
