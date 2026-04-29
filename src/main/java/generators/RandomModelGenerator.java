package generators;

import com.mifmif.common.regex.Generex;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.Random;

public class RandomModelGenerator {

    private static final Random RANDOM = new Random();

    public static <T> T generate(Class<T> clazz) {
        try {
            // Создаем экземпляр класса (требуется NoArgsConstructor)
            T entity = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true); // Разрешаем запись в private поля

                Object value;

                // 1. Проверяем наличие аннотации GeneratingRule
                if (field.isAnnotationPresent(GeneratingRule.class)) {
                    String regex = field.getAnnotation(GeneratingRule.class).regex();
                    value = new Generex(regex).random();
                }
                // 2. Если аннотации нет, генерируем по типу данных
                else {
                    value = generateDefaultValue(field.getType());
                }

                field.set(entity, value);
            }
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при генерации сущности " + clazz.getName(), e);
        }
    }

    private static Object generateDefaultValue(Class<?> type) {
        if (type == String.class) return UUID.randomUUID().toString().substring(0, 8);
        if (type == Integer.class || type == int.class) return RANDOM.nextInt(1000);
        if (type == Double.class || type == double.class) return RANDOM.nextDouble();
        if (type == Boolean.class || type == boolean.class) return RANDOM.nextBoolean();
        if (type == Long.class || type == long.class) return RANDOM.nextLong();

        return null; // Для сложных типов можно добавить рекурсию
    }
}