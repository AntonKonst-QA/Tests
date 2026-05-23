package iteration2.api;

import api.generators.RandomModelGenerator;
import api.models.GenerateChangeUserNameRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.steps.UserSteps;
import api.utils.ModelComparator;

public class ChangeUserNameTest extends BaseTest{
    private UserSteps userSteps = new UserSteps();
    @Test
    public void successChangeUserName() {
        var requestBody = RandomModelGenerator.generate(GenerateChangeUserNameRequest.class);
        var response = userSteps.changeName(requestBody);

        ModelComparator.compare(requestBody,response);

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
            String nameBefore = userSteps.getProfile().getName();

            var body = GenerateChangeUserNameRequest.builder().name(invalidName).build();

            String actualError = userSteps.changeNameAndExpectError(body);

            String nameAfter = userSteps.getProfile().getName();

            softly.assertThat(actualError).contains("Name must contain two words");
            softly.assertThat(nameAfter)
                    .as("Имя не должно измениться")
                    .isEqualTo(nameBefore);
        }
    }
}
