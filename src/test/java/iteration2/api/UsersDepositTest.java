package iteration2.api;

import api.models.GenerateDepositRequest;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.specs.ResponseSpecs;

import java.math.BigDecimal;

import static api.TestConstants.*;

@Execution(ExecutionMode.SAME_THREAD)
public class UsersDepositTest extends BaseTest{

    @Test
    @Order(1)
    public void successGenerateDepositTest() {
        BigDecimal balanceBefore = accountSteps.getBalance(ID);

        GenerateDepositRequest body = GenerateDepositRequest.builder()
                .id(ID)
                .balance(VALID_DEPOSIT)
                .build();

        accountSteps.deposit(body);

        softly.assertThat(accountSteps.getBalance(ID))
                .as("Баланс должен увеличиться на " + VALID_DEPOSIT)
                .isCloseTo(balanceBefore.add(VALID_DEPOSIT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(strings = {
                 "0.0", // Нет суммы на депозите
                "5001.0", // Превышение максимальной суммы на депозите
                "-5000.0" // Отрицательная сумма не депозите
        })

        @Order(1)
        public  void failedGenerateDepositTest(BigDecimal invalidDeposit) {

            BigDecimal balanceBefore = accountSteps.getBalance(ID);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(invalidDeposit)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс не должен измениться")
                    .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }

    @Nested
    class DepositToNonAccountOrSomeOneTests {

        @Test
        @Order(2)
        public void depositToNonAccountTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(NON_EXISTENT_ID)
                    .balance(VALID_DEPOSIT)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.forbiddenResponse());
        }

        @Test
        @Order(3)
        public void depositToSomeOneElseAccountTest() {
            BigDecimal balanceBefore = accountSteps.getBalance(ALIEN_ID);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ALIEN_ID)
                    .balance(VALID_DEPOSIT)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(ALIEN_ID))
                    .as("Баланс не должен измениться")
                    .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }

    @Nested
    class LimitValuesDepositTests {

        @Test
        @Order(4)
        public  void moreThanPermissibleAmountTest() {
            BigDecimal balanceBefore = accountSteps.getBalance(ID);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(TOO_MUCH)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс не должен измениться")
                    .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }

        @Test
        @Order(5)
        public  void maxAmountValidTest() {
            BigDecimal balanceBefore = accountSteps.getBalance(ID);

            accountSteps.deposit(GenerateDepositRequest.builder().id(ID).balance(MAX_VALID_DEPOSIT_AMOUNT).build());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс увеличится на сумму " + MAX_VALID_DEPOSIT_AMOUNT)
                    .isCloseTo(balanceBefore.add(MAX_VALID_DEPOSIT_AMOUNT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }

        @Test
        @Order(6)
        public  void moreThanZeroTest() {
            BigDecimal balanceBefore = accountSteps.getBalance(ID);

            accountSteps.deposit(GenerateDepositRequest.builder().id(ID).balance(MIN_VALID_DEPOSIT_AMOUNT).build());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс увеличится на сумму " + MIN_VALID_DEPOSIT_AMOUNT)
                    .isCloseTo(balanceBefore.add(MIN_VALID_DEPOSIT_AMOUNT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }
}
