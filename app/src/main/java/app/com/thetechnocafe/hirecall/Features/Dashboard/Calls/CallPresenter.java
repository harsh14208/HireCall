package app.com.thetechnocafe.hirecall.Features.Dashboard.Calls;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class CallPresenter implements CallContract.Presenter {

    private CallContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(CallContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();

        loadCallLogs();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    private void loadCallLogs() {
        Disposable disposable = FirebaseDB.getInstance()
                .getListOfAllCallLogs()
                .subscribe(callLogModels -> {
                    mView.onCallLogsFetched(callLogModels);
                });

        mCompositeDisposable.add(disposable);
    }
}
