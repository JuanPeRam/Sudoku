package com.example.sudoku_juanpereira;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sudoku_juanpereira.Game.GameActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class StatsFragment extends Fragment {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private TextView tv_gameswon;
    private TextView tv_gamesplayed;
    private TextView tv_totaltime;
    private TextView tv_totalpoints;

    public StatsFragment() {
        // Required empty public constructor
    }
    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_gamesplayed = view.findViewById(R.id.tv_gamesplayed);
        tv_gameswon = view.findViewById(R.id.tv_gameswon);
        tv_totalpoints = view.findViewById(R.id.tv_totalpoints);
        tv_totaltime = view.findViewById(R.id.tv_totaltime);

        db.collection("stats").whereEqualTo("user",user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int totalTime = (int)(long) document.getData().get("time");
                        int totalPoints = (int)(long) document.getData().get("points");
                        int games = (int) (long) document.getData().get("games");
                        int wins = (int) (long) document.getData().get("wins");
                        if(wins>0)
                        totalTime = totalTime/wins;
                        int minutes = totalTime/60;
                        int seconds = totalTime%60;

                        tv_gamesplayed.setText(String.valueOf(games));
                        tv_totaltime.setText(String.format("%02d:%02d",minutes,seconds));
                        tv_totalpoints.setText(String.valueOf(totalPoints));
                        tv_gameswon.setText(String.valueOf(wins));
                    }
                }
            }
        });


    }
}