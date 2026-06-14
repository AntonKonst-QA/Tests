package common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(TimingExtension.class);
    private static final String START_TIME_KEY = "START_TIME";

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = extensionContext.getRequiredTestClass().getName() + "." + extensionContext.getDisplayName();
        extensionContext.getStore(NAMESPACE).put(START_TIME_KEY, System.currentTimeMillis());

        System.out.println("Thread " + Thread.currentThread().getName() + ": Test started -> " + testName);;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        String testName = extensionContext.getRequiredTestClass().getName() + "." + extensionContext.getDisplayName();

        // Вытаскиваем время старта из Store именно этого теста
        Long startTime = extensionContext.getStore(NAMESPACE).get(START_TIME_KEY, Long.class);

        if (startTime != null) {
            long testDuration = System.currentTimeMillis() - startTime;
            System.out.println("Thread " + Thread.currentThread().getName() + ": Test finished -> " + testName + " Duration: " + testDuration + " ms");
        }
     }
}
