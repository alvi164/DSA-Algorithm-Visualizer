package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class MemoryModule extends BaseModulePanel {
    private String allocMode = "Array";
    private Point hoveredBlock = null;
    private int[][] ramGrid = new int[8][14]; 
    private int baseAddress = 0x1000;
    private int elementSize = 4; 

    private int[] treeX = {7, 3, 11, 1, 5, 9, 13};
    private int[] treeY = {1, 3, 3, 5, 5, 5, 5};

    @Override
    protected void setupControls() {
        controlPanel.add(new JLabel("<html><b>Memory Allocation & Access Math</b><br>Hover over memory blocks to view OS calculations.</html>"));
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JRadioButton arrayBtn = new JRadioButton("Array (Contiguous Memory)", true);
        JRadioButton treeBtn = new JRadioButton("Binary Tree (Scattered Pointers)");
        ButtonGroup bg = new ButtonGroup();
        bg.add(arrayBtn); bg.add(treeBtn);

        arrayBtn.addActionListener(e -> { allocMode = "Array"; canvasPanel.repaint(); });
        treeBtn.addActionListener(e -> { allocMode = "Tree"; canvasPanel.repaint(); });

        controlPanel.add(arrayBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(treeBtn);

        JPanel mathPanel = new JPanel();
        mathPanel.setLayout(new BoxLayout(mathPanel, BoxLayout.Y_AXIS));
        mathPanel.setBackground(new Color(245, 245, 250));
        mathPanel.setBorder(BorderFactory.createTitledBorder("Background Mathematics"));
        mathPanel.add(new JLabel("<html><b>Array Formula:</b><br>Addr = Base + (Index * Size)</html>"));
        mathPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mathPanel.add(new JLabel("<html><b>Tree Formula:</b><br>Addr = Dynamic Heap Pointer<br>(No direct index math, requires traversal)</html>"));

        controlPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        controlPanel.add(mathPanel);

        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = (e.getX() - 50) / 50;
                int row = (e.getY() - 50) / 50;
                if (col >= 0 && col < 14 && row >= 0 && row < 8) hoveredBlock = new Point(col, row);
                else hoveredBlock = null;
                canvasPanel.repaint();
            }
        });
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1));

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("System RAM Map (Each block = 4 Bytes)", 50, 30);

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 14; c++) {
                int x = 50 + (c * 50);
                int y = 50 + (r * 50);
                boolean isAllocated = false;

                if (allocMode.equals("Array")) {
                    if (r == 3 && c >= 2 && c <= 11) isAllocated = true; 
                } else {
                    for (int i = 0; i < treeX.length; i++) {
                        if (c == treeX[i] && r == treeY[i]) isAllocated = true; 
                    }
                }

                if (hoveredBlock != null && hoveredBlock.x == c && hoveredBlock.y == r) {
                    g2d.setColor(Color.YELLOW); 
                } else if (isAllocated) {
                    g2d.setColor(allocMode.equals("Array") ? new Color(100, 200, 100) : new Color(100, 149, 237));
                } else {
                    g2d.setColor(new Color(240, 240, 240)); 
                }

                g2d.fillRect(x, y, 50, 50);
                g2d.setColor(Color.GRAY);
                g2d.drawRect(x, y, 50, 50);
            }
        }

        if (allocMode.equals("Tree")) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            drawPointer(g2d, 7, 1, 3, 3); 
            drawPointer(g2d, 7, 1, 11, 3); 
            drawPointer(g2d, 3, 3, 1, 5);
            drawPointer(g2d, 3, 3, 5, 5);
            drawPointer(g2d, 11, 3, 9, 5);
            drawPointer(g2d, 11, 3, 13, 5);
        }

        if (hoveredBlock != null) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 16));

            int blockIndex = (hoveredBlock.y * 14) + hoveredBlock.x;
            int absoluteAddr = baseAddress + (blockIndex * elementSize);

            g2d.drawString("Hovered Block Global Index: " + blockIndex, 50, 500);

            if (allocMode.equals("Array") && hoveredBlock.y == 3 && hoveredBlock.x >= 2 && hoveredBlock.x <= 11) {
                int arrayIndex = hoveredBlock.x - 2;
                g2d.setColor(new Color(34, 139, 34));
                g2d.drawString("Array Index [ " + arrayIndex + " ] Accessed!", 50, 530);
                g2d.drawString("Calculated Physical Address = 0x" + Integer.toHexString(baseAddress) + " + (" + arrayIndex + " * 4)", 50, 560);
                g2d.drawString("Resulting RAM Pointer: 0x" + Integer.toHexString(absoluteAddr).toUpperCase(), 50, 590);
            } else if (allocMode.equals("Tree")) {
                g2d.setColor(Color.BLUE);
                g2d.drawString("Node Physical RAM Pointer: 0x" + Integer.toHexString(absoluteAddr).toUpperCase(), 50, 530);
                g2d.drawString("Note: Trees cannot be accessed via index math.", 50, 560);
                g2d.drawString("The OS must read the pointer stored in the Parent Node.", 50, 590);
            }
        }
    }

    private void drawPointer(Graphics2D g2d, int c1, int r1, int c2, int r2) {
        int x1 = 50 + (c1 * 50) + 25; int y1 = 50 + (r1 * 50) + 25;
        int x2 = 50 + (c2 * 50) + 25; int y2 = 50 + (r2 * 50) + 25;
        g2d.drawLine(x1, y1, x2, y2);
        g2d.fillOval(x2 - 5, y2 - 5, 10, 10);
    }
}
