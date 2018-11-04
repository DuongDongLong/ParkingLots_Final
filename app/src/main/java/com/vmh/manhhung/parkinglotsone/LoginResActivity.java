package com.vmh.manhhung.parkinglotsone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;

public class LoginResActivity extends AppCompatActivity {
    TextView txtRegistration;
    EditText edtEmail,edtPassword;
    CardView btnLogin;
    FirebaseAuth mAuthentication;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_res);
        AnhXa();
        mAuthentication =FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
        txtRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginResActivity.this,RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void AnhXa() {
        txtRegistration=(TextView)findViewById(R.id.txtRegistration);
        edtEmail=(EditText)findViewById(R.id.edtEmail);
        edtPassword=(EditText)findViewById(R.id.edtPassword);
        btnLogin=(CardView)findViewById(R.id.btnLogin);
    }
    public  void Login()
    {
        final String email=edtEmail.getText().toString();
        final String password=edtPassword.getText().toString();
        mAuthentication.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Customer customer=new Customer("",email,"","","",password);
                            Intent intent=new Intent(LoginResActivity.this,MainActivity.class);
                            intent.putExtra("DuLieu", (Serializable) customer);
                            startActivity(intent);
                            Toast.makeText(LoginResActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginResActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
