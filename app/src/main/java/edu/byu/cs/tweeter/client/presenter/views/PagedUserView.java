package edu.byu.cs.tweeter.client.presenter.views;

import edu.byu.cs.tweeter.model.domain.User;

/**
 * This interface exists solely because PagedView<Status> needed a navigateToWebsite function
 */
public interface PagedUserView extends PagedView<User> {
}
