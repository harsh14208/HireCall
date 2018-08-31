package app.com.thetechnocafe.hirecall.Features.Candidates;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CandidateSubFilterModel;
import app.com.thetechnocafe.hirecall.R;

/**
 * Created by rvkmr on 22-10-2017.
 */


public class CandidateSubFilterPopUpAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;
    List<String> objects;
    ArrayList<String> checkedValue = new ArrayList<>();
    String subFilter = "";
    String popupSubFilter = "";
    private String[] INTERVIEW_OPTIONS, INTERVIEW_SELECT, INTERVIEW_REJECT;
    private ArrayList<String> popSubFilters=new ArrayList<>();

    public CandidateSubFilterPopUpAdapter(@NonNull Context context, @LayoutRes int resource,
                                          @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        INTERVIEW_OPTIONS = context.getResources().getStringArray(R.array.confirmation_round);
        INTERVIEW_SELECT = context.getResources().getStringArray(R.array.interview_select_options);
        INTERVIEW_REJECT = context.getResources().getStringArray(R.array.interview_reject_options);
    }


    public class ViewHolder {
        TextView tvCandidateSubFilter;
        CheckBox cbCandidateSubFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CandidateSubFilterPopUpAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new CandidateSubFilterPopUpAdapter.ViewHolder();
            holder.tvCandidateSubFilter = (TextView) convertView.findViewById(R.id.tv_candidate_sub_filter);
            holder.cbCandidateSubFilter = (CheckBox) convertView.findViewById(R.id.cb_candidate_sub_filter);
            convertView.setTag(holder);
        } else {
            holder = (CandidateSubFilterPopUpAdapter.ViewHolder) convertView.getTag();
        }

        holder.tvCandidateSubFilter.setText(objects.get(position));
        holder.cbCandidateSubFilter.setTag(position);
        if(popSubFilters.contains(objects.get(position)))
            holder.cbCandidateSubFilter.setChecked(true);
        else
            holder.cbCandidateSubFilter.setChecked(false);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popSubFilters.contains(objects.get(position))) {
                    popSubFilters.remove(objects.get(position));
                    holder.cbCandidateSubFilter.setChecked(false);
                } else {
                    holder.cbCandidateSubFilter.setChecked(true);
                    if (!popSubFilters.contains(objects.get(position)))
                    popSubFilters.add(objects.get(position));
                }
            }
        });

        holder.cbCandidateSubFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (!popSubFilters.contains(objects.get((int) compoundButton.getTag())))
                        popSubFilters.add(objects.get((int) compoundButton.getTag()));
                    Log.v("on_checked", "" + b + popSubFilters.size());

                } else {

                    if (popSubFilters.contains(objects.get((int) compoundButton.getTag())))
                        popSubFilters.remove(objects.get((int) compoundButton.getTag()));
                    Log.v("on_checked", "" + b + popSubFilters.size());

                }
            }
        });
        return convertView;
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    public ArrayList<String> getPopSubFilters() {
        return popSubFilters;
    }
}
