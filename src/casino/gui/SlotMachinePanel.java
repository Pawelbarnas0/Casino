package casino.gui;

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
    SlotMachine machine1;
    JComboBox betAmountComboBox;
    Slot mainSlot;

    private class Slot extends JPanel{
        Graphics2D slotWindow;
        HashMap<String, BufferedImage> SlotIcons;
        String[] iconsToDraw;
        Timer slotSpinTimer;
        int cardUpdateIndx = 0;
        int spinTime;


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
        }

        public void spin(){
            spinTime = 5000;
            ArrayList<String> icons = new ArrayList<>(SlotIcons.keySet());
            for(int i = 0; i < 3; i++) {
                iconsToDraw[i] = icons.get(new Random().nextInt(icons.size()));
            }
            slotSpinTimer = new javax.swing.Timer(25, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                    iconsToDraw[cardUpdateIndx] = icons.get(new Random().nextInt(icons.size()));
                    cardUpdateIndx = new Random().nextInt(3);
                    spinTime -= 25;
                    if(spinTime <= 0){
                        slotSpinTimer.stop();
                    }
                }
            });
            slotSpinTimer.start();
        }

        public void draw(){

        }
    }


    public SlotMachinePanel() {
        machine1 = new SlotMachine(3, 5);
        setLayout(new BorderLayout());

        mainSlot = new Slot();
        mainSlot.setPreferredSize(new Dimension(200, 100));
        JButton spinButton = new JButton("Spin");

        add(mainSlot, BorderLayout.CENTER);
        add(spinButton, BorderLayout.SOUTH);


        spinButton.addActionListener(e -> onSpinButton());
    }

    private void onSpinButton(){
        mainSlot.spin();
        machine1.playRound();
    }

}
