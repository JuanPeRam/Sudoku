package com.example.sudoku_juanpereira.Game;

import java.util.ArrayList;

public class SudokuSolver {


    private int [][] board;//The board which is going to be changed during the game.
    private int [][] boardFixed;//The board with the default values of the sudoku.
    private int selected_row;//Current selected row
    private int selected_column;//Current selected column
    private ArrayList<ArrayList<Integer>> events;//An arraylist that will contain all the movements that the user will do.

    private int mistakes;


    public SudokuSolver(){
        this.selected_row = -1;
        this.selected_column = -1;
        this.mistakes = 0;

        board = new int[9][9];
        boardFixed = new int[9][9];
        for(int r = 0; r< board.length;r++){
            for(int c = 0; c<board[r].length;c++){
                board[r][c] = 0;
            }
        }

        events = new ArrayList<>();
    }

    public int[][] getBoardFixed(){
        return boardFixed;
    }

    public boolean setNumberInBoard(int num){
        if(selected_row!=-1 && selected_column!=-1 && boardFixed[selected_row-1][selected_column-1]==0){
            if(board[selected_row-1][selected_column-1] == num){
                board[selected_row-1][selected_column-1] = 0;
                addStep(0);
            }
            else {
                board[selected_row-1][selected_column-1] = num;
                addStep(num);
                if(!isCorrect(selected_row-1,selected_column-1)) mistakes++;
            }
            return true;
        }
        else return false;
    }

    private void addStep(int num){
        ArrayList<Integer> step = new ArrayList<>();
        step.add(selected_row-1);
        step.add(selected_column-1);
        step.add(num);
        events.add(step);
    }

    public int undoStep(){
        if(!events.isEmpty()){
            int row = events.get(events.size()-1).get(0);
            int col = events.get(events.size()-1).get(1);
            int num = events.get(events.size()-1).get(2);
            if(events.size()>1){
                int row2 = events.get(events.size()-2).get(0);
                int col2 = events.get(events.size()-2).get(1);
                int num2 = events.get(events.size()-2).get(2);
                if(row == row2 && col == col2){
                    board[row][col] = num2;
                }
                else{
                    board[row][col] = 0;
                }
            }
            else {
                board[row][col] = 0;
            }
            events.remove(events.size()-1);
            return num;
        }
        return 0;
    }

    public String getBoardString(){
        String text="";
        for(int r = 0;r<board.length;r++){
            for(int c = 0;c<board[r].length;c++){
                text += String.valueOf(board[r][c]);
            }
        }
        return text;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public void setBoardFixed(int[][] board){
        this.boardFixed = board;
    }

    public int getSelected_row() {
        return selected_row;
    }

    public void setSelected_row(int selected_row) {
        if(selected_row <1 || selected_row >9) selected_row = 9;
        this.selected_row = selected_row;
    }

    public int getSelected_column() {
        return selected_column;
    }

    public void setSelected_column(int selected_column) {
        if(selected_column <1 || selected_column >9) selected_column = 9;
        this.selected_column = selected_column;
    }

    public int getMistakes(){
        return mistakes;
    }

    public void setMistakes(int m){
        mistakes = m;
    }


    /**
     * Method that checks if the game has been won
     * @return True in case the game is won, false if not.
     */
    public boolean gameWon(){
        for(int r = 0;r< board.length;r++){
            for(int c = 0;c<board[r].length;c++){
                if(!isCorrect(r,c)) return false;
            }
        }
        return true;
    }

    /**
     *  Methods to check if the number is correctly placed
     */
    public boolean isCorrect(int r, int c){
        if(board[r][c] == 0) return false;
        if(foundInColumn(r,c)||foundInRow(r,c)||foundInSection(r,c)) {
            return false;
        }
        else {
            return true;
        }
    }

    private boolean foundInColumn(int r, int c){
        for(int col = 0; col<9; col++){
            if(board[r][col] == board[r][c] && c!=col) return true;
        }
        return false;
    }

    private boolean foundInRow(int r, int c){
        for(int row = 0; row<9; row++){
            if(board[row][c] == board[r][c] && r!=row) return true;
        }
        return false;
    }

    private boolean foundInSection(int r, int c){
        int boxRow = r/3;
        int boxCol = c/3;
        for(int row = (boxRow*3); row<((boxRow*3) + 3);row++){
            for(int col = (boxCol*3); col<((boxCol*3) + 3);col++){
                if(board[row][col] == board[r][c] && (r!=row && c!=col)) return true;
            }
        }
        return false;
    }

    /**
     * Function that checks if the number is contained in the board 9 times
     * @param number
     * @return
     */

    public boolean numberLimit(int number){
        int count = 0;
        for(int row = 0;row<board.length;row++){
            for(int col = 0;col<board[row].length;col++){
                if(board[row][col] == number) count++;
            }
        }
        return count==9;
    }


    /**
     * Function that resets the board to the default values generated
     */
    public void resetSudoku(){
        for(int r = 0;r< board.length;r++){
            for (int c = 0;c<board[r].length;c++){
                board[r][c] = boardFixed[r][c];
            }
        }
        selected_column = -1;
        selected_row = -1;
        events.clear();
    }

    /**
     * Function that deletes the number  of the cell selected
     * @return The number that has been deleted or 0 in case there is no possibility of deleting that number in the cell.
     * */

    public int clearCurrentCell(){
        if(selected_column!=-1 && selected_row!=-1 && boardFixed[selected_row-1][selected_column-1]!=board[selected_row-1][selected_column-1]){
            int num = board[selected_row-1][selected_column-1];
            board[selected_row-1][selected_column-1] = 0;
            addStep(0);
            return num;
        }
        else {
            return 0;
        }
    }

    /**
     * Function that checks if the last input in the board is correctly placed
     * @return
     */

    public boolean lastMovementCorrect(){
        if(selected_row==-1 || selected_column==-1 || board[selected_row-1][selected_column-1]==0) return true;
        return isCorrect(selected_row-1,selected_column-1);
    }


}
