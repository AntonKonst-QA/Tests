package common.storage;

import api.models.CustomerModel;
import api.steps.AccountSteps;
import api.steps.UserSteps;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SessionStorage {
    private static final ThreadLocal<SessionStorage> INSTANCE = ThreadLocal.withInitial(SessionStorage::new);

    private final Map<CustomerModel, UserSteps> userStepsMap = new LinkedHashMap<>();
    private final Map<CustomerModel, AccountSteps> accountStepsMap = new LinkedHashMap<>();

    private Object currentUser;

    private SessionStorage() {}

    public static void addUsers(List<CustomerModel> users) {
        for (CustomerModel user: users) {
            INSTANCE.get().userStepsMap.put(user, new UserSteps(user.getUsername(), user.getPassword()));
            INSTANCE.get().accountStepsMap.put(user, new AccountSteps(user.getUsername(), user.getPassword()));
        }
        if (!users.isEmpty()) {
            INSTANCE.get().currentUser = users.get(0);
        }
    }

    public static void setCurrentUser(CustomerModel user) {
        INSTANCE.get().currentUser = user;
    }

    public static UserSteps getUserSteps() {
        if (INSTANCE.get().currentUser == null) {
            throw new IllegalStateException("Пользователь не зарегистрирован");
        }
        return INSTANCE.get().userStepsMap.get(INSTANCE.get().currentUser);
    }

    public static AccountSteps getAccountSteps() {
        if (INSTANCE.get().currentUser == null) {
            throw new IllegalStateException("Пользователь не зарегистрирован");
        }
        return INSTANCE.get().accountStepsMap.get(INSTANCE.get().currentUser);
    }

    public static CustomerModel getUser(int index) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.keySet()).get(index);
    }

    public static CustomerModel getUser() {
        return getUser(0);
    }

    public static UserSteps getSteps(int index) {
        return new ArrayList<>(INSTANCE.get().userStepsMap.values()).get(index);
    }

    public static UserSteps getSteps() {
        return getSteps(0);
    }

    public static void clear() {
        INSTANCE.get().userStepsMap.clear();
        INSTANCE.get().accountStepsMap.clear();
        INSTANCE.get().currentUser = null;
        INSTANCE.remove();
    }
}
