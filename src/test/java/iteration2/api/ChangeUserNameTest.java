package iteration2.api;

import api.generators.RandomModelGenerator;
import api.models.GenerateChangeUserNameRequest;
import common.annotations.Browsers;
import common.storage.SessionStorage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.utils.ModelComparator;

import static api.TestConstants.*;

public class ChangeUserNameTest extends BaseTest{

    @Test
    @Browsers("chrome")
    public void successChangeUserName() {
        var requestBody = RandomModelGenerator.generate(GenerateChangeUserNameRequest.class);
        var response = userSteps.changeName(requestBody);

        ModelComparator.compare(requestBody,response);

        softly.assertThat(response.getMessage())
                .isEqualTo(UPDATED_PROFILE_MESSAGE);
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
            String nameBefore = SessionStorage.getUserSteps().getProfile().getName();

            var body = GenerateChangeUserNameRequest.builder().name(invalidName).build();

            String errorMessage = userSteps.changeNameAndExpectError(body);

            String nameAfter = userSteps.getProfile().getName();

            softly.assertThat(errorMessage)
                    .isEqualTo(INVALID_NAME_MESSAGE);

            softly.assertThat(nameAfter)
                    .isEqualTo(nameBefore);
        }
    }
}
