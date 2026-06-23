package iteration2.api;

import api.generators.RandomModelGenerator;
import api.models.GenerateChangeUserNameRequest;
import api.steps.AccountSteps;
import api.steps.UserSteps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.utils.ModelComparator;

import static api.TestConstants.*;

public class ChangeUserNameTest extends BaseTest{
    private UserSteps userSteps;
    private AccountSteps accountSteps;
    private String username;
    private String password;

    @BeforeEach
    public void prepare() {
        var userRequest = api.generators.RandomModelGenerator.generate(api.models.CreateUserRequest.class);
        this.username = "u_" + java.util.UUID.randomUUID().toString().substring(0, 6);
        this.password = userRequest.getPassword().replace("^", "").replace("$", "") + "1aA!";
        userRequest.setUsername(username);
        userRequest.setPassword(password);
        userRequest.setRole("USER");

        new api.requests.skeleton.requesters.ValidatedCrudRequester<api.models.CreateUserRequest, api.models.CreateUserResponse>(
                api.specs.RequestSpecs.adminSpec(),
                api.requests.skeleton.Endpoint.ADMIN_USER,
                api.specs.ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        this.userSteps = new UserSteps(username, password);
        this.accountSteps = new AccountSteps(username, password);
    }

    @Test
    public void successChangeUserName() {
        var requestBody = RandomModelGenerator.generate(GenerateChangeUserNameRequest.class);
        var response = userSteps.changeName(requestBody);

        ModelComparator.compare(requestBody, response);

        softly.assertThat(response.getMessage())
                .isEqualTo(UPDATED_PROFILE_MESSAGE);
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(strings = {"kate", "kateNew", "Kate1998", "Kate 1998", ""})
        public void failChangeName(String invalidName) {
            String nameBefore = userSteps.getProfile().getName();

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