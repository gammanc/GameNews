package com.gamma.gamenews;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.gamma.gamenews.Utils.Client;
import com.gamma.gamenews.Utils.DataService;
import com.gamma.gamenews.Utils.LoginDeserializer;
import com.gamma.gamenews.Utils.SharedPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.SQLOutput;

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
                            String.class,
                            new LoginDeserializer()
                    ).create();

                    btnLogin1.startAnimation();

                    DataService loginService = Client.getClientInstance(gson).create(DataService.class);
                    Call<String> login = loginService.login(user,pass);

                    login.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            String[] data = response.body().split(":");
                            if(data[0].equals("token")){
                                SharedPreference.logInUser(user, data[1]);
                                btnLogin1.doneLoadingAnimation(Color.parseColor("#6200ea")
                                        , BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp));
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }  else {
                                btnLogin1.revertAnimation();
                                new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Error")
                                        .setMessage(data[1])
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
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
