/** Doubly linked list based Double Ended Queue
 * A double-ended queue or deque (pronounced “deck”)
 * is a generalization of a stack and a queue
 * that supports adding and removing items from
 * either the front or the back of the data structure.
 * @author  Cong Chen
 * @param <Item>
 */
import java.util.Iterator;

/** sentinel's forward link always points to the last element,
 * sentinel's backward link always points to the first element. */
public class Deque<Item> implements Iterable<Item>  {
    private final OneNode sentinel; // use circular sentinel topology
    private int size;

    private class OneNode {
        public OneNode prev;
        public Item item;
        public OneNode next;

        public OneNode(OneNode p, Item i, OneNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    /** construct an empty deque */
    public Deque() {
        sentinel = new OneNode(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    /** is the deque empty? */
    public boolean isEmpty() {
        if (size > 0) {
            return false;
        }
        return true;
    }

    /** return the number of items on the deque */
    public int size() {
        return size;
    }

    /** add the item to the front
     * Throw a java.lang.IllegalArgumentException
     * if the client calls with a null argument.*/
    public void addFirst(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        OneNode oldFrontNode = sentinel.next;
        OneNode newNode = new OneNode(sentinel, item, oldFrontNode);
        sentinel.next = newNode;
        oldFrontNode.prev = newNode;
        size += 1;
    }

    /** add the item to the end
     * Throw a java.lang.IllegalArgumentException
     * if the client calls with a null argument.*/
    public void addLast(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }

        OneNode oldBackNode = sentinel.prev;
        OneNode newNode = new OneNode(oldBackNode, item, sentinel);
        sentinel.prev = newNode;
        oldBackNode.next = newNode;
        size += 1;
    }

    /** remove and return the item from the front
     * Throw a java.util.NoSuchElementException if the client calls
     * when the deque is empty.*/
    public Item removeFirst() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        OneNode oldFrontNode = sentinel.next;
        sentinel.next = oldFrontNode.next;
        oldFrontNode.next.prev = sentinel;
        size -= 1;
        return oldFrontNode.item;
    }

    /** remove and return the item from the end
     * Throw a java.util.NoSuchElementException if the client calls
     * when the deque is empty.*/
    public Item removeLast() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        OneNode oldBackNode = sentinel.prev;
        sentinel.prev = oldBackNode.prev;
        oldBackNode.prev.next = sentinel;
        size -= 1;
        return oldBackNode.item;
    }

    /** return an iterator over items in order from front to end */
    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    /**
     * if the client calls the next() method in the iterator
     * when there are no more items to return.
     * Throw a java.util.NoSuchElementException
     */
    private class DequeIterator implements Iterator<Item> {
        private OneNode ptr = sentinel.next;

        public boolean hasNext() { return (ptr != sentinel); }
        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }

            Item item = ptr.item;
            ptr = ptr.next;
            return item;
        }

        /* not supported */
        public void remove() { throw new java.lang.UnsupportedOperationException(); }
    }

    public static void main(String[] args) {
        Deque<Integer> dq = new Deque<Integer>();
        for (int i = 0; i < 5; i++) {
            dq.addFirst(i);
        }

        for (int i : dq) {
            System.out.println(i);
        }
    }

}
