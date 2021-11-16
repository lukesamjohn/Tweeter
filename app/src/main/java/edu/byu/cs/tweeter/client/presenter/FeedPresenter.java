package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.PagedStatusView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedStatusPresenter implements StatusService.GetFeedObserver, UserService.GetUserObserver {


    public FeedPresenter(FeedPresenter.View view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    public interface View extends PagedStatusView  {}

    @Override
    protected void doService() {
        getFeed(authToken, user, lastItem);
    }

    public void getFeed(AuthToken authToken, User targetUser, Status lastStatus) {
        new StatusService().getFeed(authToken, targetUser, PAGE_SIZE, lastStatus, this);
    }


}
