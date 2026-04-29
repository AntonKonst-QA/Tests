package steps;

import models.*;
import requests.skeleton.Endpoint;
import requests.skeleton.requesters.ValidatedCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class AccountSteps {
    public static double getBalance(int accountId) {
        var requester = new ValidatedCrudRequester<BaseModel, CustomerAccountsResponse>(
                RequestSpecs.authUser(),
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
        return Math.round(account.getBalance() * 100.0) / 100.0;
    }

    public GenerateTransferResponse transfer(GenerateTransferRequest body) {
        return new ValidatedCrudRequester<GenerateTransferRequest, GenerateTransferResponse>(
                RequestSpecs.authUser(),
                Endpoint.TRANSFER,
                ResponseSpecs.successResponse()
        ).post(body);
    }

    public String transferExpectingError(GenerateTransferRequest body) {
        return new ValidatedCrudRequester<GenerateTransferRequest, BaseModel>(
                RequestSpecs.authUser(),
                Endpoint.TRANSFER,
                ResponseSpecs.badRequestResponse()
        ).getCrudRequester().post(body).extract().asString();
    }

    public void deposit(GenerateDepositRequest body) {
        new ValidatedCrudRequester<GenerateDepositRequest, BaseModel>(
                RequestSpecs.authUser(),
                Endpoint.DEPOSIT,
                ResponseSpecs.successResponse()
        ).post(body);
    }

    public String depositExpectingError(GenerateDepositRequest body, io.restassured.specification.ResponseSpecification expectedResponse) {
        return new ValidatedCrudRequester<GenerateDepositRequest, BaseModel>(
                RequestSpecs.authUser(),
                Endpoint.DEPOSIT,
                expectedResponse
        ).getCrudRequester()
                .post(body)
                .extract()
                .asString();
    }
}
