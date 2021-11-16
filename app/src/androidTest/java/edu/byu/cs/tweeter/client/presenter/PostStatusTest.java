package edu.byu.cs.tweeter.client.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {

    private MainActivityPresenter.View mockMainView;
    private StatusService mockStatusService;
    private MainActivityPresenter mainActivityPresenterSpy;

@Before
    public void setup() {

    mockMainView = Mockito.mock(MainActivityPresenter.View.class);
    mockStatusService = Mockito.mock(StatusService.class);

    User user = new User();

    mainActivityPresenterSpy = Mockito.spy(new MainActivityPresenter(mockMainView, user, Mockito.any()));
    Mockito.doReturn(mockStatusService).when(mainActivityPresenterSpy).getStatusService();
}

@Test
    public void testPostStatus_postSucceeds() {
    Answer<Void> postSucceedAnswer = new Answer<Void>() {
        @Override
        public Void answer(InvocationOnMock invocation) throws Throwable {
            StatusService.PostStatusObserver observer = invocation.getArgument(2);
            observer.postStatusSuccess("Successfully Posted!");
            return null;
        }
    };

    Mockito.doAnswer(postSucceedAnswer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());

    mainActivityPresenterSpy.postStatus("Test Status");
    Mockito.verify(mockMainView).cancelPostToast();
    Mockito.verify(mockMainView).displayMessage("Successfully Posted!");

}



}
