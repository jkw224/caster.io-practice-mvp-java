package io.caster.simplemvp.presenter;

import io.caster.simplemvp.view.UserView;

public interface UserPresenter extends LifecyclePresenter {
    void setUserView(UserView view);
    void loadUserDetails();
    void saveUser();
}
