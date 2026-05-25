package org.example;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GraphTraversalModule extends BaseModulePanel {

    class Node {
        int id, x, y;
        Color color = Color.WHITE;
        public Node(int id, int x, int y) { this.id = id; this.x = x; this.y = y; }
    }

    class Edge {
        Node n1, n2;
        boolean isPath = false;
        public Edge(Node n1, Node n2) { this.n1 = n1; this.n2 = n2; }
    }

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private int nextNodeId = 0;

    private Node dragStartNode = null;
    private Point dragCurrentPoint = null;

    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    private volatile boolean shouldStop = false;
    private Thread traversalThread = null;
    private int stepCounter = 1;

    private LinkedList<Node> activeQueue = new LinkedList<>();
    private Stack<Node> activeStack = new Stack<>();
    private List<Integer> finalPath = new ArrayList<>();
    private String currentAlgo = "BFS";

    private JComboBox<String> algoSelector;
    private DefaultListModel<String> pseudoCodeModel;
    private JList<String> pseudoCodeList;
    private JTextArea traceArea;
    private JButton pauseBtn;

    public GraphTraversalModule() {
        super();
        setupMouseInteractions();
    }

    @Override
    protected void setupControls() {
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        controlPanel.add(new JLabel("• Click canvas to Add Node"));
        controlPanel.add(new JLabel("• Drag between nodes for Edge"));
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        controlPanel.add(new JLabel("Algorithm:"));
        algoSelector = new JComboBox<>(new String[]{"Breadth-First Search (BFS)", "Depth-First Search (DFS)"});
        algoSelector.addActionListener(e -> {
            updatePseudocode();
            if (isRunning && isPaused) {
                runTraversal();
            }
        });
        algoSelector.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(algoSelector);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton runBtn = new JButton("Run Traversal");
        runBtn.setBackground(new Color(34, 139, 34));
        runBtn.setForeground(Color.WHITE);
        runBtn.addActionListener(e -> runTraversal());

        pauseBtn = new JButton("Pause");
        pauseBtn.setEnabled(false);
        pauseBtn.addActionListener(e -> togglePause());

        btnPanel.add(runBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(pauseBtn);

        controlPanel.add(btnPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton clearBtn = new JButton("Clear Canvas");
        clearBtn.addActionListener(e -> {
            if(!isRunning || isPaused) {
                shouldStop = true;
                if (traversalThread != null) traversalThread.interrupt();
                nodes.clear(); edges.clear(); nextNodeId=0; finalPath.clear();
                activeQueue.clear(); activeStack.clear();
                isRunning = false; isPaused = false; pauseBtn.setEnabled(false);
                canvasPanel.repaint(); clearTrace();
            }
        });
        controlPanel.add(clearBtn);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        controlPanel.add(new JLabel("Real-Time Pseudocode:"));
        pseudoCodeModel = new DefaultListModel<>();
        pseudoCodeList = new JList<>(pseudoCodeModel);
        pseudoCodeList.setBackground(new Color(40, 44, 52));
        pseudoCodeList.setForeground(new Color(171, 178, 191));
        pseudoCodeList.setSelectionBackground(new Color(229, 192, 123));
        pseudoCodeList.setSelectionForeground(Color.BLACK);
        pseudoCodeList.setFont(new Font("Monospaced", Font.BOLD, 12));
        controlPanel.add(new JScrollPane(pseudoCodeList));

        updatePseudocode();

        traceArea = new JTextArea();
        traceArea.setEditable(false);
        traceArea.setFont(new Font("Consolas", Font.BOLD, 13));
        traceArea.setBackground(new Color(20, 20, 25));
        traceArea.setForeground(new Color(0, 255, 100));
        traceArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane traceScroll = new JScrollPane(traceArea);
        TitledBorder traceBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100)),
                "Real-Time Algo Trace (Dry Run Table)");
        traceBorder.setTitleColor(Color.DARK_GRAY);
        traceScroll.setBorder(traceBorder);
        traceScroll.setPreferredSize(new Dimension(0, 200));

        SwingUtilities.invokeLater(() -> {
            this.add(traceScroll, BorderLayout.SOUTH);
            this.revalidate();
            this.repaint();
        });
    }

    private void togglePause() {
        if (isRunning && !shouldStop) {
            isPaused = !isPaused;
            pauseBtn.setText(isPaused ? "Resume" : "Pause");
            if (isPaused) updateTrace("\n[!] TRAVERSAL PAUSED\n");
            else updateTrace("\n[!] TRAVERSAL RESUMED\n");
        }
    }

    private void updatePseudocode() {
        currentAlgo = (String) algoSelector.getSelectedItem();
        pseudoCodeModel.clear();
        if (currentAlgo.contains("BFS")) {
            pseudoCodeModel.addElement("1. Enqueue Start Node");
            pseudoCodeModel.addElement("2. While Queue is not empty:");
            pseudoCodeModel.addElement("3.   u = Dequeue()");
            pseudoCodeModel.addElement("4.   Mark u as Visited (GREEN)");
            pseudoCodeModel.addElement("5.   For each unvisited neighbor v:");
            pseudoCodeModel.addElement("6.     Mark v Discovered (YELLOW)");
            pseudoCodeModel.addElement("7.     Enqueue v");
        } else {
            pseudoCodeModel.addElement("1. Push Start Node to Stack");
            pseudoCodeModel.addElement("2. While Stack is not empty:");
            pseudoCodeModel.addElement("3.   u = Pop()");
            pseudoCodeModel.addElement("4.   if u is not Visited:");
            pseudoCodeModel.addElement("5.     Mark u as Visited (GREEN)");
            pseudoCodeModel.addElement("6.     For each unvisited neighbor v:");
            pseudoCodeModel.addElement("7.       Push v to Stack (YELLOW)");
        }
        if (!isRunning || isPaused) {
            if (activeQueue != null) activeQueue.clear();
            if (activeStack != null) activeStack.clear();
            if (canvasPanel != null) canvasPanel.repaint();
        }
    }

    private void sleep(double actionMultiplier) {
        try {
            final long BASE_DELAY_MS = 500;

            double speedFactor = 1.0;
            if (speedSlider != null) {
                int val = speedSlider.getValue();
                int min = speedSlider.getMinimum();
                int max = speedSlider.getMaximum();
                if (max > min) {
                    double t = (double)(val - min) / (max - min);
                    final double MIN_SPEED = 0.1;
                    final double MAX_SPEED = 10.0;
                    speedFactor = MIN_SPEED + t * (MAX_SPEED - MIN_SPEED);
                }
            }

            long delay = (long)(BASE_DELAY_MS * actionMultiplier / speedFactor);
            delay = Math.min(2000, Math.max(10, delay));

            Thread.sleep(delay);

            while (isPaused && !shouldStop) {
                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            shouldStop = true;
        }
    }

    private void highlightCode(int line) {
        SwingUtilities.invokeLater(() -> pseudoCodeList.setSelectedIndex(line));
    }

    private void updateTrace(String text) {
        SwingUtilities.invokeLater(() -> {
            traceArea.append(text);
            traceArea.setCaretPosition(traceArea.getDocument().getLength());
        });
    }

    private void clearTrace() {
        SwingUtilities.invokeLater(() -> traceArea.setText(""));
    }

    private void printTraceHeader() {
        String dsName = currentAlgo.contains("BFS") ? "Queue" : "Stack";
        String header = String.format("%-5s | %-30s | %-20s | %s\n", "Step", "Action", dsName, "Output (Visited)");
        String sep = "---------------------------------------------------------------------------------------------\n";
        updateTrace(header + sep);
    }

    private void printTraceRow(String action) {
        if (shouldStop) return;
        String dsString = currentAlgo.contains("BFS") ? getQueueString() : getStackString();
        String outString = finalPath.toString();
        String row = String.format("%-5d | %-30s | %-20s | %s\n", stepCounter++, action, dsString, outString);
        updateTrace(row);
    }

    private String getQueueString() {
        List<Integer> ids = new ArrayList<>();
        for (Node n : activeQueue) ids.add(n.id);
        return ids.toString();
    }

    private String getStackString() {
        List<Integer> ids = new ArrayList<>();
        for (Node n : activeStack) ids.add(n.id);
        return ids.toString();
    }

    private void setupMouseInteractions() {
        canvasPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (isRunning || e.getX() > canvasPanel.getWidth() - 200) return;
                dragStartNode = getNodeAt(e.getPoint());
                if (dragStartNode == null) {
                    nodes.add(new Node(nextNodeId++, e.getX(), e.getY()));
                    updateTrace("Added Node " + (nextNodeId - 1) + "\n");
                    canvasPanel.repaint();
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (isRunning || dragStartNode == null) return;
                Node dragEndNode = getNodeAt(e.getPoint());
                if (dragEndNode != null && dragEndNode != dragStartNode) {
                    edges.add(new Edge(dragStartNode, dragEndNode));
                    updateTrace("Added Edge (" + dragStartNode.id + " - " + dragEndNode.id + ")\n");
                }
                dragStartNode = null; dragCurrentPoint = null;
                canvasPanel.repaint();
            }
        });
        canvasPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (isRunning || dragStartNode == null) return;
                dragCurrentPoint = e.getPoint(); canvasPanel.repaint();
            }
        });
    }

    private Node getNodeAt(Point p) {
        for (Node n : nodes) if (p.distance(n.x, n.y) <= 25) return n;
        return null;
    }

    private void runTraversal() {
        if (nodes.isEmpty()) return;

        if (isRunning) {
            shouldStop = true;
            if (traversalThread != null) traversalThread.interrupt();
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        isRunning = true;
        isPaused = false;
        shouldStop = false;
        stepCounter = 1;

        SwingUtilities.invokeLater(() -> {
            pauseBtn.setEnabled(true);
            pauseBtn.setText("Pause");
        });

        activeQueue.clear(); activeStack.clear(); finalPath.clear(); clearTrace();
        for (Node n : nodes) n.color = Color.WHITE;
        for (Edge e : edges) e.isPath = false;
        canvasPanel.repaint();

        traversalThread = new Thread(() -> {
            try {
                printTraceHeader();
                if (currentAlgo.contains("BFS")) executeBFS(nodes.get(0));
                else executeDFS(nodes.get(0));
            } catch (Exception e) { e.printStackTrace(); }

            if (!shouldStop) {
                isRunning = false;
                SwingUtilities.invokeLater(() -> {
                    pauseBtn.setEnabled(false);
                    pauseBtn.setText("Pause");
                });
                highlightCode(-1);
                updateTrace("---------------------------------------------------------------------------------------------\n");
                updateTrace("TRAVERSAL COMPLETE. Final Path: " + finalPath.toString() + "\n");
            }
        });
        traversalThread.start();
    }

    private void executeBFS(Node start) {
        if (shouldStop) return;

        highlightCode(0);
        activeQueue.add(start);
        start.color = Color.YELLOW;
        canvasPanel.repaint();
        printTraceRow("Enqueue Start Node " + start.id);
        sleep(1.0); if(shouldStop) return;

        while (!activeQueue.isEmpty() && !shouldStop) {
            highlightCode(1); sleep(0.5); if(shouldStop) return;

            highlightCode(2);
            Node u = activeQueue.poll();
            canvasPanel.repaint();

            highlightCode(3);
            u.color = Color.GREEN;
            finalPath.add(u.id);
            canvasPanel.repaint();
            printTraceRow("Dequeue Node " + u.id + " (Visited)");
            sleep(1.0); if(shouldStop) return;

            highlightCode(4); sleep(0.5); if(shouldStop) return;

            List<Integer> enqueuedIds = new ArrayList<>();
            for (Node v : getNeighbors(u)) {
                if (v.color == Color.WHITE) {
                    highlightCode(5);
                    v.color = Color.YELLOW;
                    markEdge(u, v);
                    canvasPanel.repaint();
                    sleep(0.5); if(shouldStop) return;

                    highlightCode(6);
                    activeQueue.add(v);
                    enqueuedIds.add(v.id);
                    canvasPanel.repaint();
                    sleep(0.5); if(shouldStop) return;
                }
            }
            if (!enqueuedIds.isEmpty()) {
                printTraceRow("Enqueue Neighbors of " + u.id + ": " + enqueuedIds);
            }
        }
    }

    private void executeDFS(Node start) {
        if (shouldStop) return;

        highlightCode(0);
        activeStack.push(start);
        start.color = Color.YELLOW;
        canvasPanel.repaint();
        printTraceRow("Push Start Node " + start.id);
        sleep(1.0); if(shouldStop) return;

        while (!activeStack.isEmpty() && !shouldStop) {
            highlightCode(1); sleep(0.5); if(shouldStop) return;

            highlightCode(2);
            Node u = activeStack.pop();
            canvasPanel.repaint();

            highlightCode(3); sleep(0.5); if(shouldStop) return;
            if (u.color != Color.GREEN) {
                highlightCode(4);
                u.color = Color.GREEN;
                finalPath.add(u.id);
                canvasPanel.repaint();
                printTraceRow("Pop Node " + u.id + " (Visited)");
                sleep(1.0); if(shouldStop) return;

                highlightCode(5); sleep(0.5); if(shouldStop) return;

                List<Integer> pushedIds = new ArrayList<>();
                for (Node v : getNeighbors(u)) {
                    if (v.color != Color.GREEN) {
                        markEdge(u, v);
                        v.color = Color.YELLOW;
                        highlightCode(6);
                        activeStack.push(v);
                        pushedIds.add(v.id);
                        canvasPanel.repaint();
                        sleep(0.5); if(shouldStop) return;
                    }
                }
                if (!pushedIds.isEmpty()) {
                    printTraceRow("Push Neighbors of " + u.id + ": " + pushedIds);
                }
            } else {
                printTraceRow("Pop Node " + u.id + " (Skip Visited)");
            }
        }
    }

    private List<Node> getNeighbors(Node n) {
        List<Node> neighbors = new ArrayList<>();
        for (Edge e : edges) {
            if (e.n1 == n) neighbors.add(e.n2);
            if (e.n2 == n) neighbors.add(e.n1);
        }
        return neighbors;
    }

    private void markEdge(Node u, Node v) {
        for (Edge e : edges) {
            if ((e.n1 == u && e.n2 == v) || (e.n2 == u && e.n1 == v)) e.isPath = true;
        }
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        int w = canvasPanel.getWidth(), h = canvasPanel.getHeight();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Edge e : edges) {
            g2d.setColor(e.isPath ? Color.RED : Color.GRAY);
            g2d.setStroke(new BasicStroke(e.isPath ? 4 : 2));
            g2d.drawLine(e.n1.x, e.n1.y, e.n2.x, e.n2.y);
        }

        if (dragStartNode != null && dragCurrentPoint != null) {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(dragStartNode.x, dragStartNode.y, dragCurrentPoint.x, dragCurrentPoint.y);
        }

        int radius = 25;
        for (Node n : nodes) {
            drawNode(g2d, n.id, n.x - radius, n.y - radius, n.color, "");
        }

        int dsX = w - 200;
        g2d.setColor(new Color(240, 240, 245));
        g2d.fillRect(dsX, 0, 200, h);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(dsX, 0, dsX, h);

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);

        if (currentAlgo.contains("BFS")) {
            g2d.drawString("Live Queue", dsX + 55, 30);
            g2d.drawRect(dsX + 75, 40, 50, h - 60);

            int yOff = 50;
            for (Node n : new ArrayList<>(activeQueue)) {
                drawNode(g2d, n.id, dsX + 75, yOff, Color.YELLOW, "");
                yOff += 55;
            }
        } else {
            g2d.drawString("Live Stack", dsX + 55, 30);
            g2d.drawRect(dsX + 75, 40, 50, h - 60);

            int yOff = h - 80;
            for (Node n : new ArrayList<>(activeStack)) {
                drawNode(g2d, n.id, dsX + 75, yOff, Color.YELLOW, "");
                yOff -= 55;
            }
        }

        if (!finalPath.isEmpty()) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("Final Path: " + finalPath.toString(), 20, h - 20);
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
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
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
