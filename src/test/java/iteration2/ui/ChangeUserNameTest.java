package iteration2.ui;

import api.generators.CreateUserInAPIForUi;
import api.generators.RandomData;
import api.steps.AccountSteps;
import api.steps.UserSteps;
import common.annotations.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;

public class ChangeUserNameTest extends BaseUiTest {

    @BeforeEach
    public void prepareData() {
        super.setupTest();

        this.userData = CreateUserInAPIForUi.createReadyToUseUser();
        BaseUiTest.usersToDelete.add(userData.response.getId());

        this.accountSteps = new AccountSteps(userData.response.getUsername(), userData.rawPassword);
        this.userSteps = new UserSteps(userData.response.getUsername(), userData.rawPassword);

        login(userData.response.getUsername(), userData.rawPassword);
    }

    @Test
    @UserSession
    public void userCanChangeUserName() {
        String nameBefore = userSteps.getProfile().getName();
        String dynamicNewName = RandomData.generateUniqueName();

        new UserDashboard()
                .checkWelcomeText()
                .openProfilePage()
                .changeName(dynamicNewName)
                .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATE_SUCCESSFULLY);

        String nameAfter = userSteps.getProfile().getName();

        softly.assertThat(nameAfter)
                .isNotEqualTo(nameBefore)
                .isEqualTo(dynamicNewName);
    }

    @Test
    @UserSession
    public void userCanNotChangeUserName() {
        String nameBefore = userSteps.getProfile().getName();

        new UserDashboard()
                .checkWelcomeText()
                .openProfilePage()
                .changeName(EditProfilePage.INVALID_NEW_USERNAME)
                .checkAlertMessageAndAccept(BankAlerts.NAME_MUST_CONTAIN_TWO_WORDS);

        String nameAfter = userSteps.getProfile().getName();
        softly.assertThat(nameAfter)
                .isEqualTo(nameBefore)
                .isNotEqualTo(EditProfilePage.INVALID_NEW_USERNAME);
    }
}
