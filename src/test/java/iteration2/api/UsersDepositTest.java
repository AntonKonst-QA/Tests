package iteration2.api;

import api.models.GenerateDepositRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.specs.ResponseSpecs;
import api.steps.AccountSteps;
import static org.assertj.core.api.Assertions.within;

public class UsersDepositTest extends BaseTest{
    private final AccountSteps accountSteps = new AccountSteps();
    public static final int VALID_DEPOSIT = 5000;
    public static final int ID = 1;
    @Test
    public void successGenerateDepositTest() {
        double balanceBefore = accountSteps.getBalance(ID);

        GenerateDepositRequest body = GenerateDepositRequest.builder()
                .id(ID)
                .balance(VALID_DEPOSIT)
                .build();

        accountSteps.deposit(body);

        softly.assertThat(accountSteps.getBalance(ID))
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

        public  void failedGenerateDepositTest(double invalidDeposit) {

            double balanceBefore = accountSteps.getBalance(ID);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(invalidDeposit)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс не должен измениться")
                    .isEqualTo(balanceBefore);
        }
    }

    @Nested
    class DepositToNonAccountOrSomeOneTests {

        @Test
        public void depositToNonAccountTest() {
            int nonExistentID = 0;
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(nonExistentID)
                    .balance(VALID_DEPOSIT)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.forbiddenResponse());
        }

        @Test
        public void depositToSomeOneElseAccountTest() {
            int alienId = 2;
            double balanceBefore = accountSteps.getBalance(alienId);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(alienId)
                    .balance(VALID_DEPOSIT)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(alienId))
                    .as("Баланс не должен измениться")
                    .isEqualTo(balanceBefore);
        }
    }

    @Nested
    class LimitValuesDepositTests {

        @Test
        public  void moreThanPermissibleAmountTest() {
            double tooMuch = 5000.01;
            double balanceBefore = accountSteps.getBalance(ID);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(ID)
                    .balance(tooMuch)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс не должен измениться")
                    .isEqualTo(balanceBefore);
        }

        @Test
        public  void maxAmountValidTest() {
            double depositAmount = 4999.99;
            double balanceBefore = accountSteps.getBalance(ID);

            accountSteps.deposit(GenerateDepositRequest.builder().id(ID).balance(depositAmount).build());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс увеличится на сумму " + depositAmount)
                    .isEqualTo(balanceBefore + depositAmount, within(0.001));
        }

        @Test
        public  void moreThanZeroTest() {
            double depositAmount = 0.01;
            double balanceBefore = accountSteps.getBalance(ID);

            accountSteps.deposit(GenerateDepositRequest.builder().id(ID).balance(depositAmount).build());

            softly.assertThat(accountSteps.getBalance(ID))
                    .as("Баланс увеличится на сумму " + depositAmount)
                    .isEqualTo(balanceBefore + depositAmount, within(0.001));
        }
    }
}
