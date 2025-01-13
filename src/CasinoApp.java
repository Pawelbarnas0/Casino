import javax.swing.*;
import java.awt.*;

public class CasinoApp {
    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Casino Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Custom panel with an image background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundIcon = new ImageIcon("C:/Users/wiedzmok/Downloads/depositphotos_88609714-stock-illustration-casino-icon-with-dice-chips.jpg");
                if(backgroundIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                    System.out.println("Image not loaded");
                }
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Create the theme selector
        JComboBox<String> themeSelector = new JComboBox<>(new String[]{
                "Metal", "Nimbus", "Motif", "Windows"
        });
        themeSelector.addActionListener(e -> {
            String selectedTheme = (String) themeSelector.getSelectedItem();
            try {
                switch (selectedTheme) {
                    case "Metal":
                        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                        break;
                    case "Nimbus":
                        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                        break;
                    case "Motif":
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                        break;
                    case "Windows":
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                        break;
                }
                SwingUtilities.updateComponentTreeUI(frame);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Create and add panels
        JPanel topPanel = new JPanel(); // For player info
        JPanel centerPanel = new JPanel(); // For game area
        JPanel bottomPanel = new JPanel(); // For controls

        // Set panels as transparent to show the background
        topPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        bottomPanel.setOpaque(false);

        // Add labels and components to panels
        topPanel.add(new JLabel("Player Balance: $1000"));
        centerPanel.add(new JLabel("Welcome to the Casino! Select a game to play."));
        JButton slotButton = new JButton("Slot Machine");
        JButton pokerButton = new JButton("Poker");
        JButton blackjackButton = new JButton("Blackjack");
        JButton exitButton = new JButton("Exit");

        // Add buttons to the bottom panel
        bottomPanel.add(slotButton);
        bottomPanel.add(pokerButton);
        bottomPanel.add(blackjackButton);
        bottomPanel.add(exitButton);

        // Add components to the background panel
        JPanel themePanel = new JPanel(); // Panel to hold themeSelector
        themePanel.setOpaque(false); // Make the theme selector panel transparent
        themePanel.add(new JLabel("Select Theme: "));
        themePanel.add(themeSelector);

        backgroundPanel.add(themePanel, BorderLayout.NORTH);
        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Set the panel as the content pane
        frame.setContentPane(backgroundPanel);

        // Make the frame visible
        frame.setVisible(true);
    }
}
