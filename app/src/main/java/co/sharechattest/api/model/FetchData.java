package co.sharechattest.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Varun on 02/05/17.
 */

public class FetchData implements Serializable {

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

    private String authorAge;

    private String url;

    private long postedOn;

    private String postedDate;

    private boolean localImagePresent;

    /**
     * GETTER METHODS
     */
    public long getPostedOn() {
        return postedOn;
    }

    public String getAuthorContact() {
        return authorContact;
    }

    public String getAuthorDob() {
        return authorDob;
    }

    public String getAuthorGender() {
        return authorGender;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorStatus() {
        return authorStatus;
    }

    public String getId() {
        return id;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getAuthorAge() {
        return authorAge;
    }

    public boolean isLocalImagePresent() {
        return localImagePresent;
    }

    /**
     * SETTER METHODS
     */

    public void setAuthorContact(String authorContact) {
        this.authorContact = authorContact;
    }

    public void setAuthorDob(String authorDob) {
        this.authorDob = authorDob;
    }

    public void setAuthorGender(String authorGender) {
        this.authorGender = authorGender;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorStatus(String authorStatus) {
        this.authorStatus = authorStatus;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public void setAuthorAge(String authorAge) {
        this.authorAge = authorAge;
    }

    public void setLocalImagePresent(boolean localImagePresent) {
        this.localImagePresent = localImagePresent;
    }
}
