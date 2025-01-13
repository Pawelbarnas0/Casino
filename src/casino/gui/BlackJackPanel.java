package casino.gui;

import javax.swing.*;
import java.awt.*;

public class BlackJackPanel extends JPanel {
    public BlackJackPanel() {
        setLayout(new BorderLayout());

        // Create a panel for the betting buttons
        JPanel betPanel = new JPanel();
        betPanel.setLayout(new GridLayout(1, 5));
        JButton bet5Button = new JButton("Bet $5");
        JButton bet10Button = new JButton("Bet $10");
        JButton bet20Button = new JButton("Bet $20");
        JButton bet50Button = new JButton("Bet $50");
        JButton bet100Button = new JButton("Bet $100");
        betPanel.add(bet5Button);
        betPanel.add(bet10Button);
        betPanel.add(bet20Button);
        betPanel.add(bet50Button);
        betPanel.add(bet100Button);

        // Create a panel for the action buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(1, 4));
        JButton splitButton = new JButton("Split");
        JButton doubleButton = new JButton("Double");
        JButton takeCardButton = new JButton("Take Card");
        JButton passButton = new JButton("Pass");
        actionPanel.add(splitButton);
        actionPanel.add(doubleButton);
        actionPanel.add(takeCardButton);
        actionPanel.add(passButton);

        // Add panels to the main panel
        add(betPanel, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.SOUTH);
    }
}