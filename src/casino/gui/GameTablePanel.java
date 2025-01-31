package casino.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameTablePanel extends JPanel {
    private HashMap<String, BufferedImage> cardDeck;
    private BufferedImage cardBack;
    private Graphics2D table;

    private ArrayList<cardDrawinf> cardsToDraw;
    private String messageToDisplay;
    private int dealerCardNum;
    private int playerCardNum;
    private int playerCount;
    private String[] scores;

    private class cardDrawinf{
        String cardName, where;
        boolean faceup;

        cardDrawinf(String cardName, String where, boolean faceup) {
            this.cardName = cardName;
            this.where = where;
            this.faceup = faceup;
        }
    }

    GameTablePanel(){
        cardDeck = new HashMap<>();
        cardsToDraw = new ArrayList<>();
        try{
            cardBack = ImageIO.read(new File("images/cardBack.png"));
        }catch(IOException e){
            System.out.println("Could not load cardBack.png");
        }

        table = null;
        File folder = new File("images/cardFronts");
        File[] cards = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if(cards == null){
            System.out.println("Error: no cards found");
            return;
        }

        for(File card : cards)
            try{
                BufferedImage cardImg = ImageIO.read(card);
                cardDeck.put(card.getName(), cardImg);
            }catch(IOException e){
                System.out.println("Failed to load image" + card.getName());
            }
        playerCount = 0;
        scores = new String[2];
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        table = (Graphics2D) g;
        this.setBackground(new Color(34, 106, 5));

        int cardHeight = this.getHeight()/4;
        int cardWidth = cardHeight*2/3;
        int offset = cardWidth/5;
        int[] cardsLgth = { (dealerCardNum - 1)*offset + cardWidth, (playerCardNum - 1)*offset + cardWidth + cardWidth*(playerCount-1)};
        int[] startX = {(getWidth()/2) - (cardsLgth[0]/2), (getWidth()/2) - (cardsLgth[1]/2)};
        int[] Y = {getHeight()/6, getHeight()/6*5 };
        boolean drawTableText = true;

        for( cardDrawinf card : cardsToDraw){
            switch (card.where){
                case "Dealer":
                    if(!card.faceup) {
                        table.drawImage(cardBack, startX[0], Y[0]-cardHeight/2, cardWidth, cardHeight, this);
                    }else{
                        table.drawImage(cardDeck.get(card.cardName), startX[0], Y[0]-cardHeight/2, cardWidth, cardHeight, this);
                    }
                    startX[0]+=offset;
                    break;
                case "Player":
                    if(!card.faceup) {
                        table.drawImage(cardBack, startX[1], Y[1]-cardHeight/2, cardWidth, cardHeight, this);
                    }else{
                        table.drawImage(cardDeck.get(card.cardName), startX[1], Y[1]-cardHeight/2, cardWidth, cardHeight, this);
                    }
                    startX[1]+=offset;
                    break;
                case "Separator":
                    startX[1]+=cardWidth;
            }
        }
        table.setFont(new Font("Times New Roman",Font.BOLD, 15));
        table.setColor(Color.WHITE);
        FontMetrics fm = table.getFontMetrics();
        if(scores[0] != null) table.drawString("Dealer score "+scores[0], getWidth()/50, Y[0]-cardHeight/2);
        if(scores[1] != null) table.drawString("Your score "+scores[1], getWidth()/50, Y[1]-cardHeight/2 - 20);
        table.setFont(new Font("Times New Roman",Font.BOLD, 30));
        if(messageToDisplay != null){
            table.drawString(messageToDisplay, getWidth()/2-fm.stringWidth(messageToDisplay), getHeight()/2-fm.getAscent()/2);
            drawTableText = false;
        }

        if(drawTableText){
            table.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            table.drawString("Blackjack pays 3:2", getWidth()/2-fm.stringWidth("Blackjack pays 3:2"), getHeight()/2-fm.getAscent()/2);
            table.setFont(new Font("Times New Roman",Font.BOLD, 15));
            table.drawString("Dealer must stand on soft 17", getWidth()/2 - fm.stringWidth("Dealer must stand on soft 17")/2, getHeight()/2+fm.getAscent());
        }
    }

    public void drawCard(ArrayList<String> cards, String where, boolean faceup){
        for(String card : cards){
            cardsToDraw.add(new cardDrawinf(card, where, faceup));
            if(where.equals("Player")){ playerCardNum++; }
            else if(where.equals("Dealer")){ dealerCardNum++; }
        }
        if(where.equals("Player")){
            cardsToDraw.add(new cardDrawinf(null, "Separator", faceup));
            playerCount++;
        }
        repaint();
    }

    public void drawCard(String card, String where, boolean faceup){
        cardsToDraw.add(new cardDrawinf(card, where, faceup));
        if(where.equals("Player")){ playerCardNum++; }
        else if(where.equals("Dealer")){ dealerCardNum++; }
        repaint();
    }

    public void displayMessage(String message){
        messageToDisplay = message;
        repaint();
    }

    public void enterScores(int dealer, int player){
        if(dealer > 0)scores[0] = Integer.toString(dealer);
        else scores[0] = null;
        scores[1] = Integer.toString(player);
        repaint();
    }

    public void clearMessageDisplay(){
        messageToDisplay = null;
    }

    public void clearDrawnCards(){
        cardsToDraw.clear();
        playerCount = 0;
        dealerCardNum = 0;
        playerCardNum = 0;
        repaint();
    }



    public ArrayList<String> getCardList(){
        return new ArrayList<>(cardDeck.keySet());
    }

}
