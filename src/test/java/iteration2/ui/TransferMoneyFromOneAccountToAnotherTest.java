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

public class TransferMoneyFromOneAccountToAnotherTest {
    private final AccountSteps accountSteps = new AccountSteps();

    public static final int SENDER_ID = 1;
    public static final String RECEIVER_ACC_NAME = "ACC4";
    public static final int VALID_TRANSFER = 4;
    public static final int INVALID_TRANSFER = 0;

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
    public void userCanTransferMoneyTest() {
        SoftAssertions softly = new SoftAssertions();
        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double senderBefore = accountSteps.getBalance(SENDER_ID);

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
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.withText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);
        $(Selectors.byText("\uD83C\uDD95 New Transfer")).click();

        $(".account-selector")
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .selectOptionContainingText("ACC1");

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys("kate19981");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(String.valueOf(RECEIVER_ACC_NAME));
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(String.valueOf(VALID_TRANSFER));
        $("input[type='checkbox']").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String expectedAlertText = String.format("✅ Successfully transferred $%d to account %s!", VALID_TRANSFER, RECEIVER_ACC_NAME);
        assertEquals(expectedAlertText, alert.getText());
        alert.accept();

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(SENDER_ID))
                .as("Списание средств у отправителя")
                .isCloseTo(senderBefore - VALID_TRANSFER, within(0.001));

        softly.assertAll();
    }

//     Негативный тест
    @Test
    public void userCanNotTransferMoneyTest() {
        SoftAssertions softly = new SoftAssertions();
        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double senderBefore = accountSteps.getBalance(SENDER_ID);

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
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.withText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);
        $(Selectors.byText("\uD83C\uDD95 New Transfer")).click();

        $(".account-selector")
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .selectOptionContainingText("ACC1");

        $(Selectors.byAttribute("placeholder", "Enter recipient name")).sendKeys("kate19981");
        $(Selectors.byAttribute("placeholder", "Enter recipient account number")).setValue(String.valueOf(RECEIVER_ACC_NAME));
        $(Selectors.byAttribute("placeholder", "Enter amount")).setValue(String.valueOf(INVALID_TRANSFER));
        $("input[type='checkbox']").click();
        $(Selectors.byText("\uD83D\uDE80 Send Transfer")).click();

        Alert alert = switchTo().alert();
        String expectedAlertText = "❌ Error: Transfer amount must be at least 0.01";
        assertEquals(expectedAlertText, alert.getText());
        alert.accept();

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(SENDER_ID))
                .as("Баланс отправителя не должен измениться")
                .isEqualTo(senderBefore);

        softly.assertAll();
    }
}
