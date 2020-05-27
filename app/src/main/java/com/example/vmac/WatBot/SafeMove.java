package com.example.vmac.WatBot;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class SafeMove extends AppCompatActivity {
    TextView tvtext,tvtype;
    EditText etloc;
    Button btnsubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_move);
        tvtext=findViewById(R.id.tvtext);
        etloc=findViewById(R.id.etloc);
        btnsubmit=findViewById(R.id.btnsubmit);
        tvtype=findViewById(R.id.tvtype);


        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location=etloc.getText().toString().trim();
                if(location.isEmpty())
                {
                    Toast.makeText(SafeMove.this,"Please Enter the location",Toast.LENGTH_SHORT).show();
                }
                else {
                    location = location.toLowerCase();
                    location= (""+location.charAt(0)).toUpperCase()+location.substring(1);
                    DataQueryBuilder queryBuilder;
                    queryBuilder = DataQueryBuilder.create();
                    String whereClause = "location='" +location +"'";
                    queryBuilder.setWhereClause(whereClause);
                    Backendless.Persistence.of(RiskFactorTable.class).find(queryBuilder, new AsyncCallback<List<RiskFactorTable>>() {
                        @Override
                        public void handleResponse(List<RiskFactorTable> response) {
                            RiskFactorTable entry=response.get(0);
                            double riskFactor=entry.getRiskFactor();
                            if(Double.compare(riskFactor, 0.3)<=0)
                            {
                                tvtype.setText("Normal");
                                tvtype.setTextColor(Color.GREEN);
                            }
                            else if(Double.compare(riskFactor, 0.65)>0)
                            {
                                tvtype.setText("Extreme Danger");
                                tvtype.setTextColor(Color.RED);
                            }
                            else if(Double.compare(riskFactor, 0.5)>0)
                            {
                                tvtype.setText("Danger");
                                tvtype.setTextColor(Color.MAGENTA);
                            }
                            else
                            {
                                tvtype.setText("Warning");
                                tvtype.setTextColor(Color.YELLOW);
                            }
                        }
                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Toast.makeText(SafeMove.this,fault.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
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
                    Toast.makeText(SafeMove.this, "logout successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SafeMove.this, Login.class));
                    SafeMove.this.finish();
                }

                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(SafeMove.this, "Error " + fault.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

}