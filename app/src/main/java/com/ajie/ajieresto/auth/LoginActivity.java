package com.ajie.ajieresto.auth;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajie.ajieresto.DashboardActivity;
import com.ajie.ajieresto.R;
import com.ajie.ajieresto.data.Session;
import com.ajie.ajieresto.model.LoginResponse;
import com.ajie.ajieresto.utils.DialogUtils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.ajie.ajieresto.data.Constans.LOGIN;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView txtRegister;
    EditText email;
    EditText password;
    Session session;
    String mUsername = "", mPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        session = new Session(this);
        initBinding();
        initButton();
        checkPermission();
        loginCheck();
    }

    private void checkPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.MEDIA_CONTENT_CONTROL
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();
    }

    public void login() {
        DialogUtils.openDialog(this);
        AndroidNetworking.post(LOGIN)
                .addBodyParameter("userid",
                        email.getText().toString())
                .addBodyParameter("password",
                        password.getText().toString())
                .build()
                .getAsObject(LoginResponse.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof LoginResponse) {
                            LoginResponse res = (LoginResponse)
                                    response;
                            if (res.getStatus().equals("success")) {
                                session.setIsLogin(true);
                                session.setUserId(res.getLogin().getUserid());
                                loginCheck();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email Salah", Toast.LENGTH_SHORT).show();
                            }
                        }
                        DialogUtils.closeDialog();
                    }

                    @Override
                    public void onError(ANError anError) {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (cm.getActiveNetworkInfo() == null) {
                            Toast.makeText(LoginActivity.this, "Internet tidak Aktif", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email atau Password Salah", Toast.LENGTH_SHORT).show();
                        }
                        DialogUtils.closeDialog();
                    }
                });
    }

    private void loginCheck() {
        if (session.isLoggedIn()) {
            Intent i = new Intent(this, DashboardActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    private void initBinding() {
        btnLogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_link_signup);
        email = findViewById(R.id.et_input_email);
        password = findViewById(R.id.et_input_password);
    }

    private void initButton() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Email Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "Password Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Keluar Aplikasi")
                .setMessage("Yakin ingin mengakhiri aplikasi?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Tidak", null)
                .show();
    }
}