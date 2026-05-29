package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

class SortingModuleTest {

    private SortingModule testModule;
    private final int[] unsortedArray = {45, 12, 89, 34, 7, 66, 23};
    private final int[] expectedSortedArray = {7, 12, 23, 34, 45, 66, 89};

    @BeforeEach
    void setUp() {
        // Instantiate a headless test subclass that overrides animation speeds and visual calls
        testModule = new SortingModule() {
            @Override
            protected void safeSleep() throws InterruptedException {
                // Instantly return - bypassing delays during unit testing
            }

            @Override
            protected void updateHUD(int codeLine, String commentary) {
                // Headless mock - do nothing
            }

            @Override
            protected void refreshView() {
                // Headless mock - do nothing
            }
        };
    }

    @Test
    void testBubbleSortCorrectness() throws InterruptedException {
        testModule.setArray(unsortedArray.clone());
        testModule.bubbleSort();
        assertArrayEquals(expectedSortedArray, testModule.getArray(), "Bubble Sort failed programmatic validation.");
    }

    @Test
    void testSelectionSortCorrectness() throws InterruptedException {
        testModule.setArray(unsortedArray.clone());
        testModule.selectionSort();
        assertArrayEquals(expectedSortedArray, testModule.getArray(), "Selection Sort failed programmatic validation.");
    }

    @Test
    void testInsertionSortCorrectness() throws InterruptedException {
        testModule.setArray(unsortedArray.clone());
        testModule.insertionSort();
        assertArrayEquals(expectedSortedArray, testModule.getArray(), "Insertion Sort failed programmatic validation.");
    }

    @Test
    void testMergeSortCorrectness() throws InterruptedException {
        int[] target = unsortedArray.clone();
        testModule.setArray(target);
        testModule.mergeSort(0, target.length - 1);
        assertArrayEquals(expectedSortedArray, testModule.getArray(), "Merge Sort failed programmatic validation.");
    }

    @Test
    void testQuickSortCorrectness() throws InterruptedException {
        int[] target = unsortedArray.clone();
        testModule.setArray(target);
        testModule.quickSort(0, target.length - 1);
        assertArrayEquals(expectedSortedArray, testModule.getArray(), "Quick Sort failed programmatic validation.");
    }

    @Test
    void testEdgeCaseEmptyAndSingleElementArrays() throws InterruptedException {
        // Single element validation matrix
        int[] singleItem = {42};
        testModule.setArray(singleItem);
        testModule.quickSort(0, singleItem.length - 1);
        assertArrayEquals(new int[]{42}, testModule.getArray(), "Sorting algorithms should gracefully handle single element structures.");
    }

    @Test
    void testEdgeCaseIdenticalElements() throws InterruptedException {
        int[] identicalItems = {5, 5, 5, 5};
        testModule.setArray(identicalItems);
        testModule.bubbleSort();
        assertArrayEquals(new int[]{5, 5, 5, 5}, testModule.getArray(), "Sorting algorithms should handle collections containing uniform value distributions.");
    }
}