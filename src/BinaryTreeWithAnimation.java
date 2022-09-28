import java.util.*;

public class BinaryTreeWithAnimation<T extends Comparable<T>> implements IBinaryTree<T> {
    private Node<T> root;
    private boolean isThreaded;
    private final int animationSpeedInMs;

    public BinaryTreeWithAnimation(int animationSpeedInMs) {
        this.animationSpeedInMs = animationSpeedInMs;
    }

    @Override
    public void add(T value) {
        if (isThreaded) throw new IllegalStateException("Tree is already threaded");
        root = add(root, value);
    }

    @Override
    public Collection<T> preOrder() {
        if (isThreaded) throw new IllegalStateException("Tree is already threaded");

        Set<T> set = new LinkedHashSet<>();
        try {
            preOrder(root, set);
        } catch (InterruptedException ignored) {
        }

        unMark(root);
        return set;
    }

    @Override
    public Collection<T> inOrder() {
        if (isThreaded) throw new IllegalStateException("Tree is already threaded");

        Set<T> set = new LinkedHashSet<>();
        try {
            inOrder(root, set);
        } catch (InterruptedException ignored) {
        }

        unMark(root);
        return set;
    }


    @Override
    public Collection<T> postOrder() {
        if (isThreaded) throw new IllegalStateException("Tree is already threaded");

        Set<T> set = new LinkedHashSet<>();
        try {
            postOrder(root, set);
        } catch (InterruptedException ignored) {
        }

        unMark(root);
        return set;
    }

    @Override
    public void makeRightThreaded() {
        if (isThreaded) throw new IllegalStateException("Tree is already threaded");

        isThreaded = true;
        createThreaded(root);
    }

    @Override
    public String threadsToString() {
        if (!isThreaded) throw new IllegalStateException("Tree is not threaded");

        return treeValuesToString(root, new StringBuilder()).toString();
    }

    @Override
    public void delete(T value) {
        if (!isThreaded) throw new IllegalStateException("Tree is already threaded");

        Node<T> parent = null;
        Node<T> current = root;

        while (current != null) {
            if (value.equals(current.getValue())) break;

            parent = current;

            if (value.compareTo(current.getValue()) < 0) {
                current = current.getLeft();
            } else {
                if (current.isRightThreaded()) {
                    break;
                } else {
                    current = current.getRight();
                }
            }
        }

        if (current == null) throw new IllegalArgumentException("Value does not exist");

        if (current == root && current.getLeft() == null && current.getRight() == null) throw new IllegalArgumentException("Cannot delete root node");

        if (current.getLeft() == null && current.isRightThreaded() || current.getLeft() == null && current.getRight() == null) {
            noChildrenDeletion(parent, current);
        } else if (current.getLeft() != null && current.getRight() == null || current.getLeft() == null && current.getRight() != null || current.getLeft() != null && current.isRightThreaded()) {
            oneChildDeletion(parent, current);
        } else {
            twoChildrenDeletion(current);
        }
    }

    private Node<T> add(Node<T> node, T value) {
        if (node == null) return new Node<>(value);

        if (value.compareTo(node.getValue()) < 0) {
            node.setLeft(add(node.getLeft(), value));
        } else if (value.compareTo(node.getValue()) > 0) {
            node.setRight(add(node.getRight(), value));
        } else {
            throw new IllegalArgumentException("Value already exists");
        }

        return node;
    }

    private void unMark(Node<T> node) {
        if (node == null) return;

        node.unmark();
        unMark(node.getLeft());
        unMark(node.getRight());
    }

    private void preOrder(Node<T> node, Set<T> set) throws InterruptedException {
        if (node == null) return;

        set.add(node.getValue());
        node.mark();

        System.out.println(this);

        Thread.sleep(animationSpeedInMs);
        preOrder(node.getLeft(), set);
        preOrder(node.getRight(), set);
    }

    private void inOrder(Node<T> node, Set<T> set) throws InterruptedException {
        if (node == null) return;

        inOrder(node.getLeft(), set);
        set.add(node.getValue());
        node.mark();

        System.out.println(this);

        Thread.sleep(animationSpeedInMs);
        inOrder(node.getRight(), set);
    }

