package com.example.sudoku_juanpereira.Game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sudoku_juanpereira.R;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SudokuBoard extends View {

    private final int boardColor;
    private final int cellSelectedColor;
    private final int cellsSurroundingColor;
    private final int letterColor;
    private final int letterColorSolve;
    private final int letterColorWrong;
    private final int letterColorFixed;
    private int dimension;

    private final Paint boardColorPaint = new Paint();
    private final Paint cellSelectedPaint = new Paint();
    private final Paint cellsSurroundingPaint = new Paint();
    private final Paint letterPaint = new Paint();
    private final Rect letterPaintBounds = new Rect();
    private int cellSize;

    private boolean paused;


    private final SudokuSolver sudokuSolver = new SudokuSolver();


    public SudokuBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SudokuBoard,0,0);
        paused = false;

        try{
            boardColor = a.getInteger(R.styleable.SudokuBoard_boardColor,0);
            cellSelectedColor = a.getInteger(R.styleable.SudokuBoard_cellSelectedColor,0);
            cellsSurroundingColor = a.getInteger(R.styleable.SudokuBoard_cellsSurroundingColor,0);
            letterColor = a.getInteger(R.styleable.SudokuBoard_letterColor, 0);
            letterColorSolve = a.getInteger(R.styleable.SudokuBoard_letterColorSolve,0);
            letterColorWrong = a.getInteger(R.styleable.SudokuBoard_letterColorWrong,0);
            letterColorFixed = a.getInteger(R.styleable.SudokuBoard_letterColorFixed,0);
        }finally {
            a.recycle();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event){
        boolean valid;
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN) {
            sudokuSolver.setSelected_row((int)Math.ceil(y/cellSize));
            sudokuSolver.setSelected_column((int) Math.ceil(x/cellSize));
            valid = true;
        } else {
            valid = false;
        }
        return valid;
    }

    @Override
    protected void onMeasure(int width,int height){
        super.onMeasure(width,height);

        dimension = Math.min(Resources.getSystem().getDisplayMetrics().widthPixels-120,Resources.getSystem().getDisplayMetrics().heightPixels-120);
        cellSize = dimension / 9;
        setMeasuredDimension(dimension,dimension);
    }

    @Override
    protected void onDraw(Canvas canvas){
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(16);
        boardColorPaint.setColor(boardColor);
        boardColorPaint.setAntiAlias(true);

        cellSelectedPaint.setStyle(Paint.Style.FILL);
        cellSelectedPaint.setStrokeWidth(16);
        cellSelectedPaint.setColor(cellSelectedColor);
        cellSelectedPaint.setAntiAlias(true);

        cellsSurroundingPaint.setStyle(Paint.Style.FILL);
        cellsSurroundingPaint.setStrokeWidth(16);
        cellsSurroundingPaint.setColor(cellsSurroundingColor);
        cellsSurroundingPaint.setAntiAlias(true);

        letterPaint.setStyle(Paint.Style.FILL);
        letterPaint.setAntiAlias(true);
        letterPaint.setColor(letterColor);

        if(!paused){
            if(sudokuSolver.getSelected_row()!=-1){
                highLightCells(canvas);
            }
            highlightCell(canvas,sudokuSolver.getSelected_column(),sudokuSolver.getSelected_row());
        }
        canvas.drawRect(0,0,getWidth(),getHeight(),boardColorPaint);
        drawBoard(canvas);
        if(!paused) {
            drawBoardNumbers(canvas);
        }
    }

    public SudokuSolver getSudokuSolver(){
        return sudokuSolver;
    }

    private void drawBoardNumbers(Canvas canvas){
        letterPaint.setTextSize(cellSize);
        letterPaint.setTypeface(Typeface.create("casual",Typeface.BOLD));


        for(int r  =0;r<9;r++){
            for(int c = 0;c<9;c++){
                if(sudokuSolver.getBoard()[r][c]!=0){
                    drawNumber(canvas,r,c);
                }
            }
        }
    }

    private void drawNumber(Canvas canvas,int r,int c){
        String text = String.valueOf(sudokuSolver.getBoard()[r][c]);
        float width,height;

        letterPaint.getTextBounds(text,0,text.length(),letterPaintBounds);
        width = letterPaint.measureText(text);
        height = letterPaintBounds.height();

        if(sudokuSolver.getBoardFixed()[r][c] != 0){
            letterPaint.setColor(letterColorFixed);
        }
        else {
            letterPaint.setColor(letterColor);
        }
        if(!sudokuSolver.isCorrect(r,c)){
            letterPaint.setColor(letterColorWrong);
        }

        canvas.drawText(text,(c*cellSize)+((cellSize - width)/2),(r*cellSize+cellSize) - ((cellSize-height)/2),letterPaint);
    }

    private void highLightCells(Canvas canvas){
        int selectedNumber = sudokuSolver.getBoard()[sudokuSolver.getSelected_row()-1][sudokuSolver.getSelected_column()-1];
        if(selectedNumber!=0){
            for(int r = 0;r<sudokuSolver.getBoard().length;r++){
                for (int c = 0;c<sudokuSolver.getBoard().length;c++){
                    if(selectedNumber == sudokuSolver.getBoard()[r][c]){
                        highlightNumber(canvas,c+1,r+1);
                    }
                }
            }
        }
    }

    /**
     * Method that draws a line along the row, column and section pressed and also highlights itself with another color
     * @param canvas
     * @param c
     * @param r
     */
    private void highlightCell(Canvas canvas,int c, int r){
        if(sudokuSolver.getSelected_column()!=-1 && sudokuSolver.getSelected_row()!=-1){
            canvas.drawRect((c-1)*cellSize,0,c*cellSize,cellSize*9,cellsSurroundingPaint);
            canvas.drawRect(0,(r-1)*cellSize,cellSize*9,r*cellSize,cellsSurroundingPaint);
            highlightSection(canvas,r,c);

            highlightNumber(canvas,c,r);

        }

        invalidate();
    }

    private void highlightNumber(Canvas canvas,int c,int r){
        canvas.drawRect((c-1)*cellSize,(r-1)*cellSize,c*cellSize,r*cellSize,cellSelectedPaint);
    }

    /**
     * Method that highlights the 3x3 section of the clicked number
     * @param canvas
     * @param r
     * @param c
     */
    private void highlightSection(Canvas canvas, int r, int c){
        int boxRow = (r-1)/3;
        int boxCol = (c-1)/3;
        for(int row = (boxRow*3 +1); row<((boxRow*3) + 4);row++){
            for(int col = (boxCol*3 +1); col<((boxCol*3) + 4);col++){
                canvas.drawRect((col-1)*cellSize,(row-1)*cellSize,col*cellSize,row*cellSize,cellsSurroundingPaint);
            }
        }
    }

    public void togglePaused(){
        paused = !paused;
        invalidate();
    }

    /**
     * -------------METHODS TO DRAW THE EMPTY BOARD---------------
     */

    private void setBigLine(){
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(10);
        boardColorPaint.setColor(boardColor);
    }

    private void setSmallLine(){
        boardColorPaint.setStyle(Paint.Style.STROKE);
        boardColorPaint.setStrokeWidth(4);
        boardColorPaint.setColor(boardColor);
    }

    private void drawBoard(Canvas canvas){
        for (int c = 0;c<10;c++){
            if(c%3 == 0){
                setBigLine();
            }
            else setSmallLine();
            canvas.drawLine(cellSize * c,0,cellSize*c,getWidth(),boardColorPaint);
        }
        for(int r = 0;r<10;r++){
            if(r%3 == 0){
                setBigLine();
            }
            else setSmallLine();
            canvas.drawLine(0,cellSize*r,getWidth(),cellSize*r,boardColorPaint);
        }
    }
}
