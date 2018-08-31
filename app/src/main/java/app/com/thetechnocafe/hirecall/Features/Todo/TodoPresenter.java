package app.com.thetechnocafe.hirecall.Features.Todo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class TodoPresenter implements TodoContract.Presenter {

    private TodoContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(TodoContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();

        loadTodos();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    private void loadTodos() {
        Disposable disposable = FirebaseDB.getInstance()
                .getTodoList()
                .map(todoModels -> {
                    List<TodoModel> todoList = new ArrayList<>();
                    List<TodoModel> todoListCompleted = new ArrayList<>();

                    for (TodoModel todo : todoModels) {
                        if (todo.isCompleted()) {
                            todoListCompleted.add(todo);
                        } else {
                            todoList.add(todo);
                        }
                    }

                    //Sort the completed list according to completed time
                    Collections.sort(todoListCompleted, (o1, o2) -> {
                        if (o1.getCompletedTime() > o2.getCompletedTime()) {
                            return -1;
                        } else if (o1.getCompletedTime() < o2.getCompletedTime()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    todoList.addAll(todoListCompleted);
                    return todoList;
                })
                .subscribe(todoList -> {
                    mView.displayTodos(todoList, true);
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void reloadTodoList() {
        loadTodos();
    }

    @Override
    public void updateTodo(TodoModel todo) {
        Disposable disposable = FirebaseDB.getInstance()
                .changeTodoCompleted(todo)
                .subscribe(result -> {
                    mView.onTodoCompletedChanged();
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void loadCompletedTodos() {
        Disposable disposable = FirebaseDB.getInstance()
                .getTodoList()
                .map(todoModels -> {
                    List<TodoModel> todoList = new ArrayList<>();

                    for (TodoModel todo : todoModels) {
                        if (todo.isCompleted()) {
                            todoList.add(todo);
                        }
                    }

                    return todoList;
                })
                .subscribe(todoList -> {
                    mView.displayTodos(todoList, false);
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void loadPendingTodos() {
        Disposable disposable = FirebaseDB.getInstance()
                .getTodoList()
                .map(todoModels -> {
                    List<TodoModel> todoList = new ArrayList<>();

                    for (TodoModel todo : todoModels) {
                        if (!todo.isCompleted()) {
                            todoList.add(todo);
                        }
                    }

                    return todoList;
                })
                .subscribe(todoList -> {
                    mView.displayTodos(todoList, false);
                });

        mCompositeDisposable.add(disposable);
    }
}
