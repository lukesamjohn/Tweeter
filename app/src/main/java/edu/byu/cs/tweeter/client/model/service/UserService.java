package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.TaskExecution;
import edu.byu.cs.tweeter.client.model.service.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;



public class UserService {


    //Get User Service
    public interface GetUserObserver extends ServiceObserver {
        void getUserSucceeded(User user);
    }
    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken,
                alias, new GetUserHandler(observer));
        new TaskExecution<>(getUserTask).executeTask();
    }
    private static class GetUserHandler extends BackgroundTaskHandler {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }
        @Override
        protected String getFailedMessagePrefix() {
            return "Get User Service";
        }
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            ((GetUserObserver) observer).getUserSucceeded(user);
        }
    }



    //Login Service
    public interface LoginObserver extends AuthenticateObserver{}
    public void login(String alias, String password, LoginObserver loginObserver) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(loginObserver));
        new TaskExecution<>(loginTask).executeTask();
    }
    private static class LoginHandler extends BackgroundTaskHandler {
        public  LoginHandler(LoginObserver loginObserver) {
            super(loginObserver);
        }
        @Override
        protected String getFailedMessagePrefix() {
            return "Login Service";
        }
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            User loggedInUser = (User) data.getSerializable(LoginTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);
            // Cache user session information
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            ((LoginObserver) observer).handleLogin(authToken, loggedInUser);
        }
    }

    //Register Service
    public interface RegisterObserver extends AuthenticateObserver {}
    public void register(String firstName, String lastName, String alias, String password,
                         ImageView image, RegisterObserver registerObserver) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageToByteArray(image), new RegisterHandler(registerObserver));
        new TaskExecution<>(registerTask).executeTask();
    }
    private static class RegisterHandler extends BackgroundTaskHandler {
        public RegisterHandler(RegisterObserver registerObserver) {super(registerObserver);}
        @Override
        protected String getFailedMessagePrefix() {
            return "Register Service";
        }
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
            ((RegisterObserver) observer).handleLogin(authToken, registeredUser);
        }
    }


    //Logout Service
    public interface LogoutObserver extends ServiceObserver {
        void logoutSuccess();
    }
    public void logout(AuthToken authToken, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        new TaskExecution<>(logoutTask).executeTask();
    }
    private class LogoutHandler extends BackgroundTaskHandler {
        public LogoutHandler(LogoutObserver observer) {
            super(observer);
        }
        @Override
        protected String getFailedMessagePrefix() {
            return "Logout Service";
        }
        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            ((LogoutObserver) observer).logoutSuccess();
        }
    }


    // Helper Functions
    private String imageToByteArray(ImageView imageToUpload) {
        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

}
