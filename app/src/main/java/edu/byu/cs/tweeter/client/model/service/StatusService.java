package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.util.Log;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.TaskExecution;
import edu.byu.cs.tweeter.client.model.service.handler.BackgroundTaskHandler;
import edu.byu.cs.tweeter.client.model.service.handler.PagedServiceHandler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public interface GetFeedObserver extends PagedObserver<Status> {}
    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        GetFeedObserver observer) {
        GetFeedTask getFeedTask = new GetFeedTask(authToken,
                targetUser, limit, lastStatus, new GetFeedHandler(observer));
        new TaskExecution<>(getFeedTask).executeTask();
    }
    private static class GetFeedHandler extends PagedServiceHandler<Status> {
        public GetFeedHandler(GetFeedObserver observer) {
            super(observer);
        }

    }


    public interface GetStoryObserver extends PagedObserver<Status> {}
    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                         GetStoryObserver observer) {
        GetStoryTask getStoryTask = new GetStoryTask(authToken,
                targetUser, limit, lastStatus, new GetStoryHandler(observer));
        new TaskExecution<>(getStoryTask).executeTask();
    }
    private static class GetStoryHandler extends PagedServiceHandler<Status> {
        public GetStoryHandler(GetStoryObserver observer) {
            super(observer);
        }

    }



    public interface PostStatusObserver extends ServiceObserver {
        void postStatusSuccess(String message);
    }
    public void postStatus(AuthToken authToken, String post, PostStatusObserver observer) {
        try {
            Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
            PostStatusTask statusTask = new PostStatusTask(authToken,
                    newStatus, new PostStatusHandler(observer));
            new TaskExecution<>(statusTask).executeTask();
        } catch (Exception ex) {
            String LOG_TAG = "StatusService";
            Log.e(LOG_TAG, ex.getMessage(), ex);
            observer.handleFailure(ex.getMessage());
        }
    }
    private class PostStatusHandler extends BackgroundTaskHandler {
        public PostStatusHandler(PostStatusObserver observer) {
            super(observer);
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            ((PostStatusObserver)observer).postStatusSuccess("Successfully Posted!");
        }
    }



    // Helper Functions

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }



}
