package iteration2;

import models.GenerateDepositRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.UserGenerateDepositRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;
import static org.assertj.core.api.Assertions.within;

public class UsersDepositTest extends BaseTest{
    public static final int VALID_DEPOSIT = 5000;
    public static final int ID = 1;
    @Test
    public void successGenerateDepositTest() {
        UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateDepositRequest body = GenerateDepositRequest.builder()
                .id(ID)
                .balance(VALID_DEPOSIT)
                .build();

        double balanceBefore = depositAction.get(body)
                .extract()
                .jsonPath()
                .getDouble("[0].balance");

        depositAction.post(body);

        // Проверяем результаты (GET)
        double balanceAfter = depositAction.get(body)
                .extract()
                .jsonPath()
                .getDouble("[0].balance");

        softly.assertThat(balanceAfter)
                .as("Баланс должен увеличиться на " + VALID_DEPOSIT)
                .isEqualTo(balanceBefore + VALID_DEPOSIT, within(0.001));
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
            UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(1)
                    .balance(invalidDeposit)
                    .build();

            double balanceBefore = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            depositAction.post(body)
                    .spec(ResponseSpecs.badRequestResponse());

            // Проверяем результаты (GET)
            double balanceAfter = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            softly.assertThat(balanceAfter)
                    .isEqualTo(balanceBefore);
        }
    }

    @Nested
    class DepositToNonAccountOrSomeOneTests {

        @Test
        public void depositToNonAccountTest() {
            UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.forbiddenResponse()
            );

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(0)
                    .balance(VALID_DEPOSIT)
                    .build();

            double balanceBefore = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            depositAction.post(body);

            double balanceAfter = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            softly.assertThat(balanceAfter)
                    .isEqualTo(balanceBefore);
        }

        @Test
        public void depositToSomeOneElseAccountTest() {
            UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(2) // ID2 принадлежит другому пользователю
                    .balance(VALID_DEPOSIT)
                    .build();

            double balanceBefore = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");


            depositAction.post(body);

            // Проверяем результаты (GET)
            double balanceAfter = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            softly.assertThat(balanceAfter)
                    .as("Баланс не должен измениться")
                    .isEqualTo(balanceBefore);
        }
    }

    @Nested
    class LimitValuesDepositTests {

        @Test
        public  void moreThanPermissibleAmountTest() {
            UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(5000.01)
                    .build();

            double balanceBefore = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            depositAction.post(body);

            // Проверяем результаты (GET)
            double balanceAfter = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            softly.assertThat(balanceAfter)
                    .as("Баланс не должен измениться")
                    .isEqualTo(balanceBefore);
        }

        @Test
        public  void maxAmountValidTest() {
            double depositAmount = 4999.99;

            UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(depositAmount)
                    .build();


            double balanceBefore = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            depositAction.post(body);

            // Проверяем результаты (GET)
            double balanceAfter = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            softly.assertThat(balanceAfter)
                    .as("Баланс увеличится на сумму " + depositAmount)
                    .isEqualTo(balanceBefore + depositAmount, within(0.001));
        }

        @Test
        public  void moreThanZeroTest() {
            double depositAmount = 0.01;

            UserGenerateDepositRequester depositAction = new UserGenerateDepositRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(depositAmount)
                    .build();

            double balanceBefore = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            depositAction.post(body);

            // Проверяем результаты (GET)
            double balanceAfter = depositAction.get(body)
                    .extract()
                    .jsonPath()
                    .getDouble("[0].balance");

            softly.assertThat(balanceAfter)
                    .as("Баланс изменится на сумму " + depositAmount)
                    .isEqualTo(balanceBefore + depositAmount, within(0.001));
        }
    }
}
