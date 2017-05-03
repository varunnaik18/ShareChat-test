package co.sharechattest.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Varun on 03/05/17.
 */

public class UpdateData implements Serializable {

    private String type;

    private String id;

    @SerializedName("author_name")
    private String authorName;

    @SerializedName("author_contact")
    private String authorContact;

    @SerializedName("author_dob")
    private String authorDob;

    @SerializedName("author_status")
    private String authorStatus;

    @SerializedName("author_gender")
    private String authorGender;

    @SerializedName("profile_url")
    private String profileUrl;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorContact() {
        return authorContact;
    }

    public void setAuthorContact(String authorContact) {
        this.authorContact = authorContact;
    }

    public String getAuthorDob() {
        return authorDob;
    }

    public void setAuthorDob(String authorDob) {
        this.authorDob = authorDob;
    }

    public String getAuthorStatus() {
        return authorStatus;
    }

    public void setAuthorStatus(String authorStatus) {
        this.authorStatus = authorStatus;
    }

    public String getAuthorGender() {
        return authorGender;
    }

    public void setAuthorGender(String authorGender) {
        this.authorGender = authorGender;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
