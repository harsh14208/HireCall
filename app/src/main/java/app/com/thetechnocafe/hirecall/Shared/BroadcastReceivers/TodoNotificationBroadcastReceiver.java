package app.com.thetechnocafe.hirecall.Shared.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Enums.NotificationType;
import app.com.thetechnocafe.hirecall.Enums.TodoType;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.AlarmSchedulerUtility;

import static app.com.thetechnocafe.hirecall.Enums.TodoType.INTERVIEW;

public class TodoNotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String EXTRA_NOTIFICATION_TITLE = "notification_title";
    public static final String EXTRA_NOTIFICATION_BODY = "notification_body";
    public static final String EXTRA_TODO_TYPE = "todo_type";
    public static final String EXTRA_NOTIFICATION_ID = "notification_id";
    public static final String EXTRA_NOTIFICATION_TYPE = "notification_type";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE);
        String body = intent.getStringExtra(EXTRA_NOTIFICATION_BODY);

        if (FirebaseAuthDB.getInstance().isUserLoggedIn()) {
            Log.d("TAG", title + "//" + body + "//" + intent.getStringExtra(EXTRA_TODO_TYPE));
            TodoType todoType = TodoType.valueOf(intent.getStringExtra(EXTRA_TODO_TYPE));
            NotificationType notificationType = NotificationType.valueOf(intent.getStringExtra(EXTRA_NOTIFICATION_TYPE));
            int notificationID = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);

            Log.d("TODONOTIFIACTON", todoType.toString());
            Log.d("TODONOTIFIACTON", notificationType.toString());
            switch (todoType) {
                case INTERVIEW: {
                    switch (notificationType) {
                        case INTERVIEW_8_AM: {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(new Date().getTime());
                            long startTime = calendar.getTimeInMillis();
                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                            calendar.set(Calendar.MINUTE, 59);
                            long endTime = calendar.getTimeInMillis();

                            sendInterviewNotification(context, notificationType, startTime, endTime, notificationID);

                            scheduleAll1HourNotification(context);
                            break;
                        }
                        case INTERVIEW_5_PM: {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(new Date().getTime());
                            calendar.add(Calendar.DATE, 1);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            long startTime = calendar.getTimeInMillis();
                            calendar.set(Calendar.HOUR_OF_DAY, 23);
                            calendar.set(Calendar.MINUTE, 59);
                            long endTime = calendar.getTimeInMillis();
                            sendInterviewNotification(context, notificationType, startTime, endTime, notificationID);
                            break;
                        }
                        case INTERVIEW_1_HOUR: {
                            Calendar calendar = Calendar.getInstance();
                            long startTime = calendar.getTimeInMillis();
                            calendar.add(Calendar.HOUR, 1);
                            long endTime = calendar.getTimeInMillis();
                            sendInterviewNotification(context, notificationType, startTime, endTime, notificationID);
                            break;
                        }
                    }
                    break;
                }
                default: {
                    sendNotification(context, title, body, notificationID);
                }
            }
        }
    }

    private void sendInterviewNotification(Context context, NotificationType notificationType, long startTime, long endTime, int notificationID) {
        FirebaseDB.getInstance()
                .getTodoList()
                .subscribe(todoList -> {
                    int pendingInterviewCount = 0;

                    //Filter all the todos that are interviews
                    //and
                    for (TodoModel todo : todoList) {
                        TodoType type = todo.getTodoType();
                        if (type == INTERVIEW) {
                            if (todo.getTime() >= startTime && todo.getTime() <= endTime) {
                                pendingInterviewCount++;
                            }
                        }
                    }

                    //Send notification if there are pending interviews
                    if (pendingInterviewCount > 0) {
                        //Check the format of the notification
                        String title = "";
                        switch (notificationType) {
                            case INTERVIEW_1_HOUR: {
                                title = "You have 1 interview in next hour, kindly follow up";
                                break;
                            }
                            case INTERVIEW_8_AM: {
                                title = "Good Morning, You have " + pendingInterviewCount + " interviews today, kindly follow up";
                                break;
                            }
                            case INTERVIEW_5_PM: {
                                title = "Good Evening, You have " + pendingInterviewCount + " interviews tomorrow, kindly follow up";
                                break;
                            }
                        }

                        sendNotification(context, title, "", notificationID);
                    }
                });
    }

    /**
     * Filter all the todos of that particular day
     * Group all the todos with a span of 1 hour and schedule them accordingly
     */
    private void scheduleAll1HourNotification(Context context) {
        FirebaseDB.getInstance()
                .getTodoList()
                .subscribe(todoList -> {
                    //Calculate start and end time
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(new Date().getTime());
                    long startTime = calendar.getTimeInMillis();
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    long endTime = calendar.getTimeInMillis();

                    List<TodoModel> filteredTodoList = new ArrayList<>();

                    //Filter all the todos that are interviews
                    //and
                    for (TodoModel todo : todoList) {
                        TodoType type = todo.getTodoType();
                        if (type == INTERVIEW) {
                            if (todo.getTodoType() == INTERVIEW && todo.getTime() >= startTime && todo.getTime() <= endTime && !todo.isCompleted()) {
                                filteredTodoList.add(todo);
                            }
                        }
                    }

                    //Group the todos together
                    if (filteredTodoList.size() > 0) {
                        List<TodoModel> tempTodoList = new ArrayList<>();

                        long scheduleTime = filteredTodoList.get(filteredTodoList.size() - 1).getTime();

                        for (int i = filteredTodoList.size() - 1; i >= 0; i--) {
                            Log.d("TAG", "Setting batch notifications");
                            TodoModel todo = filteredTodoList.get(i);

                            if (todo.getTime() >= scheduleTime - 3600000 && todo.getTime() <= scheduleTime) {
                                tempTodoList.add(todo);
                            } else if (todo.getTime() < (scheduleTime - 3600000)) {
                                //Grouping todos
                                //Dispatch the notification
                                AlarmSchedulerUtility.getInstance()
                                        .scheduleReminder(
                                                context,
                                                "You have " + tempTodoList.size() + " interview in next hour, kindly follow up",
                                                "",
                                                scheduleTime - 3600000,    //Scheduling one hour before the last reminder of the group
                                                (int) (Math.random() * 1000),
                                                TodoType.DEFAULT,
                                                NotificationType.DEFAULT
                                        );

                                //Empty the temp list
                                tempTodoList.clear();

                                //Add the current item
                                tempTodoList.add(todo);

                                //Update the start time
                                scheduleTime = todo.getTime();
                            }
                        }

                        //If the bag is not empty then dispatch the remaining todos
                        if (!tempTodoList.isEmpty()) {
                            //Grouping todos
                            AlarmSchedulerUtility.getInstance()
                                    .scheduleReminder(
                                            context,
                                            "You have " + tempTodoList.size() + " interview in next hour, kindly follow up",
                                            "",
                                            scheduleTime - 3600000,    //Scheduling one hour before the last reminder of the group
                                            (int) (Math.random() * 1000),
                                            TodoType.DEFAULT,
                                            NotificationType.DEFAULT
                                    );
                        }
                    }
                });
    }

    /**
     * Send the system notification with title and message
     */
    private void sendNotification(Context context, String title, String message, int notificationID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Make pending intent to launch the app
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[]{500, 250, 500, 250})
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        Log.d("NOTIFICATION ID ", notificationID + "");
        notificationManager.notify(notificationID, notification);
    }
}
