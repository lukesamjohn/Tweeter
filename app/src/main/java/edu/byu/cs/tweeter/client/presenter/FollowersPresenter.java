package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.PagedUserView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedUserPresenter implements FollowService.GetFollowersObserver, UserService.GetUserObserver {

    public FollowersPresenter(FollowersPresenter.View view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    public interface View extends PagedUserView {}



    @Override
    protected void doService() {
        getFollowers(authToken, user, lastItem);
    }


    // action and possible to get more followers for the  recycler view
    public void getFollowers(AuthToken authToken, User targetUser, User lastFollower) {
        new FollowService().getFollowers(authToken, targetUser, PAGE_SIZE, lastFollower, this);
    }


}
