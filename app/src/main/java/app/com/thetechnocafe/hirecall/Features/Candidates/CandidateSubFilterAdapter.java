package app.com.thetechnocafe.hirecall.Features.Candidates;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CandidateSubFilterModel;
import app.com.thetechnocafe.hirecall.R;

/**
 * Created by rvkmr on 17-10-2017.
 */

public class CandidateSubFilterAdapter extends ArrayAdapter {
    Context context;
    int resource;
    List<CandidateSubFilterModel> objects;
    ArrayList<String> checkedValue = new ArrayList<>();
    ArrayList<String> popSubFilters = new ArrayList<>();
    String subFilter = "";
    String popupSubFilter = "";
    private String[] INTERVIEW_OPTIONS, INTERVIEW_SELECT, INTERVIEW_REJECT;


    public CandidateSubFilterAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CandidateSubFilterModel> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        INTERVIEW_OPTIONS = context.getResources().getStringArray(R.array.confirmation_round);
        INTERVIEW_SELECT = context.getResources().getStringArray(R.array.interview_select_options);
        INTERVIEW_REJECT = context.getResources().getStringArray(R.array.interview_reject_options);
    }

    public List<String> getSubFilter() {
        return checkedValue;
    }

    public class ViewHolder {
        TextView tvCandidateSubFilter;
        CheckBox cbCandidateSubFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CandidateSubFilterModel model = objects.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvCandidateSubFilter = (TextView) convertView.findViewById(R.id.tv_candidate_sub_filter);
            holder.cbCandidateSubFilter = (CheckBox) convertView.findViewById(R.id.cb_candidate_sub_filter);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvCandidateSubFilter.setText(model.getSubFilterName());
        holder.cbCandidateSubFilter.setChecked(model.getSubFilterChecked());
        settingClickListeners(convertView, model);

        return convertView;
    }

    private void settingClickListeners(View convertView, CandidateSubFilterModel model) {
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_candidate_sub_filter);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subFilter = model.getSubFilterName();

                if (subFilter.equalsIgnoreCase("pending")) {
                    setPopUpWindow(INTERVIEW_OPTIONS, cb, model);
                } else if (subFilter.equalsIgnoreCase("Interview Reject")) {
                    setPopUpWindow(INTERVIEW_REJECT, cb, model);
                } else if (subFilter.equalsIgnoreCase("Interview Select"))
                    setPopUpWindow(INTERVIEW_SELECT, cb, model);
                else
                    itemIsChecked(cb, model);
            }
        });


    }

    private void setPopUpWindow(String[] interview_options, CheckBox cb, CandidateSubFilterModel model) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_sub_filter, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView, ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.MATCH_PARENT);

        ListView lvPopUpSubFilter = (ListView) popupView.findViewById(R.id.lv_popup_sub_filter);
        Button btPopUpSubFilterAdd = (Button) popupView.findViewById(R.id.bt_popup_sub_filter_add);
        Button btPopUpSubFilterCancel = (Button) popupView.findViewById(R.id.bt_popup_sub_filter_cancel);

        String[] subfilterOptions = Arrays.copyOfRange(interview_options, 1, interview_options.length - 1);
        CandidateSubFilterPopUpAdapter adapter = new CandidateSubFilterPopUpAdapter(context, R.layout.item_list_view_candidate_sub_filter,
                Arrays.asList(subfilterOptions));
        lvPopUpSubFilter.setAdapter(adapter);
        popSubFilters = adapter.getPopSubFilters();


        btPopUpSubFilterAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cb.isChecked()) {
                    cb.setChecked(true);
                    checkedValue.add(model.getSubFilterName());
                }
                popupWindow.dismiss();
            }
        });
        btPopUpSubFilterCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(cb, Gravity.TOP, 0, 0);
    }



    private void itemIsChecked(CheckBox cb, CandidateSubFilterModel model) {
        if (cb.isChecked()) {
            checkedValue.add(model.getSubFilterName());
            subFilter = model.getSubFilterName();
        } else {
            checkedValue.remove(model.getSubFilterName());
            subFilter = "";
        }
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    public ArrayList<String> getPopSubFilters() {
        return popSubFilters;
    }

}
