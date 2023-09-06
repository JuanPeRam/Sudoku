package com.example.sudoku_juanpereira.Game;

import java.util.Random;

public class SudokuGenerator {

    private int shownCells;
    private int[][] boardGenerated;

    public int[][] getBoardGenerated(){
        return boardGenerated;
    }

    public SudokuGenerator(int difficulty){
        switch(difficulty){
            case 1:
                shownCells = 45;
                break;
            case 2:
                shownCells = 35;
                break;
            case 3:
                shownCells = 40;
                break;
        }

        boardGenerated = new int[9][9];
        boardGenerated = completeWithZeros(boardGenerated);
        boardGenerated[0][0] = (int)(Math.random()*9+1);
        boardGenerated[1][4] = (int)(Math.random()*9+1);
        boardGenerated[2][7] = (int)(Math.random()*9+1);
        boardGenerated[3][3] = (int)(Math.random()*9+1);
        boardGenerated[4][1] = (int)(Math.random()*9+1);
        boardGenerated[5][6] = (int)(Math.random()*9+1);
        boardGenerated[6][2] = (int)(Math.random()*9+1);
        boardGenerated[7][5] = (int)(Math.random()*9+1);
        boardGenerated[8][8] = (int)(Math.random()*9+1);
        solveSudoku(boardGenerated);
    }

    public boolean solveSudoku(int[][] sudoku){
        int row = -1;
        int col = -1;

        for(int r = 0;r<sudoku.length;r++){
            for(int c=0;c<sudoku[r].length;c++){
                if(sudoku[r][c] == 0){
                    row = r;
                    col = c;
                    break;
                }
            }
        }

        if(row == -1 || col == -1) return true;

        for(int i=1;i<10;i++){
            sudoku[row][col] = i;

            if(check(sudoku,row,col)){
                if(solveSudoku(sudoku)){
                    return true;
                }
            }

            sudoku[row][col] = 0;
        }

        return false;
    }

    public boolean check(int[][] sudoku,int row,int col){
        for(int c = 0;c<9;c++){
            if(sudoku[row][c] == sudoku[row][col] && c!=col) return false;
        }
        for(int r = 0;r<9;r++){
            if(sudoku[r][col] == sudoku[row][col] && r!=row) return false;
        }
        int boxRow = row/3;
        int boxCol = col/3;
        for(int r = (boxRow*3); r<((boxRow*3) + 3);r++){
            for(int c = (boxCol*3); c<((boxCol*3) + 3);c++){
                if(sudoku[row][col] == sudoku[r][c] && (r!=row && c!=col)) return false;
            }
        }
        return true;
    }

    public int[][] getSudokuGenerated(){
        int[][] game = new int[9][9];
        game = completeWithZeros(game);
        Random random = new Random();
        int count = 0;
        while(count < shownCells){
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if(game[row][col] == 0){
                game[row][col] = boardGenerated[row][col];
                count++;
            }
        }

        return game;
    }

    private int[][] completeWithZeros(int[][] sudoku){
        for(int r = 0;r<sudoku.length;r++){
            for(int c=0;c<sudoku[r].length;c++){
                sudoku[r][c] = 0;
            }
        }
        return sudoku;
    }
}
