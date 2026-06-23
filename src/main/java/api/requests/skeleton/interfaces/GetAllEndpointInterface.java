package api.requests.skeleton.interfaces;

import java.util.List;

public interface GetAllEndpointInterface<T> {
    List<T> getAll(Class<?> clazz);
}
