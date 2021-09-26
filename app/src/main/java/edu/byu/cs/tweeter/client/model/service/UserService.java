package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;


/*
Defines an observer interface
Runs background takes
Contains android message handler that propagates task messages up to observer
 */

public class UserService {

    //define an observer interface
    //the presenter is calling this method, presenters call services
    public interface LoginObserver {
        void loginSucceeded(AuthToken authToken, User user);
        void loginFailed(String message);
        void loginThrewException(Exception ex);
    }


    public void login(String alias, String password, LoginObserver observer) {

        // Run a login task to login the user
        //get the code from the login fragment
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }

    /**
     * Message handler (i.e., observer) for LoginTask
     */
    private static class LoginHandler extends Handler {

        // put in an observer so that handler can call it when task is done
        // essentially just propagate it up
        private final LoginObserver observer;
        public  LoginHandler(LoginObserver observer) {
            this.observer = observer;
        }

        //take a login observer parameter

        @Override
        public void handleMessage(@NonNull Message msg) {

            // handles three instances: login worked, failed, or it threw an exception
            boolean success = msg.getData().getBoolean(LoginTask.SUCCESS_KEY);
            if (success) {
                User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);

                // Cache user session information
                Cache.getInstance().setCurrUser(loggedInUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);


                observer.loginSucceeded(authToken, loggedInUser);
            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);

                observer.loginFailed(message);
            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);

                observer.loginThrewException(ex);
            }
        }
    }

}
