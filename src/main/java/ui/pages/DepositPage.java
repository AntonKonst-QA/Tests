package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import java.math.BigDecimal;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

@Getter
public class DepositPage extends BasePage<DepositPage> {
    private final SelenideElement accountSelector = $(".account-selector");
    private final SelenideElement amountInput = $(By.xpath("//input[@placeholder='Enter amount']"));
    private final SelenideElement depositButton = $(By.xpath("//button[contains(text(), 'Deposit')]"));
    public static String accountNumber = "ACC1";
    public static final BigDecimal VALID_DEPOSIT = new BigDecimal("500");
    public static final BigDecimal INVALID_DEPOSIT = new BigDecimal("0");
    public static final int ID = 1;

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage makeDeposit(String account, BigDecimal amount) {
        accountSelector.shouldBe(Condition.visible).selectOptionContainingText(account);
        amountInput.setValue(String.valueOf(amount));
        depositButton.click();
        return this;
    }

    public DepositPage checkAlertMessageAndAccept(BankAlerts expectedAlertText, Object... args) {
        Alert alert = switchTo().alert();
        String expectedText = expectedAlertText.getFormattedMessage(args);
        Assertions.assertThat(alert.getText())
                .isEqualTo(expectedText);
        alert.accept();

        return this;
    }
}
