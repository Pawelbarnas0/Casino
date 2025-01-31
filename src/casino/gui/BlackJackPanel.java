package casino.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;

public class BlackJackPanel extends JPanel{

    private CardLayout scenes;
    private JPanel mainPanel;
    private JPanel gamePlayPanel;
    private JPanel startPanel;
    private GameTablePanel tablePanel;

    private ArrayList<BlackJackPlayer> players;
    private BlackJackDealer dealer;
    private CardStack cardStack;
    private int currentPlayer;
    private javax.swing.Timer dealerTimer;

    private class lateDisplayMessage implements ActionListener{
        private String message;

        lateDisplayMessage(String msg){
            message = msg;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            revealHoldCard();
            tablePanel.displayMessage(message);
        }
    }


    public BlackJackPanel() {
        setLayout(new BorderLayout());

        scenes = new CardLayout();
        mainPanel = new JPanel(scenes);

        startPanel = new JPanel();
        startPanel.setLayout(new GridLayout(1,2));

        JButton dealButton = new JButton("Deal");
        dealButton.addActionListener(e -> {onDealButton();});
        JComboBox<String> betSelector = new JComboBox<>(new String[]{"$5", "$10", "$20", "$50", "$100"});

        startPanel.add(dealButton);
        startPanel.add(betSelector);

        gamePlayPanel = new JPanel();
        gamePlayPanel.setLayout(new GridLayout(1,4));

        JButton hitButton = new JButton("Hit");
        hitButton.addActionListener(e -> {hitButton();});
        JButton standButton = new JButton("Stand");
        standButton.addActionListener(e -> {standButton();});
        JButton splitButton = new JButton("Split");
        splitButton.addActionListener(e -> {splitButton();});
        JButton doubleButton = new JButton("Double");
        doubleButton.addActionListener(e -> {doubleButton();});

        gamePlayPanel.add(hitButton);
        gamePlayPanel.add(standButton);
        gamePlayPanel.add(splitButton);
        gamePlayPanel.add(doubleButton);

        mainPanel.add(gamePlayPanel, "gamePlayPanel");
        mainPanel.add(startPanel, "startPanel");

        scenes.show(mainPanel, "startPanel");

        tablePanel = new GameTablePanel();
        tablePanel.setPreferredSize(new Dimension(600, 400));

        add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.SOUTH);

        players = new ArrayList<>();
        dealer = new BlackJackDealer();
        cardStack = new CardStack(tablePanel.getCardList(), 3);
        currentPlayer = 0;
    }

    void onDealButton(){
        dealer.reset();
        players.clear();
        players.add(new BlackJackPlayer());
        currentPlayer = 0;
        tablePanel.clearMessageDisplay();

        scenes.show(mainPanel, "gamePlayPanel");

        for(int i = 0; i < 2; i++){
            dealer.Draw(cardStack.DrawCard());
            for(BlackJackPlayer player : players){
                player.Draw(cardStack.DrawCard());
            }
        }
        refreshScene();

        if(players.get(currentPlayer).GetScore() == 21){
            scenes.show(mainPanel, "startPanel");
            //BLACKJACK!!! - add winnings
            javax.swing.Timer dispMessage = new javax.swing.Timer(1000, new lateDisplayMessage("BLACKJACK!!"));
            dispMessage.setRepeats(false);
            dispMessage.start();
        }else if(dealer.GetScore() == 21){
            scenes.show(mainPanel, "startPanel");
            //BUST - subtract bet
            javax.swing.Timer dispMessage = new javax.swing.Timer(1000, new lateDisplayMessage("Dealer has BLACKJACK!!"));
            dispMessage.setRepeats(false);
            dispMessage.start();

        }else if(dealer.GetScore() == 21 && players.get(currentPlayer).GetScore() == 21){
            scenes.show(mainPanel, "startPanel");
            //PUSH - no bet gets subtracted
            javax.swing.Timer dispMessage = new javax.swing.Timer(1000, new lateDisplayMessage("PUSH!!"));
            dispMessage.setRepeats(false);
            dispMessage.start();
        }
    }

    void hitButton(){
        if(players.get(currentPlayer).GetScore() == 21){
            updatePlayer();
            return;
        }
        players.get(currentPlayer).Draw(cardStack.DrawCard());
        refreshScene();
        if(players.get(currentPlayer).Bust()){
            players.remove(currentPlayer);
            if(players.size() >= 1){
                refreshScene();
            }
            //subtract proportion of bet from account
        }
        if(players.size() == 0){
            scenes.show(mainPanel, "startPanel");
            tablePanel.displayMessage("BUST!!");
        }
    }
    void standButton() {
        if (currentPlayer < players.size() - 1) {
            updatePlayer();
            return;
        }
        revealHoldCard();

        dealerTimer = new javax.swing.Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(dealer.GetScore() < 17){
                    dealer.Draw(cardStack.DrawCard());
                    revealHoldCard();
                }else{
                    dealerTimer.stop();
                    evaluateGame();
                };
            }
        });
        dealerTimer.start();
    }
    void splitButton() {
        if (players.get(currentPlayer).hasPair()) {
            players.add(currentPlayer + 1, new BlackJackPlayer());
            players.get(currentPlayer + 1).PlayerCards.add(players.get(currentPlayer).PlayerCards.get(1));
            players.get(currentPlayer).PlayerCards.remove(1);
            players.get(currentPlayer).Draw(cardStack.DrawCard());
            players.get(currentPlayer + 1).Draw(cardStack.DrawCard());

            refreshScene();
        }
    }


    void doubleButton(){
        if(players.get(currentPlayer).GetScore() == 21){
            updatePlayer();
            return;
        }
        hitButton();
        standButton();
    }

    void updatePlayer(){
        currentPlayer++;
        currentPlayer %= players.size() > 0 ? players.size() : 1;
    }

    void refreshScene(){
        tablePanel.clearDrawnCards();
        tablePanel.drawCard(dealer.getCards().get(0), "Dealer", true);
        tablePanel.drawCard(dealer.getCards().get(1), "Dealer", false);
        refreshScores(false);

        for(BlackJackPlayer player : players){
            tablePanel.drawCard(player.getCards(), "Player", true);
        }
    }

    void revealHoldCard(){
        tablePanel.clearDrawnCards();
        tablePanel.drawCard(dealer.getCards(), "Dealer", true);
        refreshScores(true);

        for(BlackJackPlayer player : players){
            tablePanel.drawCard(player.getCards(), "Player", true);
        }
    }

    void refreshScores(boolean refreshdealer){
        if(players.size() <= 0) return;
        if(!refreshdealer) tablePanel.enterScores(0 ,players.get(currentPlayer).GetScore());
        else tablePanel.enterScores(dealer.GetScore(), players.get(currentPlayer).GetScore());
    }

    void evaluateGame(){
        if(dealer.Bust()){
            scenes.show(mainPanel, "startPanel");
            tablePanel.displayMessage("Dealer BUST!!");
        }else{
            for(int i = 0; i < players.size(); i++){
                if(players.get(i).GetScore() > dealer.GetScore()){
                    scenes.show(mainPanel, "startPanel");
                    tablePanel.displayMessage("You Win!!");
                } else if(players.get(i).GetScore() == dealer.GetScore()){
                    scenes.show(mainPanel, "startPanel");
                    tablePanel.displayMessage("PUSH!!");
                }else{
                    scenes.show(mainPanel, "startPanel");
                    tablePanel.displayMessage("Dealer Wins!!");
                }
            }
        }
    }
}