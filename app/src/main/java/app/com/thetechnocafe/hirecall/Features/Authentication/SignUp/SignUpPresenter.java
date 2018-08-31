package app.com.thetechnocafe.hirecall.Features.Authentication.SignUp;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class SignUpPresenter implements SignUpContract.Presenter {

    private SignUpContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(SignUpContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void signUp(String email, String password, String name, String domain) {
        Disposable disposable = FirebaseAuthDB.getInstance()
                .checkIfEmailAlreadyExists(email)
                .subscribe(result -> {
                    if (!result) {
                        FirebaseAuthDB.getInstance()
                                .signUpUser(email, password, name, domain)
                                .subscribe(signUpResult -> {
                                    if (signUpResult) {
                                        mView.onSignUpSuccessful();
                                    } else {
                                        mView.onErrorOccurred();
                                    }
                                });
                    } else {
                        mView.emailAlreadyExists();
                    }
                });

        mCompositeDisposable.add(disposable);
    }
}
