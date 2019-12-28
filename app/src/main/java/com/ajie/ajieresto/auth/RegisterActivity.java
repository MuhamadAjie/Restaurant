package com.ajie.ajieresto.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajie.ajieresto.R;
import com.ajie.ajieresto.model.RegisterResponse;
import com.ajie.ajieresto.utils.DialogUtils;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.ajie.ajieresto.data.Constans.REGISTER;

public class RegisterActivity extends AppCompatActivity {
    Button btnRegister;
    TextView txtLogin;
    EditText email;
    EditText userName;
    EditText password,confPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);
        initBinding();
        initButton();
    }

    private void initBinding() {
        btnRegister = findViewById(R.id.btn_reg_sign_up);
        txtLogin = findViewById(R.id.txt_reg_link_login);
        email = findViewById(R.id.et_reg_input_email);
        userName = findViewById(R.id.et_reg_input_name);
        password = findViewById(R.id.et_reg_input_password);
    }

    private void initButton() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmailValid(email.getText()))
                {
                    if (email.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Email Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (password.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Password Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (userName.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Nama harus diisi", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (password.getText().toString().equals(confPass.getText().toString())) {
                            register();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Password tidak sama", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    if (email.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Email Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (password.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Password Tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else if (userName.getText().toString().equals("")) {
                        Toast.makeText(RegisterActivity.this, "Nama harus diisi", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Format email salah", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    public void register() {
        DialogUtils.openDialog(this);
        AndroidNetworking.post(REGISTER)
                .addBodyParameter("userid",
                        email.getText().toString())
                .addBodyParameter("password",
                        password.getText().toString())
                .addBodyParameter("nama",
                        userName.getText().toString())
                .build()
                .getAsObject(RegisterResponse.class, new ParsedRequestListener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof RegisterResponse) {
                            RegisterResponse res = (RegisterResponse)
                                    response;
                            if (res.getStatus().equals("success")) {
                                Toast.makeText(RegisterActivity.this,
                                        "Register Berhasil, silakan login kembali", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Username sudah digunakan", Toast.LENGTH_SHORT).show();
                            }
                            DialogUtils.closeDialog();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (cm.getActiveNetworkInfo() == null){
                            Toast.makeText(RegisterActivity.this, "Internet tidak Aktif", Toast.LENGTH_SHORT).show();
                        }
                        DialogUtils.closeDialog();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Keluar Aplikasi")
                .setMessage("Yakin ingin mengakhiri aplikasi?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    public static boolean isEmailValid(Editable email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
