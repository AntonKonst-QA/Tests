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
    private final SelenideElement amountInput = $(".deposit-input");
    private final SelenideElement depositButton = $(".btn-primary");
    public static final BigDecimal VALID_DEPOSIT = new BigDecimal("500");
    public static final BigDecimal INVALID_DEPOSIT = new BigDecimal("0");

    @Override
    public String url() {
        return "/deposit";
    }

    public DepositPage makeDeposit(String account, BigDecimal amount) {
        accountSelector.shouldBe(Condition.visible).selectOptionContainingText(account);
        amountInput.shouldBe(Condition.visible).setValue(amount.toString());
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
