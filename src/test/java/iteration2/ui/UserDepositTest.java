package iteration2.ui;

import api.models.CustomerModel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import api.steps.AccountSteps;
import ui.pages.BankAlerts;
import ui.pages.DepositPage;

import static org.assertj.core.api.AssertionsForClassTypes.within;

public class UserDepositTest extends BaseUiTest {
    private final AccountSteps accountSteps = new AccountSteps();

    @Test
    public void userCanCreateDepositTest() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double balanceBefore = accountSteps.getBalance(DepositPage.ID);
        CustomerModel user = CustomerModel.getUser();

        // Шаг 2: Проверка UI
       authAsUser(user.getUsername(), user.getPassword())
               .checkWelcomeText()
               .openDepositModal()
               .makeDeposit(DepositPage.accountNumber, DepositPage.VALID_DEPOSIT)
               .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_DEPOSITED, DepositPage.VALID_DEPOSIT, DepositPage.accountNumber);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(DepositPage.ID))
                .as("После UI-пополнения баланс на бэкенде должен увеличиться на " + DepositPage.VALID_DEPOSIT)
                .isEqualTo(balanceBefore + DepositPage.VALID_DEPOSIT, within(0.001));

        softly.assertAll();
    }

    // Негативный тест
    @Test
    public void userCanNotCreateDepositTest() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double balanceBefore = accountSteps.getBalance(DepositPage.ID);
        CustomerModel user = CustomerModel.getUser();

        // Шаг 2: Проверка UI
        authAsUser(user.getUsername(), user.getPassword())
                .checkWelcomeText()
                .openDepositModal()
                .makeDeposit(DepositPage.accountNumber, DepositPage.INVALID_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.PLEASE_ENTER_A_VALID_AMOUNT);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(DepositPage.ID))
                .as("Баланс не должен измениться")
                .isEqualTo(balanceBefore);

        softly.assertAll();
    }
}