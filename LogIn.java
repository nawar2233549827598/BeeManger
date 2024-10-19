package com.example.fianlebee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    private EditText username;
    private EditText password;
    private Button loginButton, registButton;
    private SharedPreferences sp;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registButton = findViewById(R.id.registButton);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        loginButton.setOnClickListener(this);
        registButton.setOnClickListener(this);
        sp = getSharedPreferences("details", 0);
        String spuser = sp.getString("user", null);
        String sppass = sp.getString("password", null);
        if (spuser != null && sppass != null) {
            username.setText(spuser);
            password.setText(sppass);
        }

    }



    public void onClick(View view) {
        if (view == loginButton) {
            String userNameString = username.getText().toString();
            String passwordString = password.getText().toString();

            if(TextUtils.isEmpty(userNameString)){
                username.setError("Email Is Required");
            }
            if(TextUtils.isEmpty(passwordString)){
                username.setError("password Is Required");
            }
            else{

                progressDialog.setMessage("login in progress");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(userNameString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i = new Intent(LogIn.this,MainActivity.class);
                            startActivity(i);
                           // finish();
                            progressDialog.dismiss();
                        } else{
                            Toast.makeText(LogIn.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("user", userNameString.toString());
                editor.putString("password", passwordString.toString());
                editor.apply();
            }


        }
        if(view == registButton){
            Intent i = new Intent(LogIn.this, Register.class);
            startActivity(i);
        }
        else {
            String toastMessage = "Username or Password are not populated";
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
        }
    }

}
