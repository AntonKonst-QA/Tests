package api.steps;

import api.generators.RandomModelGenerator;
import api.models.BaseModel;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

public class AdminSteps {

    public static CreateUserResponse createUser() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        return new ValidatedCrudRequester<CreateUserRequest, CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityWasCreated()
        ).post(userRequest);
    }

    public static List<CreateUserResponse> getAllUsers() {
        return new ValidatedCrudRequester<CreateUserRequest, CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOK()
        ).getAll(CreateUserResponse[].class);
    }

    public static void deleteUser(String userId) {
        new ValidatedCrudRequester<BaseModel, BaseModel>(
                RequestSpecs.adminSpec(),
                Endpoint.DELETE_USER,
                ResponseSpecs.requestReturnsOK()
        ).deleteVoid(Long.parseLong(userId));
    }
}
