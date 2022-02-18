package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.TaskExecution;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.handler.PagedServiceHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    //Get Followers Service
    public interface GetFollowersObserver extends PagedObserver<User> {}
    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask followersTask = new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        new TaskExecution<>(followersTask).executeTask();
    }
    private static class GetFollowersHandler extends PagedServiceHandler<User> {

        public GetFollowersHandler(GetFollowersObserver observer) {
            super(observer);
        }
    }


    //Get Followees Service
    public interface GetFolloweesObserver extends PagedObserver<User> {}
    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFolloweesObserver observer) {
        GetFollowingTask followingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFolloweesHandler(observer));
        new TaskExecution<>(followingTask).executeTask();
    }
    public static class GetFolloweesHandler extends PagedServiceHandler<User> {

        public GetFolloweesHandler(GetFolloweesObserver observer) {
            super(observer);
        }
    }

    //Is Follower Service
    public interface IsFollowerObserver extends ServiceObserver {
        void isFollowerSuccess(boolean isFollower);
    }
    public void isFollower(AuthToken authToken, User follower, User followee, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                follower, followee, new IsFollowerHandler(observer));
        new TaskExecution<>(isFollowerTask).executeTask();
    }
    private static class IsFollowerHandler extends BackgroundTaskHandler {
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            ((IsFollowerObserver) observer).isFollowerSuccess(isFollower);
        }
        public IsFollowerHandler(IsFollowerObserver observer) {
            super(observer);
        }

    }

    //Follow Service
    public interface FollowObserver extends ServiceObserver {
        void followSuccess(String message, boolean added, User selectedUser);
    }
    public void follow(AuthToken authToken, User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(authToken,
                selectedUser, new FollowHandler(observer, selectedUser));
       new TaskExecution<>(followTask).executeTask();
    }
    private static class FollowHandler extends BackgroundTaskHandler {
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            String message = "Adding " + followee.getName() + "...";
            ((FollowObserver) observer).followSuccess(message, true, followee);
        }
        User followee;
        public FollowHandler(FollowObserver observer, User followee) {
            super(observer);
            this.followee = followee;
        }
    }

    //Unfollow Service
    public interface UnfollowObserver extends ServiceObserver {
        void unfollowSuccess(String message, boolean removed, User selectedUser);
    }
    public void unfollow(AuthToken authToken, User followee, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken,
                followee, new UnfollowHandler(observer, followee));
        new TaskExecution<>(unfollowTask).executeTask();
    }
    // UnfollowHandler
    private static class UnfollowHandler extends BackgroundTaskHandler {
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            String message = "Removing " + followee.getName() + "...";
            ((UnfollowObserver) observer).unfollowSuccess(message, true, followee);
        }
        User followee;
        public UnfollowHandler(UnfollowObserver observer, User followee) {
            super(observer);
            this.followee = followee;
        }
    }


    //Get Followers Count Service
    public interface GetFollowersCountObserver extends ServiceObserver {
        void getFollowersCountSuccess(int count);
    }
    private static class GetFollowersCountHandler extends BackgroundTaskHandler {


        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            ((GetFollowersCountObserver) observer).getFollowersCountSuccess(count);
        }
        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            super(observer);
        }
    }

    //Get Following Count Service
    public interface GetFollowingCountObserver extends ServiceObserver {
        void getFollowingCountSuccess(int count);
    }
    private static class GetFollowingCountHandler extends BackgroundTaskHandler {

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            ((GetFollowingCountObserver) observer).getFollowingCountSuccess(count);
        }
        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            super(observer);
        }
    }


    // Helper method to update the number of followers and people following the selected user
    public void updateSelectedUserFollowingAndFollowers(AuthToken authToken, User selectedUser, GetFollowingCountObserver followingCountObserver, GetFollowersCountObserver followersCountObserver) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(authToken,
                selectedUser, new GetFollowersCountHandler(followersCountObserver));
        executor.execute(followersCountTask);
        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(authToken,
                selectedUser, new GetFollowingCountHandler(followingCountObserver));
        executor.execute(followingCountTask);
    }

}

