package casino;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import casino.gui.GuiClass;

public class CasinoApp {
    private static int playerBalance = 1000;
    public static JLabel balanceLabel = new JLabel("Player Balance: " + playerBalance);
    public static void updateBalance() {
        balanceLabel.setText("Player Balance: " + playerBalance);
    }
    public static int getPlayerBalance() {
        return playerBalance;
    }
    public static void setPlayerBalance(int newBalance) {
        playerBalance = newBalance;
        updateBalance();
    }

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
                try {
                    Image backgroundImage = ImageIO.read(new File("images/tło.png"));
                    g.drawImage(backgroundImage, 100, 100, 600, 400, this);
                } catch (IOException e) {
                    System.out.println("Image not loaded");
                }
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
        JPanel centerPanel = new JPanel(new BorderLayout()); // For game area //
        JPanel bottomPanel = new JPanel(); // For controls

        // Set panels as transparent to show the background
        topPanel.setOpaque(false);
        centerPanel.setOpaque(false);
        bottomPanel.setOpaque(false);

        // Add labels and components to panels
        topPanel.add(CasinoApp.balanceLabel);
        JButton addBalanceButton = new JButton("Add $1000");
        JButton slotButton = new JButton("Roulette");
        JButton slotMachinerButton = new JButton("Slot Machine");
        JButton blackjackButton = new JButton("Blackjack");
        JButton exitButton = new JButton("Exit");

        // Add action listeners to buttons
        addBalanceButton.addActionListener(e -> {
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() + 1000);
            CasinoApp.updateBalance();
        });

        // Add buttons to the bottom panel
        bottomPanel.add(slotButton);
        bottomPanel.add(slotMachinerButton);
        bottomPanel.add(blackjackButton);
        bottomPanel.add(exitButton);
        bottomPanel.add(addBalanceButton);

        // Add components to the background panel
        JPanel themePanel = new JPanel(); // Panel to hold themeSelector
        themePanel.setOpaque(false); // Make the theme selector panel transparent
        themePanel.add(new JLabel("Select Theme: "));
        themePanel.add(themeSelector);

        // Combine themePanel and topPanel
        JPanel combinedTopPanel = new JPanel(new BorderLayout());
        combinedTopPanel.setOpaque(false);
        combinedTopPanel.add(themePanel, BorderLayout.NORTH);
        combinedTopPanel.add(topPanel, BorderLayout.SOUTH);

        backgroundPanel.add(combinedTopPanel, BorderLayout.NORTH);
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Set the panel as the content pane
        frame.setContentPane(backgroundPanel);
        GuiClass guiClass = new GuiClass();
        guiClass.setupSlotButton(slotButton, centerPanel);
        guiClass.setupslotMachineButton(slotMachinerButton, centerPanel);
        guiClass.setupblackjackButton(blackjackButton, centerPanel);
        guiClass.setupExitButton(exitButton, centerPanel);

        // Make the frame visible
        frame.setVisible(true);
    }
}
