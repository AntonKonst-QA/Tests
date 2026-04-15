package iteration2;

import models.GenerateTransferRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.UserGenerateTransferRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferMoneyFromOneAccountToAnotherTest {
    // Перевод на свой аккаунт
    @Test
    public void successTransferMoneyBetweenMyAccountTest() {
        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(2)
                .amount(220.1)
                .build();

        UserGenerateTransferRequest transferAction = new UserGenerateTransferRequest(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        transferAction.post(body);

        // Проверяем результаты (GET)
        transferAction.get("/api/v1/accounts/{id}/transactions");
    }

    // Перевод на чужой аккаунт
    @Test
    public void successTransferMoneyToAnotherTest(){
        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(1)
                .receiverAccountId(3)
                .amount(0.65)
                .build();

        UserGenerateTransferRequest transferAction = new UserGenerateTransferRequest(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        transferAction.post(body);

        // Проверяем результаты (GET)
        transferAction.get("/api/v1/accounts/{id}/transactions");
    }

    // Перевод больше допустимого лимита
    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {
            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(1)
                    .receiverAccountId(2)
                    .amount(10000.1)
                    .build();

            UserGenerateTransferRequest transferAction = new UserGenerateTransferRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            transferAction.post(body);

            // Проверяем результаты (GET)
            transferAction.get("/api/v1/accounts/{id}/transactions");
        }

        @Test
        public void lessThanPermissibleAmountTest() {
            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(1)
                    .receiverAccountId(2)
                    .amount(9999.99)
                    .build();

            UserGenerateTransferRequest transferAction = new UserGenerateTransferRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            transferAction.post(body);

            // Проверяем результаты (GET)
            transferAction.get("/api/v1/accounts/{id}/transactions");
        }

        @Test
        public void moreThanZeroTest() {
            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(1)
                    .receiverAccountId(2)
                    .amount(0.01)
                    .build();

            UserGenerateTransferRequest transferAction = new UserGenerateTransferRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            transferAction.post(body);

            // Проверяем результаты (GET)
            transferAction.get("/api/v1/accounts/{id}/transactions");
        }
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(doubles = {
                0.0, // Нет суммы на депозите
                10000.1, // Превышение максимальную сумму перевода
                5001.0, // Превышение максимального баланс на депозите
                -0.1 // Отрицательная сумма не депозите
        })

        public  void failedTransferMoneyTest(double invalidDeposit) {
            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(1)
                    .receiverAccountId(2)
                    .amount(invalidDeposit)
                    .build();

            UserGenerateTransferRequest transferAction = new UserGenerateTransferRequest(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            transferAction.post(body);

            // Проверяем результаты (GET)
            transferAction.get("/api/v1/accounts/{id}/transactions");
        }
    }
}
