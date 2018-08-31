package app.com.thetechnocafe.hirecall.Features.Authentication.SignIn;

import app.com.thetechnocafe.hirecall.BaseApp;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface SignInContract {
    interface View extends BaseApp.View {
        void startHomeActivity();

        void onLoginSuccessful();

        void wrongCredentials();

        void emailNotVerified();

        void onVerificationMailSent();

        void onFirstJobNotCreated();

        void startFeedbackActivity();
    }

    interface Presenter extends BaseApp.Presenter<SignInContract.View> {
        void singIn(String username, String password);

        void checkLoggedInStatus();

        void resendVerificationEmail(String email, String password);
    }
}
