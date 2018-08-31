package app.com.thetechnocafe.hirecall.Features.Calls;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class CallHistoryPresenter implements CallHistoryContract.Presenter {

    private CallHistoryContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(CallHistoryContract.View view) {
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

}
