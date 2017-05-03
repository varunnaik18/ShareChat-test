package co.sharechattest.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Varun on 02/05/17.
 */

public class PostDataFetch implements Serializable {

    @SerializedName("request_id")
    private String requestId;

    @SerializedName("id_offset")
    private int idOffset;

    /**
     * GETTER METHODS
     */
    public int getIdOffset() {
        return idOffset;
    }

    public String getRequestId() {
        return requestId;
    }

    /**
     * SETTER METHODS
     */

    public void setIdOffset(int idOffset) {
        this.idOffset = idOffset;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
