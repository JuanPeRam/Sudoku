package com.example.sudoku_juanpereira;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sudoku_juanpereira.Game.GameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    TextView tv_email;
    TextView tv_password;
    TextView tv_username;

    String email;
    String password;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tv_email = findViewById(R.id.tv_emailRegister);
        tv_password = findViewById(R.id.tv_passwordRegister);
        tv_username = findViewById(R.id.tv_usernameRegister);
    }

    public void registerUser(View view){
        email = tv_email.getText().toString();
        password = tv_password.getText().toString();
        username = tv_username.getText().toString();
        if(!email.isEmpty() && !password.isEmpty() && !username.isEmpty()){
            if(password.length()>=6){
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                            user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Map<String, Object> stat = new HashMap<>();
                                        stat.put("user", user.getUid());
                                        stat.put("name", user.getDisplayName());
                                        stat.put("games", 0);
                                        stat.put("wins", 0);
                                        stat.put("points", 0);
                                        stat.put("time", 0);

                                        db.collection("stats").add(stat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                            }
                                        });
                                        Intent home = new Intent(getApplicationContext(), Home.class);
                                        startActivity(home);
                                    }
                                }
                            });

                        }
                        else{

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
}