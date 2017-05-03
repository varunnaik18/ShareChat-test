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
    private Integer idOffset;

    private UpdateData data;

    /**
     * GETTER METHODS
     */
    public Integer getIdOffset() {
        return idOffset;
    }

    public String getRequestId() {
        return requestId;
    }

    public UpdateData getData() {
        return data;
    }

    /**
     * SETTER METHODS
     */

    public void setIdOffset(Integer idOffset) {
        this.idOffset = idOffset;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setData(UpdateData data) {
        this.data = data;
    }
}
