package io.caster.simplemvp;

import org.junit.Before;
import org.junit.Test;

import io.caster.simplemvp.model.User;
import io.caster.simplemvp.presenter.UserPresenter;
import io.caster.simplemvp.presenter.UserPresenterImpl;
import io.caster.simplemvp.repository.UserRepository;
import io.caster.simplemvp.view.UserView;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


public class PresenterTests {

    UserRepository mockUserRepository;
    UserView mockUserView;
    UserPresenter mockPresenter;
    User user;

    @Before
    public void setup() {
        mockUserRepository = mock(UserRepository.class);

        user = new User();
        user.setId(1);
        user.setFirstName("Mighty");
        user.setLastName("Mouse");
        when(mockUserRepository.getUser(anyInt())).thenReturn(user);

        mockUserView = mock(UserView.class);

        mockPresenter = new UserPresenterImpl(mockUserRepository);
    }

    @Test
    public void noInteractionsWithViewShouldTakePlaceIfUserIsNull() {
        mockPresenter.saveUser();

        // user object is not initialized, lets verify no interactions take place
        verifyZeroInteractions(mockUserView);
    }

    @Test
    public void shouldBeABleToLoadTheUserFromTheRepositoryWhenValidUserIsPresent() {
        when(mockUserView.getUserId()).thenReturn(1);

        mockPresenter.setUserView(mockUserView);

        // Verify repository interactions
        verify(mockUserRepository, times(1)).getUser(anyInt());

        // Verify view interactions
        verify(mockUserView, times(1)).getUserId();
        verify(mockUserView, times(1)).displayFirstName("Mighty");
        verify(mockUserView, times(1)).displayLastName("Mouse");
        verify(mockUserView, never()).showUserNotFoundMessage();
    }

    @Test
    public void shouldShowErrorMessageOnViewWhenUserIsNotPresent() {
        when(mockUserView.getUserId()).thenReturn(1);

        // Return null when we ask the repo for a user.
        when(mockUserRepository.getUser(anyInt())).thenReturn(null);

        mockPresenter.setUserView(mockUserView);

        // Verify repository interactions
        verify(mockUserRepository, times(1)).getUser(anyInt());

        // verify view interactions
        verify(mockUserView, times(1)).getUserId();
        verify(mockUserView, times(1)).showUserNotFoundMessage();
        verify(mockUserView, never()).displayFirstName(anyString());
        verify(mockUserView, never()).displayLastName(anyString());
    }

    @Test
    public void shouldShouldErrorMessageDuringSaveIfFirstOrLastNameIsMissing() {
        when(mockUserView.getUserId()).thenReturn(1);

        // Load the user
        mockPresenter.setUserView(mockUserView);

        verify(mockUserView, times(1)).getUserId();

        // Set up the view mock
        when(mockUserView.getFirstName()).thenReturn(""); // empty string

        mockPresenter.saveUser();

        verify(mockUserView, times(1)).getFirstName();
        verify(mockUserView, never()).getLastName();
        verify(mockUserView, times(1)).showUserNameIsRequired();

        // Now tell mockUserView to return a value for first name and an empty last name
        when(mockUserView.getFirstName()).thenReturn("Foo");
        when(mockUserView.getLastName()).thenReturn("");

        mockPresenter.saveUser();

        verify(mockUserView, times(2)).getFirstName(); // Called two times now, once before, and once now
        verify(mockUserView, times(1)).getLastName();  // Only called once
        verify(mockUserView, times(2)).showUserNameIsRequired(); // Called two times now, once before and once now
    }

    @Test
    public void shouldBeAbleToSaveAValidUser() {
        when(mockUserView.getUserId()).thenReturn(1);

        // Load the user
        mockPresenter.setUserView(mockUserView);

        verify(mockUserView, times(1)).getUserId();

        when(mockUserView.getFirstName()).thenReturn("Foo");
        when(mockUserView.getLastName()).thenReturn("Bar");

        mockPresenter.saveUser();

        // Called two more times in the saveUser call.
        verify(mockUserView, times(2)).getFirstName();
        verify(mockUserView, times(2)).getLastName();

        assertThat(user.getFirstName(), is("Foo"));
        assertThat(user.getLastName(), is("Bar"));

        // Make sure the repository saved the user
        verify(mockUserRepository, times(1)).save(user);

        // Make sure that the view showed the user saved message
        verify(mockUserView, times(1)).showUserSavedMessage();
    }

    @Test
    public void shouldLoadUserDetailsWhenTheViewIsSet() {
        mockPresenter.setUserView(mockUserView);
        verify(mockUserRepository, times(1)).getUser(anyInt());
        verify(mockUserView, times(1)).displayFirstName(anyString());
        verify(mockUserView, times(1)).displayLastName(anyString());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionWhenViewIsNull() {
        // Null out the view
        mockPresenter.setUserView(null);

        // Try to load the screen which will force interactions with the view
        mockPresenter.loadUserDetails();
    }
}
