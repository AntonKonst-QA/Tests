package api.requests.skeleton.interfaces;

import api.models.BaseModel;

import java.util.List;

public interface CrudEndpointInterface<T extends BaseModel, R> {
    R post(T model);
    R put(T model);
    R get();
    R get (long id);
    List<R> getAll();
}
