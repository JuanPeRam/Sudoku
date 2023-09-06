package com.example.sudoku_juanpereira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInScreen extends AppCompatActivity {

    TextView tv_email;
    TextView tv_password;

    String email;
    String password;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_in_screen);

        mAuth = FirebaseAuth.getInstance();

        tv_email = findViewById(R.id.tv_emaillogin);
        tv_password = findViewById(R.id.tv_passwordlogin);

    }

    public void logIn(View view){
        email = tv_email.getText().toString();
        password = tv_password.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            if(password.length()>=6) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent home = new Intent(getApplicationContext(), Home.class);
                            startActivity(home);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.badUserOrPassword), Toast.LENGTH_SHORT).show();
                            changeToError();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this,getString(R.string.incorrectPassword),Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this,getString(R.string.emptyField),Toast.LENGTH_SHORT).show();
        }
    }

    public void register(View view){
        Intent register = new Intent(this,RegisterActivity.class);
        startActivity(register);
    }

    private void changeToError(){
        tv_email.setText(null);
        tv_password.setText(null);

        tv_password.setHintTextColor(getResources().getColor(com.firebase.ui.auth.R.color.design_default_color_error));
        tv_email.setHintTextColor(getResources().getColor(com.firebase.ui.auth.R.color.design_default_color_error));

        tv_password.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(com.firebase.ui.auth.R.color.design_default_color_error)));
        tv_email.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(com.firebase.ui.auth.R.color.design_default_color_error)));
    }
}