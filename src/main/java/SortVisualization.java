import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortVisualization extends JFrame {

    private int[] numbers = {};
    private SortPanel panel;
    private JLabel statusLabel;

    public SortVisualization() {
        List<Integer> numberList = getNumber(1000);
        Collections.shuffle(numberList);
        numbers = listToIntArray(numberList);

        setTitle("Sort Visualization");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        panel = new SortPanel(numbers);
        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        // Shuffle 버튼 추가
        JButton shuffleButton = new JButton("Shuffle");
        shuffleButton.addActionListener(e -> {
            Collections.shuffle(numberList);
            numbers = listToIntArray(numberList);
            panel.setNumbers(numbers);
            panel.repaint();
            updateStatusLabel("Ready", 0); // 상태 업데이트
        });

        JButton bubbleSortButton = new JButton("Bubble Sort");
        JButton quickSortButton = new JButton("Quick Sort");
        JButton heapSortButton = new JButton("Heap Sort");

        // 정렬 버튼 액션 리스너에 시간 측정 로직 추가
        bubbleSortButton.addActionListener(e -> new Thread(() -> {
            long startTime = System.currentTimeMillis();
            updateStatusLabel("정렬 중...", startTime);
            panel.bubbleSort();
            long endTime = System.currentTimeMillis();
            updateStatusLabel("완료", endTime - startTime);
        }).start());

        quickSortButton.addActionListener(e -> new Thread(() -> {
            long startTime = System.currentTimeMillis();
            updateStatusLabel("정렬 중...", startTime);
            panel.quickSort(0, numbers.length - 1);
            long endTime = System.currentTimeMillis();
            updateStatusLabel("완료", endTime - startTime);
        }).start());

        heapSortButton.addActionListener(e -> new Thread(() -> {
            long startTime = System.currentTimeMillis();
            updateStatusLabel("정렬 중...", startTime);
            panel.heapSort();
            long endTime = System.currentTimeMillis();
            updateStatusLabel("완료", endTime - startTime);
        }).start());

        buttonPanel.add(shuffleButton);
        buttonPanel.add(bubbleSortButton);
        buttonPanel.add(quickSortButton);
        buttonPanel.add(heapSortButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 상태 표시 레이블 추가
        statusLabel = new JLabel("Ready");
        add(statusLabel, BorderLayout.NORTH);
    }

    private void updateStatusLabel(String status, long time) {
        SwingUtilities.invokeLater(() -> {
            if ("Sorting".equals(status)) {
                statusLabel.setText("Status: " + status);
            } else if ("Done".equals(status)) {
                statusLabel.setText("Status: " + status + " (Time: " + time + " ms)");
            } else {
                statusLabel.setText("Status: " + status);
            }
        });
    }


    private List<Integer> getNumber(int num) {
        List<Integer> numList = new ArrayList<>();
        for (int i=1; i<num; i++) {
            numList.add(i);
        }
        return numList;
    }

    private int[] listToIntArray(List<Integer> numberList) {
        int[] array = new int[numberList.size()];

        for (int i = 0; i < numberList.size(); i++) {
            array[i] = numberList.get(i);
        }
        return array;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SortVisualization sv = new SortVisualization();
            sv.setVisible(true);
        });
    }

    static class SortPanel extends JPanel {
        private int[] numbers;

        public SortPanel(int[] numbers) {
            this.numbers = Arrays.copyOf(numbers, numbers.length);
        }

        public void setNumbers(int[] numbers) {
            this.numbers = numbers;
        }

        // 여기에 버블 정렬, 퀵 정렬, 힙 정렬 메소드 구현
        public void bubbleSort() {
            int n = numbers.length;
            for (int i = 0; i < n-1; i++)
                for (int j = 0; j < n-i-1; j++)
                    if (numbers[j] > numbers[j+1]) {
                        // swap temp and numbers[i]
                        int temp = numbers[j];
                        numbers[j] = numbers[j+1];
                        numbers[j+1] = temp;
                        doRepaint();
                    }
        }


        public void quickSort(int low, int high) {
            if (low < high) {
                int pi = partition(low, high);

                quickSort(low, pi - 1);
                quickSort(pi + 1, high);
            }
        }

        private int partition(int low, int high) {
            int pivot = numbers[high];
            int i = (low - 1);
            for (int j = low; j < high; j++) {
                if (numbers[j] < pivot) {
                    i++;

                    int temp = numbers[i];
                    numbers[i] = numbers[j];
                    numbers[j] = temp;

                    doRepaint();
                }
            }

            int temp = numbers[i + 1];
            numbers[i + 1] = numbers[high];
            numbers[high] = temp;

            doRepaint();
            return i + 1;
        }

        public void heapSort() {
            int n = numbers.length;

            // 힙 생성 (배열 재구성)
            for (int i = n / 2 - 1; i >= 0; i--)
                heapify(numbers, n, i);

            // 힙에서 원소 하나씩 추출
            for (int i = n - 1; i > 0; i--) {
                // 현재 Root를 마지막으로 이동
                int temp = numbers[0];
                numbers[0] = numbers[i];
                numbers[i] = temp;

                // 환원된 Heap에서 최대 Heapify 호출
                heapify(numbers, i, 0);

                doRepaint();
            }
        }

        void heapify(int arr[], int n, int i) {
            int largest = i; // 가장 큰 값을 Root로 초기화
            int l = 2 * i + 1; // left = 2*i + 1
            int r = 2 * i + 2; // right = 2*i + 2

            // Left 자식 요소가 Root보다 크면
            if (l < n && arr[l] > arr[largest])
                largest = l;

            // Right 자식 요소가 Root보다 크면
            if (r < n && arr[r] > arr[largest])
                largest = r;

            // 가장 큰 값이 Root가 아닌 경우
            if (largest != i) {
                int swap = arr[i];
                arr[i] = arr[largest];
                arr[largest] = swap;

                // 영향받는 서브트리를 재귀적으로 Heap화
                heapify(arr, n, largest);

                doRepaint();
            }
        }

        private void doRepaint() {
            try {
                repaint();
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int barWidth = panelWidth / numbers.length;

            // 최대값 찾기 (그래프의 높이 조절을 위해)
            int maxVal = Arrays.stream(numbers).max().getAsInt();

            for (int i = 0; i < numbers.length; i++) {
                // 바의 높이를 현재 패널 높이에 맞게 조절
                int barHeight = (int) (((double) numbers[i] / maxVal) * (panelHeight - 100)); // 패널 높이에 여유 공간을 주기 위해 -100을 함
                int x = i * barWidth;
                int y = panelHeight - barHeight - 50; // 하단 여유 공간을 주기 위해 -50을 함

                g.fillRect(x, y, barWidth, barHeight);
            }
        }
    }
}
