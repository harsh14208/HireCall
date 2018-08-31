package app.com.thetechnocafe.hirecall.Features.JobDetailsDialNumber;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class JobDetailsDialNumberPresenter implements JobDetailsDialNumberContract.Presenter {

    private JobDetailsDialNumberContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    List<List<CallLogModel>> temp= new ArrayList<List<CallLogModel>>();
    List<CallLogModel> mCallLog = new ArrayList<CallLogModel>();
    List<String> mDomain = new ArrayList<String>();
    List<String> mClient = new ArrayList<String>();



    @Override
    public void subscribe(JobDetailsDialNumberContract.View view) {
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
    public void searchForNumber(String number, String client, String jobID) {
        String domain = SharedPreferencesUtility.getInstance().getDomain(mView.getAppContext());

        String convertedNumber = PhoneCallContactUtility.getInstance().covertNumberToName(mView.getAppContext(), number);
        if (!convertedNumber.equals(number)) {
            mView.changeNumberToName(convertedNumber);
        }

        Disposable disposable = FirebaseDB.getInstance()
                .getListOfCallLogs(number)
                .map(lists -> {
                    temp=lists;
                    List<List<CallLogModel>> completeCallLogs = new ArrayList<>();

                    for (List<CallLogModel> callLogList : lists) {
                        for (CallLogModel temp : callLogList) {
                            if(!mDomain.contains(temp.getDomain())) {
                                mDomain.add(temp.getDomain());
                            }
                            if(!mClient.contains(temp.getClient())){
                                mClient.add(temp.getClient());
                            }
                        }

                    }




                    for (List<CallLogModel> callLogList : lists) {
                        List<CallLogModel> callLogModelList = new ArrayList<>();

                        //    CallLogModel callLog= callLogList.get(0);
                        //This loop adds only the jobs with the requested jobID
                        //    for (CallLogModel callLog : callLogList) {
                        //      if (callLog.getJobID().equals(jobID)) {
                        //        callLog.setDisplayName(PhoneCallContactUtility.getInstance()
                        //              .covertNumberToName(mView.getAppContext(), callLog.getPhoneNumber()));
                        //    callLogModelList.add(callLog);
                        //  }

                        List<CallLogModel> uniquelist = new ArrayList<CallLogModel>();

                        Log.e("domain added-", mDomain.toString());
                        Log.e("Client added-", mClient.toString());


                        for (CallLogModel callLogs : callLogList) {
                            for (String cli : mClient) {
                                if (callLogs.getClient().equals(cli)) {
                                    if(!uniquelist.contains(callLogs))
                                    {
                                        uniquelist.add(callLogs);
                                    }
                                    callLogs.setDisplayName(PhoneCallContactUtility.getInstance()
                                            .covertNumberToName(mView.getAppContext(), callLogs.getPhoneNumber()));
                                    //    Log.e("client added-", callLogs.getClient());
                                    int index = mClient.indexOf(cli);
                                    mClient.set(index, "0");

                                }
                            }
                            for (String dom : mDomain) {
                                if (callLogs.getDomain().equals(dom)) {
                                    if(!uniquelist.contains(callLogs))
                                    {
                                        uniquelist.add(callLogs);
                                    }
                                    callLogs.setDisplayName(PhoneCallContactUtility.getInstance()
                                            .covertNumberToName(mView.getAppContext(), callLogs.getPhoneNumber()));
                                    int index = mDomain.indexOf(dom);
                                    mDomain.set(index, "0");
                                }
                            }

                        }



                        //       if (!callLog.getDomain().equals(domain) && callLog.getClient().equals(client)) {
                        //        callLog.setDisplayName(PhoneCallContactUtility.getInstance()
                          //              .covertNumberToName(mView.getAppContext(), callLog.getPhoneNumber()));
                            //    callLogModelList.add(callLog);
                           // }
                      //  }


              //          if (callLogModelList.size() > 0) {
                            completeCallLogs.add(uniquelist);
                //        }
                    }
                    return completeCallLogs;
                })
                .subscribe(callLogs -> {
                    for(List<CallLogModel> callLog: temp){
                        mCallLog.addAll(callLog);
                    }
                   // Log.e("allCallLogs:  ",mCallLog.toString());
                        mView.setCallLogs(mCallLog);
                        mView.onCallLogsFetched(callLogs, domain);
                });

        mCompositeDisposable.add(disposable);
    }
}
