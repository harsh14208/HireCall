package app.com.thetechnocafe.hirecall.Features.CreateTodo;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.Utilities.AlarmSchedulerUtility;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class CreateTodoPresenter implements CreateTodoContract.Presenter {

    private CreateTodoContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(CreateTodoContract.View view) {
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
    public void createReminder(TodoModel todo) {
        FirebaseDB.getInstance()
                .createReminder(todo)
                .subscribe(result -> {
                    AlarmSchedulerUtility.getInstance()
                            .scheduleSimpleReminder(
                                    mView.getAppContext(),
                                    todo.getTitle(),
                                    todo.getDescription(),
                                    todo.getTime()
                            );
                    mView.reminderSetSuccessful();
                });
    }
}
