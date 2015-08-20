package uk.co.wehive.hive.entities;

public class NewsFeed {

    private int id_newsfeed;
    private int id_user;
    private String username;
    private boolean premium;
    private String fullname;
    private String photo;
    private String type_newsfeed;
    private int age_newsfeed;
    private NewsfeedDetail detail;
    private String message;

    public int getId_newsfeed() {
        return id_newsfeed;
    }

    public void setId_newsfeed(int id_newsfeed) {
        this.id_newsfeed = id_newsfeed;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getType_newsfeed() {
        return type_newsfeed;
    }

    public void setType_newsfeed(String type_newsfeed) {
        this.type_newsfeed = type_newsfeed;
    }

    public int getAge_newsfeed() {
        return age_newsfeed;
    }

    public void setAge_newsfeed(int age_newsfeed) {
        this.age_newsfeed = age_newsfeed;
    }

    public NewsfeedDetail getDetail() {
        return detail;
    }

    public void setDetail(NewsfeedDetail detail) {
        this.detail = detail;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
