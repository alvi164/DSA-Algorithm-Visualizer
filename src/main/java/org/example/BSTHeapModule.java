package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class BSTHeapModule extends BaseModulePanel {
    static class TreeNode {
        int val, x, y, targetX, targetY;
        TreeNode left, right;
        public TreeNode(int val, int x, int y) {
            this.val = val;
            this.x = x;
            this.y = y;
            this.targetX = x;
            this.targetY = y;
        }
    }

    private TreeNode bstRoot = null;

    private final ArrayList<Integer> heapArray = new ArrayList<>();

    private JComboBox<String> structSelector;
    private JTextField inputField;
    private JLabel statusLabel;

    private boolean isAnimating = false;
    private Integer animatingVal = null;
    private int animX, animY;
    private int highlightIndex1 = -1, highlightIndex2 = -1;

    @Override
    protected void setupControls() {
        controlPanel.add(new JLabel("Data Structure:"));
        structSelector = new JComboBox<>(new String[]{"Binary Search Tree", "Min-Heap", "Max-Heap"});

        structSelector.addActionListener(e -> {
            bstRoot = null;
            heapArray.clear();
            canvasPanel.repaint();
        });
        controlPanel.add(structSelector);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        controlPanel.add(new JLabel("Insert Value:"));
        inputField = new JTextField();
        inputField.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(inputField);

        JButton insertBtn = new JButton("Insert & Animate");
        insertBtn.setBackground(new Color(34, 139, 34));
        insertBtn.setForeground(Color.WHITE);
        insertBtn.addActionListener(e -> {
            try {
                animateInsertion(Integer.parseInt(inputField.getText()));
                inputField.setText("");
            } catch (Exception ex) {
                animateInsertion(new Random().nextInt(99) + 1);
            }
        });
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(insertBtn);

        JButton clearBtn = new JButton("Clear Tree/Heap");
        clearBtn.setBackground(new Color(220, 53, 69));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> {
            bstRoot = null;
            heapArray.clear();
            canvasPanel.repaint();
        });
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(clearBtn);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        controlPanel.add(statusLabel);
    }

    private void updateStatus(String msg) {
        SwingUtilities.invokeLater(() -> statusLabel.setText("Status: " + msg));
    }

    private void animateInsertion(int val) {
        if (isAnimating) return;
        isAnimating = true;
        animatingVal = val;
        animX = canvasPanel.getWidth() / 2;
        animY = 20;

        String type = (String) structSelector.getSelectedItem();

        new Thread(() -> {
            try {
                if ("Binary Search Tree".equals(type)) {
                    bstRoot = insertBST(bstRoot, val, canvasPanel.getWidth() / 2, 80, 200);
                } else {
                    updateStatus("Inserting " + val + " at end of array");
                    heapArray.add(val);
                    int currentIdx = heapArray.size() - 1;
                    canvasPanel.repaint();
                    sleep();

                    while (currentIdx > 0) {
                        int parentIdx = (currentIdx - 1) / 2;

                        boolean swap = "Min-Heap".equals(type) ?
                                (heapArray.get(currentIdx) < heapArray.get(parentIdx)) :
                                (heapArray.get(currentIdx) > heapArray.get(parentIdx));

                        highlightIndex1 = currentIdx; highlightIndex2 = parentIdx;
                        updateStatus("Comparing " + heapArray.get(currentIdx) + " and Parent " + heapArray.get(parentIdx));
                        canvasPanel.repaint();
                        sleep();

                        if (swap) {
                            updateStatus("Swapping to maintain " + type + " property!");
                            int temp = heapArray.get(currentIdx);
                            heapArray.set(currentIdx, heapArray.get(parentIdx));
                            heapArray.set(parentIdx, temp);
                            currentIdx = parentIdx;
                            canvasPanel.repaint();
                            sleep();
                        } else {
                            updateStatus("Heap property maintained. Stopping.");
                            break;
                        }
                    }
                    highlightIndex1 = -1; highlightIndex2 = -1;
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            } finally {
                animatingVal = null;
                isAnimating = false;
                updateStatus("Insertion Complete");
                canvasPanel.repaint();
            }
        }).start();
    }

    private TreeNode insertBST(TreeNode node, int val, int x, int y, int xOffset) {
        if (node == null) return new TreeNode(val, x, y);

        updateStatus("Comparing " + val + " with " + node.val);
        int steps = 15;
        for (int i = 0; i < steps; i++) {
            animX += (x - animX) / 2; animY += (y - animY) / 2;
            canvasPanel.repaint(); sleep();
        }

        if (val < node.val) {
            updateStatus(val + " < " + node.val + " -> Moving Left");
            node.left = insertBST(node.left, val, x - xOffset, y + 80, xOffset / 2);
        } else if (val > node.val) {
            updateStatus(val + " > " + node.val + " -> Moving Right");
            node.right = insertBST(node.right, val, x + xOffset, y + 80, xOffset / 2);
        }
        return node;
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        String type = (String) structSelector.getSelectedItem();
        g2d.setStroke(new BasicStroke(2));

        if ("Binary Search Tree".equals(type)) {
            if (bstRoot != null) drawTree(g2d, bstRoot);
        } else {
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString("Array Memory Representation:", 20, 30);

            for (int i = 0; i < heapArray.size(); i++) {
                int ax = 20 + (i * 45); int ay = 40;
                if (i == highlightIndex1 || i == highlightIndex2) g2d.setColor(Color.YELLOW);
                else g2d.setColor(new Color(240, 240, 245));
                g2d.fillRect(ax, ay, 40, 40);

                g2d.setColor(Color.BLACK); g2d.drawRect(ax, ay, 40, 40);
                g2d.drawString(String.valueOf(heapArray.get(i)), ax + 10, ay + 25);

                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                g2d.drawString("[" + i + "]", ax + 12, ay + 55);
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
            }
            if (!heapArray.isEmpty()) drawHeapTree(g2d, 0, canvasPanel.getWidth() / 2, 140, 200);
        }

        if (isAnimating && animatingVal != null && "Binary Search Tree".equals(type)) {
            draw3DNode(g2d, animX, animY, String.valueOf(animatingVal), Color.ORANGE);
        }
    }

    private void drawTree(Graphics2D g2d, TreeNode node) {
        if (node == null) return;
        if (node.left != null) {
            g2d.setColor(Color.GRAY);
            g2d.drawLine(node.x, node.y, node.left.x, node.left.y);
            drawTree(g2d, node.left);
        }
        if (node.right != null) {
            g2d.setColor(Color.GRAY);
            g2d.drawLine(node.x, node.y, node.right.x, node.right.y);
            drawTree(g2d, node.right);
        }
        draw3DNode(g2d, node.x, node.y, String.valueOf(node.val), Color.CYAN);
    }

    private void drawHeapTree(Graphics2D g2d, int idx, int x, int y, int xOff) {
        if (idx >= heapArray.size()) return;
        int leftIdx = 2 * idx + 1, rightIdx = 2 * idx + 2;

        g2d.setColor(Color.GRAY);
        if (leftIdx < heapArray.size()) {
            g2d.drawLine(x, y, x - xOff, y + 70);
            drawHeapTree(g2d, leftIdx, x - xOff, y + 70, xOff / 2);
        }
        if (rightIdx < heapArray.size()) {
            g2d.drawLine(x, y, x + xOff, y + 70);
            drawHeapTree(g2d, rightIdx, x + xOff, y + 70, xOff / 2);
        }

        Color c = (idx == highlightIndex1 || idx == highlightIndex2) ? Color.YELLOW : Color.CYAN;
        draw3DNode(g2d, x, y, String.valueOf(heapArray.get(idx)), c);
    }

    private void draw3DNode(Graphics2D g2d, int x, int y, String label, Color baseColor) {
        float radius = 25f; 
        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(x - (int)radius + 4, y - (int)radius + 4, (int)radius * 2, (int)radius * 2);

        Point2D center = new Point2D.Float(x, y);
        Point2D focus = new Point2D.Float(x - 10, y - 10);

        RadialGradientPaint p = new RadialGradientPaint(
                center,
                radius,
                focus,
                new float[]{0f, 1f},
                new Color[]{Color.WHITE, baseColor.darker()},
                MultipleGradientPaint.CycleMethod.NO_CYCLE
        );

        g2d.setPaint(p);
        g2d.fillOval(x - (int)radius, y - (int)radius, (int)radius * 2, (int)radius * 2);

        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - (int)radius, y - (int)radius, (int)radius * 2, (int)radius * 2);

        g2d.setColor((baseColor == Color.YELLOW) ? Color.BLACK : Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label, x - fm.stringWidth(label) / 2, y + fm.getAscent() / 2 - 2);
    }
}
