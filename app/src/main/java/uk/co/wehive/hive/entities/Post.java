package uk.co.wehive.hive.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import uk.co.wehive.hive.utils.swipe.SwipeListViewItemRow;

public class Post extends SwipeListViewItemRow implements Parcelable {

    private String age_post;
    private String comments;
    private String first_name;
    private String id_post;
    private String id_user;
    private String last_name;
    private String liked;
    private String likes;
    private String media_absolute_file;
    private String media_absolute_thumbvideo_file;
    private String media_id_media;
    private String media_id_post;
    private String media_type;
    private String media_video_thumbnail;
    private String mentions;
    private String ponderation;
    private String post_text;
    private String text;
    private boolean premium;
    private String share_link;
    private String shares;
    private String user_photo;
    private String username;
    private String user_first_name;
    private String user_id_user;
    private String user_last_name;
    private String user_real_photo;
    private String user_real_username;

    //Fields for pending Posts
    private String postEventId;
    private File postPhotoFile;
    private String postVideoPath;
    private String postType;

    public Post() {
    }

    public String getAge_post() {
        return age_post;
    }

    public void setAge_post(String age_post) {
        this.age_post = age_post;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFirst_name() {
        if (user_first_name == null) {
            return first_name;
        } else {
            return user_first_name;
        }
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getLast_name() {
        if (user_last_name == null) {
            return last_name;
        } else {
            return user_last_name;
        }
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getMedia_absolute_file() {
        return media_absolute_file;
    }

    public void setMedia_absolute_file(String media_absolute_file) {
        this.media_absolute_file = media_absolute_file;
    }

    public String getMedia_absolute_thumbvideo_file() {
        return media_absolute_thumbvideo_file;
    }

    public void setMedia_absolute_thumbvideo_file(String media_absolute_thumbvideo_file) {
        this.media_absolute_thumbvideo_file = media_absolute_thumbvideo_file;
    }

    public String getMedia_id_media() {
        return media_id_media;
    }

    public void setMedia_id_media(String media_id_media) {
        this.media_id_media = media_id_media;
    }

    public String getMedia_id_post() {
        return media_id_post;
    }

    public void setMedia_id_post(String media_id_post) {
        this.media_id_post = media_id_post;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }


    public String getMedia_video_thumbnail() {
        return media_video_thumbnail;
    }

    public void setMedia_video_thumbnail(String media_video_thumbnail) {
        this.media_video_thumbnail = media_video_thumbnail;
    }

    public String getMentions() {
        return mentions;
    }

    public void setMentions(String mentions) {
        this.mentions = mentions;
    }

    public String getPonderation() {
        return ponderation;
    }

    public void setPonderation(String ponderation) {
        this.ponderation = ponderation;
    }

    public String getPost_text() {
        if (text == null) {
            return post_text;
        } else {
            return text;
        }
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getShare_link() {
        return share_link;
    }

    public void setShare_link(String share_link) {
        this.share_link = share_link;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public String getUser_photo() {
        if (user_real_photo == null) {
            return user_photo;
        } else {
            return user_real_photo;
        }
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUsername() {
        if (user_real_username == null) {
            return username;
        } else {
            return user_real_username;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUser_first_name() {
        return user_first_name;
    }

    public void setUser_first_name(String user_first_name) {
        this.user_first_name = user_first_name;
    }

    public String getUser_id_user() {
        return user_id_user;
    }

    public void setUser_id_user(String user_id_user) {
        this.user_id_user = user_id_user;
    }

    public String getUser_last_name() {
        return user_last_name;
    }

    public void setUser_last_name(String user_last_name) {
        this.user_last_name = user_last_name;
    }

    public String getUser_real_photo() {
        return user_real_photo;
    }

    public void setUser_real_photo(String user_real_photo) {
        this.user_real_photo = user_real_photo;
    }

    public String getUser_real_username() {
        return user_real_username;
    }

    public void setUser_real_username(String user_real_username) {
        this.user_real_username = user_real_username;
    }

    public String getPostEventId() {
        return postEventId;
    }

    public void setPostEventId(String postEventId) {
        this.postEventId = postEventId;
    }

    public File getPostPhotoFile() {
        return postPhotoFile;
    }

    public void setPostPhotoFile(File postPhotoFile) {
        this.postPhotoFile = postPhotoFile;
    }

    public String getPostVideoPath() {
        return postVideoPath;
    }

    public void setPostVideoPath(String postVideoPath) {
        this.postVideoPath = postVideoPath;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    private Post(Parcel obj) {
        age_post = obj.readString();
        comments = obj.readString();
        first_name = obj.readString();
        id_post = obj.readString();
        id_user = obj.readString();
        last_name = obj.readString();
        liked = obj.readString();
        likes = obj.readString();
        media_absolute_file = obj.readString();
        media_absolute_thumbvideo_file = obj.readString();
        media_id_media = obj.readString();
        media_id_post = obj.readString();
        media_type = obj.readString();
        media_video_thumbnail = obj.readString();
        mentions = obj.readString();
        ponderation = obj.readString();
        post_text = obj.readString();
        premium = obj.readByte() != 0;
        share_link = obj.readString();
        shares = obj.readString();
        user_photo = obj.readString();
        username = obj.readString();
        postEventId = obj.readString();
        postVideoPath = obj.readString();
        postType = obj.readString();
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        dest.writeString(age_post);
        dest.writeString(comments);
        dest.writeString(first_name);
        dest.writeString(id_post);
        dest.writeString(id_user);
        dest.writeString(last_name);
        dest.writeString(liked);
        dest.writeString(likes);
        dest.writeString(media_absolute_file);
        dest.writeString(media_absolute_thumbvideo_file);
        dest.writeString(media_id_media);
        dest.writeString(media_id_post);
        dest.writeString(media_type);
        dest.writeString(media_video_thumbnail);
        dest.writeString(mentions);
        dest.writeString(ponderation);
        dest.writeString(post_text);
        dest.writeByte((byte) (premium ? 1 : 0));
        dest.writeString(share_link);
        dest.writeString(shares);
        dest.writeString(user_photo);
        dest.writeString(username);
        dest.writeString(postEventId);
        dest.writeString(postVideoPath);
        dest.writeString(postType);
    }
}
