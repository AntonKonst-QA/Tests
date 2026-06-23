package iteration2.api;

import api.steps.AdminSteps;
import common.extensions.TimingExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ExtendWith(TimingExtension.class)
public class BaseTest {
    protected SoftAssertions softly;
    protected static final Set<String> usersToDelete = ConcurrentHashMap.newKeySet();

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
    }

    @AfterEach
    public void cleanup() {
    }

    @AfterAll
    public static void afterAllTests() {
        for (String userId : usersToDelete) {
            AdminSteps.deleteUser(userId);
        }
        usersToDelete.clear();
    }
}
