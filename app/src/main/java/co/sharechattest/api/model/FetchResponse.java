package co.sharechattest.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Varun on 02/05/17.
 */

public class FetchResponse implements Serializable {

    private boolean success;

    private String error;

    private List<FetchData> data;


    /**
     * GETTER METHODS
     */

    public boolean isSuccess() {
        return success;
    }

    public List<FetchData> getData() {
        return data;
    }

    public String getError() {
        return error;
    }

    /**
     * SETTER METHODS
     */
    public void setData(List<FetchData> data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
