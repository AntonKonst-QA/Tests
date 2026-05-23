package iteration2.ui;

import api.configs.Config;
import api.models.BaseModel;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;
import java.util.Map;

public class BaseUiTest extends BaseModel {

    @BeforeAll
    public static void setupSelenoid(){
        Configuration.remote = Config.getProperty("uiRemote");
        Configuration.baseUrl = Config.getProperty("uiBaseUrl");
        Configuration.browser = Config.getProperty("uiBrowser");
        Configuration.browserSize = Config.getProperty("uiBrowserSize");

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    public UserDashboard authAsUser(String username, String password) {
        return new LoginPage()
                .open()
                .login(username, password);
    }
}
