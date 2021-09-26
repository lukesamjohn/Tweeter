package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

//you have to make the class implement a login observer interface
public class LoginPresenter implements UserService.LoginObserver {
    //basically everything the user can do in the view,
    //there is a corresponding method in the presenter
    // here it is just click the login button
    // however, it also needs the methods the presenter can call
    // on the view

    // there are function calls going both directions
    // these are the methods that the presenter will be able
    // to call on the view, like displaying pop-ups or whatever
    public interface View {

        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();


    }

    //view and presenter need pointers to each other
    private final View view;

    public LoginPresenter(View view) {
        this.view = view;
    }

    public void login(String alias, String password) {
        // pass in itself as an observer
        // Login and move to MainActivity.
        view.clearErrorMessage();
        view.clearInfoMessage();

        String message = validateLogin(alias, password);
        if (message == null) {
            view.displayInfoMessage("Logging in...");
            new UserService().login(alias, password, this);
        } else {
            //display error message
            view.displayErrorMessage("Login failed: " + message);

        }
    }

    private String validateLogin(String alias, String password) {

        if (alias.charAt(0) != '@') {
            return ("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            return ("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            return ("Password cannot be empty.");
        }
        return null;
    }

    @Override
    public void loginSucceeded(AuthToken authToken, User user) {

        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());

    }

    @Override
    public void loginFailed(String message) {
        view.displayErrorMessage("Login failed: " + message);
    }

    @Override
    public void loginThrewException(Exception ex) {
        view.displayErrorMessage("Login failed: " + ex.getMessage());
    }
}
