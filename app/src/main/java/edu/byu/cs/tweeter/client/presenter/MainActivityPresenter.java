package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter implements FollowService.IsFollowerObserver {

    private final MainActivityPresenter.View view;
    private final User user;
    private final AuthToken authToken;


    public MainActivityPresenter(MainActivityPresenter.View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
    }

    public interface View {
        void updateFollowButtonAppearance(boolean isFollower);
        void displayMessage(String message);
    }


    public void isFollower(User follower, User followee) {
        new FollowService().isFollower(authToken, follower, followee, this);
    }

    @Override
    public void isFollowerSuccess(boolean isFollower) {
        view.updateFollowButtonAppearance(isFollower);
    }

    @Override
    public void isFollowerFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void isFollowerException(Exception exception) {
        String message = "Failed to determine following relationship because of exception: " + exception.getMessage();
        view.displayMessage(message);
    }
}
