package app.com.thetechnocafe.hirecall.Features.Todo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import app.com.thetechnocafe.hirecall.Enums.TodoType;
import app.com.thetechnocafe.hirecall.Features.CreateTodo.CreateTodoActivity;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TodoFragment extends Fragment implements TodoContract.View {

    @BindView(R.id.todo_recycler_view)
    RecyclerView mTodoRecyclerView;
    @BindView(R.id.add_reminder_floating_action_button)
    FloatingActionButton mAddReminderFloatingActionButton;
    @BindView(R.id.select_type_spinner)
    Spinner mSelectTypeSpinner;
    @BindView(R.id.no_task_text_view)
    TextView mNoTaskTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private TodoContract.Presenter mPresenter;
    private static final int RC_TODO_CREATED = 1;
    private static String[] TODO_OPTIONS;
    private ProgressDialog mProgressDialog;

    //Instance method
    public static TodoFragment getInstance() {
        return new TodoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        ButterKnife.bind(this, view);

        TODO_OPTIONS = getResources().getStringArray(R.array.todo_type_options);

        mPresenter = new TodoPresenter();
            mPresenter.subscribe(this);

        initViews();

        return view;
    }

    private void initViews() {
        mTodoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mTodoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mAddReminderFloatingActionButton.hide();
                } else if (dy < 0) {
                    mAddReminderFloatingActionButton.show();
                }
            }
        });

        mAddReminderFloatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CreateTodoActivity.class);
            startActivityForResult(intent, RC_TODO_CREATED);
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, TODO_OPTIONS);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectTypeSpinner.setAdapter(arrayAdapter);
        mSelectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reloadTodos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            reloadTodos();
        });
    }

    private void reloadTodos() {
        switch (TODO_OPTIONS[mSelectTypeSpinner.getSelectedItemPosition()]) {
            case "All": {
                mPresenter.reloadTodoList();
                break;
            }
            case "Todo": {
                mPresenter.loadPendingTodos();
                break;
            }
            case "Done": {
                mPresenter.loadCompletedTodos();
                break;
            }
        }
    }

    private void startProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public Context getAppContext() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public void displayTodos(List<TodoModel> todoList, boolean isAllList) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (todoList.size() > 0) {
            mNoTaskTextView.setVisibility(View.GONE);
            mTodoRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mNoTaskTextView.setVisibility(View.VISIBLE);
            mTodoRecyclerView.setVisibility(View.GONE);
        }

        TodoRecyclerAdapter adapter = new TodoRecyclerAdapter(getContext(), todoList, isAllList);
        adapter.setOnTodoActionListener(new TodoRecyclerAdapter.OnTodoActionListener() {
            @Override
            public void onTodoCheckedChanged(TodoModel todo) {
                startProgressDialog("Changing todo status...");

                //Set the completed time and type
                todo.setCompletedTime(new Date().getTime());
                todo.setTodoType(TodoType.DEFAULT);

                mPresenter.updateTodo(todo);
            }
        });
        mTodoRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onTodoCompletedChanged() {
        reloadTodos();
        stopProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_TODO_CREATED: {
                if (resultCode == Activity.RESULT_OK) {
                    mPresenter.reloadTodoList();
                }
                break;
            }
        }
    }
}
