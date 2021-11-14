package edu.byu.cs.tweeter.client.presenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter implements FollowService.IsFollowerObserver, FollowService.FollowObserver, FollowService.UnfollowObserver,
        FollowService.GetFollowersCountObserver, FollowService.GetFollowingCountObserver, UserService.LogoutObserver {

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
        void updateFollowerCount(int count);
        void updateFolloweeCount(int count);
        void showLogoutToast(String message);
        void cancelLogoutToast();
        void logoutUser();
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
    public void isFollowerFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void isFollowerException(Exception exception) {
        String message = "Failed to determine following relationship because of exception: " + exception.getMessage();
        view.displayMessage(message);
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

    @Override
    public void followFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void followException(Exception exception) {
        String message = "Failed to follow because of exception: " + exception.getMessage();
        view.displayMessage(message);
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

    @Override
    public void unfollowFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void unfollowException(Exception exception) {
        String message = "Failed to unfollow because of exception: " + exception.getMessage();
        view.displayMessage(message);
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
    public void getFollowersCountFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void getFollowersCountException(Exception exception) {
        String message = "Failed to get followers count because of exception: " + exception.getMessage();
        view.displayMessage(message);
    }

    @Override
    public void getFollowingCountSuccess(int count) {
        view.updateFolloweeCount(count);
    }

    @Override
    public void getFollowingCountFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void getFollowingCountException(Exception exception) {
        String message = "Failed to get following count because of exception: " + exception.getMessage();
        view.displayMessage(message);
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

    @Override
    public void logoutFailure(String message) {
        view.displayMessage(message);
    }

    @Override
    public void logoutException(Exception ex) {
        String message = "Failed to logout because of exception: " + ex.getMessage();
        view.displayMessage(message);
    }
}
