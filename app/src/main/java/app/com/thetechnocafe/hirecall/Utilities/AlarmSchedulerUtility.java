package app.com.thetechnocafe.hirecall.Utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import app.com.thetechnocafe.hirecall.Enums.NotificationType;
import app.com.thetechnocafe.hirecall.Enums.TodoType;
import app.com.thetechnocafe.hirecall.Shared.BroadcastReceivers.TodoNotificationBroadcastReceiver;

/**
 * Created by gurleen on 5/5/17.
 */

public class AlarmSchedulerUtility {
    private static final AlarmSchedulerUtility sInstance = new AlarmSchedulerUtility();
    private static final int RC_SIMPLE_NOTIFICATION_ID = 1;
    private static final int RC_INTERVIEW_ID = 2;
    private static final int RC_INTERVIEW_8_AM = 3;
    private static final int RC_INTERVIEW_5_PM = 4;

    public static AlarmSchedulerUtility getInstance() {
        return sInstance;
    }

    private AlarmSchedulerUtility() {
    }

    public void scheduleReminder(Context context, String title, String body, long time, int requestCode, TodoType todoType, NotificationType notificationType) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TodoNotificationBroadcastReceiver.class);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_TITLE, title);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_BODY, body);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_ID, requestCode);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_TYPE, notificationType.toString());
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_TODO_TYPE, todoType.toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

        Log.d("Setting Notification", "Current Time : " + new Date().getTime()
                + "/Notification set for : " + time
                + "////REquest Code :" + requestCode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    public void scheduleSimpleReminder(Context context, String title, String body, long time) {
        scheduleReminder(context, title, body, time, (int) (Math.random() * 10000), TodoType.DEFAULT, NotificationType.DEFAULT);
    }

    public void scheduleReminderForInterview(Context context, String title, String body, long time) {
        //Schedule a notification 1 hour before the interview
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.HOUR, -1);
        long oneHourBeforeTime = calendar.getTimeInMillis();

        calendar.setTimeInMillis(new Date().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        long lastTimeOfDay = calendar.getTimeInMillis();

        if (oneHourBeforeTime < lastTimeOfDay) {
            Log.d("TAG", "Scheduling one hour notification");
            scheduleReminder(context, title, body, oneHourBeforeTime, (int) (Math.random() * 10000), TodoType.INTERVIEW, NotificationType.INTERVIEW_1_HOUR);
        }
    }

    public void scheduleRepeatedAlarms(Context context) {
        scheduleRepeatedAlarmFor8AM(context);
        scheduleRepeatedAlarmFor5PM(context);
    }

    /**
     * Schedule repeated alarms for 8 AM everyday
     */
    public void scheduleRepeatedAlarmFor8AM(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TodoNotificationBroadcastReceiver.class);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_TITLE, "8 AM daily reminder");
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_BODY, "Please check pending interviews (8AM)");
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_ID, RC_INTERVIEW_8_AM);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_TYPE, NotificationType.INTERVIEW_8_AM.toString());
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_TODO_TYPE, TodoType.INTERVIEW.toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RC_INTERVIEW_8_AM, intent, PendingIntent.FLAG_ONE_SHOT);

        //Calculate time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Cancel existing alarms
        alarmManager.cancel(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    /**
     * Schedule repeated alarms for 5 PM everyday
     */
    public void scheduleRepeatedAlarmFor5PM(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, TodoNotificationBroadcastReceiver.class);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_TITLE, "5 PM daily reminder");
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_BODY, "Please check pending interviews (5 PM)");
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_ID, RC_INTERVIEW_5_PM);
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_NOTIFICATION_TYPE, NotificationType.INTERVIEW_5_PM.toString());
        intent.putExtra(TodoNotificationBroadcastReceiver.EXTRA_TODO_TYPE, TodoType.INTERVIEW.toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RC_INTERVIEW_5_PM, intent, PendingIntent.FLAG_ONE_SHOT);

        //Calculate time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        //Cancel existing alarms
        alarmManager.cancel(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }
}
