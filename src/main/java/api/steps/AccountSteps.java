package api.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class AccountSteps {
    private final String username;
    private final String password;

    public BigDecimal getBalance(int accountId) {
        var requester = new ValidatedCrudRequester<BaseModel, CustomerAccountsResponse>(
                RequestSpecs.authUser(this.username, this.password),
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
                RequestSpecs.authUser(this.username, this.password),
                Endpoint.TRANSFER,
                ResponseSpecs.successResponse()
        ).post(body);
    }

    public String transferExpectingError(GenerateTransferRequest body) {
        return new ValidatedCrudRequester<GenerateTransferRequest, BaseModel>(RequestSpecs.authUser(this.username, this.password),
                Endpoint.TRANSFER,
                ResponseSpecs.badRequestResponse()
        ).getCrudRequester()
                .post(body)
                .extract()
                .asString();
    }

    public void deposit(GenerateDepositRequest body) {
        new ValidatedCrudRequester<GenerateDepositRequest, BaseModel>(
                RequestSpecs.authUser(this.username, this.password),
                Endpoint.DEPOSIT,
                ResponseSpecs.successResponse()
        ).post(body);
    }

    public String depositExpectingError(GenerateDepositRequest body, io.restassured.specification.ResponseSpecification expectedResponse) {
        return new ValidatedCrudRequester<GenerateDepositRequest, BaseModel>(
                RequestSpecs.authUser(this.username, this.password),
                Endpoint.DEPOSIT,
                expectedResponse
        ).getCrudRequester()
                .post(body)
                .extract()
                .asString();
    }
}
