package app.com.thetechnocafe.hirecall.Data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app.com.thetechnocafe.hirecall.Enums.FirebaseSignInResult;
import app.com.thetechnocafe.hirecall.Models.UserModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class FirebaseAuthDB implements DatabaseAuthContract {
    private static FirebaseAuthDB sInstance;
    private FirebaseAuth mFirebaseAuth;

    //Instance method
    public static FirebaseAuthDB getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseAuthDB();
        }
        return sInstance;
    }

    //Singleton class
    private FirebaseAuthDB() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public Observable<Boolean> signUpUser(String email, String password, String name, String domain) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Create the user model
                            UserModel userModel = new UserModel();
                            userModel.setName(name);
                            userModel.setEmail(email);
                            userModel.setDomain(domain);

                            //Get the user from result
                            FirebaseUser user = task.getResult().getUser();

                            //Add user data to database
                            FirebaseDB.getInstance()
                                    .addUserToDatabase(user.getUid(), userModel)
                                    .subscribe(result -> {
                                        if (result) {
                                            //Send verification email
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(emailTask -> {
                                                        if (emailTask.isSuccessful()) {
                                                            emitter.onNext(true);
                                                        } else {
                                                            emitter.onNext(false);
                                                        }

                                                        //Sign out the user from app
                                                        mFirebaseAuth.signOut();
                                                    });
                                        } else {
                                            emitter.onNext(false);
                                        }
                                    });
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Check whether the user already exists with the same email id
     */
    @Override
    public Observable<Boolean> checkIfEmailAlreadyExists(String email) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            mFirebaseAuth.fetchProvidersForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Get the number of emails
                            int length = task.getResult().getProviders().size();

                            //If the list is empty, the email doesn't exists
                            if (length == 0) {
                                emitter.onNext(false);
                            } else {
                                //Email exists
                                emitter.onNext(true);
                            }
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Sign in the user using firebase login
     * Check if the email has been verified before proceeding
     *
     * @param email    User provided email
     * @param password User provided password
     */
    @Override
    public Observable<FirebaseSignInResult> signInUser(String email, String password) {
        Observable<FirebaseSignInResult> observable = Observable.create(emitter -> {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Check if the user is verified
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                //Check if the first job is created or not
                                FirebaseDB.getInstance()
                                        .checkForFirstJob()
                                        .subscribe(result -> {
                                            if (result) {
                                                emitter.onNext(FirebaseSignInResult.SUCCESSFUL);
                                            } else {
                                                emitter.onNext(FirebaseSignInResult.FIRST_JOB_NOT_CREATED);
                                            }
                                        });
                            } else {
                                //Logout user
                                mFirebaseAuth.signOut();
                                emitter.onNext(FirebaseSignInResult.NOT_VERIFIED);
                            }
                        } else {
                            emitter.onNext(FirebaseSignInResult.WRONG_CREDENTIALS);
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Return the signed in user's uid
     */
    @Override
    public String getSignedInUserUID() {
        return mFirebaseAuth.getCurrentUser().getUid();
    }

    public String getSignedInUserName(){
        return  mFirebaseAuth.getCurrentUser().getDisplayName();
    }

    /**
     * Check if the user is logged in
     **/
    public boolean isUserLoggedIn() {
        return mFirebaseAuth.getCurrentUser() != null;
    }

    /**
     * Logout the user
     */
    @Override
    public void signOut() {
        mFirebaseAuth.signOut();
    }

    /**
     * Resend the verification email by logging again and getting the user
     *
     * @param email    User provided email
     * @param password User provided password
     */
    @Override
    public Observable<Boolean> resendVerificationEmail(String email, String password) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Get the firebase user
                            FirebaseUser user = task.getResult().getUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(emailTask -> {
                                        if (emailTask.isSuccessful()) {
                                            emitter.onNext(true);
                                        } else {
                                            emitter.onNext(false);
                                        }

                                        //Logout the user
                                        mFirebaseAuth.signOut();
                                    });
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
