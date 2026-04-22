package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.GenerateDepositRequest;

import static io.restassured.RestAssured.given;

public class UserGenerateDepositRequester extends Request <GenerateDepositRequest> {
    public UserGenerateDepositRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(GenerateDepositRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/accounts/deposit")
                .then();
    }

    @Override
    public ValidatableResponse put(GenerateDepositRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse get(GenerateDepositRequest model) {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/api/v1/customer/accounts")
                .then();
    }
}
