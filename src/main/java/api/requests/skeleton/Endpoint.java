package api.requests.skeleton;

import api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Endpoint {
    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),

    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    ),

    USER_NAME(
            "/customer/profile",
            GenerateChangeUserNameRequest.class,
            GenerateChangeUserNameResponse.class
    ),

    ACCOUNTS(
            "/accounts",
            "/customer/accounts",
            BaseModel.class,
            AccountModel.class
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
    ),

    DELETE_USER(
            "/admin/users/{id}",
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
