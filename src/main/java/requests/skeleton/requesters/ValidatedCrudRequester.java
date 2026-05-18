package requests.skeleton.requesters;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.Getter;
import models.BaseModel;
import requests.skeleton.Endpoint;
import requests.skeleton.HttpRequest;
import requests.skeleton.interfaces.CrudEndpointInterface;

public class ValidatedCrudRequester<RQ extends BaseModel, RS extends BaseModel> extends HttpRequest implements CrudEndpointInterface<RQ, RS> {
    @Getter
    private CrudRequester crudRequester;
    private Class<RS> responseClass;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
        this.responseClass = (Class<RS>) endpoint.getResponseModel();
    }

    private RS extractResponse(io.restassured.response.Response response) {
        try {
            return response.as(responseClass);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public RS post(RQ model) {
        return extractResponse(crudRequester.post(model).extract().response());
    }

    @Override
    public RS put(RQ model) {
        return extractResponse(crudRequester.put(model).extract().response());
    }

    @Override
    public RS get() {
        return extractResponse(crudRequester.get().extract().response());
    }

    @Override
    public RS get(long id) {
        return extractResponse(crudRequester.get(id).extract().response());
    }

    public RS getWithParam(String paramName, Object value) {
        return extractResponse(crudRequester.getWithPathParam(paramName, value).extract().response());
    }
}
