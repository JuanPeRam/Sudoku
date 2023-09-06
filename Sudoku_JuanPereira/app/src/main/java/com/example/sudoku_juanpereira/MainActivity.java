package com.example.sudoku_juanpereira;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        if(session()){
            startHome();
        }
        else {
            Intent login = new Intent(this,SignInScreen.class);
            startActivity(login);
            //signInLauncher.launch(signInIntent);
        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    Intent signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build();


    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        //IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in

            db.collection("stats").whereEqualTo("user",FirebaseAuth.getInstance().getCurrentUser().getUid()).count().get(AggregateSource.SERVER).addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                    if (task.isSuccessful() && task.getResult().getCount() == 0) {
                        Map<String, Object> stat = new HashMap<>();
                        stat.put("user", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        stat.put("name",FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                        stat.put("games", 0);
                        stat.put("wins", 0);
                        stat.put("points", 0);
                        stat.put("time", 0);

                        db.collection("stats").add(stat).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                System.out.println("Registro añadido con éxito " + documentReference.getId());
                            }
                        });
                    }
                }
            });
            startHome();
        } else {
            System.out.println(result.getResultCode().byteValue());
        }
    }

    private boolean session(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_users), Context.MODE_PRIVATE);
        String email = preferences.getString("email",null);
        String uID = preferences.getString("uID",null);
        return !(email==null || uID == null);
    }

    private void startHome(){
        Handler handler = new Handler();
        Intent main = new Intent(this, Home.class);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(main);
                finish();
            }
        }, 3000);
    }
}