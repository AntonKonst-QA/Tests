package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.GenerateChangeUserNameRequest;

import static io.restassured.RestAssured.given;

public class ChangeUserNameRequester extends Request <GenerateChangeUserNameRequest> {
    public ChangeUserNameRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(GenerateChangeUserNameRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse put(GenerateChangeUserNameRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .put("/api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse get(GenerateChangeUserNameRequest model) {
        return get();
    }

    // Создал метод без параметров, чтобы гет запрос был более гибким
    public ValidatableResponse get() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/v1/customer/profile")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }
}
