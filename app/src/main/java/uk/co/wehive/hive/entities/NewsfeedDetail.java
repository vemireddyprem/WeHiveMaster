package uk.co.wehive.hive.entities;

public class NewsfeedDetail {

    //commun newsfeed details
    private String event_name;
    private String event_venue;
    private String city;

    //good_time and checkin_event
    private int id_event;
    private String events_photo;
    private int counter;

    //checkin_event and post_created
    private String event_date;
    private String event_time;

    //goodtime
    private long age_event;

    //checkin_event
    private int counter_likes;
    private int counter_shares;
    private int counter_comments;

    //post_created
    private int id_post;
    private String post_text;
    private int id_user;
    private boolean is_checkin;
    private boolean is_liked;
    private String post_media;
    private String video_thumb;
    private String post_media_type;
    private int total_comments;
    private int total_likes;
    private int total_shares;
    private boolean has_liked;
    private String share_link;
    private String country;

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_venue() {
        return event_venue;
    }

    public void setEvent_venue(String event_venue) {
        this.event_venue = event_venue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getId_event() {
        return id_event;
    }

    public void setId_event(int id_event) {
        this.id_event = id_event;
    }

    public String getEvents_photo() {
        return events_photo;
    }

    public void setEvents_photo(String events_photo) {
        this.events_photo = events_photo;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public long getAge_event() {
        return age_event;
    }

    public void setAge_event(long age_event) {
        this.age_event = age_event;
    }

    public int getCounter_likes() {
        return counter_likes;
    }

    public void setCounter_likes(int counter_likes) {
        this.counter_likes = counter_likes;
    }

    public int getCounter_shares() {
        return counter_shares;
    }

    public void setCounter_shares(int counter_shares) {
        this.counter_shares = counter_shares;
    }

    public int getCounter_comments() {
        return counter_comments;
    }

    public void setCounter_comments(int counter_comments) {
        this.counter_comments = counter_comments;
    }

    public int getId_post() {
        return id_post;
    }

    public void setId_post(int id_post) {
        this.id_post = id_post;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public boolean isIs_checkin() {
        return is_checkin;
    }

    public void setIs_checkin(boolean is_checkin) {
        this.is_checkin = is_checkin;
    }

    public boolean isIs_liked() {
        return is_liked;
    }

    public void setIs_liked(boolean is_liked) {
        this.is_liked = is_liked;
    }

    public String getPost_media() {
        return post_media;
    }

    public void setPost_media(String post_media) {
        this.post_media = post_media;
    }

    public String getVideo_thumb() {
        return video_thumb;
    }

    public void setVideo_thumb(String video_thumb) {
        this.video_thumb = video_thumb;
    }

    public String getPost_media_type() {
        return post_media_type;
    }

    public void setPost_media_type(String post_media_type) {
        this.post_media_type = post_media_type;
    }

    public int getTotal_comments() {
        return total_comments;
    }

    public void setTotal_comments(int total_comments) {
        this.total_comments = total_comments;
    }

    public int getTotal_likes() {
        return total_likes;
    }

    public void setTotal_likes(int total_likes) {
        this.total_likes = total_likes;
    }

    public int getTotal_shares() {
        return total_shares;
    }

    public void setTotal_shares(int total_shares) {
        this.total_shares = total_shares;
    }

    public boolean isHas_liked() {
        return has_liked;
    }

    public void setHas_liked(boolean has_liked) {
        this.has_liked = has_liked;
    }

    public String getShare_link() {
        return share_link;
    }

    public void setShare_link(String share_link) {
        this.share_link = share_link;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
