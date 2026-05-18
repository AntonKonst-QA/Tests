package iteration2.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import models.CustomerModel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import steps.UserSteps;

import java.time.Duration;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.AssertionsForClassTypes.setAllowComparingPrivateFields;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChangeUserNameTest {
    public static final String INVALID_NEW_USERNAME = "nothing";

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://192.168.50.95:3000";
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }

    @Test
    public void userCanChangeUserName() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили имя пользователя на бэке ДО изменения имени пользователя
        UserSteps userSteps = new UserSteps();
        String nameBefore = userSteps.getProfile().getName();

        String dynamicNewName = nameBefore.equals ("Kate First") ? "Kate Second" : "Kate First";

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

        $(".user-username").click();
        $(Selectors.byText("✏\uFE0F Edit Profile")).click();
        $(Selectors.byAttribute("placeholder", "Enter new name")).setValue(dynamicNewName);
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();

        Alert alert = switchTo().alert();
        String expectedAlertText = "✅ Name updated successfully!";
        assertEquals(expectedAlertText, alert.getText());
        alert.accept();

        // Шаг 3: Проверка изменения на бэке
        String nameAfter = userSteps.getProfile().getName();
        softly.assertThat(nameAfter)
                .isEqualTo(dynamicNewName);

        softly.assertAll();
    }

    @Test
    public void userCanNotChangeUserName() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили имя пользователя на бэке ДО изменения имени пользователя
        UserSteps userSteps = new UserSteps();
        String nameBefore = userSteps.getProfile().getName();


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
        $(".user-username").click();

        $(Selectors.byText("✏\uFE0F Edit Profile")).click();
        $(Selectors.byAttribute("placeholder", "Enter new name")).setValue(INVALID_NEW_USERNAME);
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();

        Alert alert = switchTo().alert();
        String expectedAlertText = "Name must contain two words with letters only";
        assertEquals(expectedAlertText, alert.getText());
        alert.accept();

        // Шаг 3: Проверка отсутствия изменения на бэке
        String nameAfter = userSteps.getProfile().getName();
        softly.assertThat(nameAfter)
                .isEqualTo(nameBefore);

        softly.assertAll();
    }
}
