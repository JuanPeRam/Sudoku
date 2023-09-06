package com.example.sudoku_juanpereira.Game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sudoku_juanpereira.Home;
import com.example.sudoku_juanpereira.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity{

    private SudokuBoard board;
    private SudokuSolver solver;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private LinearLayout lbuttons;
    private LinearLayout lOptions;

    private TextView tv_mistakes;
    private TextView tv_timer;

    private ImageView iv_pause;
    private boolean paused;

    private Bundle bundle;

    private String gameID;
    private String uID;

    private ThreadTimer timer;

    private int difficulty;

    private MediaPlayer mp_commonclick;
    private MediaPlayer mp_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();

        mp_commonclick = MediaPlayer.create(this,R.raw.click);
        mp_error = MediaPlayer.create(this,R.raw.error);

        bundle = getIntent().getExtras();
        board = findViewById(R.id.sudokuBoard);
        solver = board.getSudokuSolver();

        tv_mistakes = findViewById(R.id.tv_mistakes);
        tv_timer = findViewById(R.id.tv_timer);
        lbuttons = findViewById(R.id.lbuttons);
        lOptions = findViewById(R.id.lOptions);
        iv_pause = findViewById(R.id.iv_pause);
        paused = false;
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_users), Context.MODE_PRIVATE);
        uID = preferences.getString("uID",null);
        long limit  =1;
        try{
            difficulty = bundle.getInt("difficulty");
            SudokuGenerator sg = new SudokuGenerator(difficulty);
            int[][] board = sg.getSudokuGenerated();
            int[][] boardFixed = copyMatrix(board);
            solver.setBoard(board);
            solver.setBoardFixed(boardFixed);
            setMistakes(0);

            String boardString = solver.getBoardString();
            int mistakes = solver.getMistakes();
            for(int i=1;i<10;i++){
                if(solver.numberLimit(i)) hideButton(i);
            }
            timer = new ThreadTimer();
            timer.start();

            createNewGame(uID,boardString,difficulty,mistakes);
        } catch(Exception e){
            db.collection("games").whereEqualTo("state","In progress").whereEqualTo("user",uID).limit(limit).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if(task.getResult().size()>0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String boardS = (String) document.getData().get("board");
                                String boardF = (String) document.getData().get("boardFixed");
                                difficulty = (int) (long) document.getData().get("difficulty");
                                int[][] board = toMatrix(boardS);
                                int[][] boardFixed = toMatrix(boardF);
                                gameID = document.getId();
                                int mistakes = (int) ((long) document.getData().get("mistakes"));
                                solver.setBoard(board);
                                solver.setBoardFixed(boardFixed);
                                solver.setMistakes(mistakes);
                                setMistakes(solver.getMistakes());
                                checkNumbersLimit();
                                int time = (int) (long) document.getData().get("time");
                                timer = new ThreadTimer(time / 60,time % 60);
                                timer.start();
                            }
                        }
                        else{
                            Toast.makeText(GameActivity.this, getString(R.string.nosavedgames), Toast.LENGTH_SHORT).show();
                            Intent home = new Intent(GameActivity.this, Home.class);
                            startActivity(home);
                        }
                    }
                }
            });
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        saveData();
        timer.setPause(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(timer != null && !paused){
            timer.setPause(false);
        }
        changeTheme();

    }

    private void changeTheme(){
        SharedPreferences preferences = getSharedPreferences(getString(R.string.preference_game_theme), Context.MODE_PRIVATE);
        String theme = preferences.getString("theme","classic");

        ConstraintLayout cl_main = findViewById(R.id.cl_game);
        Button btn_1 = findViewById(R.id.btn_1);
        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView iv_pause = findViewById(R.id.iv_pause);
        ImageView iv_clock = findViewById(R.id.iv_clock);

        int text_color = 0;
        int background_color = 0;


        switch (theme){
            case "classic":
                text_color = R.color.textColorClassic;
                background_color = R.color.backgroundColorClassic;
                cl_main.setBackgroundColor(getResources().getColor(R.color.backgroundColorClassic));
                break;
            case "neon":
                text_color = R.color.white;
                background_color = R.color.backgroundColorNeon;
                cl_main.setBackgroundColor(getResources().getColor(R.color.backgroundColorNeon));
                break;
            case "dark":
                text_color = R.color.textColorDark;
                background_color = R.color.backgroundColorDark;
                cl_main.setBackgroundColor(getResources().getColor(R.color.backgroundColorDark));
                break;
            case "light":
                text_color = R.color.textColorLight;
                background_color = R.color.backgroundColorLight;
                cl_main.setBackgroundColor(getResources().getColor(R.color.backgroundColorLight));
                break;
        }

        iv_back.setColorFilter(text_color);


    }

    private void checkNumbersLimit(){
        for (int i = 1; i < 10; i++) {
            if (solver.numberLimit(i)) hideButton(i);
        }
    }

    private int[][] toMatrix (String board){
        int[][] boardM = new int[9][9];
        int cont = 0;
        for (int r = 0; r < boardM.length; r++) {
            for (int c = 0; c < boardM[r].length; c++) {
                boardM[r][c] = Character.getNumericValue(board.charAt(cont));
                cont++;
            }
        }
        return boardM;
    }

    public void goBack(View view){
        changeActivity(Home.class);
    }

    public void goThemes(View view){
        changeActivity(ThemeActivity.class);
    }

    private void changeActivity(Class activityClass){
        saveData();
        timer.setPause(true);
        Intent intent = new Intent(this,activityClass);
        startActivity(intent);
    }

    public void btnOneClicked(View view){
        numberClicked(1,view);
    }
    public void btnTwoClicked(View view){
        numberClicked(2,view);
    }
    public void btnThreeClicked(View view){
        numberClicked(3,view);
    }
    public void btnFourClicked(View view){
        numberClicked(4,view);
    }
    public void btnFiveClicked(View view){
        numberClicked(5,view);
    }
    public void btnSixClicked(View view){
        numberClicked(6,view);
    }
    public void btnSevenClicked(View view){
        numberClicked(7,view);
    }
    public void btnEightClicked(View view){
        numberClicked(8,view);
    }
    public void btnNineClicked(View view){
        numberClicked(9,view);
    }

    private void numberClicked(int number, View view){
        solver.setNumberInBoard(number);
        board.invalidate();
        setMistakes(solver.getMistakes());
        if(solver.lastMovementCorrect()){
            mp_commonclick.start();
        }
        else{
            mp_error.start();
        }
        if(solver.numberLimit(number)) hideButton((Button) view);
        checkWin();
    }



    private void setMistakes(int mistakes){
        tv_mistakes.setText(String.valueOf(mistakes));
    }

    private void saveData(){
        Map<String,Object> game = new HashMap<>();
        game.put("board",solver.getBoardString());
        game.put("mistakes",solver.getMistakes());
        game.put("time",getTimeInSeconds());
        db.collection("games").document(gameID).update(game).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    public void pause(View view){
        toggleButtons(paused);
        if(paused){
            paused = false;
            iv_pause.setImageResource(R.drawable.ic_baseline_pause_24);
            lOptions.setVisibility(View.VISIBLE);
            timer.setPause(false);
            checkNumbersLimit();
        }
        else{
            paused = true;
            iv_pause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            lOptions.setVisibility(View.INVISIBLE);
            timer.setPause(true);
        }
        saveData();
        board.togglePaused();

    }

    private void toggleButtons(boolean enable){
        for(int i=0;i<lbuttons.getChildCount();i++){
            Button b = (Button) lbuttons.getChildAt(i);
            if(!enable) hideButton(b);
            else showButton(b);
        }
    }

    private void hideButton(Button b){
        b.setVisibility(View.INVISIBLE);
    }

    private void hideButton(int number){
        lbuttons.getChildAt(number-1).setVisibility(View.INVISIBLE);
    }

    private void showButton(Button b){
        b.setVisibility(View.VISIBLE);
    }

    public void undo(View view){
        int num = solver.undoStep();
        if(num!=0) showButton((Button) lbuttons.getChildAt(num-1));
    }

    public void clear(View view){
        int num = solver.clearCurrentCell();
        if(num!=0) showButton((Button) lbuttons.getChildAt(num-1));
    }

    public void resetBoard(View view){
        solver.resetSudoku();
        toggleButtons(true);
    }

    private void createNewGame(String userID,String board, int difficulty, int mistakes){
        Map<String,Object> game = new HashMap<>();
        game.put("user",userID);
        game.put("board",board);
        game.put("boardFixed",board);
        game.put("difficulty",difficulty);
        game.put("mistakes",mistakes);
        game.put("state","In progress");
        game.put("time",0);

        db.collection("games").add(game).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                gameID = documentReference.getId();
            }
        });

        db.collection("stats").whereEqualTo("user",userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int games = (int)(long) document.getData().get("games");
                        games++;
                        Map<String,Object> addedGame = new HashMap<>();
                        addedGame.put("games",games);
                        db.collection("stats").document(document.getId()).update(addedGame);

                    }
                }
            }
        });
    }

    private int[][] copyMatrix(int[][] matrix){
        int[][] copy = new int[9][9];
        for(int r = 0;r<copy.length;r++){
            for(int c = 0;c<copy[r].length;c++){
                copy[r][c] = matrix[r][c];
            }
        }
        return copy;
    }

    protected void setTimer(String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_timer.setText(text);
            }
        });
    }

    private void checkWin(){
        if(solver.gameWon()){
            timer.setStop(true);
            pause(null);
            int points = calcPoints();
            db.collection("games").document(gameID).delete();
            db.collection("stats").whereEqualTo("user",uID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int time = getTimeInSeconds();
                            int wins = (int)(long) document.getData().get("wins");
                            int totalTime = (int)(long) document.getData().get("time");
                            int totalPoints = (int)(long) document.getData().get("points");
                            wins++;
                            totalTime+=time;
                            totalPoints+=points;
                            Map<String,Object> updatedData = new HashMap<>();
                            updatedData.put("wins",wins);
                            updatedData.put("points",totalPoints);
                            updatedData.put("time",totalTime);
                            db.collection("stats").document(document.getId()).update(updatedData);
                        }
                    }
                }
            });
            showPersDialog(R.layout.dialog_game_won);


        }
    }

    private int calcPoints(){
        int points = 1000*difficulty;
        int mistakes = solver.getMistakes()*100;
        int time = getTimeInSeconds();
        points =points-mistakes;
        points = points-(time/10);
        return points;
    }

    private int getTimeInSeconds(){
        return (timer.minutes*60)+timer.seconds;
    }

    private void showPersDialog(int dial){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(dial,null);

        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        TextView tv_points = view.findViewById(R.id.tv_finalpoints);
        tv_points.setText(String.valueOf(calcPoints()));
    }

    private class ThreadTimer extends Thread{
        public int minutes;
        public int seconds;
        private boolean stop;
        private boolean pause;

        public ThreadTimer(){
            minutes = 0;
            seconds = 0;
            stop = false;
            pause = false;
        }

        public ThreadTimer(int m, int s){
            minutes = m;
            seconds = s;
            stop = false;
            pause = false;
        }

        public void run(){
            while(!stop){
                while(!pause){
                    String text = String.format("%02d:%02d",minutes,seconds);
                    setTimer(text);
                    seconds++;
                    if(seconds>59){
                        seconds = 0;
                        minutes++;
                    }
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setStop(boolean s){
            stop = s;
        }

        public void setPause(boolean p){
            pause = p;
        }
    }
}