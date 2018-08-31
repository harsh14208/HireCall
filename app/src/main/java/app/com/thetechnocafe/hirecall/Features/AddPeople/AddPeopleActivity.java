package app.com.thetechnocafe.hirecall.Features.AddPeople;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CompanyHeadModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddPeopleActivity extends AppCompatActivity {

    @BindView(R.id.add_people_toolbar)
    Toolbar mAddPeopleToolBar;
    @BindView(R.id.et_add_people_company)
    TextInputEditText etAddPeopleCompany;
    @BindView(R.id.lv_add_people_list)
    ListView lvAddCompanyHeads;
    @BindView(R.id.iv_show_people_list)
    ImageView ivShowPeopleList;
    @BindView(R.id.bt_add_people)
    Button btAddPeople;
    @BindView(R.id.ll_add_people_name)
    LinearLayout llAddPeopleName;
    @BindView(R.id.et_add_people_name)
    TextInputEditText etAddPeopleName;
    @BindView(R.id.bt_add_people_name)
    Button btAddPeopleName;
    @BindView(R.id.iv_create_people)
    ImageView ivCreatePeople;
    @BindView(R.id.add_people_linear_layout)
    LinearLayout mAddPeopleLinearLayout;

    public static String GET_HEADS_OF_A_DOMAIN_URL = "http://13.71.116.40:4040/getHeadsOfDomain.php";
    public static String REMOVE_HEADS_FROM_A_DOMAIN_URL = "http://13.71.116.40:4040/removeHeadsOfDomain.php";
    public static String ADD_HEADS_TO_A_DOMAIN_URL = "http://13.71.116.40:4040/addHeadsOfDomain.php";
    public static String CREATE_HEADS_TO_A_DOMAIN_URL = "http://13.71.116.40:4040/createHeadsOfDomain.php";

    String domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_add_heads);
        ButterKnife.bind(this);
        initViews();

        ivShowPeopleList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btAddPeople.setVisibility(View.VISIBLE);
                lvAddCompanyHeads.setVisibility(View.VISIBLE);
                llAddPeopleName.setVisibility(View.GONE);
                try {
                    List<String> headsList = new ArrayList<>();
                    domain = etAddPeopleCompany.getText().toString().trim();
                    JSONObject obj = new JSONObject();
                    obj.put("domain", domain);
                    Log.d("Json_get", obj.toString());
                    RequestQueue queue = Volley.newRequestQueue(AddPeopleActivity.this);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, GET_HEADS_OF_A_DOMAIN_URL, obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d("Json _response", jsonObject.toString());

                            try {
                                JSONArray array = jsonObject.getJSONArray("data");
                                if (array.length() == 0) {
                                    Snackbar.make(mAddPeopleLinearLayout, "NO people in the List", Snackbar.LENGTH_SHORT);
                                } else {
                                    for (int i = 0; i < array.length(); i++) {
                                        headsList.add(array.getString(i));
                                        Log.d("data_array", array.getString(i));
                                        Log.d("HeadsList", "" + headsList.size());

                                        AddPeopleAdapter adapter = new AddPeopleAdapter(AddPeopleActivity.this, R.layout.item_company_head, headsList, domain);
                                        lvAddCompanyHeads.setAdapter(adapter);
                                    }

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
                    request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    queue.add(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btAddPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llAddPeopleName.setVisibility(View.VISIBLE);
                lvAddCompanyHeads.setVisibility(View.GONE);
            }

        });

        btAddPeopleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CompanyHeadModel model = new CompanyHeadModel();
                ArrayList<String> names = new ArrayList<>();
                model.setDomain(etAddPeopleCompany.getText().toString().trim());
                names.add(etAddPeopleName.getText().toString().trim());
                model.setHeads(names);
                try {
                    uploadData(model, ADD_HEADS_TO_A_DOMAIN_URL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        ivCreatePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(AddPeopleActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_create_heads);

                TextInputEditText etDialogDomain = (TextInputEditText) dialog.findViewById(R.id.et_dilaog_domain);
                TextInputEditText etDialogName = (TextInputEditText) dialog.findViewById(R.id.et_dilaog_name);
                Button btDialogCreateDomain = (Button) dialog.findViewById(R.id.bt_dialog_create_domian);

                btDialogCreateDomain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CompanyHeadModel model = new CompanyHeadModel();
                        ArrayList<String> heads = new ArrayList<>();
                        model.setDomain(etDialogDomain.getText().toString().trim());
                        heads.add(etDialogName.getText().toString().trim());
                        model.setHeads(heads);
                        try {
                            uploadData(model, CREATE_HEADS_TO_A_DOMAIN_URL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });

    }

    private void uploadData(CompanyHeadModel model, String URL) throws JSONException {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        JSONObject obj = new JSONObject(json);
        Log.d("json_post", "" + obj.toString());
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, URL, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    if (URL.equalsIgnoreCase(ADD_HEADS_TO_A_DOMAIN_URL))
                        if (jsonObject.getString("nModified").equalsIgnoreCase("1"))
                            Snackbar.make(mAddPeopleLinearLayout, "Added Successfully", Snackbar.LENGTH_SHORT).show();

                        else
                            Snackbar.make(mAddPeopleLinearLayout, "Created Successfully", Snackbar.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("Volley_error", "" + volleyError.toString());

                    }
                });
        queue.add(req);
    }


    private void initViews() {
        setSupportActionBar(mAddPeopleToolBar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
