package edu.byu.cs.tweeter.client.presenter.views;

import edu.byu.cs.tweeter.model.domain.Status;

public interface PagedStatusView extends PagedView<Status> {
    void navigateToWebsite(String clickable);
}
