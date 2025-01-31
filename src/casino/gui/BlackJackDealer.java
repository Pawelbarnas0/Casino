package casino.gui;

import java.util.ArrayList;

public class BlackJackDealer{
    final int Threshold = 21;
    private ArrayList<String> dealerCards = new ArrayList<>();

    public void Draw(String card){
        dealerCards.add(card);
    }

    public ArrayList<String> getCards(){
        return dealerCards;
    }
    public int GetScore(){
        int score = 0;
        int aceNumber = 0;
        for(String card : dealerCards){
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
            score += score + 11 > Threshold ? 1 : 11;
        }
        return score;
    }

    public boolean Bust(){
        return GetScore() > Threshold;
    }

    public void reset(){
        dealerCards.clear();
    }

}
