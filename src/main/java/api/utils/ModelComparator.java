package api.utils;

import api.configs.Config;
import org.assertj.core.api.Assertions;
import java.lang.reflect.Field;

public class ModelComparator {

    public static void compare(Object request, Object response) {
        String className = request.getClass().getSimpleName();
        String mapping = Config.getProperty(className + ".name");

        Object expectedValue = getFieldValue(request, "name");
        Object actualValue = getNestedFieldValue(response, mapping);

        Assertions.assertThat(actualValue)
                .as("Сравнение поля запроса 'name' и поля ответа '%s'", mapping)
                .isEqualTo(expectedValue);

        System.out.println("✅ Сравнение успешно: [" + expectedValue + "] == [" + actualValue + "]");
    }

    private static Object getNestedFieldValue(Object obj, String fieldPath) {
        Object current = obj;
        try {
            for (String part : fieldPath.split("\\.")) {
                if (current == null) return null;
                current = getFieldValue(current, part);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при разборе пути: " + fieldPath, e);
        }
        return current;
    }

    private static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = getFieldRecursive(obj.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            throw new RuntimeException("Поле '" + fieldName + "' не найдено!");
        }
    }

    private static Field getFieldRecursive(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getFieldRecursive(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
}