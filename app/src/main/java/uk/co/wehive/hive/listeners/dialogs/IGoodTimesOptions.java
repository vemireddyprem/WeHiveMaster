package uk.co.wehive.hive.listeners.dialogs;

import uk.co.wehive.hive.entities.Post;
import uk.co.wehive.hive.utils.AppConstants;

public interface IGoodTimesOptions {

    void setAction(Post post, AppConstants.OPTIONS_GOOD_TIMES option, String active);
}
