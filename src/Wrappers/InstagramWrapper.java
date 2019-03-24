package Wrappers;

import WebSiteDrivers.SeleniumInstagramDriver;

public abstract class InstagramWrapper {

    protected SeleniumInstagramDriver seleniumInstagramDriver;

    public abstract void clearFollowingFile();

    public abstract void clearUnFollowingFile();

    public abstract void clearPostingFile();

    public abstract void startUnFollowing();

    public abstract void stopUnFollowing();

    public abstract void startFollowing();

    public abstract void stopFollowing();

    public abstract void startPosting();

    public abstract void stopPosting();

    public abstract void addFollowingToFollowingFile(String username, String amountStr);

    public abstract void addFollowersToFollowingFile(String username, String amountStr);

    public abstract void addFollowingToUnFollowingFile(String username, String amountStr);

    public abstract void addPostToPostFile(String internalUrl, String caption);

    public abstract void copyImagePost(String internalUrl, String caption);

    public abstract void addComment(String internalUrl, String comment);

    public abstract void setFollowingSettings(String FOLLOWING_PER_DAY, String FOLLOWING_ONE_TIME, String MAX_FOLLOWING_OFFSET);

    public abstract void setUnFollowingSettings(String UNFOLLOWING_PER_DAY, String UNFOLLOWING_ONE_TIME, String MAX_UNFOLLOWING_OFFSET);

    public abstract void setPostingSettings(String POSTING_PER_DAY, String POSTING_ONE_TIME, String MAX_POSTING_OFFSET);

}
