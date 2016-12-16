package io.caster.simplemvp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.caster.simplemvp.presenter.UserPresenter;
import io.caster.simplemvp.presenter.UserPresenterImpl;
import io.caster.simplemvp.repository.UserRepository;
import io.caster.simplemvp.repository.InMemoryUserRepositoryImpl;

@Module
public class AppModule {
    @Provides @Singleton
    public UserRepository provideUserRepository() {
        return new InMemoryUserRepositoryImpl();
    }

    @Provides
    public UserPresenter provideUserPresenter(UserRepository userRepository) {
        return new UserPresenterImpl(userRepository);
    }
}
