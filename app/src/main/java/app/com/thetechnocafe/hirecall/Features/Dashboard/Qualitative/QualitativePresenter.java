package app.com.thetechnocafe.hirecall.Features.Dashboard.Qualitative;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class QualitativePresenter implements QualitativeContract.Presenter {

    private QualitativeContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(QualitativeContract.View view) {
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
