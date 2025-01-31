package casino.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class CardStack {
    private String[] cardStack;
    private int top, deckNum;
    private ArrayList<String> cardDeck;

    CardStack(ArrayList<String> cardList, int deckNum){
        cardDeck = new ArrayList<String>();
        cardDeck.addAll(cardList);
        top = cardList.size()*deckNum;
        cardStack = new String[top];
        this.deckNum = deckNum;
        Shuffle();
    }

    String DrawCard(){
        try{
            return cardStack[top--];
        }catch(IndexOutOfBoundsException e){
            Shuffle();
            return cardStack[top--];
        }
    }

    private void Shuffle(){
        ArrayList<String> cardList = new ArrayList<String>();
        cardList.addAll(cardDeck);
        int i = 0;
        for(int j=0; j<deckNum; j++){
            while(!cardList.isEmpty()){
                int indx = new Random().nextInt(cardList.size());
                cardStack[i++] = cardList.get(indx);
                cardList.remove(indx);
            }
            cardList.addAll(cardDeck);
        }
        top = i-1;
    }
}
