package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    USER_NAME(
            "/customer/profile",
            GenerateChangeUserNameRequest.class,
            GenerateChangeUserNameResponse.class
    ),

    ACCOUNTS(
            "/customer/accounts",
            null,
            CustomerAccountsResponse.class
    ),

    TRANSACTIONS(
            "/accounts/{accountId}/transactions",
            null,
            TransactionResponse.class
    ),

    DEPOSIT(
            "/accounts/deposit",
            "/customer/accounts",
            GenerateDepositRequest.class,
            GenerateDepositResponse.class
    ),

    TRANSFER(
            "/accounts/transfer",
            "/customer/accounts",
            GenerateTransferRequest.class,
            GenerateTransferResponse.class
    );


    private final String url;
    private final String getUrl;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

    Endpoint(String url, Class<? extends BaseModel> rq, Class<? extends BaseModel> rs) {
        this(url, url, rq, rs);
    }
}
