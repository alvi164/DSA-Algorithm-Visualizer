# Advanced DSA Visualizer Suite

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A22?style=for-the-badge&logo=apachemaven&logoColor=white)
![Swing](https://img.shields.io/badge/UI-Java_Swing-blue?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

An enterprise-grade Java desktop application engineered to simulate, animate, and trace core Data Structures and Algorithms (DSA) in real-time. Built with a clean, object-oriented architecture and managed via Maven, this platform serves as an interactive educational tool to bridge the gap between theoretical algorithm logic and practical memory state execution.

**Developed by:** [Syed Alvi (alvi164)](https://github.com/alvi164)

---

## 🎯 Project Objective

The primary goal of this suite is to provide a concurrent, visually responsive environment where users can manipulate data structures and observe algorithm execution step-by-step. It demonstrates advanced Java concepts including **Multithreading**, **Polymorphism**, and **Hardware-Accelerated 2D Rendering**.

---

## ⚙️ Core Modules & Features

The application is strictly modular, featuring 5 independent visualization engines:

### 1. Sorting & Array Manipulation
* **Algorithms:** Selection, Bubble, Insertion, Merge, and Quick Sort.
* **Rendering:** Dynamically maps array values to visual gradient bars, highlighting active indices, pivots, and sorted boundaries in real-time.

### 2. Graph Theory & Traversal
* **Interactive Canvas:** Supports real-time node generation and edge mapping via mouse drag-and-drop.
* **Algorithms:** Breadth-First Search (BFS) and Depth-First Search (DFS).
* **Execution Tracing:** Features a live dry-run terminal that outputs the operational states of underlying FIFO Queues and LIFO Stacks during traversal.

### 3. Tree Structures & Heaps
* **Binary Search Trees (BST):** Animates node insertions, rebalancing logic, and pointer updates.
* **Heaps:** Visualizes the mathematical mapping of contiguous arrays into Min-Heap and Max-Heap 3D node structures.
* **Graphics:** Utilizes `Graphics2D` radial gradients and anti-aliasing to render pseudo-3D hierarchical relationships.

### 4. Linear Memory Structures
* **Stack & Queue:** Animates Push/Pop and Enqueue/Dequeue mechanics.
* **Linked Lists:** Dynamically generates simulated hexadecimal memory addresses (e.g., `0x4F`), visualizing the exact mechanics of Head, Tail, and Temporary traversal pointers.

### 5. Hardware Memory Allocation Simulator
* A unique interactive RAM grid that models how an Operating System calculates physical memory addresses.
* Visually contrasts the contiguous address mathematics of Arrays against the scattered pointer memory allocations of Binary Trees.

---

## 🏗️ Software Architecture & Design Patterns

This project was built with a strong emphasis on maintainability and clean code principles:

* **Polymorphic UI Lifecycle:** Engineered around an abstract `BaseModulePanel` class. All individual algorithm modules inherit standardized UI layouts, slider controls, and canvas split-panes, adhering to the **DRY (Don't Repeat Yourself)** principle.
* **Concurrent Execution (Multithreading):** Algorithm logic and UI rendering are strictly decoupled. Heavy iterative algorithms run on isolated background `Thread` processes, safely communicating with the Java Swing Event Dispatch Thread (EDT) via `SwingUtilities.invokeLater()` to prevent UI freezing.
* **State Management:** Utilizes `volatile` threading flags and synchronized locks to allow users to pause, resume, and step through algorithms smoothly.

---

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit (JDK):** Version 17 or higher.
* **Apache Maven:** Required for dependency resolution and build automation.

### Installation & Execution

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/alvi164/DSA-Algorithm-Visualizer.git](https://github.com/alvi164/DSA-Algorithm-Visualizer.git)
   cd DSA-Algorithm-Visualizer

   ## 🧪 Testing & Code Quality

To ensure absolute algorithmic correctness and prevent regression bugs, this platform features a completely decoupled, headless unit testing suite powered by **JUnit 5**.

### Test Execution Matrix
The test suite isolates computation logic from the Java Swing Event Dispatch Thread (EDT), allowing assertions to verify states instantly without triggering GUI rendering delays.

* **Algorithmic Correctness:** Validates structural stability for Bubble, Selection, Insertion, Merge, and Quick Sort routines.
* **Edge-Case Boundaries:** Programmatically tests empty matrices, single-element structures, and uniform/identical element distributions.

### Running Tests Locally
To run the programmatic validation suite headlessly on your machine, execute the following standard Maven command in the root directory:

```bash
mvn test


