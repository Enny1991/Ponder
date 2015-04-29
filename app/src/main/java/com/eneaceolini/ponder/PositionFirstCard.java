package com.eneaceolini.ponder;

/**
 * Created by Enea on 26/04/15.
 */
public class PositionFirstCard {
    int width,high;
    float x,y;
    CardModel card;

    public PositionFirstCard(float x,float y,int width,int high,CardModel card){
        this.x = x;
        this.y = y;
        this.width = width;
        this.high = high;
        this.card = card;
    }
}
