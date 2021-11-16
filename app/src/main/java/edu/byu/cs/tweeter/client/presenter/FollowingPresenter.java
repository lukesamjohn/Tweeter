package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.PagedUserView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The presenter for the "following" functionality of the application.
 */
public class FollowingPresenter extends PagedUserPresenter implements FollowService.GetFolloweesObserver, UserService.GetUserObserver {

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View extends PagedUserView {}


    /**
     * Creates an instance.
     *
     * @param view      the view for which this class is the presenter.
     * @param user      the user that is currently logged in.
     * @param authToken the auth token for the current session.
     */
    public FollowingPresenter(View view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }



    /**
     * Requests the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned for a previous request. This is an asynchronous
     * operation.
     *
     * @param authToken    the session auth token.
     * @param targetUser   the user for whom followees are being retrieved.
     * @param lastFollowee the last followee returned in the previous request (can be null).
     */
    public void getFollowing(AuthToken authToken, User targetUser, User lastFollowee) {
        new FollowService().getFollowees(authToken, targetUser, PAGE_SIZE, lastFollowee, this);
    }

    @Override
    protected void doService() {
        getFollowing(authToken, user, lastItem);
    }
}