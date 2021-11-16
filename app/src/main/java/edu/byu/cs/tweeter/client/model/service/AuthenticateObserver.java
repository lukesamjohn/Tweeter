package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticateObserver extends ServiceObserver{

    void handleLogin(AuthToken authToken, User user);

}
