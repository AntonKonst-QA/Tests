package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import models.CustomerModel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import steps.AccountSteps;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDepositTest {

    private final AccountSteps accountSteps = new AccountSteps();

    public static final int VALID_DEPOSIT = 500;
    public static final int INVALID_DEPOSIT = 0;
    public static final int ID = 1;

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.50.95:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    @Test
    public void userCanCreateDepositTest() {
        SoftAssertions softly = new SoftAssertions();
        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double balanceBefore = accountSteps.getBalance(ID);

        CustomerModel user = CustomerModel.builder()
                .username("kate1998")
                .password("verysTRongPassword33$")
                .build();

        // Шаг 2: Проверка UI
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();

        $(".welcome-text")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .should(Condition.matchText("^Welcome, .+\\!$"));
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();

        $(".account-selector")
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .selectOptionContainingText("ACC1");

        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(String.valueOf(VALID_DEPOSIT));
        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();

        Alert alert = switchTo().alert();
        String expectedAlertText = String.format("✅ Successfully deposited $%d to account ACC1!", VALID_DEPOSIT);
        assertEquals(expectedAlertText, alert.getText());
        alert.accept();

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(ID))
                .as("После UI-пополнения баланс на бэкенде должен увеличиться на " + VALID_DEPOSIT)
                .isEqualTo(balanceBefore + VALID_DEPOSIT, within(0.001));

        softly.assertAll();
    }

    // Негативный тест
    @Test
    public void userCanNotCreateDepositTest() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double balanceBefore = accountSteps.getBalance(ID);

        CustomerModel user = CustomerModel.builder()
                .username("kate1998")
                .password("verysTRongPassword33$")
                .build();

        // Шаг 2: Проверка UI
        Selenide.open("/login");
        $(Selectors.byAttribute("placeholder", "Username")).sendKeys(user.getUsername());
        $(Selectors.byAttribute("placeholder", "Password")).sendKeys(user.getPassword());
        $("button").click();

        $(".welcome-text")
                .shouldBe(Condition.visible, Duration.ofSeconds(10))
                .should(Condition.matchText("^Welcome, .+\\!$"));
        $(Selectors.byText("\uD83D\uDCB0 Deposit Money")).click();

        $(".account-selector")
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .selectOptionContainingText("ACC1");

        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(String.valueOf(INVALID_DEPOSIT));
        $(Selectors.byText("\uD83D\uDCB5 Deposit")).click();

        Alert alert = switchTo().alert();
        String expectedAlertText = "❌ Please enter a valid amount.";
        assertEquals(expectedAlertText, alert.getText());
        alert.accept();

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(ID))
                .as("Баланс не должен измениться")
                .isEqualTo(balanceBefore);

        softly.assertAll();
    }
}