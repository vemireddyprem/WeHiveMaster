package uk.co.wehive.hive.listeners;

import uk.co.wehive.hive.entities.NewsFeed;

public interface INewsFeedEventsListener {

    void setAction(NewsFeed newsFeed, String option, String active);
}