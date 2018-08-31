package app.com.thetechnocafe.hirecall.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import app.com.thetechnocafe.hirecall.Models.SimplifiedCallLogModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gurleen on 20/4/17.
 */

public class PhoneCallContactUtility {
    private static PhoneCallContactUtility sInstance;
    private Map<String, String> numberToNameMap;

    public static PhoneCallContactUtility getInstance() {
        if (sInstance == null) {
            sInstance = new PhoneCallContactUtility();
        }
        return sInstance;
    }

    private PhoneCallContactUtility() {
    }

    public SimplifiedCallLogModel getLastCallDetails(Context context, String targetNumber) {
        SimplifiedCallLogModel callLog = new SimplifiedCallLogModel();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.TYPE + "=" + CallLog.Calls.OUTGOING_TYPE, null, null);

        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);

        if (cursor.getColumnCount() > 0) {
            cursor.moveToLast();

            String phoneNumber = cursor.getString(number);
            int callDuration = Integer.parseInt(cursor.getString(duration));
            long callDate = Long.parseLong(cursor.getString(date));

            Log.d("CALL DETAILS", phoneNumber + " - " + callDuration + " - " + callDate);

            if (phoneNumber.startsWith("+91")) {
                phoneNumber = phoneNumber.substring(3);
            }

            if (phoneNumber.equals(targetNumber)) {
                Log.d("Call Duration", callDuration + "");
                callLog.setNumber(phoneNumber);
                callLog.setDuration(callDuration);
                callLog.setCallDate(callDate);
            } else {
                callLog = null;
            }
        }

        return callLog;
    }

    public String covertNumberToName(Context context, String number) {
        if (numberToNameMap == null) {
            return number;
        }

        String name = numberToNameMap.get(number);

        if (name == null) {
            return number;
        } else {
            return name;
        }
    }

    private void createNumberToNameMapping(Context context) throws Exception {
        numberToNameMap = new HashMap<>();

        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            //Check if phone number are present
            if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor phoneNumberQuery = context.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null
                );

                if (phoneNumberQuery.getCount() > 0) {

                    phoneNumberQuery.moveToFirst();

                    //Iterate and put all the n number in map
                    do {
                        String contactNumber = phoneNumberQuery.getString(phoneNumberQuery.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if (contactNumber.startsWith("+91") && contactNumber.length() > 10) {
                            contactNumber = contactNumber.substring(3);
                        }
                        numberToNameMap.put(contactNumber, name);
                    } while (phoneNumberQuery.moveToNext());
                }

                phoneNumberQuery.close();
            }
        }

        cursor.close();
    }

    public void prepareContactsMap(Context context) {
        Observable<Void> observable = Observable.create(observableEmitter -> {
            createNumberToNameMapping(context);
        });

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                }, Throwable::printStackTrace);
    }
}
