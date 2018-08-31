package app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by stark on 15/6/17.
 */

public class AnalysisPresenter implements AnalysisContract.Presenter {

        private AnalysisContract.View mView;
        private CompositeDisposable mCompositeDisposable;

        @Override
        public void subscribe(AnalysisContract.View view) {
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
