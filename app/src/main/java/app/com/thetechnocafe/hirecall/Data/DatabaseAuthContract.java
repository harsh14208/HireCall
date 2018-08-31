package app.com.thetechnocafe.hirecall.Data;

import app.com.thetechnocafe.hirecall.Enums.FirebaseSignInResult;
import io.reactivex.Observable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface DatabaseAuthContract {
    Observable<Boolean> signUpUser(String email, String password, String name, String user);

    Observable<Boolean> checkIfEmailAlreadyExists(String email);

    Observable<FirebaseSignInResult> signInUser(String email, String password);

    Observable<Boolean> resendVerificationEmail(String email, String password);

    String getSignedInUserUID();

    void signOut();
}
