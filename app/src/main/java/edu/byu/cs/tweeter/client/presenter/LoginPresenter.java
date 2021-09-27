package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

//you have to make the class implement a login observer interface
public class LoginPresenter implements UserService.LoginObserver {

    /**
     * A View interface for the LoginFragment to implement. Contains
     * all the functions that the presenter should be able to call on the view
     * To communicate with the user. Now all these methods are testable without having to
     * be able to view the screen.
     */
    public interface View {
        void navigateToUser(User user);

        void displayErrorMessage(String message);
        void clearErrorMessage();

        void displayInfoMessage(String message);
        void clearInfoMessage();
    }

    private final View view; // the LoginFragment that represents what is on the screen

    /**
     * Constructor for the LoginPresenter. Implies that every presenter has to be attaches to a
     * view (Login Fragment)
     * @param view A View defined by the presenter and implemented in LoginFragment
     */
    public LoginPresenter(View view) {
        this.view = view;
    }

    /**
     * The login method to bridge that view and model (UserService)
     * @param alias The alias typed in by the user
     * @param password The password typed in by the user
     */
    public void login(String alias, String password) {

        // clear any previous messages that were on the screen
        view.clearErrorMessage();
        view.clearInfoMessage();

        // make sure the username and password have the proper format to be looked up in the database
        String message = validateLogin(alias, password);
        if (message == null) {
            //alert the user that they are being looked up in the database
            view.displayInfoMessage("Logging in...");
            new UserService().login(alias, password, this); // start the real login routine
        } else {
            //display error message if the format is incorrect
            view.displayErrorMessage("Login failed: " + message);
        }
    }

    /**
     * Make sure the user input the correct format
     * @param alias The alias typed in by the user
     * @param password The password typed in by the user
     * @return An error message if incorrect format or null is everything is correct
     */
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

    /*
    These are really the heart of the Presenter idea - they are implementations of methods declared
     in the Observer interface from the UserService (Model) class. Data gets passed from here in the
     Presenter class into the Model class. The Model (UserService) class manipulates the data, then
     calls these Observer methods (which, by the way, were defined in the UserService class) which
     then call methods in the View class to display this information. Those methods in the View
     (LoginFragment) class were declared here in the Presenter class as part of the View interface.
    */
    /**
     * Start main activity and greet the user
     * @param authToken the authtoken for the user this session
     * @param user the user that was logged in
     */
    @Override
    public void loginSucceeded(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());
    }

    /**
     * Let the user know that the login attempt failed
     * @param message Message explaining the failure
     */
    @Override
    public void loginFailed(String message) {
        view.displayErrorMessage("Login failed: " + message);
    }

    /**
     * Let the user or system know that an exception happened
     * @param ex the exception that happened in the UserService
     */
    @Override
    public void loginThrewException(Exception ex) {
        view.displayErrorMessage("Login failed: " + ex.getMessage());
    }
}
