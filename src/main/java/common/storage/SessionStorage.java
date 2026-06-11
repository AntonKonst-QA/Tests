package common.storage;

import api.models.CustomerModel;
import api.steps.AccountSteps;
import api.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SessionStorage {
    private static final SessionStorage INSTANCE = new SessionStorage();

    private final Map<CustomerModel, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final Map<CustomerModel, AccountSteps> accountStepsMap = new LinkedHashMap<>();

    private static Object currentUser;

    private SessionStorage() {}

    public static void addUsers(List<CustomerModel> users) {
        for (CustomerModel user: users) {
            INSTANCE.userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
            INSTANCE.accountStepsMap.put(user, new AccountSteps(user.getUsername(), user.getPassword()));
        }
        if (!users.isEmpty()) {
            currentUser = users.get(0);
        }
    }

    public static void setCurrentUser(CustomerModel user) {
        currentUser = user;
    }

    public static UserSteps getUserSteps() {
        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не зарегистрирован");
        }
        return INSTANCE.userStepsMap.get(currentUser);
    }

    public static AccountSteps getAccountSteps() {
        if (currentUser == null) {
            throw new IllegalStateException("Пользователь не зарегистрирован");
        }
        return INSTANCE.accountStepsMap.get(currentUser);
    }

    public static CustomerModel getUser(int index) {
        return new ArrayList<>(INSTANCE.userStepsMap.keySet()).get(index);
    }

    public static CustomerModel getUser() {
        return getUser(0);
    }

    public static UserSteps getSteps(int index) {
        return new ArrayList<>(INSTANCE.userStepsMap.values()).get(index);
    }

    public static UserSteps getSteps() {
        return getSteps(0);
    }

    public static void clear() {
        INSTANCE.userStepsMap.clear();
        INSTANCE.accountStepsMap.clear();
        currentUser = null;
    }
}
