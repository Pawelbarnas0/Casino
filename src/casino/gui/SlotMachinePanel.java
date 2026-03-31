package casino.gui;

import casino.BetValidator;
import casino.CasinoApp;
import casino.InsufficientBalanceException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

class SlotMachinePanel extends JPanel {
    private SlotMachine machine1;
    private JComboBox betAmountComboBox;
    Slot mainSlot;
    private JButton spinButton;
    private int currentBet;
    private JPanel gamePlayPanel;

    private class Slot extends JPanel{
        private Graphics2D slotWindow;
        private HashMap<String, BufferedImage> SlotIcons;
        private String[] iconsToDraw;
        private Timer slotSpinTimer;
        private int[] cardUpdateIndx;
        private int spinTime;
        private String messageToDisplay;
        ArrayList<String> barIcons;

        Slot(){
            File folder = new File("images/SlotMachineIcons");
            File[] icons = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            SlotIcons = new HashMap<>();

            if(icons == null){
                System.out.println("Error: no icons found");
                return;
            }

            for(File icon : icons)
                try{
                    BufferedImage cardImg = ImageIO.read(icon);
                    SlotIcons.put(icon.getName(), cardImg);
                }catch(IOException e){
                    System.out.println("Failed to load image " + icon.getName());
                }
            iconsToDraw = new String[3];
            messageToDisplay = null;
            cardUpdateIndx = new int[2];
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            slotWindow = (Graphics2D) g;
            //this.setBackground(new Color(38, 41, 44));

            int iconDim = getWidth()/8;
            int start = getWidth()/2 - 3*iconDim/2;

            for(String icon:iconsToDraw){
                slotWindow.drawImage(SlotIcons.get(icon), start, getHeight()/2 - iconDim/2 ,iconDim, iconDim, null);
                start+=iconDim;

            }

            slotWindow.setColor(Color.RED);
            FontMetrics fm = slotWindow.getFontMetrics();
            slotWindow.setFont(new Font("Times New Roman",Font.BOLD, 30));
            if(messageToDisplay != null){
                slotWindow.drawString(messageToDisplay, getWidth()/2-fm.stringWidth(messageToDisplay), 3*getHeight()/4+fm.getAscent()/2);
            }
        }

        public void spin(){
            spinTime = 2000;
            cardUpdateIndx[1] = new Random().nextInt(3);
            ArrayList<String> icons = new ArrayList<>(SlotIcons.keySet());
            for(int i = 0; i < 3; i++) {
                iconsToDraw[i] = icons.get(new Random().nextInt(icons.size()));
            }
            slotSpinTimer = new javax.swing.Timer(25, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                    iconsToDraw[cardUpdateIndx[1]] = icons.get(new Random().nextInt(icons.size()));
                    cardUpdateIndx[0] = cardUpdateIndx[1];
                    int a = 0;
                    do{
                        a = new Random().nextInt(3);
                    }while(a == cardUpdateIndx[0]);
                    cardUpdateIndx[1] = a;
                    spinTime -= 25;
                    if(spinTime <= 0){
                        slotSpinTimer.stop();
                        spinButton.setEnabled(true);
                        evaluate();
                    }
                }
            });
            spinButton.setEnabled(false);
            slotSpinTimer.start();
        }

        public void displayMessage(String message){
            messageToDisplay = message;
            repaint();
        }

        public void draw(int icons[]){
            barIcons = new ArrayList<>(SlotIcons.keySet());
            for(int i = 0; i < 3; i++) {
                iconsToDraw[i] = (barIcons.get(icons[i] - 1));
            }
            repaint();
        }

        public void clearDisplay(){
            messageToDisplay = null;
            repaint();
        }

        public void evaluate(){
            int[] endBar = machine1.playRound();
            draw(endBar);
            if(machine1.roundWon()){
                displayMessage("Jackpot!!");
                calculatePayouts(endBar);
            }else{
                displayMessage("Try again!!");
            }
        }

        private void calculatePayouts(int[] icons){
            barIcons = new ArrayList<>(SlotIcons.keySet());
            for(int i = 0; i < 3; i++) {
                iconsToDraw[i] = (barIcons.get(icons[i] - 1));
            }

            int winnings;
            switch(iconsToDraw[1]){
                case"bell.png":
                    winnings = currentBet;
                    break;
                case "diamond.png":
                    winnings = currentBet/2;
                    break;
                default:
                    winnings = 3*currentBet/10;
                    break;
            }
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() + winnings + currentBet);
        }

    }


    SlotMachinePanel() {
        machine1 = new SlotMachine(3, 5);
        setLayout(new BorderLayout());

        mainSlot = new Slot();
        mainSlot.setPreferredSize(new Dimension(200, 100));
        mainSlot.draw(new int[]{2,2,2});
        spinButton = new JButton("Spin");
        betAmountComboBox = new JComboBox<>(new String[]{"$5", "$10", "$15", "$25", "$50"});

        gamePlayPanel = new JPanel();
        gamePlayPanel.setLayout(new GridLayout(1,2));
        gamePlayPanel.add(spinButton);
        gamePlayPanel.add(betAmountComboBox);

        add(mainSlot, BorderLayout.CENTER);
        add(gamePlayPanel, BorderLayout.SOUTH);

        spinButton.addActionListener(e -> onSpinButton());
    }

    private void onSpinButton(){
        String betval = (String)betAmountComboBox.getSelectedItem();
        betval = betval.substring(1, betval.length());
        currentBet = Integer.parseInt(betval);
        try {
            BetValidator.validateBet(currentBet);
        } catch (InsufficientBalanceException e) {
            ErrorPanel.showError(e.getMessage());
            return;
        }
        CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance()-currentBet);
        mainSlot.clearDisplay();
        mainSlot.spin();
    }
}
