package casino.gui;
import javax.swing.*;
import java.awt.*;

public class GuiClass {
    public void setupSlotButton(JButton slotButton, JPanel centerPanel) {
        slotButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(new RouletteGamePanel());
            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }
    public void setuppokerButton(JButton pokerButton, JPanel centerPanel) {
        pokerButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(new JLabel("poker Coming Soon!"));
            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }
    public void setupblackjackButton(JButton blackjackButton, JPanel centerPanel) {
        blackjackButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(new BlackJackPanel());
            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }
    public void setupExitButton(JButton exitButton, JPanel centerPanel) {
        exitButton.addActionListener(e -> {
            centerPanel.removeAll();
            centerPanel.add(new JLabel("Welcome to the Casino! Select a game to play."));
            centerPanel.revalidate();
            centerPanel.repaint();
        });
    }



}
