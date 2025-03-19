package com.main;

import java.util.List;

// have a json which this gets loaded at the start with base values which then get updated when switing between scenes
// persit play if players want to pause ect
public class GameState {
    public List<Player> players;
    public List<Node> nodes;
    public int globalTurn;
    public int years;
    public List<String> seasons;
    public String currentSeason;
    public String gameMode;

    // Default constructor
    public GameState() {}

    public GameState(List<Player> players, List<Node> nodes, int globalTurn, int years,
                     List<String> seasons, String currentSeason, String gameMode) {
        this.players = players;
        this.nodes = nodes;
        this.globalTurn = globalTurn;
        this.years = years;
        this.seasons = seasons;
        this.currentSeason = currentSeason;
        this.gameMode = gameMode;
    }
}
