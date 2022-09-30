public class Node<T> {
    private T value;
    private boolean isMarked;
    private Node<T> left;
    private Node<T> right;
    private boolean isRightThreaded;

    public Node(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Node<T> getLeft() {
        return left;
    }

    public Node<T> getRight() {
        return right;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setLeft(Node<T> left) {
        this.left = left;
    }

    public void setRight(Node<T> right) {
        this.right = right;
    }

    public void mark() {
        isMarked = true;
    }

    public void unmark() {
        isMarked = false;
    }

    public void setRightThreaded(boolean isRightThreaded) {
        this.isRightThreaded = isRightThreaded;
    }

    public boolean isRightThreaded() {
        return isRightThreaded;
    }

    private StringBuilder toString(StringBuilder prefix, boolean isTail, StringBuilder sb) {
        if (right != null && !isRightThreaded) right.toString(new StringBuilder().append(prefix).append(isTail ? "│   " : "    "), false, sb);

        sb.append(prefix).append(isTail ? "└── " : "┌── ").append(isMarked || isRightThreaded ? "[" + value + "]" : value).append("\n");

        if (left != null) left.toString(new StringBuilder().append(prefix).append(isTail ? "    " : "│   "), true, sb);

        return sb;
    }

    @Override
    public String toString() {
        return toString(new StringBuilder(), true, new StringBuilder()).toString();
    }
}