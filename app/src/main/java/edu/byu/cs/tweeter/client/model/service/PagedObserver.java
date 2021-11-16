package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

public interface PagedObserver<T> extends ServiceObserver {

    void getListSucceeded(List<T> list, boolean hasMorePages);

}
