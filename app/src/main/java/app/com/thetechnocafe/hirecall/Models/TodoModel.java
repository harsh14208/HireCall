package app.com.thetechnocafe.hirecall.Models;

import com.google.firebase.database.Exclude;

import app.com.thetechnocafe.hirecall.Enums.TodoType;

/**
 * Created by gurleen on 2/5/17.
 */

public class TodoModel {
    @Exclude
    private String id;
    private String title;
    private String description;
    private long time;
    private boolean isCompleted;
    private String number;
    private TodoType todoType;
    private long completedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean complete) {
        isCompleted = complete;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public TodoType getTodoType() {
        return todoType;
    }

    public void setTodoType(TodoType todoType) {
        this.todoType = todoType;
    }

    public long getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(long completedTime) {
        this.completedTime = completedTime;
    }
}
