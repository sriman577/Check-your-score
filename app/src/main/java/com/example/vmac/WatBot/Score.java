package com.example.vmac.WatBot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Score extends AppCompatActivity {

//    private com.android.volley.RequestQueue mQueue;
//    double riskScore=0.2;
//    int activeCases;
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad,tvriskScore;
    boolean flag=true;

    List<LocationsData> list=new ArrayList<>();
    HashSet<String> set=new HashSet<>();
    String email=ApplicationClass.user.getEmail();
    double riskFactor=0.2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        tvriskScore=findViewById(R.id.tvriskScore);

//        mQueue = Volley.newRequestQueue(this);
//        jsonparse();
        showProgress(true);
        DataQueryBuilder queryBuilder;
        queryBuilder = DataQueryBuilder.create();
        String whereClause = "location='"+ ApplicationClass.Location +"'";
        queryBuilder.setWhereClause(whereClause);
        Backendless.Persistence.of(LocationsData.class).find(queryBuilder, new AsyncCallback<List<LocationsData>>() {
            @Override
            public void handleResponse(List<LocationsData> response) {
                list = response;
                // list variable contains the all the objects;
                for (int i = 0; i < list.size(); i++) {
                    String email2 = list.get(i).getUserEmail();
                    if (!email2.equals(email) && !set.contains(email2)) {
                        set.add(email2);
                        riskFactor = riskFactor + list.get(i).getRiskFactor();
                    }
                }
                riskFactor /= set.size();
                Log.i("riskFactor", "" + riskFactor);
                DataQueryBuilder queryBuilder;
                queryBuilder = DataQueryBuilder.create();
                String whereClause = "location='" + ApplicationClass.Location + "'";
                queryBuilder.setWhereClause(whereClause);
                Backendless.Persistence.of(RiskFactorTable.class).find(queryBuilder, new AsyncCallback<List<RiskFactorTable>>() {
                    @Override
                    public void handleResponse(List<RiskFactorTable> response) {
                        riskFactor = Math.max(riskFactor, response.get(0).getRiskFactor());
                        Log.i("riskfactor", "" + response.get(0).getRiskFactor());
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        //do Nothing
                    }
                });
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Score.this,fault.toString(),Toast.LENGTH_LONG).show();
                flag=false;
                showProgress(false);
            }
        });
        showProgress(true);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                tvriskScore.setText("Your Risk Score is -"+ riskFactor);
                if(Double.compare(riskFactor, 0.3)<=0)
                {
                    tvriskScore.setTextColor(Color.GREEN);
                }
                else if(Double.compare(riskFactor, 0.65)>0)
                {
                    tvriskScore.setTextColor(Color.RED);
                }
                else if(Double.compare(riskFactor, 0.5)>0)
                {
                    tvriskScore.setTextColor(Color.MAGENTA);
                }
                else
                {
                    tvriskScore.setTextColor(Color.YELLOW);
                }
                if(flag) createRecord();

            }
        }, 1000);



    }

    public void createRecord()
    {
        LocationsData loc=new LocationsData();
        loc.setUserEmail(ApplicationClass.user.getEmail());
        loc.setLocation(ApplicationClass.Location);
        loc.setRiskFactor(riskFactor);
        Backendless.Persistence.of(LocationsData.class).save(loc, new AsyncCallback<LocationsData>() {
            @Override
            public void handleResponse(LocationsData response) {
                Toast.makeText(Score.this,"Location Fetched",Toast.LENGTH_SHORT).show();
                showProgress(false);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showProgress(false);
            }
        });


    }

//    private void jsonparse()
//    {
//        Log.i("button", "button clicked");
//        String url="https://api.covid19india.org/state_district_wise.json";
//        final JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                //                    JSONArray jsonArray=response.getJSONArray("Andhra Pradesh");
//                try {
//                    JSONObject s= (JSONObject)response.get();
//                    Log.i("Ap",s.toString());
//                    JSONObject distd= (JSONObject) s.get("districtData");
//                    JSONObject dis = (JSONObject) distd.get("West Godavari");
//                    activeCases=dis.getInt("active");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//        mQueue.add(request);
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.removeItem(R.id.refresh);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            Backendless.UserService.logout(new AsyncCallback<Void>() {
                public void handleResponse(Void response) {
                    Toast.makeText(Score.this, "logout successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Score.this, Login.class));
                    Score.this.finish();
                }

                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(Score.this, "Error " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });
        } else {

            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
        }
    }

}
