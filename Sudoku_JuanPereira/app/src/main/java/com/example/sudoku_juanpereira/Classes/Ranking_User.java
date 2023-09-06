package com.example.sudoku_juanpereira.Classes;

public class Ranking_User {

    private String user;
    private int points;

    public Ranking_User(String user, int points) {
        this.user = user;
        this.points = points;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
