package casino.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BlackJackPanel extends JPanel{

    private CardLayout scenes;
    private JPanel mainPanel;
    private JPanel gamePlayPanel;
    private JPanel startPanel;
    private GameTablePanel tablePanel;

    private ArrayList<BlackJackHand> hands;
    private BlackJackDealer dealer;
    private CardStack cardStack;
    private int currentHand;
    private javax.swing.Timer dealerTimer;
    private boolean buttonsWorking;

    private JButton hitButton;
    private JButton standButton;
    private JButton splitButton;
    private JButton doubleButton;

    private class lateDisplayMessage implements ActionListener{
        private String message;

        lateDisplayMessage(String msg){
            message = msg;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            revealHoldCard();
            tablePanel.displayMessage(message);
            scenes.show(mainPanel, "startPanel");
            triggerButtons();
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

        hitButton = new JButton("Hit");
        hitButton.addActionListener(e -> {onHitButton();});
        standButton = new JButton("Stand");
        standButton.addActionListener(e -> {onStandButton();});
        splitButton = new JButton("Split");
        splitButton.addActionListener(e -> {onSplitButton();});
        doubleButton = new JButton("Double");
        doubleButton.addActionListener(e -> {onDoubleButton();});

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

        hands = new ArrayList<>();
        dealer = new BlackJackDealer();
        cardStack = new CardStack(tablePanel.getCardList(), 3);
        currentHand = 0;
        buttonsWorking = true;
    }

    void onDealButton(){
        dealer.reset();
        hands.clear();
        hands.add(new BlackJackHand());
        currentHand = 0;
        tablePanel.clearMessageDisplay();

        scenes.show(mainPanel, "gamePlayPanel");

        for(int i = 0; i < 2; i++){
            dealer.Draw(cardStack.DrawCard());
            for(BlackJackHand player : hands){
                player.Draw(cardStack.DrawCard());
            }
        }
        refreshScene();

        if(hands.get(currentHand).GetScore() == 21){
            secureLateMessageDisplay("BLACKJACK!!", 500);
            //add 3/2 of the bet
        }else if(dealer.GetScore() == 21){
            secureLateMessageDisplay("Dealer has BLACKJACK!!", 500);
            //subtract the bet

        }else if(dealer.GetScore() == 21 && hands.get(currentHand).GetScore() == 21){
            secureLateMessageDisplay("PUSH!!", 500);
        }
    }

    void onHitButton(){
        if(!buttonsWorking) return;
        if(hands.get(currentHand).GetScore() == 21){
            updatePlayer();
            return;
        }
        hands.get(currentHand).Draw(cardStack.DrawCard());
        refreshScene();
        if(hands.get(currentHand).Bust()){
            //subtract portion of bet: totalbet/hands.size
            hands.remove(currentHand);
            if(hands.size() >= 1){
                refreshScene();
            }
        }
        if(hands.size() == 0){
            tablePanel.displayMessage("BUST!!");
            scenes.show(mainPanel,"startPanel");
        }
    }
    void onStandButton() {
        if(!buttonsWorking) return;
        if (currentHand < hands.size() - 1) {
            updatePlayer();
            refreshScene();
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
                    triggerButtons();
                };
            }
        });
        triggerButtons();
        dealerTimer.start();
    }
    void onSplitButton() {
        if(!buttonsWorking) return;
        if (hands.get(currentHand).hasPair()) {
            hands.add(currentHand + 1, new BlackJackHand());
            hands.get(currentHand + 1).PlayerCards.add(hands.get(currentHand).PlayerCards.get(1));
            hands.get(currentHand).PlayerCards.remove(1);
            hands.get(currentHand).Draw(cardStack.DrawCard());
            hands.get(currentHand + 1).Draw(cardStack.DrawCard());
            //double the bet
            refreshScene();
        }
    }


    void onDoubleButton(){
        if(!buttonsWorking) return;
        if(hands.get(currentHand).PlayerCards.size() != 2){ return;}
        if(hands.get(currentHand).GetScore() == 21){
            updatePlayer();
            return;
        }
        onHitButton();
        onStandButton();
        //double the bet
    }

    void updatePlayer(){
        currentHand++;
        currentHand %= hands.size() > 0 ? hands.size() : 1;
    }

    void refreshScene(){
        tablePanel.clearDrawnCards();
        tablePanel.drawCard(dealer.getCards().get(0), "Dealer", true);
        tablePanel.drawCard(dealer.getCards().get(1), "Dealer", false);
        refreshScores(false);

        for(BlackJackHand player : hands){
            tablePanel.drawCard(player.getCards(), "Player", true);
        }
    }

    void revealHoldCard(){
        tablePanel.clearDrawnCards();
        tablePanel.drawCard(dealer.getCards(), "Dealer", true);
        refreshScores(true);

        for(BlackJackHand player : hands){
            tablePanel.drawCard(player.getCards(), "Player", true);
        }
    }

    void refreshScores(boolean refreshdealer){
        try {
            if (!refreshdealer) tablePanel.enterScores(0, hands.get(currentHand).GetScore());
            else tablePanel.enterScores(dealer.GetScore(), hands.get(currentHand).GetScore());
        }catch(Exception e){
            if(hands.isEmpty()) return;
            updatePlayer();
            if (!refreshdealer) tablePanel.enterScores(0, hands.get(currentHand).GetScore());
            else tablePanel.enterScores(dealer.GetScore(), hands.get(currentHand).GetScore());
        }
    }

    void triggerButtons(){
        if(buttonsWorking) buttonsWorking = false;
        else buttonsWorking = true;
    }

    void evaluateGame(){
        if(dealer.Bust()){
            tablePanel.displayMessage("Dealer BUST!!");
            scenes.show(mainPanel, "startPanel");
            //add bet to account
        }else{
            int delay = 0;
            for(int i = 0; i < hands.size(); i++){
                if(i > 0) delay = 1000;
                if(hands.get(i).GetScore() > dealer.GetScore()){
                    currentHand = i;
                    scenes.show(mainPanel, "startPanel");
                    secureLateMessageDisplay("You Win!!", delay);
                    revealHoldCard();
                    //add bet to account
                } else if(hands.get(i).GetScore() == dealer.GetScore()){
                    currentHand = i;
                    scenes.show(mainPanel, "startPanel");
                    secureLateMessageDisplay("PUSH!!", delay);
                    revealHoldCard();
                }else{
                    currentHand = i;
                    scenes.show(mainPanel, "startPanel");
                    secureLateMessageDisplay("Dealer wins!!", delay);
                    revealHoldCard();
                    //subtract bet from account
                }
            }
        }
    }

    void secureLateMessageDisplay(String message, int delay){
        triggerButtons();
        javax.swing.Timer dispMessage = new javax.swing.Timer(delay, new lateDisplayMessage(message));
        dispMessage.setRepeats(false);
        dispMessage.start();
    }

}