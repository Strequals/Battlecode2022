package birb2;

public strictfp class MinHeap<T> {

    private Pair[] arr;
    private int size;
    private int capacity;

    private static final int INITIAL_CAPACITY = 16;
    
    public MinHeap() {
        this(INITIAL_CAPACITY);
    }

    public MinHeap(int capacity) {
        arr = new Pair[capacity + 1];
        size = 0;
        this.capacity = capacity;
    }

    private int parent(int p) {
        return p / 2;
    }

    private int leftChild(int p) {
        return 2 * p;
    }

    private int rightChild(int p) {
        return 2 * p + 1;
    }

    private boolean isLeaf(int p) {
        return p > size / 2;
    }

    private void swap(int p1, int p2) {
        Pair tmp = arr[p1];
        arr[p1] = arr[p2];
        arr[p2] = tmp;
    }

    public void push(double key, T value) {
        if (size >= capacity) {
            Pair[] newarr = new Pair[capacity * 2 + 1];
            System.arraycopy(arr, 1, newarr, 1, size);
            arr = newarr;
        }

        arr[++size] = new Pair(key, value);
        
        int current = size;
        while (arr[current].key < arr[parent(current)].key) {
            swap(current, parent(current));
            current = parent(current);
        }
    }

    private void heapify(int p) {
        if (p <= size && !isLeaf(p)) {
            double lk = arr[leftChild(p)].key;
            double rk = arr[rightChild(p)].key;
            if (arr[p].key > lk
                || arr[p].key > rk) {
                if (lk < rk) {
                    swap(p, leftChild(p));
                    heapify(leftChild(p));
                } else {
                    swap(p, rightChild(p));
                    heapify(rightChild(p));
                }
            }
        }
    }

    public T poll() {
        T v = (T) arr[1].value;
        arr[1] = arr[size--];
        heapify(1);

        return v;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
    }
}

class Pair {
    double key;
    Object value;

    public Pair(double k, Object v) {
        key = k;
        value = v;
    }
}
