package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.presenter.views.BaseView;

public abstract class PresenterBase {

    protected BaseView view;

    public PresenterBase(BaseView view) {
        this.view = view;
    }

}
