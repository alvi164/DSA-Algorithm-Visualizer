package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class GraphTreeViewerModule extends BaseModulePanel {
    private JComboBox<String> typeSelector;

    @Override
    protected void setupControls() {
        controlPanel.add(new JLabel("View 3D Structural Model:"));
        typeSelector = new JComboBox<>(new String[]{
                "Graph: Connected", "Graph: Disconnected", "Graph: Isolated",
                "Tree: Balanced Binary", "Tree: Imbalanced"
        });
        typeSelector.addActionListener(e -> canvasPanel.repaint());
        controlPanel.add(typeSelector);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel legend = new JPanel(new GridLayout(4,1));
        legend.setBackground(new Color(245,245,250));
        legend.setBorder(BorderFactory.createTitledBorder("Relational Terminology"));
        legend.add(new JLabel("<html><b>Father/Parent:</b> Node above connecting downward.</html>"));
        legend.add(new JLabel("<html><b>Child:</b> Node directly below a Parent.</html>"));
        legend.add(new JLabel("<html><b>Ancestor:</b> Any node traversing UP to the root.</html>"));
        legend.add(new JLabel("<html><b>Descendant:</b> Any node traversing DOWN to leaves.</html>"));
        controlPanel.add(legend);
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        String type = (String) typeSelector.getSelectedItem();
        int cx = canvasPanel.getWidth() / 2;
        int cy = 100;

        g2d.setStroke(new BasicStroke(3));
        g2d.setColor(Color.GRAY);

        if (type.equals("Graph: Connected")) {
            drawLine(g2d, cx, cy, cx-100, cy+100); drawLine(g2d, cx, cy, cx+100, cy+100);
            drawLine(g2d, cx-100, cy+100, cx, cy+200); drawLine(g2d, cx+100, cy+100, cx, cy+200);
            drawLine(g2d, cx-100, cy+100, cx+100, cy+100); 
            draw3DNode(g2d, cx, cy, "V1"); draw3DNode(g2d, cx-100, cy+100, "V2");
            draw3DNode(g2d, cx+100, cy+100, "V3"); draw3DNode(g2d, cx, cy+200, "V4");
        }
        else if (type.equals("Graph: Disconnected")) {
            drawLine(g2d, cx-100, cy, cx-100, cy+100); 
            drawLine(g2d, cx+100, cy, cx+100, cy+100); 
            draw3DNode(g2d, cx-100, cy, "A"); draw3DNode(g2d, cx-100, cy+100, "B");
            draw3DNode(g2d, cx+100, cy, "C"); draw3DNode(g2d, cx+100, cy+100, "D");
            g2d.setColor(Color.BLACK); g2d.drawString("Two separate components. No path between A/B and C/D.", cx-150, cy+150);
        }
        else if (type.equals("Graph: Isolated")) {
            draw3DNode(g2d, cx, cy+50, "Loner");
            g2d.setColor(Color.BLACK); g2d.drawString("Isolated Node: 0 Edges connected.", cx-80, cy+100);
        }
        else if (type.equals("Tree: Balanced Binary")) {
            drawLine(g2d, cx, cy, cx-150, cy+100); drawLine(g2d, cx, cy, cx+150, cy+100);
            drawLine(g2d, cx-150, cy+100, cx-200, cy+200); drawLine(g2d, cx-150, cy+100, cx-100, cy+200);
            drawLine(g2d, cx+150, cy+100, cx+100, cy+200); drawLine(g2d, cx+150, cy+100, cx+200, cy+200);
            
            draw3DNode(g2d, cx, cy, "Root");
            draw3DNode(g2d, cx-150, cy+100, "L-Child"); draw3DNode(g2d, cx+150, cy+100, "R-Child");
            draw3DNode(g2d, cx-200, cy+200, "Leaf"); draw3DNode(g2d, cx-100, cy+200, "Leaf");
            draw3DNode(g2d, cx+100, cy+200, "Leaf"); draw3DNode(g2d, cx+200, cy+200, "Leaf");

            g2d.setColor(Color.RED); g2d.drawString("<- Father/Parent of L-Child", cx + 30, cy);
            g2d.setColor(Color.BLUE); g2d.drawString("<- Ancestor of Leaves", cx + 180, cy+100);
        }
        else if (type.equals("Tree: Imbalanced")) {
            drawLine(g2d, cx, cy, cx+50, cy+100);
            drawLine(g2d, cx+50, cy+100, cx+100, cy+200);
            drawLine(g2d, cx+100, cy+200, cx+150, cy+300);
            draw3DNode(g2d, cx, cy, "Root");
            draw3DNode(g2d, cx+50, cy+100, "N1");
            draw3DNode(g2d, cx+100, cy+200, "N2");
            draw3DNode(g2d, cx+150, cy+300, "Leaf");
            g2d.setColor(Color.BLACK); g2d.drawString("Highly Right-Skewed (Looks like a Linked List)", cx-150, cy+150);
        }
    }

    private void drawLine(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(x1, y1, x2, y2);
    }

    private void draw3DNode(Graphics2D g2d, int x, int y, String label) {
        int radius = 30;

        g2d.setColor(new Color(0, 0, 0, 80));
        g2d.fillOval(x - radius + 5, y - radius + 5, radius * 2, radius * 2);

        Point2D center = new Point2D.Float(x, y);
        Point2D focus = new Point2D.Float(x - 10, y - 10);
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {Color.CYAN.brighter(), Color.BLUE.darker()};
        RadialGradientPaint p = new RadialGradientPaint(center, radius, focus, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);

        g2d.setPaint(p);
        g2d.fillOval(x - radius, y - radius, radius * 2, radius * 2);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawOval(x - radius, y - radius, radius * 2, radius * 2);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label, x - fm.stringWidth(label) / 2, y + fm.getAscent() / 2 - 2);
    }
}
