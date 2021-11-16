package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.AuthenticateView;

//you have to make the class implement a login observer interface
public class LoginPresenter extends AuthenticatePresenter implements UserService.LoginObserver {

    /**
     * A View interface for the LoginFragment to implement. Contains
     * all the functions that the presenter should be able to call on the view
     * To communicate with the user. Now all these methods are testable without having to
     * be able to view the screen.
     */
    public interface View extends AuthenticateView {}

    private final View view; // the LoginFragment that represents what is on the screen

    /**
     * Constructor for the LoginPresenter. Implies that every presenter has to be attaches to a
     * view (Login Fragment)
     * @param view A View defined by the presenter and implemented in LoginFragment
     */
    public LoginPresenter(View view) {
        super(view);
        this.view = view;
    }

    /**
     * The login method to bridge that view and model (UserService)
     * @param alias The alias typed in by the user
     * @param password The password typed in by the user
     */
    public void login(String alias, String password) {

        clearMessages();

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


}
