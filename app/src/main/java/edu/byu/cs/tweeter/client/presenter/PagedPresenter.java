package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.views.PagedView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends AuthorizedPresenter implements UserService.GetUserObserver, PagedObserver<T> {

    protected static final int PAGE_SIZE = 10;
    protected T lastItem; //last status or last user
    private boolean hasMorePages = true;
    private boolean isLoading = false;
    private final PagedView<T> pagedView;

    public PagedPresenter(PagedView<T> view, User user, AuthToken authToken) {
        super(view, user, authToken);
        pagedView = view;
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
    }

    protected void setLastItem(T lastItem) {this.lastItem = lastItem;}

    private void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }



    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            pagedView.setLoading(true);
            doService();
        }
    }


    // action and possible results to load up a new user when it is clicked
    public void gotoUser(AuthToken authToken, String alias) {
        new UserService().getUser(authToken, alias, this);
    }


    @Override
    public void getUserSucceeded(User user) {
        pagedView.navigateToUser(user);
    }



    protected abstract void doService();

    @Override
    public void getListSucceeded(List<T> list, boolean hasMorePages) {
        setLastItem((list.size() > 0) ? list.get(list.size() - 1) : null);
        setHasMorePages(hasMorePages);

        pagedView.setLoading(false);
        pagedView.addItems(list);
        setLoading(false);
    }

    @Override
    public void handleFailure(String message) {
        String errorMessage = "Failed to retrieve new items to add: " + message;

        pagedView.setLoading(false);
        pagedView.displayMessage(errorMessage);
        setLoading(false);
    }

    @Override
    public void handleException(Exception exception) {
        String errorMessage = "Failed to retrieve new items to add due to exception: " + exception.getMessage();

        pagedView.setLoading(false);
        pagedView.displayMessage(errorMessage);
        setLoading(false);
    }
}
