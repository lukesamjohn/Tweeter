package edu.byu.cs.tweeter.client.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Implements the login screen.
 */
public class LoginFragment extends Fragment implements LoginPresenter.View {
//    private static final String LOG_TAG = "LoginFragment";

    private Toast loginInToast; // toast displayed on the login screen
    private EditText alias; // the text box where the user can type in their username
    private EditText password; // the text box where the user can type in their password
    private TextView errorView; // a text view that will display an error to the user
    private final LoginPresenter presenter = new LoginPresenter(this); // register this View to a login presenter

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    /**
     * Starts the main activity if the user succeeds
     * @param user the user that was successfully logged in
     */
    @Override
    public void navigateToUser(User user) {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        startActivity(intent);
    }

    /**
     * Show the user an error message
     * @param message the error message to be shown to the user
     */
    @Override
    public void displayErrorMessage(String message) {
        errorView.setText(message);
    }

    /**
     * Clear the error message
     */
    @Override
    public void clearErrorMessage() {
        errorView.setText("");
    }

    /**
     * Communicate information to the user in a toast
     * @param message the message to convey to the user
     */
    @Override
    public void displayInfoMessage(String message) {
        clearInfoMessage();
        loginInToast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        loginInToast.show();
    }

    /**
     * Clear the info message
     */
    @Override
    public void clearInfoMessage() {
        if (loginInToast != null) {
            loginInToast.cancel();
            loginInToast = null;
        }
    }

    /**
     * What is to happen when the view is created. Also calls presenter.login when button is clicked
     * @param inflater an inflater to inflate the view
     * @param container the ViewGroup the fragment is in
     * @param savedInstanceState The Bundle for the LoginFragment
     * @return the View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the view on the screen
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // get user input data
        alias = view.findViewById(R.id.loginUsername);
        password = view.findViewById(R.id.loginPassword);
        errorView = view.findViewById(R.id.loginError);

        // create a button and set a listener so that presenter can be notified to run login
        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view1 -> {
            // Login and move to MainActivity.
            presenter.login(alias.getText().toString(), password.getText().toString());
        });

        return view;
    }


}
