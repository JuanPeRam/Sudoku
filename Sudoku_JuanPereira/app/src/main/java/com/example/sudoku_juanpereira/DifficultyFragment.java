package com.example.sudoku_juanpereira;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sudoku_juanpereira.Game.SudokuGenerator;


public class DifficultyFragment extends Fragment {


    public DifficultyFragment() {
        // Required empty public constructor
    }

    public static DifficultyFragment newInstance(String param1, String param2) {
        DifficultyFragment fragment = new DifficultyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_difficulty, container, false);
    }
}