package app.com.thetechnocafe.hirecall.Features.Todo;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.TodoModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface TodoContract {
    interface View extends BaseApp.View {
        void displayTodos(List<TodoModel> todoList, boolean isAllList);

        void onTodoCompletedChanged();
    }

    interface Presenter extends BaseApp.Presenter<TodoContract.View> {
        void reloadTodoList();

        void updateTodo(TodoModel todo);

        void loadCompletedTodos();

        void loadPendingTodos();
    }
}
