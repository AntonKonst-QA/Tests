package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.GenerateTransferRequest;
import models.TransactionModel;

import static io.restassured.RestAssured.given;

public class UserGenerateTransferRequester extends Request <GenerateTransferRequest> {

    public UserGenerateTransferRequester(RequestSpecification requestSpecification, ResponseSpecification responseSpecification) {
        super(requestSpecification, responseSpecification);
    }

    @Override
    public ValidatableResponse post(GenerateTransferRequest model) {
        return given()
                .spec(requestSpecification)
                .body(model)
                .post("/api/v1/accounts/transfer")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse put(GenerateTransferRequest model) {
        return null;
    }

    @Override
    public ValidatableResponse get(GenerateTransferRequest model) {
        return given()
                .spec(requestSpecification)
                .pathParam("id", model.getSenderAccountId())
                .when()
                .get("/api/v1/accounts/{id}/transactions")
                .then()
                .assertThat()
                .spec(responseSpecification);
    }

    public java.util.List<TransactionModel> getTransactionList(int accountId) {
        return given()
                .spec(requestSpecification)
                .pathParam("id", accountId)
                .when()
                .get("/api/v1/accounts/{id}/transactions")
                .then()
                .spec(specs.ResponseSpecs.successResponse())
                .extract()
                .jsonPath()
                .getList(".", TransactionModel.class);
    }
}
