package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class DSAFundamentalsTourModule extends BaseModulePanel {
    private int currentSlide = 0;
    private final int TOTAL_SLIDES = 7;

    private JButton prevBtn, nextBtn;
    private JTextArea explanationArea;
    private JProgressBar tourProgress;
    private JLabel stepLabel;

    @Override
    protected void setupControls() {
        controlPanel.setLayout(new BorderLayout(5, 10));
        controlPanel.setBackground(new Color(245, 245, 250));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel navContainer = new JPanel(new BorderLayout(5, 5));
        navContainer.setBackground(new Color(245, 245, 250));

        stepLabel = new JLabel("Module 1 of " + TOTAL_SLIDES, SwingConstants.CENTER);
        stepLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(new Color(245, 245, 250));

        prevBtn = new JButton("◀ Previous");
        prevBtn.addActionListener(e -> changeSlide(-1));

        nextBtn = new JButton("Next ▶");
        nextBtn.addActionListener(e -> changeSlide(1));

        btnPanel.add(prevBtn);
        btnPanel.add(nextBtn);

        tourProgress = new JProgressBar(0, TOTAL_SLIDES - 1);
        tourProgress.setValue(0);
        tourProgress.setForeground(new Color(34, 139, 34));

        navContainer.add(stepLabel, BorderLayout.NORTH);
        navContainer.add(btnPanel, BorderLayout.CENTER);
        navContainer.add(tourProgress, BorderLayout.SOUTH);

        controlPanel.add(navContainer, BorderLayout.NORTH);

        explanationArea = new JTextArea();
        explanationArea.setEditable(false);
        explanationArea.setWrapStyleWord(true);
        explanationArea.setLineWrap(true);
        explanationArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        explanationArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(explanationArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Concept Breakdown"));
        controlPanel.add(scrollPane, BorderLayout.CENTER);

        updateSlideContent();
    }

    private void changeSlide(int direction) {
        currentSlide += direction;
        prevBtn.setEnabled(currentSlide > 0);
        nextBtn.setEnabled(currentSlide < TOTAL_SLIDES - 1);
        stepLabel.setText("Module " + (currentSlide + 1) + " of " + TOTAL_SLIDES);
        tourProgress.setValue(currentSlide);
        updateSlideContent();
        canvasPanel.repaint();
    }

    private void updateSlideContent() {
        String text = "";
        switch (currentSlide) {
            case 0:
                text = "WELCOME TO DSA MASTERY\n\n"
                        + "Data Structures and Algorithms (DSA) dictate how software stores data (Structures) and solves problems (Algorithms).\n\n"
                        + "TIME & SPACE COMPLEXITY (Big O):\n"
                        + "- O(1) Constant: Speed never changes, regardless of data size.\n"
                        + "- O(log n) Logarithmic: Time halves each step (very efficient).\n"
                        + "- O(n) Linear: Time scales directly with data size.\n"
                        + "- O(n^2) Quadratic: Time squares as data grows (often bad).";
                break;
            case 1:
                text = "MEMORY ALLOCATION: HOW RAM WORKS\n\n"
                        + "1. STATIC vs. DYNAMIC:\n"
                        + "- Static (Stack Memory): Fixed size, allocated at compile time. Fast, but rigid.\n"
                        + "- Dynamic (Heap Memory): Flexible size, allocated at runtime. Slower to access, but grows endlessly.\n\n"
                        + "2. CONTIGUOUS vs. SCATTERED:\n"
                        + "- Arrays (Contiguous): Blocks of memory placed directly next to each other. O(1) access time via math (Index * Size).\n"
                        + "- Nodes (Scattered): Memory placed randomly in the Heap. Connected by 'Pointers' (memory addresses). Requires traveling O(n) to find data.";
                break;
            case 2:
                text = "LINKED LISTS: THE SCATTERED CHAIN\n\n"
                        + "A sequence of Nodes connected by pointers. They do not need contiguous memory.\n\n"
                        + "TYPES OF LINKED LISTS:\n"
                        + "1. Singly Linked List: Each node points only to the NEXT node. You can only travel forward.\n"
                        + "2. Doubly Linked List: Each node has two pointers (NEXT and PREV). You can travel forward and backward, but it uses more memory.\n"
                        + "3. Circular Linked List: The Tail (last node) points back to the Head (first node) instead of null. Great for round-robin tasks like CPU scheduling.";
                break;
            case 3:
                text = "LINEAR RULES: STACKS & QUEUES\n\n"
                        + "STACKS (LIFO: Last-In, First-Out):\n"
                        + "Like a stack of books. You only Push (add) and Pop (remove) from the Top.\n"
                        + "- Uses: Undo buttons, Browser history, Call Stack memory.\n\n"
                        + "QUEUES (FIFO: First-In, First-Out):\n"
                        + "Like a line at a store. Enqueue (add) at the Back, Dequeue (remove) from the Front.\n"
                        + "- Uses: Printer tasks, Web server requests, BFS algorithms.\n\n"
                        + "PRIORITY QUEUE (PQ):\n"
                        + "Unlike a standard FIFO queue, a PQ orders elements strictly by their 'priority' or weight.\n"
                        + "- High-priority elements bypass the line and are Dequeued first, regardless of when they arrived.\n"
                        + "- Uses: Hospital emergency rooms (triage), CPU task scheduling, Dijkstra's Shortest Path.";
                break;
            case 4:
                text = "TREES: HIERARCHICAL DATA\n\n"
                        + "Data branching downward from a single Root node to Leaf nodes.\n\n"
                        + "TYPES OF TREES:\n"
                        + "1. Binary Search Tree (BST): Left child is strictly smaller, Right child is strictly larger. Makes searching O(log n).\n"
                        + "2. General Tree / N-ary Tree: A node can have any number of children.\n"
                        + "3. Balanced Trees (Red-Black / AVL): Auto-colors and rotates to prevent becoming a straight line. Guarantees fast searches.\n"
                        + "4. Trie (Prefix Tree): Optimized for storing strings. Used in Autocomplete.";
                break;
            case 5:
                text = "GRAPHS: COMPLEX NETWORKS\n\n"
                        + "Nodes (Vertices) connected by Edges. Can contain loops and disconnected islands.\n\n"
                        + "TYPES OF GRAPHS:\n"
                        + "1. Undirected & Unweighted: Two-way streets, equal distance.\n"
                        + "2. Directed, Weighted, & Cyclic: One-way streets with costs/distances, containing loops where you can travel in a circle.\n"
                        + "3. DAG (Directed Acyclic Graph): Directed edges but NO loops. Used for scheduling tasks with prerequisites.";
                break;
            case 6:
                text = "COURSE COMPLETE\n\n"
                        + "You now understand the fundamental memory structures and abstract data types of computer science.\n\n"
                        + "Head to the Sorting Module, Tree Module, or Graph Module to see algorithms manipulate these exact structures in real-time.";
                break;
        }
        explanationArea.setText(text);
        explanationArea.setCaretPosition(0);
    }

    private final int NODE_RADIUS = 22;

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        int w = canvasPanel.getWidth();
        int h = canvasPanel.getHeight();
        int cx = w / 2;
        int cy = h / 2;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, w, h);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (currentSlide) {
            case 0: drawComplexityGraphs(g2d, cx, cy); break;
            case 1: drawMemoryTypes(g2d, cx, cy); break;
            case 2: drawAllLinkedLists(g2d, cx, cy, w); break;
            case 3: drawStacksAndQueues(g2d, cx, cy); break;
            case 4: drawAllTrees(g2d, cx, cy); break;
            case 5: drawAllGraphs(g2d, cx, cy); break;
            case 6: drawCredits(g2d, cx, cy); break;
        }
    }

    private void drawComplexityGraphs(Graphics2D g2d, int cx, int cy) {
        int ox = cx - 200;
        int oy = cy + 150;

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(ox, oy, ox + 450, oy);
        g2d.drawLine(ox, oy, ox, oy - 350);

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Data Elements (n)", ox + 320, oy + 30);
        g2d.drawString("Operations (Time)", ox - 50, oy - 360);

        g2d.setStroke(new BasicStroke(4));

        g2d.setColor(new Color(34, 139, 34));
        g2d.drawLine(ox, oy - 40, ox + 400, oy - 40);
        g2d.drawString("O(1)", ox + 410, oy - 35);

        g2d.setColor(new Color(30, 144, 255));
        Path2D logPath = new Path2D.Double();
        logPath.moveTo(ox, oy);
        for (int x = 1; x <= 400; x++) {
            double y = oy - (Math.log(x) * 20);
            logPath.lineTo(ox + x, y);
        }
        g2d.draw(logPath);
        g2d.drawString("O(log n)", ox + 410, oy - 120);

        g2d.setColor(new Color(255, 140, 0));
        g2d.drawLine(ox, oy, ox + 300, oy - 300);
        g2d.drawString("O(n)", ox + 310, oy - 300);

        g2d.setColor(Color.RED);
        Path2D quadPath = new Path2D.Double();
        quadPath.moveTo(ox, oy);
        for (int x = 0; x <= 200; x++) {
            double y = oy - (Math.pow(x, 2) / 100);
            if (y > oy - 350) quadPath.lineTo(ox + x, y);
        }
        g2d.draw(quadPath);
        g2d.drawString("O(n²)", ox + 190, oy - 350);
    }

    private void drawMemoryTypes(Graphics2D g2d, int cx, int cy) {
        g2d.setFont(new Font("Arial", Font.BOLD, 18));

        g2d.setColor(Color.BLACK);
        g2d.drawString("1. Contiguous Memory (Array / Stack)", cx - 250, cy - 150);

        int startX = cx - 250;
        for (int i = 0; i < 5; i++) {
            int x = startX + (i * 90);
            g2d.setColor(new Color(70, 130, 180));
            g2d.fillRect(x, cy - 120, 90, 60);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x, cy - 120, 90, 60);

            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("Data" + (i+1), x + 20, cy - 85);
            g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString("0x10" + (i*4), x + 15, cy - 40);
        }

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("2. Scattered Memory (Nodes / Heap)", cx - 250, cy + 40);

        drawMemoryNode(g2d, cx - 200, cy + 130, "A", "0x2F4");
        drawMemoryNode(g2d, cx, cy + 200, "B", "0x8A1");
        drawMemoryNode(g2d, cx + 200, cy + 100, "C", "0x4B2");

        drawNodeArrow(g2d, cx - 200, cy + 130, cx, cy + 200, "next");
        drawNodeArrow(g2d, cx, cy + 200, cx + 200, cy + 100, "next");
    }

    private void drawAllLinkedLists(Graphics2D g2d, int cx, int cy, int width) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));

        int row1 = cy - 180;
        int row2 = cy;
        int row3 = cy + 180;

        g2d.drawString("1. Singly Linked List", cx - 300, row1 - 40);
        drawNode(g2d, cx - 150, row1, "A", Color.CYAN, Color.BLACK);
        drawNode(g2d, cx, row1, "B", Color.CYAN, Color.BLACK);
        drawNode(g2d, cx + 150, row1, "C", Color.CYAN, Color.BLACK);
        drawNodeArrow(g2d, cx - 150, row1, cx, row1, "");
        drawNodeArrow(g2d, cx, row1, cx + 150, row1, "");
        drawSimpleArrow(g2d, cx + 150 + NODE_RADIUS, row1, cx + 230, row1);
        g2d.drawString("NULL", cx + 240, row1 + 5);

        g2d.drawString("2. Doubly Linked List", cx - 300, row2 - 40);
        g2d.drawString("NULL", cx - 280, row2 + 5);
        drawSimpleArrow(g2d, cx - 150 - NODE_RADIUS, row2, cx - 220, row2);

        drawNode(g2d, cx - 150, row2, "X", Color.CYAN, Color.BLACK);
        drawNode(g2d, cx, row2, "Y", Color.CYAN, Color.BLACK);

        drawOffsetArrow(g2d, cx - 150, row2, cx, row2, -10); 
        drawOffsetArrow(g2d, cx, row2, cx - 150, row2, 10);  

        drawSimpleArrow(g2d, cx + NODE_RADIUS, row2, cx + 100, row2);
        g2d.drawString("NULL", cx + 110, row2 + 5);

        g2d.drawString("3. Circular Linked List", cx - 300, row3 - 40);
        drawNode(g2d, cx - 150, row3, "H", Color.CYAN, Color.BLACK);
        drawNode(g2d, cx, row3, "M", Color.CYAN, Color.BLACK);
        drawNode(g2d, cx + 150, row3, "T", Color.CYAN, Color.BLACK);
        drawNodeArrow(g2d, cx - 150, row3, cx, row3, "");
        drawNodeArrow(g2d, cx, row3, cx + 150, row3, "");

        g2d.setColor(Color.RED);
        g2d.drawArc(cx - 150, row3 - 50, 300, 100, 0, -180);
        g2d.fillPolygon(new int[]{cx - 150, cx - 140, cx - 160}, new int[]{row3 + 50, row3 + 60, row3 + 60}, 3);
    }

    private void drawStacksAndQueues(Graphics2D g2d, int cx, int cy) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));

        int stackX = cx - 250;
        int stackY = cy - 150;
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(stackX, stackY, stackX, stackY + 250);
        g2d.drawLine(stackX, stackY + 250, stackX + 100, stackY + 250);
        g2d.drawLine(stackX + 100, stackY + 250, stackX + 100, stackY);

        g2d.drawString("STACK (LIFO)", stackX - 10, stackY - 30);

        drawNode(g2d, stackX + 50, stackY + 200, "1st", Color.GREEN, Color.BLACK);
        drawNode(g2d, stackX + 50, stackY + 140, "2nd", Color.GREEN, Color.BLACK);
        drawNode(g2d, stackX + 50, stackY + 80, "Top", Color.YELLOW, Color.BLACK);

        drawArrowWithText(g2d, stackX - 80, stackY + 40, stackX + 10, stackY + 80, "Push");
        drawArrowWithText(g2d, stackX + 90, stackY + 80, stackX + 180, stackY + 40, "Pop");

        int queueX = cx + 50;
        int queueY = cy - 180;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(queueX, queueY, queueX + 300, queueY);
        g2d.drawLine(queueX, queueY + 80, queueX + 300, queueY + 80);

        g2d.drawString("STANDARD QUEUE (FIFO)", queueX + 40, queueY - 20);
        drawNode(g2d, queueX + 50, queueY + 40, "1st", Color.GREEN, Color.BLACK);
        drawNode(g2d, queueX + 150, queueY + 40, "2nd", Color.GREEN, Color.BLACK);
        drawNode(g2d, queueX + 250, queueY + 40, "Back", Color.YELLOW, Color.BLACK);

        drawArrowWithText(g2d, queueX + 380, queueY + 40, queueX + 290, queueY + 40, "Enqueue");
        drawArrowWithText(g2d, queueX + 10, queueY + 40, queueX - 80, queueY + 40, "Dequeue");

        int pqX = cx + 50;
        int pqY = cy + 100;
        g2d.setColor(Color.BLACK);
        g2d.drawLine(pqX, pqY, pqX + 300, pqY);
        g2d.drawLine(pqX, pqY + 80, pqX + 300, pqY + 80);
        g2d.drawString("PRIORITY QUEUE (Orders by Rank)", pqX + 10, pqY - 55);

        drawNode(g2d, pqX + 50, pqY + 40, "P1", Color.RED, Color.WHITE);
        drawNode(g2d, pqX + 250, pqY + 40, "P5", Color.LIGHT_GRAY, Color.BLACK);

        drawNode(g2d, pqX + 260, pqY - 30, "P2", Color.ORANGE, Color.BLACK);

        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
        g2d.drawString("Bypasses P5!", pqX + 100, pqY - 35);

        drawSimpleArrow(g2d, pqX + 230, pqY - 30, pqX + 150, pqY - 30);
        drawSimpleArrow(g2d, pqX + 150, pqY - 30, pqX + 150, pqY + 15);
    }

    private void drawAllTrees(Graphics2D g2d, int cx, int cy) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        int tlX = cx - 200, tlY = cy - 150;
        g2d.drawString("1. BST (Sorted)", tlX - 80, tlY - 50);
        drawNodeArrow(g2d, tlX, tlY, tlX - 60, tlY + 70, "");
        drawNodeArrow(g2d, tlX, tlY, tlX + 60, tlY + 70, "");
        drawNode(g2d, tlX, tlY, "50", Color.ORANGE, Color.BLACK);
        drawNode(g2d, tlX - 60, tlY + 70, "25", Color.ORANGE, Color.BLACK);
        drawNode(g2d, tlX + 60, tlY + 70, "75", Color.ORANGE, Color.BLACK);

        int trX = cx + 200, trY = cy - 150;
        g2d.drawString("2. N-Ary Tree", trX - 60, trY - 50);
        drawNodeArrow(g2d, trX, trY, trX - 80, trY + 70, "");
        drawNodeArrow(g2d, trX, trY, trX, trY + 70, "");
        drawNodeArrow(g2d, trX, trY, trX + 80, trY + 70, "");
        drawNode(g2d, trX, trY, "Root", Color.PINK, Color.BLACK);
        drawNode(g2d, trX - 80, trY + 70, "A", Color.PINK, Color.BLACK);
        drawNode(g2d, trX, trY + 70, "B", Color.PINK, Color.BLACK);
        drawNode(g2d, trX + 80, trY + 70, "C", Color.PINK, Color.BLACK);

        int blX = cx - 200, blY = cy + 100;
        g2d.drawString("3. Red-Black Tree", blX - 80, blY - 40);
        drawNodeArrow(g2d, blX, blY, blX - 50, blY + 60, "");
        drawNodeArrow(g2d, blX, blY, blX + 50, blY + 60, "");
        drawNodeArrow(g2d, blX - 50, blY + 60, blX - 80, blY + 120, "");

        drawNode(g2d, blX, blY, "10", Color.BLACK, Color.WHITE);
        drawNode(g2d, blX - 50, blY + 60, "5", Color.RED, Color.WHITE);
        drawNode(g2d, blX + 50, blY + 60, "20", Color.BLACK, Color.WHITE);
        drawNode(g2d, blX - 80, blY + 120, "3", Color.BLACK, Color.WHITE);

        int brX = cx + 200, brY = cy + 50;
        g2d.drawString("4. Trie (String: 'CAT')", brX - 80, brY - 30);
        drawNodeArrow(g2d, brX, brY, brX, brY + 60, "");
        drawNodeArrow(g2d, brX, brY + 60, brX, brY + 120, "");
        drawNode(g2d, brX, brY, "'C'", new Color(147, 112, 219), Color.WHITE);
        drawNode(g2d, brX, brY + 60, "'A'", new Color(147, 112, 219), Color.WHITE);
        drawNode(g2d, brX, brY + 120, "'T'", new Color(147, 112, 219), Color.WHITE);
    }

    private void drawAllGraphs(Graphics2D g2d, int cx, int cy) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));

        int tlX = cx - 200, tlY = cy - 100;
        g2d.drawString("1. Undirected / Unweighted", tlX - 100, tlY - 70);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(tlX, tlY, tlX + 100, tlY);
        g2d.drawLine(tlX, tlY, tlX + 50, tlY + 80);
        g2d.drawLine(tlX + 100, tlY, tlX + 50, tlY + 80);
        drawNode(g2d, tlX, tlY, "A", Color.LIGHT_GRAY, Color.BLACK);
        drawNode(g2d, tlX + 100, tlY, "B", Color.LIGHT_GRAY, Color.BLACK);
        drawNode(g2d, tlX + 50, tlY + 80, "C", Color.LIGHT_GRAY, Color.BLACK);

        int trX = cx + 150, trY = cy - 100;
        g2d.drawString("2. Directed, Weighted, Cyclic", trX - 60, trY - 70);
        drawNodeArrowWithText(g2d, trX, trY, trX + 120, trY, "5");
        drawNodeArrowWithText(g2d, trX + 120, trY, trX + 120, trY + 100, "2");
        drawNodeArrowWithText(g2d, trX + 120, trY + 100, trX, trY + 100, "7");
        drawNodeArrowWithText(g2d, trX, trY + 100, trX, trY, "1");
        drawNode(g2d, trX, trY, "V1", Color.YELLOW, Color.BLACK);
        drawNode(g2d, trX + 120, trY, "V2", Color.YELLOW, Color.BLACK);
        drawNode(g2d, trX + 120, trY + 100, "V3", Color.YELLOW, Color.BLACK);
        drawNode(g2d, trX, trY + 100, "V4", Color.YELLOW, Color.BLACK);
        int bX = cx, bY = cy + 100;
        g2d.drawString("3. DAG (Directed Acyclic Graph)", bX - 120, bY - 20);
        drawNodeArrow(g2d, bX - 120, bY + 80, bX - 30, bY + 40, "");
        drawNodeArrow(g2d, bX - 120, bY + 80, bX - 30, bY + 120, "");
        drawNodeArrow(g2d, bX - 30, bY + 40, bX + 80, bY + 80, "");
        drawNodeArrow(g2d, bX - 30, bY + 120, bX + 80, bY + 80, "");

        drawNode(g2d, bX - 120, bY + 80, "Start", Color.CYAN, Color.BLACK);
        drawNode(g2d, bX - 30, bY + 40, "T1", Color.CYAN, Color.BLACK);
        drawNode(g2d, bX - 30, bY + 120, "T2", Color.CYAN, Color.BLACK);
        drawNode(g2d, bX + 80, bY + 80, "End", Color.CYAN, Color.BLACK);
    }
    private void drawCredits(Graphics2D g2d, int cx, int cy) {
        g2d.setColor(new Color(245, 245, 250));
        g2d.fillRoundRect(cx - 280, cy - 140, 560, 280, 25, 25);
        g2d.setColor(new Color(200, 200, 210));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRoundRect(cx - 280, cy - 140, 560, 280, 25, 25);

        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 26));
        g2d.drawString("Platform Architect & Lead Developer", cx - 220, cy - 80);

        g2d.setFont(new Font("Consolas", Font.BOLD, 30));
        g2d.setColor(new Color(41, 128, 185));
        g2d.drawString("Syad Mehedi Hasan Alvi", cx - 185, cy - 30);

        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        g2d.setColor(Color.BLACK);
        g2d.drawString("ID: 011222164", cx - 60, cy + 15);
        g2d.drawString("United International University (UIU)", cx - 150, cy + 50);

        g2d.setFont(new Font("Arial", Font.ITALIC, 16));
        g2d.setColor(new Color(100, 100, 100));
        g2d.drawString("Computer Science & Engineering (CSE)", cx - 145, cy + 80);
        g2d.drawString("Specializing in Software Development, Algorithms & Neural Networks", cx - 250, cy + 110);
    }

    private void drawNode(Graphics2D g2d, int x, int y, String label, Color bgColor, Color textColor) {
        g2d.setColor(bgColor);
        g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);

        g2d.setColor(textColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label, x - fm.stringWidth(label) / 2, y + (fm.getAscent() - fm.getDescent()) / 2);
    }

    private void drawMemoryNode(Graphics2D g2d, int x, int y, String label, String hex) {
        drawNode(g2d, x, y, label, Color.ORANGE, Color.BLACK);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Consolas", Font.PLAIN, 14));
        g2d.drawString(hex, x - 20, y - NODE_RADIUS - 5);
    }

    private void drawNodeArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, String text) {
        double dist = Math.hypot(x2 - x1, y2 - y1);
        if (dist <= NODE_RADIUS * 2) return;

        int startX = (int) (x1 + NODE_RADIUS * (x2 - x1) / dist);
        int startY = (int) (y1 + NODE_RADIUS * (y2 - y1) / dist);
        int endX = (int) (x2 - NODE_RADIUS * (x2 - x1) / dist);
        int endY = (int) (y2 - NODE_RADIUS * (y2 - y1) / dist);

        drawSimpleArrow(g2d, startX, startY, endX, endY);
        if (!text.isEmpty()) {
            g2d.setColor(Color.RED);
            g2d.drawString(text, (startX + endX)/2, (startY + endY)/2 - 10);
        }
    }

    private void drawNodeArrowWithText(Graphics2D g2d, int x1, int y1, int x2, int y2, String text) {
        drawNodeArrow(g2d, x1, y1, x2, y2, text);
    }

    private void drawSimpleArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(x1, y1, x2, y2);

        double angle = Math.atan2(y2 - y1, x2 - x1);
        int arrSize = 12;
        int x3 = (int) (x2 - arrSize * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - arrSize * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (x2 - arrSize * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - arrSize * Math.sin(angle + Math.PI / 6));
        g2d.fillPolygon(new int[]{x2, x3, x4}, new int[]{y2, y3, y4}, 3);
    }

    private void drawArrowWithText(Graphics2D g2d, int x1, int y1, int x2, int y2, String text) {
        drawSimpleArrow(g2d, x1, y1, x2, y2);
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(text, (x1 + x2) / 2 - 15, (y1 + y2) / 2 - 10);
    }

    private void drawOffsetArrow(Graphics2D g2d, int x1, int y1, int x2, int y2, int offsetHeight) {
        int startX = x1 + (x2 > x1 ? NODE_RADIUS : -NODE_RADIUS);
        int endX = x2 + (x1 > x2 ? NODE_RADIUS : -NODE_RADIUS);
        drawSimpleArrow(g2d, startX, y1 + offsetHeight, endX, y2 + offsetHeight);
    }
}
