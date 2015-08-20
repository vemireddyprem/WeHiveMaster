package uk.co.wehive.hive.listeners.dialogs;

import uk.co.wehive.hive.entities.Post;

public interface IPendingPost {

    void createPost(Post post);

    void deletePost(Post post);
}