package com.gamma.gamenews.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.gamma.gamenews.R;
import com.gamma.gamenews.data.network.DataService;
import com.gamma.gamenews.data.network.deserializer.MessageDeserializer;
import com.gamma.gamenews.data.network.NetworkUtils;
import com.gamma.gamenews.utils.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText txtUser, txtPassword;
    CircularProgressButton btnLogin1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreference.init(getApplicationContext());
        checkLogin();
    }

    void checkLogin(){
        txtUser = findViewById(R.id.txt_user);
        txtPassword = findViewById(R.id.txt_password);
        btnLogin1 = findViewById(R.id.btnLogin);

        btnLogin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String user = txtUser.getText().toString();
                final String pass = txtPassword.getText().toString();

                if(user.trim().length() == 0) txtUser.setError("User is required");
                if(pass.trim().length() == 0) txtPassword.setError("Password is required");

                if(user.trim().length() >0 && pass.trim().length()>0){

                    Gson gson = new GsonBuilder().registerTypeAdapter(
                            String.class, //lo que devuelve
                            new MessageDeserializer() //lo que transforma
                    ).create();

                    btnLogin1.startAnimation();

                    DataService loginService = NetworkUtils.getClientInstance(gson);
                    Call<String> login = loginService.login(user,pass);

                    login.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()){
                                String[] data = response.body().split(":");
                                if (data[0].equals("token")) {
                                    SharedPreference.logInUser(user, data[1]);

                                    btnLogin1.doneLoadingAnimation(Color.parseColor("#6200ea")
                                            , BitmapFactory.decodeResource(getResources(), R.drawable.ic_done_white_48dp));
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            } else {
                                btnLogin1.revertAnimation();
                                try {
                                    if(response.errorBody()!=null){
                                        String error = response.errorBody().string();
                                        JSONObject object = new JSONObject(error);
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("Error")
                                                .setMessage(object.getString("message"))
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            t.printStackTrace();
                            btnLogin1.revertAnimation();
                            Snackbar.make(v,"Login failed. Try again later.",Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnLogin1.dispose();
    }
}
