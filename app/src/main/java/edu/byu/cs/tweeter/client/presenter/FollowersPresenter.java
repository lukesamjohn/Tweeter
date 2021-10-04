package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter implements FollowService.GetFollowersObserver, UserService.GetUserObserver {
    private static final int PAGE_SIZE = 10;
    private final FollowersPresenter.View view;
    private final User user;
    private final AuthToken authToken;


    public FollowersPresenter(FollowersPresenter.View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    User lastFollower;
    private boolean hasMorePages = true;
    private boolean isLoading = false;
    public interface View {
        void setLoading(boolean value);

        void addItems(List<User> newUsers);

        void displayMessage(String message);

        void navigateToUser(User user);
    }



    private void setLastFollower(User lastFollowee) {
        this.lastFollower = lastFollowee;
    }


    private void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }


    private void setLoading(boolean loading) {
        isLoading = loading;
    }

    /**
     * Called by the view to request that another page of "following" users be loaded.
     */
    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);
            getFollowers(authToken, user, PAGE_SIZE, lastFollower);
        }
    }

    // action and possible results to load up a new user when it is clicked
    public void gotoUser(AuthToken authToken, String alias) {
        new UserService().getUser(authToken, alias, this);
    }

    @Override
    public void getUserSucceeded(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void getUserFailed(String message) {
        view.displayMessage(message);
    }

    @Override
    public void getUserThrewException(Exception ex) {
        view.displayMessage(ex.getMessage());
    }





    // action and possible to get more followers for the  recycler view
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower) {
        new FollowService().getFollowers(authToken, targetUser, limit, lastFollower, this);
    }

    @Override
    public void getFollowersSuccess(List<User> followers, boolean hasMorePages) {
        setLastFollower((followers.size() > 0) ? followers.get(followers.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(followers);
        setLoading(false);
    }

    @Override
    public void getFollowersFailure(String message) {
        String errorMessage = "Failed to retrieve followers: " + message;

        view.setLoading(false);
        view.displayMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void getFollowersException(Exception exception) {
        String errorMessage = "Failed to retrieve followers because of exception: " + exception.getMessage();

        view.setLoading(false);
        view.displayMessage(errorMessage);
        setLoading(false);
    }
}
