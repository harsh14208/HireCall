package app.com.thetechnocafe.hirecall.Features.Authentication.SignUp;

import app.com.thetechnocafe.hirecall.BaseApp;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface SignUpContract {
    interface View extends BaseApp.View {
        void onSignUpSuccessful();

        void emailAlreadyExists();

        void onErrorOccurred();
    }

    interface Presenter extends BaseApp.Presenter<SignUpContract.View> {
        void signUp(String email, String password, String name, String domain);
    }
}
