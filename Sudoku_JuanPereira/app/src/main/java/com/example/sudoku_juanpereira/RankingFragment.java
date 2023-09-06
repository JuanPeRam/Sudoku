package com.example.sudoku_juanpereira;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sudoku_juanpereira.Classes.Ranking_RecyclerViewAdapter;
import com.example.sudoku_juanpereira.Classes.Ranking_User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;


public class RankingFragment extends Fragment {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView rv_ranking;

    public RankingFragment() {

    }

    public RankingFragment newInstance(String param1, String param2){
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_ranking = view.findViewById(R.id.rv_ranking);

        ArrayList<Ranking_User> usersRanking = new ArrayList<>();
        long limit = 50;
        db.collection("stats").limit(limit).orderBy("points", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        String username = (String) document.getData().get("name");
                        int points = (int) (long) document.getData().get("points");

                        Ranking_User user = new Ranking_User(username,points);
                        usersRanking.add(user);
                    }
                }
                Ranking_RecyclerViewAdapter adapter = new Ranking_RecyclerViewAdapter(view.getContext(),usersRanking);
                rv_ranking.setAdapter(adapter);
                rv_ranking.setLayoutManager(new LinearLayoutManager(view.getContext()));
            }
        });


    }
}