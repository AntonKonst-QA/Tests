package iteration2;

import models.GenerateDepositRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.UserGenerateDepositRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UsersDepositTest {
    public static final int VALID_DEPOSIT = 5000;
    public static final int ID = 1;
    @Test
    public void successGenerateDepositTest() {
        GenerateDepositRequest body = GenerateDepositRequest.builder()
                .id(1)
                .balance(VALID_DEPOSIT)
                .build();

        UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );
        depositAction.post(body);

        // Проверяем результаты (GET)
        depositAction.get("/api/v1/accounts/{id}/transactions");
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(ints = {
                 0, // Нет суммы на депозите
                5001, // Превышение максимальной суммы на депозите
                -5000 // Отрицательная сумма не депозите
        })

        public  void failedGenerateDepositTest(int invalidDeposit) {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(1)
                    .balance(invalidDeposit)
                    .build();

            UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );
            depositAction.post(body);
        }
    }

    @Nested
    class DepositToNonAccountOrSomeOneTests {

        @Test
        public void depositToNonAccountTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(0)
                    .balance(VALID_DEPOSIT)
                    .build();
            UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.forbiddenResponse()
            );
            depositAction.post(body);
        }

        @Test
        public void depositToSomeOneElseAccountTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(2) // ID2 принадлежит другому пользователю
                    .balance(VALID_DEPOSIT)
                    .build();
            UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );
            depositAction.post(body);
        }
    }

    @Nested
    class LimitValuesDepositTests {

        @Test
        public  void moreThanPermissibleAmountTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(5000.01)
                    .build();
            UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );
            depositAction.post(body);
        }

        @Test
        public  void lessThanPermissibleAmountTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(4999.99)
                    .build();
            UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );
            depositAction.post(body);
        }

        @Test
        public  void moreThanZeroTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(0.01)
                    .build();
            UserGenerateDepositRequest depositAction = new UserGenerateDepositRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );
            depositAction.post(body);
        }
    }
}
