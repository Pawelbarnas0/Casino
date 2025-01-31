package casino.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class RouletteGamePanel extends JPanel {

    private static final String[] numbers = {"0", "00", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
            "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36"};

    private static final Color[] colors = {Color.GREEN, Color.GREEN, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK,
            Color.RED, Color.BLACK, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED,
            Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED,
            Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK};
    private static final int DIAMETER = 300; // Diameter of the wheel
    private static final int BALL_DIAMETER = 15;
    private static final int NUMBERS = 37; // European roulette (0 to 36)
    private double ballAngle = 0;
    private int finalNumber = -1;
    private Timer spinTimer;
    private JLabel resultLabel;
    private JComboBox<String> betComboBox;
    private JComboBox<String> betAmountComboBox;
    private JButton spinButton;
    private JButton[][] numberButtons;
    private String selectedBet = "";
    private Timer animationTimer;
    private JLabel spinningLabel;

    public RouletteGamePanel() {
        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Roulette Game", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Main Betting Grid (Center)
        JPanel bettingGrid = createBettingGrid();
        add(bettingGrid, BorderLayout.CENTER);

        // Betting Options (South)
        JPanel controlsPanel = createControlsPanel();
        add(controlsPanel, BorderLayout.SOUTH);

        // Result Label
        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultLabel.setForeground(Color.BLUE);
        add(resultLabel, BorderLayout.EAST);

        // Spinning Label
        spinningLabel = new JLabel("", JLabel.CENTER);
        spinningLabel.setFont(new Font("Arial", Font.BOLD, 24));
        spinningLabel.setForeground(Color.RED);
        add(spinningLabel, BorderLayout.WEST);
    }

    private JPanel createBettingGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(4, 12));

        for (int i = 0; i < numbers.length-1; i++) {
            String number = numbers[i];
            JButton numberButton = new JButton(number);
            numberButton.setBackground(colors[i]);
            numberButton.setForeground(Color.WHITE);
            numberButton.addActionListener(e -> selectedBet = number);
            gridPanel.add(numberButton);
        }

        return gridPanel;
    }

    public void startSpin() {
        finalNumber = Integer.parseInt(numbers[(int) (Math.random() * NUMBERS)]);
        spinTimer = new Timer(20, e -> spinWheel());
        spinTimer.start();
    }

    private void spinWheel() {
        ballAngle += Math.PI / 30; // Increment the angle

        // Gradually slow down the wheel
        if (ballAngle >= 2 * Math.PI * 10) { // After 10 full rotations
            ballAngle = 2 * Math.PI * finalNumber / NUMBERS;
            spinTimer.stop();
            JOptionPane.showMessageDialog(this, "The ball landed on " + finalNumber + "!");
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw wheel
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillOval(centerX - DIAMETER / 2, centerY - DIAMETER / 2, DIAMETER, DIAMETER);

        // Draw ball
        double ballX = centerX + (DIAMETER / 2 - 30) * Math.cos(ballAngle) - BALL_DIAMETER / 2;
        double ballY = centerY + (DIAMETER / 2 - 30) * Math.sin(ballAngle) - BALL_DIAMETER / 2;

        g2d.setColor(Color.WHITE);
        g2d.fillOval((int) ballX, (int) ballY, BALL_DIAMETER, BALL_DIAMETER);
    }

    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel(new FlowLayout());

        // Bet Option Selector
        JLabel betLabel = new JLabel("Bet Option:");
        betComboBox = new JComboBox<>(new String[]{"Red", "Black", "Green", "Odd", "Even", "1-18", "19-36"});

        // Bet Amount Selector
        JLabel betAmountLabel = new JLabel("Bet Amount:");
        betAmountComboBox = new JComboBox<>(new String[]{"$5", "$10", "$20", "$50", "$100"});

        // Spin Button
        spinButton = new JButton("Spin");
        spinButton.addActionListener(new SpinButtonListener());

        controlsPanel.add(betLabel);
        controlsPanel.add(betComboBox);
        controlsPanel.add(betAmountLabel);
        controlsPanel.add(betAmountComboBox);
        controlsPanel.add(spinButton);

        return controlsPanel;
    }

    private class SpinButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedBet.isEmpty() && betComboBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(RouletteGamePanel.this, "Please select a number or betting option to bet on!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            spinButton.setEnabled(false);
            resultLabel.setText("");
            spinningLabel.setText("Spinning...");

            Random random = new Random();
            int spinResult = random.nextInt(38); // 0 to 36 and 37 for 00

            String spinResultText = spinResult == 37 ? "00" : String.valueOf(spinResult);

            // Determine the color of the spin result
            Color resultColor = (spinResultText.equals("0") || spinResultText.equals("00")) ? Color.GREEN
                    : (spinResult % 2 == 0 ? Color.BLACK : Color.RED);

            // Start animation
            animationTimer = new Timer(100, new ActionListener() {
                int counter = 0;

                @Override
                public void actionPerformed(ActionEvent e) {
                    counter++;
                    if (counter >= 20) { // Stop animation after 20 updates
                        animationTimer.stop();
                        spinningLabel.setText("");
                        spinButton.setEnabled(true);

                        resultLabel.setText("The ball landed on: " + spinResultText);

                        // Determine if the bet is a winner
                        boolean won = false;
                        String betOption = (String) betComboBox.getSelectedItem();

                        if (selectedBet.equals(spinResultText)) {
                            won = true;
                        } else if (betOption != null) {
                            switch (betOption) {
                                case "Red":
                                    won = resultColor == Color.RED;
                                    break;
                                case "Black":
                                    won = resultColor == Color.BLACK;
                                    break;
                                case "Green":
                                    won = resultColor == Color.GREEN;
                                    break;
                                case "Odd":
                                    won = !spinResultText.equals("0") && !spinResultText.equals("00") && (spinResult % 2 != 0);
                                    break;
                                case "Even":
                                    won = !spinResultText.equals("0") && !spinResultText.equals("00") && (spinResult % 2 == 0);
                                    break;
                                case "1-18":
                                    won = spinResult >= 1 && spinResult <= 18;
                                    break;
                                case "19-36":
                                    won = spinResult >= 19 && spinResult <= 36;
                                    break;
                            }
                        }

                        String betAmount = (String) betAmountComboBox.getSelectedItem();
                        if (won) {
                            JOptionPane.showMessageDialog(RouletteGamePanel.this, "Congratulations! You won " + betAmount + "!", "Winner", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(RouletteGamePanel.this, "Sorry, you lost " + betAmount + ". Try again!", "Loser", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });
            animationTimer.start();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Roulette Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        RouletteGamePanel rouletteGamePanel = new RouletteGamePanel();
        frame.add(rouletteGamePanel);

        JButton spinButton = new JButton("Spin");
        spinButton.addActionListener(e -> rouletteGamePanel.startSpin());
        frame.add(spinButton, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}