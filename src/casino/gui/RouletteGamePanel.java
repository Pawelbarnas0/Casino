package casino.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.security.SecureRandom;
import casino.BetValidator;
import casino.CasinoApp;
import casino.InsufficientBalanceException;

public class RouletteGamePanel extends JPanel {
    private JComboBox<String> betType;
    private JComboBox<String> betAmount;
    private JTextField betNumberField;
    private JButton spinButton;
    private JLabel resultLabel;
    private Timer spinTimer;
    private int spinningCount;
    private int winningNumber;
    private double ballAngle;
    private double finalAngle;
    private final int slices = 37;
    private final int wheelSize = 300;
    private final Random random = new Random();

    public RouletteGamePanel() {
        setLayout(new BorderLayout());
        JPanel betPanel = new JPanel();
        betPanel.add(new JLabel("Bet Type:"));
        betType = new JComboBox<>(new String[]{"Black", "Red", "Green", "Number"});
        betPanel.add(betType);

        // Bet amount selector
        betPanel.add(new JLabel("Amount:"));
        betAmount = new JComboBox<>(new String[]{"1", "5", "10", "25", "500"});
        betPanel.add(betAmount);

        betPanel.add(new JLabel("Number (0-36):"));
        betNumberField = new JTextField(5);
        betPanel.add(betNumberField);
        spinButton = new JButton("Spin");
        betPanel.add(spinButton);
        add(betPanel, BorderLayout.NORTH);

        resultLabel = new JLabel("Place your bet and spin!", SwingConstants.CENTER);
        add(resultLabel, BorderLayout.SOUTH);

        spinButton.addActionListener(e -> startSpin());
    }

    private void startSpin() {
        int currentBet = Integer.parseInt((String) betAmount.getSelectedItem());
        try {
            BetValidator.validateBet(currentBet);
        } catch (InsufficientBalanceException e) {
            ErrorPanel.showError(e.getMessage());
            return;
        }
        spinningCount = 0;
        SecureRandom ranSeed;
        try {
            ranSeed = SecureRandom.getInstance("NativePRNG");
            byte[] seed = ranSeed.generateSeed(8);
            long seedVal = seed[0];
            for (int i = 1; i < 8; i++) {
                seedVal += seed[i];
            }
            Random random = new Random(seedVal);
            winningNumber = random.nextInt(slices);
        } catch (NoSuchAlgorithmException e) {
            Random random = new Random();
            winningNumber = random.nextInt(slices);
        }
        ballAngle = 0;
        int numberOfSpins = 5; // Number of full spins before stopping
        finalAngle = (winningNumber + 0.5) * (360.0 / slices) + numberOfSpins * 360.0;

        if (spinTimer != null && spinTimer.isRunning()) {
            spinTimer.stop();
        }
        int totalSteps = 100; // Total steps for animation
        spinTimer = new Timer(30, e -> { // Faster timer interval
            spinningCount++;
            if (spinningCount <= totalSteps) {
                ballAngle += finalAngle / totalSteps;
            } else {
                spinTimer.stop();
                checkBetOutcome((String) betType.getSelectedItem());
            }
            repaint();
        });
        spinTimer.start();
    }

    private void checkBetOutcome(String betChoice) {
        String betNumText = betNumberField.getText().trim();
        int currentBet = Integer.parseInt((String) betAmount.getSelectedItem());
        String color = winningNumber == 0 ? "Green" :
                (winningNumber % 2 == 0 ? "Black" : "Red");
        boolean win = false;
        int payoutMultiplier = 0;

        if ("Green".equalsIgnoreCase(betChoice) && "Green".equalsIgnoreCase(color)) {
            win = true;
            payoutMultiplier = 35; // payout multiplier for green
        } else if ("Red".equalsIgnoreCase(betChoice) && "Red".equalsIgnoreCase(color)) {
            win = true;
            payoutMultiplier = 1; // 1:1 payout for red
        } else if ("Black".equalsIgnoreCase(betChoice) && "Black".equalsIgnoreCase(color)) {
            win = true;
            payoutMultiplier = 1; // 1:1 payout for black
        } else if ("Number".equalsIgnoreCase(betChoice)) {
            try {
                if (Integer.parseInt(betNumText) == winningNumber) {
                    win = true;
                    payoutMultiplier = 35; // 35:1 payout for numbers
                }
            } catch (NumberFormatException ignored) {
            }
        }

        String result;
        if (win) {
            int winAmount = currentBet * payoutMultiplier;
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() + winAmount);
            result = "Rolled " + winningNumber + " (" + color + ") - You win $" + winAmount + "!";
        } else {
            CasinoApp.setPlayerBalance(CasinoApp.getPlayerBalance() - currentBet);
            result = "Rolled " + winningNumber + " (" + color + ") - You lose $" + currentBet + "!";
        }

        // Append updated balance to the result display.
        result += " New Balance: $" + CasinoApp.getPlayerBalance();
        resultLabel.setText(result);
        repaint();
        CasinoApp.updateBalance();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2 + 50;

        // 1. Draw the wheel slices (arcs) without drawing numbers
        for (int i = 0; i < slices; i++) {
            Color sliceColor = getSliceColor(i);
            double start = i * (360.0 / slices);
            double extent = 360.0 / slices;
            Arc2D arc = new Arc2D.Double(cx - wheelSize / 2.0, cy - wheelSize / 2.0,
                    wheelSize, wheelSize, start, extent, Arc2D.PIE);
            g2.setColor(sliceColor);
            g2.fill(arc);
            g2.setColor(Color.BLACK);
            g2.draw(arc);
        }

        // 2. Draw the ball
        double radians = Math.toRadians(ballAngle % 360);
        int radius = wheelSize / 2 - 10;
        int ballSize = 15;
        int bx = cx + (int) (Math.cos(radians) * radius) - ballSize / 2;
        int by = cy + (int) (Math.sin(radians) * radius) - ballSize / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(bx, by, ballSize, ballSize);

        // 3. Draw the numbers on top
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < slices; i++) {
            double start = i * (360.0 / slices);
            double extent = 360.0 / slices;
            double angleRad = Math.toRadians(start + extent / 2);
            int textRadius = wheelSize / 2 - 25;
            int tx = cx + (int) (Math.cos(angleRad) * textRadius);
            int ty = cy + (int) (Math.sin(angleRad) * textRadius);

            AffineTransform old = g2.getTransform();
            g2.translate(tx, ty);
            g2.rotate(angleRad);

            String num = (i == 0) ? "00" : String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(num);
            int textHeight = fm.getAscent();
            g2.setColor(Color.WHITE);
            // Adjust the drawing position so that the number is centered
            g2.drawString(num, -textWidth / 2, textHeight / 2 - 2);
            g2.setTransform(old);
        }

        g2.dispose();
    }

    private Color getSliceColor(int index) {
        if (index == 34)
            return Color.BLACK;
        if (index == 36)
            return Color.GREEN;
        return (index % 2 == 0) ? Color.BLACK : Color.RED;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Roulette Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(new RouletteGamePanel());
        frame.setVisible(true);
    }
}