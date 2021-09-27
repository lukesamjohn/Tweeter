package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver {
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

    private final RegisterPresenter.View view; // the LoginFragment that represents what is on the screen


    /**
     * Constructor for the LoginPresenter. Implies that every presenter has to be attaches to a
     * view (Login Fragment)
     * @param view A View defined by the presenter and implemented in LoginFragment
     */
    public RegisterPresenter(RegisterPresenter.View view) {
        this.view = view;
    }

    public void register(String firstName, String lastName, String alias, String password, ImageView image) {
        // clear any previous messages that were on the screen
        view.clearErrorMessage();
        view.clearInfoMessage();

        // make sure the username and password have the proper format to be looked up in the database
        String message = validateRegistration(firstName, lastName, alias, password, image);
        if (message == null) {
            view.displayInfoMessage("Hello, " + firstName + "!");
            new UserService().register(firstName, lastName, alias, password, image, this); // start the registration routine
        }
        else {
            view.displayErrorMessage("Registration failed: " + message);
        }
    }

    private String validateRegistration(String firstName, String lastName, String alias, String password, ImageView image) {
        if (firstName.length() == 0) {
            return ("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            return ("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            return ("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            return ("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            return ("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            return ("Password cannot be empty.");
        }
        if (image.getDrawable() == null) {
            return ("Profile image must be uploaded.");
        }

        return null;
    }

    @Override
    public void registrationSucceeded(AuthToken authToken, User user) {
        view.navigateToUser(user);
        view.clearErrorMessage();
        view.displayInfoMessage("Hello " + user.getName());
    }

    @Override
    public void registrationFailed(String message) {
        view.displayErrorMessage("Failed to register: " + message);
    }

    @Override
    public void registrationThrewException(Exception ex) {
        view.displayErrorMessage("Failed to register because of exception: " + ex.getMessage());
    }
}
