package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.PagedStatusView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedStatusPresenter implements StatusService.GetStoryObserver, UserService.GetUserObserver {


    public StoryPresenter(StoryPresenter.View view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    public interface View extends PagedStatusView {}

    @Override
    protected void doService() {
        getStory(authToken,user, lastItem);
    }

    public void getStory(AuthToken authToken, User targetUser, Status lastStatus) {
         new StatusService().getStory(authToken, targetUser, PAGE_SIZE, lastStatus, this);
     }

}
