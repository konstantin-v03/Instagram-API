package WebSiteDrivers;

import java.io.Closeable;
import java.util.Set;

public interface InstagramDriver extends Closeable {

    boolean login(String username, String password);

    boolean loginUsingCookie(String username);

    boolean like(String internalUrl);

    boolean unLike(String internalUrl);

    boolean follow(String internalUrl);

    boolean unFollow(String internalUrl);

    boolean addComment(String internalUrl, String comment);

    boolean removeComment(String internalUrl, String comment);

    boolean addPost(String internalPath, String caption);

    boolean copyImagePost(String internalUrl, String caption);

    boolean removePost(String internalUrl);

    Set<String> getUrlsToAccountPosts(String internalUrl);

    Set<String> getUrlsToAccountPosts(String internalUrl, int amount);

    Set<String> getFollowers(String internalUrl);

    Set<String> getFollowers(String internalUrl, int amount);

    Set<String> getFollowing(String internalUrl);

    Set<String> getFollowing(String internalUrl, int amount);
}
