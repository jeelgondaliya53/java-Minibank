package service;
import java.util.LinkedList;
import java.util.Queue;
public class TransactionBuffer {
    private final Queue<Runnable> queue = new LinkedList<>();
    private boolean finished;
    public synchronized void put(Runnable transaction) throws InterruptedException {
        queue.add(transaction);
        notifyAll();
    }
    public synchronized Runnable take() throws InterruptedException {
        while (queue.isEmpty() && !finished) wait();
        return queue.poll();
    }
    public synchronized void finish() { finished = true; notifyAll(); }
}
