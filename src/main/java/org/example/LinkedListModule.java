package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class LinkedListModule extends BaseModulePanel {
    class LLNode {
        int val, x, y;
        String address;
        public LLNode(int val, int x, int y) {
            this.val = val; this.x = x; this.y = y;
            this.address = "0x" + Integer.toHexString((int)(Math.random() * 255)).toUpperCase();
        }
    }

    private ArrayList<LLNode> list = new ArrayList<>();
    private boolean isAnimating = false;
    private int tempIndex = -1; 

    private JTextField valField, indexField;
    private DefaultListModel<String> pseudoCodeModel;
    private JList<String> pseudoCodeList;

    @Override
    protected void setupControls() {
        controlPanel.add(new JLabel("Value to Add/Delete:"));
        valField = new JTextField();
        valField.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(valField);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(new JLabel("Target Index (For Adding):"));
        indexField = new JTextField();
        indexField.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(indexField);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton addBtn = new JButton("Add at Index");
        addBtn.addActionListener(e -> startAnimation("ADD"));
        controlPanel.add(addBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton delBtn = new JButton("Delete Target Value");
        delBtn.addActionListener(e -> startAnimation("DELETE"));
        controlPanel.add(delBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        controlPanel.add(new JLabel("Real-Time Pseudocode:"));
        pseudoCodeModel = new DefaultListModel<>();
        pseudoCodeList = new JList<>(pseudoCodeModel);
        pseudoCodeList.setBackground(new Color(40, 44, 52));
        pseudoCodeList.setForeground(new Color(171, 178, 191));
        pseudoCodeList.setSelectionBackground(new Color(229, 192, 123));
        pseudoCodeList.setSelectionForeground(Color.BLACK);
        pseudoCodeList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        controlPanel.add(new JScrollPane(pseudoCodeList));
    }

    private void updateCode(String op) {
        pseudoCodeModel.clear();
        if (op.equals("ADD")) {
            pseudoCodeModel.addElement("1. Node newNode = new Node(val)");
            pseudoCodeModel.addElement("2. Node temp = HEAD");
            pseudoCodeModel.addElement("3. While reaching target index - 1:");
            pseudoCodeModel.addElement("4.   temp = temp.next");
            pseudoCodeModel.addElement("5. newNode.next = temp.next");
            pseudoCodeModel.addElement("6. temp.next = newNode");
        } else {
            pseudoCodeModel.addElement("1. Node temp = HEAD, prev = null");
            pseudoCodeModel.addElement("2. While temp.val != target:");
            pseudoCodeModel.addElement("3.   prev = temp");
            pseudoCodeModel.addElement("4.   temp = temp.next");
            pseudoCodeModel.addElement("5. prev.next = temp.next");
            pseudoCodeModel.addElement("6. free(temp)");
        }
    }

    private void highlightCode(int line) {
        SwingUtilities.invokeLater(() -> pseudoCodeList.setSelectedIndex(line));
    }

    private void startAnimation(String action) {
        if (isAnimating) return;
        try {
            int val = Integer.parseInt(valField.getText().trim());
            int idx = indexField.getText().isEmpty() ? 0 : Integer.parseInt(indexField.getText().trim());

            isAnimating = true;
            updateCode(action);

            new Thread(() -> {
                try {
                    int spd = Math.max(10, speedSlider.getValue() / 10);
                    if (action.equals("ADD")) animateAdd(val, idx, spd);
                    else animateDelete(val, spd);
                } catch (Exception e) {}
                isAnimating = false;
                tempIndex = -1;
                highlightCode(-1);
                canvasPanel.repaint();
            }).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers.");
        }
    }

    private void animateAdd(int val, int idx, int spd) throws Exception {
        highlightCode(0);
        LLNode newNode = new LLNode(val, 50, 300); 
        canvasPanel.repaint(); Thread.sleep(500);

        if (idx <= 0 || list.isEmpty()) {
            list.add(0, newNode);
            shiftRight(1, spd);
            moveNodeTo(newNode, 50, 150, spd);
            highlightCode(5); Thread.sleep(500);
        } else {
            idx = Math.min(idx, list.size());
            highlightCode(1);
            tempIndex = 0; canvasPanel.repaint(); Thread.sleep(800);

            highlightCode(2);
            for (int i = 0; i < idx - 1; i++) {
                highlightCode(3);
                tempIndex = i + 1; canvasPanel.repaint(); Thread.sleep(800);
            }

            list.add(idx, newNode);
            shiftRight(idx + 1, spd);

            highlightCode(4); 
            moveNodeTo(newNode, 50 + (idx * 140), 150, spd);
            highlightCode(5); 
            Thread.sleep(800);
        }
    }

    private void animateDelete(int targetVal, int spd) throws Exception {
        if (list.isEmpty()) return;
        highlightCode(0);
        tempIndex = 0; canvasPanel.repaint(); Thread.sleep(800);

        int targetIdx = -1;
        highlightCode(1);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).val == targetVal) { targetIdx = i; break; }
            highlightCode(2); Thread.sleep(300);
            highlightCode(3); tempIndex = i + 1; canvasPanel.repaint(); Thread.sleep(800);
        }

        if (targetIdx != -1) {
            LLNode toDel = list.get(targetIdx);
            highlightCode(4);
            Thread.sleep(800); 

            highlightCode(5);
            moveNodeTo(toDel, toDel.x, 350, spd); 
            list.remove(targetIdx);

            for (int step = 0; step < 28; step++) {
                for (int i = targetIdx; i < list.size(); i++) list.get(i).x -= 5;
                canvasPanel.repaint(); Thread.sleep(spd);
            }
        }
    }

    private void shiftRight(int startIndex, int spd) throws Exception {
        for (int step = 0; step < 28; step++) {
            for (int i = startIndex; i < list.size(); i++) list.get(i).x += 5;
            canvasPanel.repaint(); Thread.sleep(spd);
        }
    }

    private void moveNodeTo(LLNode n, int targetX, int targetY, int spd) throws Exception {
        while (n.x != targetX || n.y != targetY) {
            if (n.x < targetX) n.x += 5; else if (n.x > targetX) n.x -= 5;
            if (n.y < targetY) n.y += 5; else if (n.y > targetY) n.y -= 5;
            canvasPanel.repaint(); Thread.sleep(spd);
        }
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        for (int i = 0; i < list.size(); i++) {
            LLNode curr = list.get(i);

            g2d.setColor(new Color(100, 149, 237));
            g2d.fillRect(curr.x, curr.y, 60, 40); 
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRect(curr.x + 60, curr.y, 50, 40); 

            g2d.setColor(Color.BLACK);
            g2d.drawRect(curr.x, curr.y, 60, 40);
            g2d.drawRect(curr.x + 60, curr.y, 50, 40);

            g2d.setColor(Color.WHITE); g2d.drawString(String.valueOf(curr.val), curr.x + 20, curr.y + 25);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.setColor(Color.BLACK);

            String nextAddr = (i < list.size() - 1) ? list.get(i+1).address : "NULL";
            g2d.drawString(nextAddr, curr.x + 65, curr.y + 25);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));

            g2d.setColor(Color.RED);
            if (i == 0) g2d.drawString("HEAD", curr.x, curr.y - 10);
            if (i == list.size() - 1) g2d.drawString("TAIL", curr.x + 60, curr.y - 10);
            if (i == tempIndex) {
                g2d.setColor(Color.MAGENTA);
                g2d.drawString("TEMP ↓", curr.x + 30, curr.y - 30);
            }

            if (i < list.size() - 1) {
                g2d.setColor(Color.BLACK);
                drawArrow(g2d, curr.x + 110, curr.y + 20, list.get(i+1).x, list.get(i+1).y + 20);
            }
        }
    }

    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.drawLine(x1, y1, x2, y2);
        int d = 6;
        g2d.fillPolygon(new int[]{x2, x2-d, x2-d}, new int[]{y2, y2+d, y2-d}, 3);
    }
}
