package Admin.Dashboard;

import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {

    private int seconds = 0;
    private JLabel timerLabel;
    private Thread timerThread;
    private volatile boolean running = false;

    public TimerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        timerLabel = new JLabel("Temps passé: 00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setForeground(new Color(100, 88, 255));

        add(timerLabel, BorderLayout.CENTER);

        startTimer();
    }

    private void startTimer() {
        running = true;
        timerThread = new Thread(() -> {
            try {
                while (running) {
                    Thread.sleep(1000);
                    seconds++;
                    SwingUtilities.invokeLater(this::updateTimerLabel);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timerThread.start();
    }

    private void updateTimerLabel() {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        String timeFormatted = String.format("Temps passé: %02d:%02d", minutes, remainingSeconds);
        timerLabel.setText(timeFormatted);
    }

    public void stopTimer() {
        running = false;
        if (timerThread != null) {
            timerThread.interrupt();
        }
    }

    public void resetTimer() {
        seconds = 0;
        updateTimerLabel();
    }
}
