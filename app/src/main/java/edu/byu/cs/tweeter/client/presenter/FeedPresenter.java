package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter implements StatusService.GetFeedObserver, UserService.GetUserObserver {

    private static final int PAGE_SIZE = 10;
    private final FeedPresenter.View view;
    private final User user;
    private final AuthToken authToken;

    public FeedPresenter(FeedPresenter.View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    private Status lastStatus;
    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public interface View {
        void setLoading(boolean value);

        void addItems(List<Status> newStatuses);

        void displayMessage(String message);

        void navigateToUser(User user);

        void navigateToWebsite(String clickable);
    }


    private void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }


    private void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }


    private void setLoading(boolean loading) {
        isLoading = loading;
    }


    public void gotoWebsite(String clickable) {
        view.navigateToWebsite(clickable);
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


    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);
            getFeed(authToken, user, PAGE_SIZE, lastStatus);
        }
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus) {
        new StatusService().getFeed(authToken, targetUser, limit, lastStatus, this);
    }


    @Override
    public void getFeedSucceeded(List<Status> statuses, boolean hasMorePages) {
        setLastStatus((statuses.size() > 0) ? statuses.get(statuses.size() - 1) : null);
        setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(statuses);
        setLoading(false);
    }

    @Override
    public void getFeedFailed(String msg) {
        String errorMessage = "Failed to retrieve new statuses for feed: " + msg;

        view.setLoading(false);
        view.displayMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void getFeedThrewException(Exception ex) {
        String errorMessage = "Failed to retrieve new statuses for feed: " + ex.getMessage();

        view.setLoading(false);
        view.displayMessage(errorMessage);
        setLoading(false);
    }
}
