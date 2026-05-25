package org.example;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private JTabbedPane tabbedPane;

    public MainApp() {
        setTitle("Pro DSA Visualizer Platform");
        setSize(1280, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("1. Sorting Algorithms", new SortingModule());
        tabbedPane.addTab("2. DSA Fundamentals Tour", new DSAFundamentalsTourModule());
        tabbedPane.addTab("3. Graph Traversal", new GraphTraversalModule());
        tabbedPane.addTab("4. Stack & Queue", new StackQueueModule());
        tabbedPane.addTab("5. BST & Heaps", new BSTHeapModule());
        tabbedPane.addTab("6. Linked List", new LinkedListModule());
        tabbedPane.addTab("7. Memory Allocation", new MemoryModule());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
