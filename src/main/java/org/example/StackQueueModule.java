package org.example;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class StackQueueModule extends BaseModulePanel {

    private LinkedList<Integer> stack = new LinkedList<>();
    private LinkedList<Integer> queue = new LinkedList<>();
    private LinkedList<Integer> pQueue = new LinkedList<>();

    private boolean isAnimating = false;
    private String animType = "";
    private Integer animValue = null;
    private int animX = 0, animY = 0;
    private int targetPqIndex = -1;

    private JTextField inputField;
    private JTextArea traceArea;

    public StackQueueModule() {
        super();
    }

    @Override
    protected void setupControls() {
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(new Color(245, 245, 250));
        inputPanel.add(new JLabel("Manual Value:"));
        inputField = new JTextField(10);
        inputPanel.add(inputField);
        controlPanel.add(inputPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel stackControls = new JPanel(new GridLayout(2, 2, 5, 5));
        stackControls.setBackground(new Color(245, 245, 250));
        stackControls.setBorder(BorderFactory.createTitledBorder("Stack (LIFO)"));

        JButton pushBtn = new JButton("Push (Input)");
        pushBtn.addActionListener(e -> triggerAction("PUSH", getInputValue()));

        JButton pushRandBtn = new JButton("Push (Random)");
        pushRandBtn.addActionListener(e -> triggerAction("PUSH", new Random().nextInt(99) + 1));

        JButton popBtn = new JButton("Pop");
        popBtn.addActionListener(e -> triggerAction("POP", null));

        stackControls.add(pushBtn);
        stackControls.add(pushRandBtn);
        stackControls.add(popBtn);
        controlPanel.add(stackControls);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel queueControls = new JPanel(new GridLayout(2, 2, 5, 5));
        queueControls.setBackground(new Color(245, 245, 250));
        queueControls.setBorder(BorderFactory.createTitledBorder("Standard Queue (FIFO)"));

        JButton enqBtn = new JButton("Enqueue (Input)");
        enqBtn.addActionListener(e -> triggerAction("ENQUEUE", getInputValue()));

        JButton enqRandBtn = new JButton("Enq (Random)");
        enqRandBtn.addActionListener(e -> triggerAction("ENQUEUE", new Random().nextInt(99) + 1));

        JButton deqBtn = new JButton("Dequeue");
        deqBtn.addActionListener(e -> triggerAction("DEQUEUE", null));

        queueControls.add(enqBtn);
        queueControls.add(enqRandBtn);
        queueControls.add(deqBtn);
        controlPanel.add(queueControls);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pqControls = new JPanel(new GridLayout(2, 2, 5, 5));
        pqControls.setBackground(new Color(245, 245, 250));
        pqControls.setBorder(BorderFactory.createTitledBorder("Priority Queue"));

        JButton pqEnqBtn = new JButton("PQ Enq (Input)");
        pqEnqBtn.addActionListener(e -> triggerAction("ENQUEUE_PQ", getInputValue()));

        JButton pqEnqRandBtn = new JButton("PQ Enq (Random)");
        pqEnqRandBtn.addActionListener(e -> triggerAction("ENQUEUE_PQ", new Random().nextInt(99) + 1));

        JButton pqDeqBtn = new JButton("PQ Dequeue");
        pqDeqBtn.addActionListener(e -> triggerAction("DEQUEUE_PQ", null));

        pqControls.add(pqEnqBtn);
        pqControls.add(pqEnqRandBtn);
        pqControls.add(pqDeqBtn);
        controlPanel.add(pqControls);

        traceArea = new JTextArea();
        traceArea.setEditable(false);
        traceArea.setFont(new Font("Consolas", Font.BOLD, 14));
        traceArea.setBackground(new Color(20, 20, 25));
        traceArea.setForeground(new Color(0, 255, 100));
        traceArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane traceScroll = new JScrollPane(traceArea);
        TitledBorder traceBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Real-Time Algo Trace (Terminal)");
        traceBorder.setTitleColor(Color.DARK_GRAY);
        traceScroll.setBorder(traceBorder);
        traceScroll.setPreferredSize(new Dimension(0, 160));

        SwingUtilities.invokeLater(() -> {
            this.add(traceScroll, BorderLayout.SOUTH);
            this.revalidate();
            this.repaint();
        });
    }

    private Integer getInputValue() {
        try {
            return Integer.parseInt(inputField.getText());
        } catch (Exception e) {
            return new Random().nextInt(99) + 1;
        }
    }

    private void updateTrace(String text) {
        SwingUtilities.invokeLater(() -> {
            traceArea.append(text + "\n");
            traceArea.setCaretPosition(traceArea.getDocument().getLength());
        });
    }

    private void clearTrace() {
        SwingUtilities.invokeLater(() -> traceArea.setText(""));
    }

    private void triggerAction(String action, Integer val) {
        if (isAnimating) return;

        if (action.equals("PUSH") && stack.size() >= 6) {
            JOptionPane.showMessageDialog(this, "Stack Overflow!"); return;
        }
        if (action.equals("ENQUEUE") && queue.size() >= 7) {
            JOptionPane.showMessageDialog(this, "Queue Overflow!"); return;
        }
        if (action.equals("ENQUEUE_PQ") && pQueue.size() >= 7) {
            JOptionPane.showMessageDialog(this, "Priority Queue Overflow!"); return;
        }
        if (action.equals("POP") && stack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Stack Underflow!"); return;
        }
        if (action.equals("DEQUEUE") && queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Queue Underflow!"); return;
        }
        if (action.equals("DEQUEUE_PQ") && pQueue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Priority Queue Underflow!"); return;
        }

        isAnimating = true;
        animType = action;
        animValue = val;
        clearTrace();

        new Thread(() -> {
            try {
                int baseSpeed = speedSlider != null ? speedSlider.getValue() / 15 : 4;
                if (baseSpeed < 2) baseSpeed = 2;
                int traceDelay = 600;

                if (action.equals("PUSH")) {
                    updateTrace("Executing: stack.push(" + val + ");");
                    Thread.sleep(traceDelay);
                    updateTrace("-> Pushing " + val + " to the TOP of the stack...");

                    animX = 75; animY = 20;
                    int targetY = 320 - (stack.size() * 55);
                    while (animY < targetY) { animY += 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }

                    stack.push(val);
                }
                else if (action.equals("POP")) {
                    updateTrace("Executing: stack.pop();");
                    Thread.sleep(traceDelay);

                    animValue = stack.pop();
                    updateTrace("-> Removing and returning: " + animValue);

                    animX = 75; animY = 320 - (stack.size() * 55);
                    while (animY > -50) { animY -= 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }
                }
                else if (action.equals("ENQUEUE")) {
                    updateTrace("Executing: queue.enqueue(" + val + ");");
                    Thread.sleep(traceDelay);

                    animY = 100; animX = canvasPanel.getWidth() + 50;
                    int targetX = 260 + (queue.size() * 65);
                    while (animX > targetX) { animX -= 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }

                    queue.addLast(val);
                }
                else if (action.equals("DEQUEUE")) {
                    updateTrace("Executing: queue.dequeue();");
                    Thread.sleep(traceDelay);

                    animValue = queue.removeFirst();

                    animY = 100; animX = 260;
                    while (animX > 150) { animX -= 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }
                }
                else if (action.equals("ENQUEUE_PQ")) {
                    updateTrace("Executing: pQueue.enqueue(" + val + ");");
                    Thread.sleep(traceDelay);

                    targetPqIndex = 0;
                    while (targetPqIndex < pQueue.size()) {
                        if (pQueue.get(targetPqIndex) <= val) {
                            targetPqIndex++;
                        } else {
                            break;
                        }
                    }

                    animY = 180;
                    animX = canvasPanel.getWidth() + 50;
                    int targetX = 260 + (targetPqIndex * 65);

                    while (animX > targetX) { animX -= 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }
                    while (animY < 250) { animY += 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }

                    pQueue.add(targetPqIndex, val);
                }
                else if (action.equals("DEQUEUE_PQ")) {
                    updateTrace("Executing: pQueue.dequeue();");
                    Thread.sleep(traceDelay);

                    animValue = pQueue.removeFirst();

                    animY = 250; animX = 260;
                    while (animX > 150) { animX -= 5; canvasPanel.repaint(); Thread.sleep(baseSpeed); }
                }
            } catch (Exception e) {}

            isAnimating = false;
            animValue = null;
            canvasPanel.repaint();
        }).start();
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        int width = canvasPanel.getWidth();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("STACK (LIFO)", 50, 30);

        g2d.drawRect(50, 50, 100, 330);

        for (int i = 0; i < stack.size(); i++) {
            int yPos = 320 - (i * 55);
            boolean isTop = (i == stack.size() - 1);
            Color nodeColor = isTop ? Color.YELLOW : Color.GREEN;
            String label = isTop ? "Top" : "";
            drawNode(g2d, stack.get(i), 75, yPos, nodeColor, label);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("QUEUE (FIFO)", 230, 60);
        g2d.drawRect(230, 80, width - 260, 90);

        for (int i = 0; i < queue.size(); i++) {
            int xPos = 260 + (i * 65);
            boolean isBack = (i == queue.size() - 1);
            Color nodeColor = isBack ? Color.YELLOW : Color.GREEN;
            String label = isBack ? "Back" : "";
            drawNode(g2d, queue.get(i), xPos, 100, nodeColor, label);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("PRIORITY QUEUE", 230, 210);
        g2d.drawRect(230, 230, width - 260, 90);

        for (int i = 0; i < pQueue.size(); i++) {
            int shiftX = (isAnimating && animType.equals("ENQUEUE_PQ") && i >= targetPqIndex && animY > 230) ? 65 : 0;
            int xPos = 260 + (i * 65) + shiftX;
            drawNode(g2d, pQueue.get(i), xPos, 250, Color.GREEN, "");
        }

        if (isAnimating && animValue != null) {
            drawNode(g2d, animValue, animX, animY, Color.YELLOW, "");
        }
    }

    private void drawNode(Graphics2D g2d, int value, int x, int y, Color bgColor, String label) {
        int diameter = 50;

        g2d.setColor(bgColor);
        g2d.fillOval(x, y, diameter, diameter);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x, y, diameter, diameter);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        FontMetrics fm = g2d.getFontMetrics();
        String text = String.valueOf(value);
        int textX = x + (diameter - fm.stringWidth(text)) / 2;
        int textY = y + (diameter - fm.getHeight()) / 2 + fm.getAscent();
        g2d.drawString(text, textX, textY);

        if (!label.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            int labelX = x + (diameter - g2d.getFontMetrics().stringWidth(label)) / 2;
            g2d.drawString(label, labelX, y - 5);
        }
    }
}
