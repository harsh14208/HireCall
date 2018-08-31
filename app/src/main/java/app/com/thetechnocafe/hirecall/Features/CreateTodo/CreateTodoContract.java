package app.com.thetechnocafe.hirecall.Features.CreateTodo;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.TodoModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface CreateTodoContract {
    interface View extends BaseApp.View {
        void reminderSetSuccessful();
    }

    interface Presenter extends BaseApp.Presenter<CreateTodoContract.View> {
        void createReminder(TodoModel todo);
    }
}
