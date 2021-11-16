package edu.byu.cs.tweeter.client.presenter.views;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedView<T> extends AuthenticatedView{

   void setLoading(boolean value);

    void addItems(List<T> newItemList);

    void navigateToUser(User user);



}