    private void postOrder(Node<T> node, Set<T> set) throws InterruptedException {
        if (node == null) return;

        postOrder(node.getLeft(), set);
        postOrder(node.getRight(), set);
        set.add(node.getValue());
        node.mark();

        System.out.println(this);

        Thread.sleep(animationSpeedInMs);
    }

    private void populateQueue(Node<T> node, Queue<Node<T>> queue) {
        if (node == null) return;

        if (node.getLeft() != null) populateQueue(node.getLeft(), queue);

        queue.add(node);

        if (node.getRight() != null) populateQueue(node.getRight(), queue);
    }

    private void createThreadedUtil(Node<T> node, Queue<Node<T>> queue) {
        if (node == null) return;

        if (node.getLeft() != null) createThreadedUtil(node.getLeft(), queue);

        queue.remove();

        if (node.getRight() != null)
            createThreadedUtil(node.getRight(), queue);
        else {
            node.setRight(queue.peek());

            if (node.getRight() != null) node.setRightThreaded(true);
        }
    }

    private void createThreaded(Node<T> node) {
        Queue<Node<T>> q = new LinkedList<>();
        populateQueue(node, q);
        createThreadedUtil(node, q);
    }

    private StringBuilder treeValuesToString(Node<T> node, StringBuilder sb) {
        if (node == null) return sb;

        if (node.isRightThreaded())
            sb.append(node.getValue()).append(" -> ").append(node.getRight().getValue()).append("\n");

        if (!node.isRightThreaded()) treeValuesToString(node.getRight(), sb);

        treeValuesToString(node.getLeft(), sb);
        return sb;
    }

    private void noChildrenDeletion(Node<T> parent, Node<T> current) {
        if (parent == null) {
            root = null;
            return;
        }

        if (parent.getLeft() != null && parent.getLeft() == current) {
            parent.setLeft(null);
        } else {
            if (current.isRightThreaded()) {
                parent.setRight(current.getRight());
                parent.setRightThreaded(true);
            } else {
                parent.setRight(null);
            }
        }
    }

    private void oneChildDeletion(Node<T> parent, Node<T> current) {
        if (parent == null) {
            if (current.getLeft() != null) {
                root = current.getLeft();
                Node<T> inPred = inPred(current);

                if (inPred.isRightThreaded()) {
                    inPred(current).setRight(null);
                    inPred(current).setRightThreaded(false);
                }
            } else {
                root = current.getRight();
            }
            return;
        }

        if (current.isRightThreaded()) {
            Node<T> inPred = inPred(current);

            if (current == current.getRight()) {
                inPred.setRightThreaded(false);
                inPred.setRight(null);
            } else {
                inPred.setRight(current.getRight());
            }

            if (parent.getLeft() != null && parent.getLeft() == current) {
                parent.setLeft(current.getLeft());
            } else {
                parent.setRight(current.getLeft());
            }
        } else {
            if (parent.getLeft() != null && parent.getLeft() == current) {
                if (current.getLeft() != null) {
                    parent.setLeft(current.getLeft());
                } else {
                    parent.setLeft(current.getRight());
                }
            } else {
                if (current.getLeft() != null) {
                    parent.setRight(current.getLeft());
                    Node<T> inPred = inPred(current);
                    if (inPred.isRightThreaded() && inPred.getRight() == current) {
                        inPred.setRight(null);
                        inPred.setRightThreaded(false);
                    }
                } else {
                    parent.setRight(current.getRight());
                }
            }
        }
    }

    private void twoChildrenDeletion(Node<T> current) {
        Node<T> inPredParent;
        Node<T> inPred = inPred(current);

        if (current.getLeft().getRight() == null || current.getLeft().isRightThreaded()) {
            inPredParent = current;
        } else {
            inPredParent = current.getLeft();
            while (inPredParent.getRight().getRight() != null && !inPredParent.getRight().isRightThreaded()) {
                inPredParent = inPredParent.getRight();
            }
        }

        current.setValue(inPred.getValue());

        if (inPred.getLeft() == null && inPred.isRightThreaded() || inPred.getLeft() == null && inPred.getRight() == null) {
            noChildrenDeletion(inPredParent, inPred);
        } else {
            oneChildDeletion(inPredParent, inPred);
        }
    }

    private Node<T> inPred(Node<T> ptr) {
        ptr = ptr.getLeft();
        while (ptr.getRight() != null && !ptr.isRightThreaded()) {
            ptr = ptr.getRight();
        }
        return ptr;
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
