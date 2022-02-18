package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.views.BaseView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthorizedPresenter extends PresenterBase {
    protected final User user;
    protected final AuthToken authToken;

    public AuthorizedPresenter(BaseView view, User user, AuthToken authToken) {
        super(view);
        this.user = user;
        this.authToken = authToken;
    }
}
