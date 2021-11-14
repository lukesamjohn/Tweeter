package edu.byu.cs.tweeter.client.model.service;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;



public class UserService {


    // observer for every task, and every task has a handler
    public interface GetUserObserver {
        void getUserSucceeded(User user);
        void getUserFailed(String message);
        void getUserThrewException(Exception ex);
    }


    public void getUser(AuthToken authToken, String alias, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken,
                alias, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
    }

    /**
     * Message handler (i.e., observer) for GetUserTask.
     */
    private class GetUserHandler extends Handler {

        private GetUserObserver observer;

        public GetUserHandler(GetUserObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(GetUserTask.SUCCESS_KEY);
            if (success) {
                User user = (User) msg.getData().getSerializable(GetUserTask.USER_KEY);
                observer.getUserSucceeded(user);
            } else if (msg.getData().containsKey(GetUserTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(GetUserTask.MESSAGE_KEY);
                observer.getUserFailed(message);
            } else if (msg.getData().containsKey(GetUserTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(GetUserTask.EXCEPTION_KEY);
                observer.getUserThrewException(ex);
            }
        }
    }



    /**
     * LoginObserver interface used by presenter
     * The methods are the tasks that we want to accomplish when a User click "Login"
     */
    public interface LoginObserver {
        void loginSucceeded(AuthToken authToken, User user);
        void loginFailed(String message);
        void loginThrewException(Exception ex);
    }


    /**
     * Executes the login task on a new thread
     * @param alias The alias typed in by the user
     * @param password The password typed in by the user
     * @param loginObserver A LoginObserver used in a handler created in the presenter that passes info to the View
     */
    public void login(String alias, String password, LoginObserver loginObserver) {
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(loginObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(loginTask);
    }


    /**
     * Message handler (i.e., Observer) for LoginTask
     */
    private static class LoginHandler extends Handler {
        private final LoginObserver loginObserver;
        public  LoginHandler(LoginObserver loginObserver) {
            this.loginObserver = loginObserver;
        }
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

                loginObserver.loginSucceeded(authToken, loggedInUser);
            } else if (msg.getData().containsKey(LoginTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(LoginTask.MESSAGE_KEY);

                loginObserver.loginFailed(message);
            } else if (msg.getData().containsKey(LoginTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LoginTask.EXCEPTION_KEY);

                loginObserver.loginThrewException(ex);
            }
        }
    }

    /**
     * Registration observer interface to be used by the presenter
     * Methods are outcomes that could happen upon clicking login using the data from this Model
     */
    public interface RegisterObserver {
        void registrationSucceeded(AuthToken authToken, User user);
        void registrationFailed(String message);
        void registrationThrewException(Exception ex);
    }
    public void register(String firstName, String lastName, String alias, String password,
                         ImageView image, RegisterObserver registerObserver) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, imageToByteArray(image), new RegisterHandler(registerObserver));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(registerTask);
    }


    // RegisterHandler
    private static class RegisterHandler extends Handler {
        private final RegisterObserver registerObserver;
        public RegisterHandler(RegisterObserver registerObserver) {this.registerObserver = registerObserver;}
        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(RegisterTask.SUCCESS_KEY);
            if (success) {
                User registeredUser = (User) msg.getData().getSerializable(RegisterTask.USER_KEY);
                AuthToken authToken = (AuthToken) msg.getData().getSerializable(RegisterTask.AUTH_TOKEN_KEY);

                Cache.getInstance().setCurrUser(registeredUser);
                Cache.getInstance().setCurrUserAuthToken(authToken);

                registerObserver.registrationSucceeded(authToken, registeredUser);

            } else if (msg.getData().containsKey(RegisterTask.MESSAGE_KEY)) {
                String message = msg.getData().getString(RegisterTask.MESSAGE_KEY);
                registerObserver.registrationFailed(message);

            } else if (msg.getData().containsKey(RegisterTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(RegisterTask.EXCEPTION_KEY);
                registerObserver.registrationThrewException(ex);
            }
        }
    }



    public interface LogoutObserver {
        void logoutSuccess();
        void logoutFailure(String message);
        void logoutException(Exception ex);
    }

    public void logout(AuthToken authToken, LogoutObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(logoutTask);
    }



    // LogoutHandler

    private class LogoutHandler extends Handler {

        LogoutObserver observer;

        public LogoutHandler(LogoutObserver observer) {
            this.observer = observer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            boolean success = msg.getData().getBoolean(LogoutTask.SUCCESS_KEY);
            if (success) {
                observer.logoutSuccess();
            } else if (msg.getData().containsKey(LogoutTask.MESSAGE_KEY)) {
                String message = "Failed to logout: " + msg.getData().getString(LogoutTask.MESSAGE_KEY);
                observer.logoutFailure(message);
            } else if (msg.getData().containsKey(LogoutTask.EXCEPTION_KEY)) {
                Exception ex = (Exception) msg.getData().getSerializable(LogoutTask.EXCEPTION_KEY);
                observer.logoutException(ex);
            }
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
