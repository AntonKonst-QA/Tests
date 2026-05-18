package requests.skeleton.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface<T extends BaseModel, R> {
    R post(T model);
    R put(T model);
    R get();
    R get (long id);
}
