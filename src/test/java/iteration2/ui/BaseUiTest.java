package iteration2.ui;

import api.configs.Config;
import api.generators.CreateUserInAPIForUi;
import api.models.CreateUserRequest;
import api.steps.AccountSteps;
import api.steps.AdminSteps;
import api.steps.UserSteps;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import iteration2.api.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import ui.pages.LoginPage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.codeborne.selenide.Selenide.executeJavaScript;

public class BaseUiTest extends BaseTest {
    protected AccountSteps accountSteps;
    protected UserSteps userSteps;
    protected CreateUserInAPIForUi.UserData userData;

    protected static final Set<Integer> usersToDelete = ConcurrentHashMap.newKeySet();

    protected void deleteUser(int userId) {
        api.steps.AdminSteps.deleteUser(String.valueOf(userId));
    }

    @BeforeAll
    public static void setupSelenoid() {
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("uiBrowser");
        Configuration.browserSize = Config.getProperty("uiBrowserSize");
        Configuration.headless = true;

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
        );
    }

    @AfterEach
    public void tearDown() {
        if (userData != null) {
            AdminSteps.deleteUser(String.valueOf(userData.response.getId()));
        }
        Selenide.closeWebDriver();
    }

    public void authAsUser(String username, String password) {
        Selenide.open("/");
        String userAuthHeader = api.specs.RequestSpecs.getUserToken(username, password);
        executeJavaScript("localStorage.setItem('authToken', arguments[0]);", userAuthHeader);
    }

    public void authAsUser(CreateUserRequest createUserRequest) {
        authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword());
    }

    public void initSteps(String username, String password) {
        this.accountSteps = new AccountSteps(username, password);
        this.userSteps = new UserSteps(username, password);
    }

    public void login(String username, String password) {
        Selenide.open("/login");
        new LoginPage().login(username, password);
    }

    public void openDashboard() {
        Selenide.open("/dashboard");
    }
}