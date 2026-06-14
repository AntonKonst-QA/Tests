package iteration2.api;

import api.configs.Config;
import api.models.CustomerModel;
import api.steps.AccountSteps;
import api.steps.UserSteps;
import common.extensions.TimingExtension;
import common.storage.SessionStorage;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(TimingExtension.class)
public class BaseTest {

    protected AccountSteps accountSteps;
    protected UserSteps userSteps;
    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();

        String defaultLogin = Config.getProperty("user.username");
        String defaultPassword = Config.getProperty("user.password");

        if (defaultLogin == null || defaultPassword == null) {
            throw new IllegalStateException("Пользоватлеь не указан");
        }

        CustomerModel defaultUser = CustomerModel.builder()
                .username(defaultLogin)
                .password(defaultPassword)
                .build();

        SessionStorage.addUsers(List.of(defaultUser));

        this.accountSteps = SessionStorage.getAccountSteps();
        this.userSteps = SessionStorage.getUserSteps();
    }

    @AfterEach
    public void afterTest() {

        try {
            if (softly != null) {
                softly.assertAll();
            }
        } finally {
            SessionStorage.clear();
        }
    }
}
