package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface GetFollowersObserver {
        void getFollowersSuccess(List<User> followers, boolean hasMorePages);

        void getFollowersFailure(String message);

        void getFollowersException(Exception exception);
    }




    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollower, GetFollowersObserver observer) {
        GetFollowersTask followersTask = new GetFollowersTask(authToken, targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followersTask);
    }


    /**
     * Message handler (i.e., observer) for GetFollowersTask.
     */
    private static class GetFollowersHandler extends Handler {

        private final GetFollowersObserver observer;

        public GetFollowersHandler(GetFollowersObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersTask.SUCCESS_KEY);
            if (success) {
                List<User> followers = (List<User>) msg.getData().getSerializable(GetFollowersTask.FOLLOWERS_KEY);
                boolean hasMorePages = msg.getData().getBoolean(GetFollowersTask.MORE_PAGES_KEY);
                observer.getFollowersSuccess(followers, hasMorePages);
            } else if (msg.getData().containsKey(GetFollowersTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetFollowersTask.MESSAGE_KEY);
                observer.getFollowersFailure(message);
            } else if (msg.getData().containsKey(GetFollowersTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersTask.EXCEPTION_KEY);
                observer.getFollowersException(ex);
            }
        }
    }


    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface GetFolloweesObserver {
        void getFolloweesSuccess(List<User> followees, boolean hasMorePages);

        void getFolloweesFailure(String message);

        void getFolloweesException(Exception exception);
    }



    /**
     * Requests the users that the user specified in the request is following.
     * Limits the number of followees returned and returns the next set of
     * followees after any that were returned in a previous request.
     * This is an asynchronous operation.
     *
     * @param authToken    the session auth token.
     * @param targetUser   the user for whom followees are being retrieved.
     * @param limit        the maximum number of followees to return.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, GetFolloweesObserver observer) {
        GetFollowingTask followingTask = new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new GetFolloweesHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followingTask);
    }


    /**
     * Handles messages from the background task indicating that the task is done, by invoking
     * methods on the observer.
     */
    public static class GetFolloweesHandler extends Handler {

        private final GetFolloweesObserver observer;

        public GetFolloweesHandler(GetFolloweesObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(GetFollowingTask.SUCCESS_KEY);
            if (success) {
                List<User> followees = (List<User>) bundle.getSerializable(GetFollowingTask.FOLLOWEES_KEY);
                boolean hasMorePages = bundle.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
                observer.getFolloweesSuccess(followees, hasMorePages);
            } else if (bundle.containsKey(GetFollowingTask.MESSAGE_KEY)) {
                String errorMessage = bundle.getString(GetFollowingTask.MESSAGE_KEY);
                observer.getFolloweesFailure(errorMessage);
            } else if (bundle.containsKey(GetFollowingTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(GetFollowingTask.EXCEPTION_KEY);
                observer.getFolloweesException(ex);
            }
        }
    }

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface IsFollowerObserver {
        void isFollowerSuccess(boolean isFollower);

        void isFollowerFailure(String message);

        void isFollowerException(Exception exception);
    }

    public void isFollower(AuthToken authToken, User follower, User followee, IsFollowerObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken,
                follower, followee, new IsFollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    // IsFollowerHandler
    private class IsFollowerHandler extends Handler {

        private final IsFollowerObserver observer;

        public IsFollowerHandler(IsFollowerObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(Message message) {
            Bundle bundle = message.getData();
            boolean success = bundle.getBoolean(IsFollowerTask.SUCCESS_KEY);
            if (success) {
                boolean isFollower = bundle.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
                observer.isFollowerSuccess(isFollower);

            } else if (bundle.containsKey(IsFollowerTask.MESSAGE_KEY)) {
                String errorMessage = "Failed to determine following relationship: " + bundle.getString(IsFollowerTask.MESSAGE_KEY);
                observer.isFollowerFailure(errorMessage);
            } else if (bundle.containsKey(IsFollowerTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) bundle.getSerializable(IsFollowerTask.EXCEPTION_KEY);
                observer.isFollowerException(ex);
            }
        }
    }


    //Follow Service

    public interface FollowObserver {
        void followSuccess(String message, boolean added, User selectedUser);

        void followFailure(String message);

        void followException(Exception exception);
    }

    public void follow(AuthToken authToken, User selectedUser, FollowObserver observer) {
        FollowTask followTask = new FollowTask(authToken,
                selectedUser, new FollowHandler(observer, selectedUser));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);
    }

    // FollowHandler

    private class FollowHandler extends Handler {

        FollowObserver observer;
        User followee;

        public FollowHandler(FollowObserver observer, User followee) {
            this.observer = observer;
            this.followee = followee;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
            if (success) {
                String message = "Adding " + followee.getName() + "...";
                observer.followSuccess(message, true, followee);
            } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
                observer.followFailure(message);
            } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
                observer.followException(ex);
            }

        }
    }


    //Unfollow Service

    public interface UnfollowObserver {
        void unfollowSuccess(String message, boolean removed, User selectedUser);

        void unfollowFailure(String message);

        void unfollowException(Exception exception);
    }

    public void unfollow(AuthToken authToken, User followee, UnfollowObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(authToken,
                followee, new UnfollowHandler(observer, followee));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    // UnfollowHandler
    private class UnfollowHandler extends Handler {

        UnfollowObserver observer;
        User followee;

        public UnfollowHandler(UnfollowObserver observer, User followee) {
            this.observer = observer;
            this.followee = followee;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
            if (success) {
                String message = "Removing " + followee.getName() + "...";
                observer.unfollowSuccess(message, true, followee);
            } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
                String message = "Failed to unfollow: " +  msg.getData().getString(UnfollowTask.MESSAGE_KEY);
                observer.unfollowFailure(message);
            } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
                observer.unfollowException(ex);
            }

        }
    }



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



    public interface GetFollowersCountObserver {
        void getFollowersCountSuccess(int count);

        void getFollowersCountFailure(String message);

        void getFollowersCountException(Exception exception);
    }

    private class GetFollowersCountHandler extends Handler {
        GetFollowersCountObserver observer;

        public GetFollowersCountHandler(GetFollowersCountObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowersCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.getFollowersCountSuccess(count);
            } else if (msg.getData().containsKey(GetFollowersCountTask.MESSAGE_KEY)) {
                String message = "Failed to get followers count: " + msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.getFollowersCountFailure(message);

            } else if (msg.getData().containsKey(GetFollowersCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.getFollowersCountException(ex);
            }
        }
    }


    public interface GetFollowingCountObserver {

        void getFollowingCountSuccess(int count);

        void getFollowingCountFailure(String message);

        void getFollowingCountException(Exception exception);
    }

    private class GetFollowingCountHandler extends Handler {

        GetFollowingCountObserver observer;

        public GetFollowingCountHandler(GetFollowingCountObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
            if (success) {
                int count = msg.getData().getInt(GetFollowersCountTask.COUNT_KEY);
                observer.getFollowingCountSuccess(count);
            } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
                String message = "Failed to get following count: " + msg.getData().getString(GetFollowersCountTask.MESSAGE_KEY);
                observer.getFollowingCountFailure(message);
            } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetFollowersCountTask.EXCEPTION_KEY);
                observer.getFollowingCountException(ex);
            }
        }
    }

}

