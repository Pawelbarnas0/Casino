package casino.gui;

import javax.smartcardio.Card;
import java.util.ArrayList;
import java.util.Random;

public class BlackJackPlayer {

    ArrayList<String> PlayerCards;

    BlackJackPlayer(){
        PlayerCards = new ArrayList<>();
    }

    public void Draw(String card){
        PlayerCards.add(card);
    }

    public int GetScore(){
        int score = 0;
        int aceNumber = 0;
        for(String card : PlayerCards){
            String type = card.substring(0,card.indexOf("_"));
            try{
                score += Integer.parseInt(type);
            }catch(NumberFormatException e){
                if(type.equals("ace")){
                    aceNumber++;
                }else{
                    score += 10;
                }
            }
        }
        for(int i = 0; i < aceNumber; i++){
            score += score + 11 > 21 ? 1 : 11;
        }
        return score;
    }

    public boolean Bust(){
        return GetScore() > 21;
    }

    public boolean hasPair(){
        if(PlayerCards.size() != 2) return false;
        String[] cards = {PlayerCards.get(0), PlayerCards.get(1)};
        String[] types = {cards[0].substring(0, cards[0].indexOf("_")), cards[1].substring(0, cards[1].indexOf("_"))};

        try{
            return  Integer.parseInt(types[0]) == Integer.parseInt(types[1]);
        }catch(NumberFormatException e){
            if(types[0].length() == 1 || types[1].length() == 1) return false;
            if(!types[0].equals("ace")&&types[1].equals("ace") || types[0].equals("ace")&&!types[1].equals("ace")) return false;
            return true;
        }
    }

    public ArrayList<String> getCards(){
        return PlayerCards;
    }
    public void reset(){
        PlayerCards.clear();
    }
}
