package api.steps;

import api.models.*;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class UserSteps {
    private ValidatedCrudRequester<GenerateChangeUserNameRequest, GenerateChangeUserNameResponse> validatedRequester() {
        return new ValidatedCrudRequester<>(
                RequestSpecs.authUser(),
                Endpoint.USER_NAME,
                ResponseSpecs.successResponse()
        );
    }

    public GenerateChangeUserNameResponse getProfile() {
        return validatedRequester().get();
    }

    public GenerateChangeUserNameResponse changeName(GenerateChangeUserNameRequest body) {
        return validatedRequester().put(body);
    }

    public String changeNameAndExpectError(GenerateChangeUserNameRequest body) {
        return new ValidatedCrudRequester<GenerateChangeUserNameRequest, BaseModel>(
                RequestSpecs.authUser(),
                Endpoint.USER_NAME,
                ResponseSpecs.badRequestResponse()
        ).getCrudRequester().put(body).extract().asString();
    }

    public List<TransactionModel> getTransactionsForAccount(int accountId) {
        return new ValidatedCrudRequester<BaseModel, TransactionResponse>(
                RequestSpecs.authUser(),
                Endpoint.TRANSACTIONS,
                ResponseSpecs.successResponse()
        ).getWithParam("accountId", accountId).getTransactions();
    }


}
