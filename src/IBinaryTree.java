import java.util.Collection;

/* Create a binary tree
   Implement the following methods:
   - add(T value) - inserts a value into the tree
   - preOrder() - returns a collection of values in pre-order
   - inOrder() - returns a collection of values in in-order
   - postOrder() - returns a collection of values in post-order
   - makeRightThreaded() - converts the tree into a right-threaded tree
   - threadsToString() - returns a string representation of the tree with threads
   - delete(T value) - deletes a value from the tree after converting it to a right-threaded tree
 */

public interface IBinaryTree<T> {
    void add(T value);

    Collection<T> preOrder();

    Collection<T> inOrder();

    Collection<T> postOrder();

    void makeRightThreaded();

    void delete(T value);

    String threadsToString();
}
