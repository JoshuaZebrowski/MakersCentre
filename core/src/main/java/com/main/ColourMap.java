package com.main;

public class ColourMap {

    private final String colourName;
    private final String standardHex;
    private final String colourBlindHex;

    public ColourMap(String colourName, String standardHex, String colourBlindHex) {
        this.colourName = colourName;
        this.standardHex = standardHex;
        this.colourBlindHex = colourBlindHex;
    }

    public String getColourName() {
        return colourName;
    }

    public String getStandardHex() {
        return standardHex;
    }

    public String getColourBlindHex(){
        return colourBlindHex;
    }


}
