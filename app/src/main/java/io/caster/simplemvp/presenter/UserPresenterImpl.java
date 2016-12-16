package io.caster.simplemvp.presenter;

import io.caster.simplemvp.model.User;
import io.caster.simplemvp.repository.UserRepository;
import io.caster.simplemvp.view.UserView;

public class UserPresenterImpl implements UserPresenter {

    private UserView userView;
    private UserRepository userRepository;
    private User u;

    public UserPresenterImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void resume() {
        loadUserDetails();
    }

    @Override
    public void pause() {

    }

    @Override
    public void setUserView(UserView view) {
        this.userView = view;
        loadUserDetails();
    }

    @Override
    public void loadUserDetails() {
        int userId = userView.getUserId();
        u = userRepository.getUser(userId);
        if(u == null) {
            userView.showUserNotFoundMessage();
        } else {
            userView.displayFirstName(u.getFirstName());
            userView.displayLastName(u.getLastName());
        }
    }

    @Override
    public void saveUser() {
        if(u != null) {
            if(userView.getFirstName().trim().equals("") || userView.getLastName().trim().equals("")) {
                userView.showUserNameIsRequired();
            } else {
                u.setFirstName(userView.getFirstName());
                u.setLastName(userView.getLastName());
                userRepository.save(u);
                userView.showUserSavedMessage();
            }

        }
    }

}
