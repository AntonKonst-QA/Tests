package iteration2.ui;

import common.annotations.UserSession;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ui.pages.BankAlerts;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

@Execution(ExecutionMode.SAME_THREAD)
public class TransferMoneyFromOneAccountToAnotherTest extends BaseUiTest {

    @Test
    @UserSession
    @Order(1)
    public void userCanTransferMoneyTest() {

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        BigDecimal senderBefore = accountSteps.getBalance(TransferPage.SENDER_ID);

        // Шаг 2: Проверка UI
        new UserDashboard()
                .checkWelcomeText()
                .openTransferPage()
                .makeTransfer(TransferPage.recipientAccount, TransferPage.recipientName, TransferPage.RECEIVER_ACC_NAME, TransferPage.VALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_TRANSFERRED_FROM_ONE_ACCOUNT_TO_ANOTHER, TransferPage.VALID_TRANSFER,TransferPage.RECEIVER_ACC_NAME);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(TransferPage.SENDER_ID))
                .isCloseTo(senderBefore.subtract(TransferPage.VALID_TRANSFER), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

//     Негативный тест
    @Test
    @UserSession
    @Order(2)
    public void userCanNotTransferMoneyTest() {

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        BigDecimal senderBefore = accountSteps.getBalance(TransferPage.SENDER_ID);

        // Шаг 2: Проверка UI
        new UserDashboard()
                .checkWelcomeText()
                .openTransferPage()
                .makeTransfer(TransferPage.recipientAccount, TransferPage.recipientName, TransferPage.RECEIVER_ACC_NAME, TransferPage.INVALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.ERROR_TRANSFER_AMOUNT);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(TransferPage.SENDER_ID))
                .isCloseTo(senderBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }
}
