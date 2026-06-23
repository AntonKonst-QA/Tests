package api.generators;

import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.steps.AccountSteps;

public class CreateUserInAPIForUi {
    public static class UserData {
        public CreateUserResponse response;
        public String rawPassword;

        public UserData(CreateUserResponse response, String rawPassword) {
            this.response = response;
            this.rawPassword = rawPassword;
        }
    }

    public static UserData createDefaultUser() {
        var userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        String originalPassword = userRequest.getPassword();
        userRequest.setRole("USER");

        CreateUserResponse response = new ValidatedCrudRequester<CreateUserRequest, CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        return new UserData(response, originalPassword);
    }

    public static UserData createReadyToUseUser() {
        UserData userData = createDefaultUser();

        AccountSteps steps = new AccountSteps(userData.response.getUsername(), userData.rawPassword);
        steps.createAccount();

        return userData;
    }

    public static void deleteUser(Integer id) {
        if (id == null) return;

        io.restassured.RestAssured.given()
                .spec(RequestSpecs.adminSpec())
                .delete(Endpoint.ADMIN_USER.getUrl() + "/" + id)
                .then()
                .statusCode(200);
    }
}
