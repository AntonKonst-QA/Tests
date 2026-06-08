package iteration2.ui;

import common.annotations.UserSession;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

public class UserDepositTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateDepositTest() {

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        BigDecimal balanceBefore = accountSteps.getBalance(DepositPage.ID);

        // Шаг 2: Проверка UI
       new UserDashboard()
               .checkWelcomeText()
               .openDepositModal()
               .makeDeposit(DepositPage.accountNumber, DepositPage.VALID_DEPOSIT)
               .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_DEPOSITED, DepositPage.VALID_DEPOSIT, DepositPage.accountNumber);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(DepositPage.ID))
                .as("После UI-пополнения баланс на бэкенде должен увеличиться на " + DepositPage.VALID_DEPOSIT)
                .isCloseTo(balanceBefore.add(DepositPage.VALID_DEPOSIT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

    // Негативный тест
    @Test
    @UserSession
    public void userCanNotCreateDepositTest() {

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        BigDecimal balanceBefore = accountSteps.getBalance(DepositPage.ID);

        // Шаг 2: Проверка UI
        new UserDashboard()
                .checkWelcomeText()
                .openDepositModal()
                .makeDeposit(DepositPage.accountNumber, DepositPage.INVALID_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.PLEASE_ENTER_A_VALID_AMOUNT);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(DepositPage.ID))
                .as("Баланс не должен измениться")
                .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }
}