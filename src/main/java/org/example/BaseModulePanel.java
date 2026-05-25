package org.example;

import javax.swing.*;
import java.awt.*;

public abstract class BaseModulePanel extends JPanel {
    protected JPanel controlPanel;
    protected JPanel canvasPanel;
    protected JSlider speedSlider;

    public BaseModulePanel() {
        setLayout(new BorderLayout());

        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(320, getHeight()));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        controlPanel.setBackground(new Color(245, 245, 250));

        JLabel speedLabel = new JLabel("Animation Speed:");
        speedLabel.setFont(new Font("Arial", Font.BOLD, 12));
        speedSlider = new JSlider(10, 1000, 500); 
        speedSlider.setInverted(true); 
        speedSlider.setBackground(new Color(245, 245, 250));

        JPanel speedPanel = new JPanel(new BorderLayout());
        speedPanel.setBackground(new Color(245, 245, 250));
        speedPanel.add(speedLabel, BorderLayout.NORTH);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        speedPanel.setMaximumSize(new Dimension(300, 60));

        controlPanel.add(speedPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                drawCanvas(g2d);
            }
        };
        canvasPanel.setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, controlPanel, canvasPanel);
        splitPane.setDividerLocation(320);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);

        setupControls(); 
    }

    protected void sleep() {
        try {
            Thread.sleep(speedSlider.getValue());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected abstract void setupControls();
    protected abstract void drawCanvas(Graphics2D g2d);
}
