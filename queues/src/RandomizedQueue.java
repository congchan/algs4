/**
 * Array based randomized queue
 * A randomized queue is similar to a stack or queue,
 * except that the item removed is chosen uniformly at random from items in the data structure.
 * 1. The starting capacity is 8.
 * 2. For arrays of length 16 or more, usage factor should always be at least 25%.
 * @author  Cong Chen
 * @param <Item>
 */
import java.util.Iterator;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int RFACTOR = 2; // resize factor
    private static final int USAGE = 4; // usage factor
    private static final int MINCAPACITY = 8; // the min capacity of the container
    private Item[] items;
    private int size;


    /** construct an empty randomized queue. */
    public RandomizedQueue() {
        items = (Item[]) new Object[MINCAPACITY];
        size = 0;
    }

    /** Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        if (size > 0) {
            return false;
        }
        return true;
    }

    /** return the number of items on the randomized queue */
    public int size() {
        return size;
    }

    /** add the item
     * Throw a java.lang.IllegalArgumentException if the client calls with a null argument.*/
    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (size == items.length) {
            resize(size * RFACTOR);
        }
        items[size++] = item;
    }

    /** remove and return a random item
     * move the last item to the removed position
     * Throw a java.util.NoSuchElementException if the client calls
     * when the randomized queue is empty.*/
    public Item dequeue() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int index = StdRandom.uniform(size);
        Item returnItem = items[index];
        items[index] = items[size - 1];
        items[size - 1] = null;
        size--;
        if (size >= MINCAPACITY && size <= items.length / USAGE) {
            resize(items.length / RFACTOR);
        }
        return returnItem;
    }

    /** return a random item (but do not remove it)
     * Throw a java.util.NoSuchElementException if the client calls
     * when the randomized queue is empty.*/
    public Item sample() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int index = StdRandom.uniform(size);
        return items[index];
    }

    /** return an independent iterator over items in random order */
    @Override
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    /**
     * if the client calls the next() method in the iterator
     * when there are no more items to return.
     * Throw a java.util.NoSuchElementException
     */
    private class RandomizedQueueIterator implements Iterator<Item> {
        private int i = size;
        private final int[] index = new int[size];
        public RandomizedQueueIterator() {
            for (int j = 0; j < size; j++) {
                index[j] = j;
            }
            StdRandom.shuffle(index);
        }
        public boolean hasNext() { return (i > 0); }
        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            return items[index[--i]];
        }

        /* not supported */
        public void remove() { throw new java.lang.UnsupportedOperationException(); }
    }

    /** resize the container to target capacity */
    private void resize(int capacity) {
        Item[] newContainer = (Item[]) new Object[capacity];
        System.arraycopy(items, 0, newContainer, 0, size);
        items = newContainer;
    }

    public static void main(String[] args) {
        System.out.println("Running remove/resize test.");

        // System.out.println("Make sure to uncomment the lines below (and delete this print statement).");
        RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        // should be empty
        System.out.println("isEmpty() returned " + rq.isEmpty());

        for (int i = 0; i < 100; i++) {
            rq.enqueue(i);
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(rq.dequeue());
        }

        // should not be empty
        System.out.println("isEmpty() returned " + rq.isEmpty());

    }

}
