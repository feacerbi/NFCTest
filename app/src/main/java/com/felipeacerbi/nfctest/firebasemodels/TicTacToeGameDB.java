package com.felipeacerbi.nfctest.firebasemodels;

import com.felipeacerbi.nfctest.models.TicTacToeGame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TicTacToeGameDB implements Serializable {

    private String playerOne;
    private String playerTwo;
    private String currentTurn;
    private String ready;
    private List<Integer> places;

    public TicTacToeGameDB() {
    }

    public TicTacToeGameDB(String playerOne, String playerTwo) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        currentTurn = playerOne;
        ready = "false";
        fillPlaces();
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public String getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }

    public String getReady() {
        return ready;
    }

    public void setReady(String ready) {
        this.ready = ready;
    }

    public List<Integer> getPlaces() {

        if(places == null) {
            fillPlaces();
        }
        return places;
    }

    public void setPlaces(List<Integer> places) {
        this.places = places;
    }

    public void fillPlaces() {
        places = new ArrayList<>();
        for(int i = 0; i < TicTacToeGame.TOTAL_PLACES; i++) {
            places.add(i, TicTacToeGame.PLACE_AVAILABLE);
        }
    }
}
