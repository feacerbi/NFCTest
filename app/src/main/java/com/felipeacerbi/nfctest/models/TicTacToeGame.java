package com.felipeacerbi.nfctest.models;

import com.felipeacerbi.nfctest.firebasemodels.TicTacToeGameDB;

import java.io.Serializable;
import java.util.List;

/**
 * Created by felipe.acerbi on 07/10/2016.
 */

public class TicTacToeGame implements Serializable {

    public static final int TOTAL_PLACES = 9;
    public static final int PLACE_AVAILABLE = 0;
    public static final int X_MARKER = 1;
    public static final int O_MARKER = 2;
    private String gameId;
    private TicTacToeGameDB ticTacToeGameDB;

    public TicTacToeGame(TicTacToeGameDB ticTacToeGameDB) {
        gameId = ticTacToeGameDB.getPlayerOne() + ticTacToeGameDB.getPlayerTwo();
        this.ticTacToeGameDB = ticTacToeGameDB;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public TicTacToeGameDB getTicTacToeGameDB() {
        return ticTacToeGameDB;
    }

    public void setTicTacToeGameDB(TicTacToeGameDB ticTacToeGameDB) {
        this.ticTacToeGameDB = ticTacToeGameDB;
    }

    public String checkResult() {
        List<Integer> places = ticTacToeGameDB.getPlaces();
        String winner = "";

        if(checkRows(places, X_MARKER) || checkCollumns(places, X_MARKER) || checkDiagonals(places, X_MARKER)) {
            winner = ticTacToeGameDB.getPlayerOne();
        } else if(checkRows(places, O_MARKER) || checkCollumns(places, O_MARKER) || checkDiagonals(places, O_MARKER)) {
            winner = ticTacToeGameDB.getPlayerTwo();
        } else if(checkTie(places)) {
            winner = "tie";
        }
        return winner;
    }

    public String checkResult(List<Integer> places) {
        String winner = "";

        if(checkRows(places, X_MARKER) || checkCollumns(places, X_MARKER) || checkDiagonals(places, X_MARKER)) {
            winner = ticTacToeGameDB.getPlayerOne();
        }else if(checkRows(places, O_MARKER) || checkCollumns(places, O_MARKER) || checkDiagonals(places, O_MARKER)) {
            winner = ticTacToeGameDB.getPlayerTwo();
        }
        return winner;
    }

    public boolean checkRows(List<Integer> places, int marker) {
        return places.get(0) == marker && places.get(1) == marker && places.get(2) == marker
                || places.get(3) == marker && places.get(4) == marker && places.get(5) == marker
                || places.get(6) == marker && places.get(7) == marker && places.get(8) == marker;
    }

    public boolean checkCollumns(List<Integer> places, int marker) {
        return places.get(0) == marker && places.get(3) == marker && places.get(6) == marker
                || places.get(1) == marker && places.get(4) == marker && places.get(7) == marker
                || places.get(2) == marker && places.get(5) == marker && places.get(8) == marker;
    }

    public boolean checkDiagonals(List<Integer> places, int marker) {
        return places.get(0) == marker && places.get(4) == marker && places.get(8) == marker
                || places.get(2) == marker && places.get(4) == marker && places.get(6) == marker;
    }

    public boolean checkTie(List<Integer> places) {
        for(int place : places) {
            if(place == PLACE_AVAILABLE) return false;
        }
        return true;
    }
}
