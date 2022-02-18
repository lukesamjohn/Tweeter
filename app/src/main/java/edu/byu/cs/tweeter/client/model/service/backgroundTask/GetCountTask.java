package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class GetCountTask extends AuthorizedTask{

    public static final String COUNT_KEY = "count";


    protected User targetUser;

    public GetCountTask(AuthToken authToken,  User targetUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.targetUser = targetUser;
    }


    @Override
    protected void runTask() {
        // nothing to do for now, loadBundle gets the count from followers or followees and that
        // is called in BackgroundTask

    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        msgBundle.putInt(COUNT_KEY, runCountTask());
    }

    protected abstract int runCountTask();
}
