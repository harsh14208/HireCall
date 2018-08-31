package app.com.thetechnocafe.hirecall.Features.AddPeople;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CompanyHeadModel;
import app.com.thetechnocafe.hirecall.R;

/**
 * Created by rvkmr on 27-10-2017.
 */

public class AddPeopleAdapter extends ArrayAdapter<String> {
    Context context;
    int resource;
    List<String> objects;
    String domain;

    public AddPeopleAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects, String domain) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.domain = domain;
    }

    public static class ViewHolder {
        TextView tvAddPeopleCompany;
        ImageView ivRemoveHead;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String model = objects.get(position);
        AddPeopleAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new AddPeopleAdapter.ViewHolder();
            holder.tvAddPeopleCompany = (TextView) convertView.findViewById(R.id.tv_company_name);
            holder.ivRemoveHead = (ImageView) convertView.findViewById(R.id.iv_remove_head);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        holder.tvAddPeopleCompany.setText(model);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (holder.ivRemoveHead.getVisibility() != View.VISIBLE)
                    holder.ivRemoveHead.setVisibility(View.VISIBLE);
                return false;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.ivRemoveHead.getVisibility() == View.VISIBLE)
                    holder.ivRemoveHead.setVisibility(View.GONE);
            }
        });
        holder.ivRemoveHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> headList = new ArrayList<>();
                CompanyHeadModel headModel = new CompanyHeadModel();
                headList.add(model);
                headModel.setDomain(domain);
                headModel.setHeads(headList);
                Gson gson = new Gson();
                try {
                    JSONObject json = new JSONObject(gson.toJson(headModel));
                    RequestQueue request = Volley.newRequestQueue(context);
                    JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, AddPeopleActivity.REMOVE_HEADS_FROM_A_DOMAIN_URL, json, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if(jsonObject.getString("nModified").equalsIgnoreCase("1")){
                                    Toast.makeText(context,"Removed Sucessfully",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    });
                    request.add(jRequest);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

}
