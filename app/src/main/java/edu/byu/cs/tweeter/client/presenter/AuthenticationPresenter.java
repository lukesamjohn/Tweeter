package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.AuthenticateObserver;
import edu.byu.cs.tweeter.client.presenter.views.AuthenticateView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationPresenter extends PresenterBase implements AuthenticateObserver {

    private final AuthenticateView aView = (AuthenticateView) view;
    public AuthenticationPresenter(AuthenticateView view) {
        super(view);
    }

    @Override
    public void handleLogin(AuthToken authToken, User user) {
        aView.navigateToUser(user);
        aView.clearErrorMessage();
        aView.displayInfoMessage("Hello " + user.getName());
    }

    @Override
    public void handleFailure(String message) {
        aView.displayErrorMessage("Login failed: " + message);
    }

    @Override
    public void handleException(Exception exception) {
        aView.displayErrorMessage("Exception occurred: " + exception.getMessage());

    }

    protected void clearMessages() {
        // clear any previous messages that were on the screen
        aView.clearErrorMessage();
        aView.clearInfoMessage();
    }
}
