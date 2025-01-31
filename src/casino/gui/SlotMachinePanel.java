package casino.gui;

import javax.swing.*;
import java.awt.*;

class SlotMachinePanel extends JPanel {
    SlotMachine machine1;
    JComboBox betAmountComboBox;


    public SlotMachinePanel() {
        machine1 = new SlotMachine(3, 5);
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Slot Machine Game", JLabel.CENTER);
        add(label, BorderLayout.CENTER);

        JButton spinButton = new JButton("Spin");
        add(spinButton, BorderLayout.SOUTH);

        spinButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Spinning the reels!");
            machine1.playRound();
        });
    }
}
