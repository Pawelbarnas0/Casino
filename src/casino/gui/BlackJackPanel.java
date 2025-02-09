package casino.gui;

import casino.BetValidator;
import casino.CasinoApp;
import casino.InsufficientBalanceException;

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
    private int roundBet;

    private JButton hitButton;
    private JButton standButton;
    private JButton splitButton;
    private JButton doubleButton;
    private JComboBox<String> betSelector;

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
        betSelector = new JComboBox<>(new String[]{"$5", "$10", "$20", "$50", "$100"});

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
        currentHand = 0;
        String betval = (String)betSelector.getSelectedItem();
        betval = betval.substring(1, betval.length());
        roundBet = Integer.parseInt(betval);
        try {
            BetValidator.validateBet(roundBet);
        } catch (InsufficientBalanceException e) {
            ErrorPanel.showError(e.getMessage());
            return;
        }
        hands.add(new BlackJackHand(roundBet));
        CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() - hands.get(currentHand).bet);
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
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance()+ 5*hands.get(currentHand).bet/2);
        }else if(dealer.GetScore() == 21){
            secureLateMessageDisplay("Dealer has BLACKJACK!!", 500);
            //subtract the bet
        }else if(dealer.GetScore() == 21 && hands.get(currentHand).GetScore() == 21){
            secureLateMessageDisplay("PUSH!!", 500);
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance()+ hands.get(currentHand).bet);
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
            //double the bet
            try {
                BetValidator.validateBet(roundBet);
            } catch (InsufficientBalanceException e) {
                ErrorPanel.showError(e.getMessage());
                return;
            }
            hands.add(currentHand + 1, new BlackJackHand(roundBet));
            hands.get(currentHand + 1).PlayerCards.add(hands.get(currentHand).PlayerCards.get(1));
            hands.get(currentHand).PlayerCards.remove(1);
            hands.get(currentHand).Draw(cardStack.DrawCard());
            hands.get(currentHand + 1).Draw(cardStack.DrawCard());
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() - hands.get(currentHand).bet);
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
        try {
            BetValidator.validateBet(roundBet);
        } catch (InsufficientBalanceException e) {
            ErrorPanel.showError(e.getMessage());
            return;
        }
        CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() - roundBet);
        hands.get(currentHand).bet += roundBet;
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
            for(int i = 0; i < hands.size(); i++) {
                CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() + 2 * hands.get(i).bet);
            }
        }else{
            int delay = 0;
            for(int i = 0; i < hands.size(); i++){
                if(i > 0) delay = 1000;
                if(hands.get(i).GetScore() > dealer.GetScore()){
                    currentHand = i;
                    scenes.show(mainPanel, "startPanel");
                    CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance()+ hands.get(i).bet *2);
                    secureLateMessageDisplay("You Win!!", delay);
                    revealHoldCard();
                    //add bet to account
                } else if(hands.get(i).GetScore() == dealer.GetScore()){
                    currentHand = i;
                    scenes.show(mainPanel, "startPanel");
                    CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance()+ hands.get(i).bet);
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