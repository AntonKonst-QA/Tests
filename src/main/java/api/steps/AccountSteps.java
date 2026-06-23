package api.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
public class AccountSteps {
    private final String username;
    private final String password;

    public BigDecimal getBalance(int accountId) {
        var requester = new ValidatedCrudRequester<BaseModel, CustomerAccountsResponse>(
                RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.ACCOUNTS,
                ResponseSpecs.successResponse()
        );

        AccountModel[] accountsArray = requester.getCrudRequester()
                .get()
                .extract()
                .as(AccountModel[].class);

        AccountModel account = java.util.Arrays.stream(accountsArray)
                .filter(a -> a.getId() == accountId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Аккаунт с ID " + accountId + " не найден!"));
        return account.getBalance();
    }

    public GenerateTransferResponse transfer(GenerateTransferRequest body) {
        return new ValidatedCrudRequester<GenerateTransferRequest, GenerateTransferResponse>(
                RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.TRANSFER,
                ResponseSpecs.successResponse()
        ).post(body);
    }

    public String transferExpectingError(GenerateTransferRequest body) {
        return new ValidatedCrudRequester<GenerateTransferRequest, BaseModel>(RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.TRANSFER,
                ResponseSpecs.badRequestResponse()
        ).getCrudRequester()
                .post(body)
                .extract()
                .asString();
    }

    public void deposit(GenerateDepositRequest body) {
        new ValidatedCrudRequester<GenerateDepositRequest, BaseModel>(
                RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.DEPOSIT,
                ResponseSpecs.successResponse()
        ).post(body);
    }

    public String depositExpectingError(GenerateDepositRequest body, io.restassured.specification.ResponseSpecification expectedResponse) {
        return new ValidatedCrudRequester<GenerateDepositRequest, BaseModel>(
                RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.DEPOSIT,
                expectedResponse
        ).getCrudRequester()
                .post(body)
                .extract()
                .asString();
    }

    public AccountModel createAccount() {
        return io.restassured.RestAssured.given()
                .spec(api.specs.RequestSpecs.authAsUser(this.username, this.password))
                .body(new api.models.BaseModel(){})
                .post("/accounts")
                .then()
                .statusCode(org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.greaterThanOrEqualTo(200),
                        org.hamcrest.Matchers.lessThan(300)
                ))
                .extract()
                .as(AccountModel.class);
    }

    public List<AccountModel> getAccounts() {
        return new ValidatedCrudRequester<BaseModel, BaseModel>(
                RequestSpecs.authAsUser(this.username, this.password),
                Endpoint.ACCOUNTS,
                ResponseSpecs.successResponse()
        ).getCrudRequester()
                .get()
                .extract()
                .jsonPath()
                .getList(".", AccountModel.class);
    }
}
