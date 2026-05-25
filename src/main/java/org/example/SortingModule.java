package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SortingModule extends BaseModulePanel {
    private int[] array;
    private int activeIndex = -1, compareIndex = -1, sortedBoundary = -1;

    private Thread sortingThread;
    private volatile boolean isSorting = false;
    private volatile boolean isPaused = false;
    private volatile boolean isStopped = false;
    private final Object pauseLock = new Object();

    private JComboBox<String> algoSelector;
    private JTextField inputField;
    private DefaultListModel<String> pseudoCodeModel;
    private JList<String> pseudoCodeList;
    private JLabel complexityLabel;
    private JTextArea commentaryArea;

    private JButton playBtn, pauseBtn, resetBtn;

    public SortingModule() {
        super();
        generateRandomArray();
    }

    @Override
    protected void setupControls() {
        controlPanel.add(new JLabel("Algorithm:"));
        algoSelector = new JComboBox<>(new String[]{"Selection Sort", "Bubble Sort", "Insertion Sort", "Merge Sort", "Quick Sort"});
        algoSelector.addActionListener(e -> { stopSorting(); updateAlgorithmInfo(); });
        algoSelector.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(algoSelector);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        controlPanel.add(new JLabel("Manual Input (comma-separated):"));
        inputField = new JTextField();
        inputField.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(inputField);

        JPanel dataBtns = new JPanel(new GridLayout(1, 2, 5, 0));
        dataBtns.setBackground(new Color(245, 245, 250));
        JButton setBtn = new JButton("Set Data");
        setBtn.addActionListener(e -> setManualArray());
        JButton randBtn = new JButton("Random Data");
        randBtn.addActionListener(e -> generateRandomArray());
        dataBtns.add(setBtn); dataBtns.add(randBtn);
        dataBtns.setMaximumSize(new Dimension(300, 30));
        controlPanel.add(dataBtns);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel mediaPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        mediaPanel.setBackground(new Color(245, 245, 250));

        playBtn = new JButton("▶ Play");
        playBtn.setBackground(new Color(34, 139, 34));
        playBtn.setForeground(Color.WHITE);
        playBtn.addActionListener(e -> startAlgorithm());

        pauseBtn = new JButton("⏸ Pause");
        pauseBtn.setBackground(new Color(255, 165, 0));
        pauseBtn.setForeground(Color.BLACK);
        pauseBtn.setEnabled(false);
        pauseBtn.addActionListener(e -> togglePause());

        resetBtn = new JButton("⏹ Reset");
        resetBtn.setBackground(new Color(220, 53, 69));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setEnabled(false);
        resetBtn.addActionListener(e -> { stopSorting(); generateRandomArray(); });

        mediaPanel.add(playBtn);
        mediaPanel.add(pauseBtn);
        mediaPanel.add(resetBtn);
        mediaPanel.setMaximumSize(new Dimension(300, 35));
        controlPanel.add(mediaPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        controlPanel.add(new JLabel("Real-Time Pseudocode:"));
        pseudoCodeModel = new DefaultListModel<>();
        pseudoCodeList = new JList<>(pseudoCodeModel);
        pseudoCodeList.setBackground(new Color(40, 44, 52));
        pseudoCodeList.setForeground(new Color(171, 178, 191));
        pseudoCodeList.setSelectionBackground(new Color(229, 192, 123));
        pseudoCodeList.setSelectionForeground(Color.BLACK);
        pseudoCodeList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(pseudoCodeList);
        scrollPane.setPreferredSize(new Dimension(300, 130));
        controlPanel.add(scrollPane);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        controlPanel.add(new JLabel("Live Execution Commentary:"));
        commentaryArea = new JTextArea();
        commentaryArea.setEditable(false);
        commentaryArea.setLineWrap(true);
        commentaryArea.setWrapStyleWord(true);
        commentaryArea.setBackground(new Color(20, 20, 20));
        commentaryArea.setForeground(new Color(0, 255, 0)); 
        commentaryArea.setFont(new Font("Monospaced", Font.BOLD, 12));
        commentaryArea.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane commScroll = new JScrollPane(commentaryArea);
        commScroll.setPreferredSize(new Dimension(300, 80));
        controlPanel.add(commScroll);

        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        complexityLabel = new JLabel("");
        controlPanel.add(complexityLabel);

        updateAlgorithmInfo();
    }

    private void updateHUD(int codeLine, String commentary) {
        SwingUtilities.invokeLater(() -> {
            if (codeLine >= 0) pseudoCodeList.setSelectedIndex(codeLine);
            else pseudoCodeList.clearSelection();
            commentaryArea.setText(commentary);
        });
    }

    private void safeSleep() throws InterruptedException {
        Thread.sleep(speedSlider.getValue());
        synchronized (pauseLock) {
            while (isPaused && !isStopped) {
                pauseLock.wait(); 
            }
        }
        if (isStopped) throw new InterruptedException("Execution Stopped");
    }

    private void togglePause() {
        if (!isSorting) return;
        isPaused = !isPaused;
        if (isPaused) {
            pauseBtn.setText("▶ Resume");
            pauseBtn.setBackground(new Color(30, 144, 255));
            updateHUD(pseudoCodeList.getSelectedIndex(), "PAUSED: " + commentaryArea.getText());
        } else {
            pauseBtn.setText("⏸ Pause");
            pauseBtn.setBackground(new Color(255, 165, 0));
            synchronized (pauseLock) { pauseLock.notify(); } 
        }
    }

    private void stopSorting() {
        isStopped = true;
        isPaused = false;
        synchronized (pauseLock) { pauseLock.notify(); } 

        isSorting = false;
        SwingUtilities.invokeLater(() -> {
            playBtn.setEnabled(true);
            pauseBtn.setEnabled(false);
            resetBtn.setEnabled(false);
            pauseBtn.setText("⏸ Pause");
            pauseBtn.setBackground(new Color(255, 165, 0));
            updateHUD(-1, "Execution stopped. Ready.");
        });
    }

    private void generateRandomArray() {
        stopSorting();
        array = new int[25];
        Random r = new Random();
        for (int i = 0; i < 25; i++) array[i] = r.nextInt(90) + 10;
        resetState();
    }

    private void setManualArray() {
        stopSorting();
        try {
            String[] parts = inputField.getText().split(",");
            array = new int[parts.length];
            for (int i = 0; i < parts.length; i++) array[i] = Integer.parseInt(parts[i].trim());
            resetState();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid Format. Use: 10,20,30");
        }
    }

    private void resetState() {
        activeIndex = compareIndex = sortedBoundary = -1;
        updateHUD(-1, "Data loaded. Press Play to start.");
        canvasPanel.repaint();
    }

    private void startAlgorithm() {
        if (isSorting || array == null) return;
        isSorting = true;
        isStopped = false;
        isPaused = false;

        playBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        resetBtn.setEnabled(true);

        String algo = (String) algoSelector.getSelectedItem();

        sortingThread = new Thread(() -> {
            try {
                if (algo.equals("Selection Sort")) selectionSort();
                else if (algo.equals("Bubble Sort")) bubbleSort();
                else if (algo.equals("Insertion Sort")) insertionSort();
                else if (algo.equals("Merge Sort")) mergeSort(0, array.length - 1);
                else if (algo.equals("Quick Sort")) quickSort(0, array.length - 1);

                if (!isStopped) {
                    sortedBoundary = array.length;
                    updateHUD(-1, "Sorting Complete! Array is now ordered.");
                }
            } catch (InterruptedException e) {
            } finally {
                stopSorting();
                canvasPanel.repaint();
            }
        });
        sortingThread.start();
    }

    private void bubbleSort() throws InterruptedException {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            updateHUD(0, "Starting Pass " + (i + 1) + ". Largest unsorted item will bubble to the top.");
            safeSleep();
            for (int j = 0; j < n - i - 1; j++) {
                activeIndex = j; compareIndex = j + 1;
                updateHUD(2, "Comparing arr[" + j + "] (" + array[j] + ") and arr[" + (j+1) + "] (" + array[j+1] + ").");
                canvasPanel.repaint(); safeSleep();

                if (array[j] > array[j + 1]) {
                    updateHUD(3, array[j] + " > " + array[j+1] + " is TRUE. Swapping elements.");
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    canvasPanel.repaint(); safeSleep();
                } else {
                    updateHUD(2, array[j] + " is not > " + array[j+1] + ". No swap needed.");
                    safeSleep();
                }
            }
            sortedBoundary = n - i - 1;
            updateHUD(1, "Pass complete. Element at index " + sortedBoundary + " is locked in its final position.");
            safeSleep();
        }
    }

    private void selectionSort() throws InterruptedException {
        for (int i = 0; i < array.length - 1; i++) {
            updateHUD(0, "Pass " + (i+1) + ": Looking for the smallest element from index " + i + " to end.");
            safeSleep();

            int min_idx = i;
            updateHUD(1, "Assuming arr[" + i + "] (" + array[i] + ") is the minimum.");
            safeSleep();

            for (int j = i + 1; j < array.length; j++) {
                activeIndex = min_idx; compareIndex = j;
                updateHUD(3, "Checking if arr[" + j + "] (" + array[j] + ") < current minimum (" + array[min_idx] + ")");
                canvasPanel.repaint(); safeSleep();

                if (array[j] < array[min_idx]) {
                    min_idx = j;
                    updateHUD(4, "New minimum found! Updated min_idx to " + j + " (Value: " + array[j] + ").");
                    safeSleep();
                }
            }
            updateHUD(5, "Pass finished. Swapping starting element (" + array[i] + ") with minimum (" + array[min_idx] + ").");
            int temp = array[min_idx];
            array[min_idx] = array[i];
            array[i] = temp;
            sortedBoundary = i;
            canvasPanel.repaint(); safeSleep();
        }
    }

    private void insertionSort() throws InterruptedException {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            updateHUD(1, "Selected KEY: " + key + " at index " + i + ". Finding its correct position to the left.");
            safeSleep();

            while (j >= 0 && array[j] > key) {
                activeIndex = j; compareIndex = j + 1;
                updateHUD(2, array[j] + " > " + key + ". Shifting " + array[j] + " one position to the right.");
                canvasPanel.repaint(); safeSleep();

                array[j + 1] = array[j];
                updateHUD(3, "Shift complete.");
                canvasPanel.repaint(); safeSleep();
                j--;
            }
            array[j + 1] = key;
            updateHUD(5, "Found position! Inserting KEY (" + key + ") at index " + (j+1) + ".");
            canvasPanel.repaint(); safeSleep();
            sortedBoundary = i;
        }
    }

    private void mergeSort(int l, int r) throws InterruptedException {
        if (l < r) {
            int m = l + (r - l) / 2;
            updateHUD(2, "Splitting array from index " + l + " to " + r + " at mid point " + m);
            safeSleep();
            mergeSort(l, m);
            mergeSort(m + 1, r);
            merge(l, m, r);
        }
    }

    private void merge(int l, int m, int r) throws InterruptedException {
        updateHUD(5, "Merging sub-arrays: [" + l + " to " + m + "] and [" + (m+1) + " to " + r + "].");
        safeSleep();
        int[] left = Arrays.copyOfRange(array, l, m + 1);
        int[] right = Arrays.copyOfRange(array, m + 1, r + 1);
        int i = 0, j = 0, k = l;

        while (i < left.length && j < right.length) {
            activeIndex = k;
            updateHUD(5, "Comparing left[" + i + "] (" + left[i] + ") with right[" + j + "] (" + right[j] + ")");
            canvasPanel.repaint(); safeSleep();

            if (left[i] <= right[j]) {
                array[k] = left[i]; i++;
                updateHUD(5, "Placed left element.");
            } else {
                array[k] = right[j]; j++;
                updateHUD(5, "Placed right element.");
            }
            k++; canvasPanel.repaint(); safeSleep();
        }
        while (i < left.length) { array[k] = left[i]; i++; k++; canvasPanel.repaint(); safeSleep(); }
        while (j < right.length) { array[k] = right[j]; j++; k++; canvasPanel.repaint(); safeSleep(); }
    }

    private void quickSort(int low, int high) throws InterruptedException {
        if (low < high) {
            updateHUD(2, "Partitioning array from index " + low + " to " + high);
            safeSleep();
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) throws InterruptedException {
        int pivot = array[high];
        int i = (low - 1);
        updateHUD(2, "Selected PIVOT as " + pivot + " at index " + high + ". Moving smaller elements to the left.");
        safeSleep();

        for (int j = low; j < high; j++) {
            compareIndex = j; activeIndex = high;
            updateHUD(2, "Checking if arr[" + j + "] (" + array[j] + ") < Pivot (" + pivot + ")");
            canvasPanel.repaint(); safeSleep();

            if (array[j] < pivot) {
                i++;
                updateHUD(2, "Condition TRUE. Swapping arr[" + i + "] with arr[" + j + "]");
                int temp = array[i]; array[i] = array[j]; array[j] = temp;
                canvasPanel.repaint(); safeSleep();
            }
        }
        updateHUD(2, "Partition loop done. Placing pivot in its final sorted position (" + (i+1) + ").");
        int temp = array[i + 1]; array[i + 1] = array[high]; array[high] = temp;
        canvasPanel.repaint(); safeSleep();
        return i + 1;
    }

    private void updateAlgorithmInfo() {
        String algo = (String) algoSelector.getSelectedItem();
        pseudoCodeModel.clear();
        if ("Selection Sort".equals(algo)) {
            pseudoCodeModel.addElement("0. for i = 0 to n - 1");
            pseudoCodeModel.addElement("1.   min_idx = i");
            pseudoCodeModel.addElement("2.   for j = i+1 to n");
            pseudoCodeModel.addElement("3.     if arr[j] < arr[min_idx]");
            pseudoCodeModel.addElement("4.       min_idx = j");
            pseudoCodeModel.addElement("5.   swap(arr[i], arr[min_idx])");
            complexityLabel.setText("<html><b>Time Complexity:</b><br>O(n²) Best/Avg/Worst</html>");
        } else if ("Bubble Sort".equals(algo)) {
            pseudoCodeModel.addElement("0. for i = 0 to n - 1");
            pseudoCodeModel.addElement("1.   for j = 0 to n - i - 1");
            pseudoCodeModel.addElement("2.     if arr[j] > arr[j+1]");
            pseudoCodeModel.addElement("3.       swap(arr[j], arr[j+1])");
            complexityLabel.setText("<html><b>Time Complexity:</b><br>O(n) Best | O(n²) Avg/Worst</html>");
        } else if ("Insertion Sort".equals(algo)) {
            pseudoCodeModel.addElement("0. for i = 1 to n - 1");
            pseudoCodeModel.addElement("1.   key = arr[i], j = i-1");
            pseudoCodeModel.addElement("2.   while j >= 0 and arr[j] > key");
            pseudoCodeModel.addElement("3.     arr[j+1] = arr[j]");
            pseudoCodeModel.addElement("4.     j = j - 1");
            pseudoCodeModel.addElement("5.   arr[j+1] = key");
            complexityLabel.setText("<html><b>Time Complexity:</b><br>O(n) Best | O(n²) Avg/Worst</html>");
        } else if ("Merge Sort".equals(algo)) {
            pseudoCodeModel.addElement("0. MergeSort(arr, l, r)");
            pseudoCodeModel.addElement("1.   if l < r");
            pseudoCodeModel.addElement("2.     m = l + (r - l) / 2");
            pseudoCodeModel.addElement("3.     MergeSort(arr, l, m)");
            pseudoCodeModel.addElement("4.     MergeSort(arr, m+1, r)");
            pseudoCodeModel.addElement("5.     Merge(arr, l, m, r)");
            complexityLabel.setText("<html><b>Time Complexity:</b><br>O(n log n) Best/Avg/Worst</html>");
        } else if ("Quick Sort".equals(algo)) {
            pseudoCodeModel.addElement("0. QuickSort(arr, low, high)");
            pseudoCodeModel.addElement("1.   if low < high");
            pseudoCodeModel.addElement("2.     pi = partition(arr, low, high)");
            pseudoCodeModel.addElement("3.     QuickSort(arr, low, pi - 1)");
            pseudoCodeModel.addElement("4.     QuickSort(arr, pi + 1, high)");
            complexityLabel.setText("<html><b>Time Complexity:</b><br>O(n log n) Best/Avg | O(n²) Worst</html>");
        }
    }

    @Override
    protected void drawCanvas(Graphics2D g2d) {
        if (array == null) return;
        int w = canvasPanel.getWidth(), h = canvasPanel.getHeight();
        int max = Arrays.stream(array).max().orElse(1);
        int barW = w / array.length;

        for (int i = 0; i < array.length; i++) {
            int barH = (int) (((double) array[i] / max) * (h - 50));
            int x = i * barW;
            int y = h - barH;

            Color c = new Color(70, 130, 180);
            if (i == activeIndex) c = Color.RED;
            else if (i == compareIndex) c = Color.ORANGE;
            else if ("Selection Sort".equals(algoSelector.getSelectedItem()) && i <= sortedBoundary && sortedBoundary != -1) c = Color.GREEN;
            else if ("Bubble Sort".equals(algoSelector.getSelectedItem()) && i >= sortedBoundary && sortedBoundary != -1) c = Color.GREEN;
            else if ("Insertion Sort".equals(algoSelector.getSelectedItem()) && i <= sortedBoundary && sortedBoundary != -1) c = Color.GREEN;
            else if (sortedBoundary == array.length) c = Color.GREEN;

            GradientPaint gp = new GradientPaint(x, y, c.brighter(), x + barW, y, c.darker());
            g2d.setPaint(gp);
            g2d.fillRect(x + 2, y, barW - 4, barH);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x + 2, y, barW - 4, barH);
            g2d.drawString(String.valueOf(array[i]), x + (barW/4), y - 5);
        }
    }
}
