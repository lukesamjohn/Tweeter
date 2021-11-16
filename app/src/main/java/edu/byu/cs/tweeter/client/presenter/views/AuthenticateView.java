package edu.byu.cs.tweeter.client.presenter.views;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticateView extends BaseView{

    void navigateToUser(User user);


    void displayErrorMessage(String message);
    void clearErrorMessage();

    void displayInfoMessage(String message);
    void clearInfoMessage();

}
