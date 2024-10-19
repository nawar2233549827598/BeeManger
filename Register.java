package com.example.fianlebee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private EditText username,gmail,password;
    private Button continueButton;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);


        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        gmail = findViewById(R.id.gmail);
        password = findViewById(R.id.password);
        continueButton = findViewById(R.id.continueButton);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameString = username.getText().toString();
                String passwordString = password.getText().toString();

                if(TextUtils.isEmpty(userNameString)){
                    username.setError("username Is Required");
                }
                if(TextUtils.isEmpty(passwordString)){
                    username.setError("password Is Required");
                } else{

                    progressDialog.setMessage("Registratin in progress");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(userNameString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(Register.this,CameraPage.class);
                                startActivity(i);
                                finish();
                                progressDialog.dismiss();
                            } else{
                                Toast.makeText(Register.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });
    }
}