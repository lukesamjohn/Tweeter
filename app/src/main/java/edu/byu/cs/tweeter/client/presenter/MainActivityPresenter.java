package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.AuthenticatedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter implements FollowService.IsFollowerObserver, FollowService.FollowObserver, FollowService.UnfollowObserver,
        FollowService.GetFollowersCountObserver, FollowService.GetFollowingCountObserver, UserService.LogoutObserver, StatusService.PostStatusObserver {

    private final MainActivityPresenter.View view;
    private final User user;
    private final AuthToken authToken;
    private final StatusService statusService;


    public MainActivityPresenter(MainActivityPresenter.View view, User user, AuthToken authToken) {
        this.view = view;
        this.user = user;
        this.authToken = authToken;
        statusService = new StatusService();
    }

    public interface View extends AuthenticatedView {
        void updateFollowButtonAppearance(boolean isFollower);
        void updateFollowerCount(int count);
        void updateFolloweeCount(int count);
        void showLogoutToast(String message);
        void cancelLogoutToast();
        void logoutUser();
        void showPostToast(String message);
        void cancelPostToast();
    }


    // Presenter's link to isFollower service
    public void isFollower(User follower, User followee) {
        new FollowService().isFollower(authToken, follower, followee, this);
    }

    @Override
    public void isFollowerSuccess(boolean isFollower) {
        view.updateFollowButtonAppearance(isFollower);
    }

    @Override
    public void handleFailure(String message) {
        view.displayMessage("Error in Main Activity Presenter: " + message);
    }

    // Presenter's link to follow service
    public void follow(User selectedUser) {
        new FollowService().follow(authToken, selectedUser, this);
    }

    @Override
    public void followSuccess(String message, boolean added, User selectedUser) {
        view.updateFollowButtonAppearance(added);
        view.displayMessage(message);
        updateSelectedUserFollowingAndFollowers(selectedUser);
    }

    // Presenter's link to unfollow service
    public void unfollow(User followee) {
        new FollowService().unfollow(authToken, followee, this);
    }

    @Override
    public void unfollowSuccess(String message, boolean removed, User selectedUser) {
        view.updateFollowButtonAppearance(!removed);
        view.displayMessage(message);
        updateSelectedUserFollowingAndFollowers(selectedUser);
    }



    // Updating Selected User's following and followers count
    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        new FollowService().updateSelectedUserFollowingAndFollowers(authToken, selectedUser, this, this);
    }

    @Override
    public void getFollowersCountSuccess(int count) {
        view.updateFollowerCount(count);
    }

    @Override
    public void getFollowingCountSuccess(int count) {
        view.updateFolloweeCount(count);
    }



    // Logging out

    public void logout() {
        view.showLogoutToast("Logging Out...");
        new UserService().logout(authToken, this);
    }


    @Override
    public void logoutSuccess() {
        view.cancelLogoutToast();
        view.logoutUser();
    }


    // Posting Status

    public void postStatus (String post) {
        view.showPostToast("Posting Status...");
        getStatusService().postStatus(authToken, post, this);
    }

    @Override
    public void postStatusSuccess(String message) {
        view.cancelPostToast();
        view.displayMessage(message);
    }

    public StatusService getStatusService() {
        return statusService;
    }


}
