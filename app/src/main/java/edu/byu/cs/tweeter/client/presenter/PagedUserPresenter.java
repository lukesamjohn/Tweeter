package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.views.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedUserPresenter extends PagedPresenter<User>{
    public PagedUserPresenter(PagedView<User> view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }
}
