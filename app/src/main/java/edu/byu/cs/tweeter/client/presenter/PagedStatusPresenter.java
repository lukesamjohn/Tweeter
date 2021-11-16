package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.views.PagedStatusView;
import edu.byu.cs.tweeter.client.presenter.views.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedStatusPresenter extends PagedPresenter<Status> {

    public PagedStatusPresenter(PagedView<Status> view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    public void gotoWebsite(String clickable) {
        PagedStatusView pView =  (PagedStatusView) view;
        pView.navigateToWebsite(clickable);
    }


}
