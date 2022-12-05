import java.util.concurrent.locks.*;
import java.util.Queue;
import java.util.LinkedList;

public class PrinterQueue implements IMPMCQueue<PrintItem>
{
    // TODO: This is all yours

    private static Lock lock = new ReentrantLock();
    private static Condition notEmpty = lock.newCondition();
    private static Condition notFull = lock.newCondition();

    private Queue<PrintItem> teachersQueue;
    private Queue<PrintItem> studentsQueue;

    private final int maxElementCount;
    private int curCount ;
    private boolean queueIsClosed;

    private int waiting_jobs ;
    private int waiting_printers ;


    public PrinterQueue(int maxElementCount)
    {
        // TODO: Implement

        // You can change this signature but also don't forget to
        // change the instantiation signature on the
        // Printer room
        this.maxElementCount = maxElementCount;
        curCount = 0;
        queueIsClosed = false;

        waiting_jobs = 0;
        waiting_printers = 0;

        teachersQueue = new LinkedList<>();
        studentsQueue = new LinkedList<>();

    }

    private boolean isQueueFull() {
        return (maxElementCount == curCount);
    }

    private boolean isQueueEmpty() {
        return (0 == curCount);
    }

    public void Add(PrintItem data) throws QueueIsClosedExecption {
        lock.lock();
        try {
            while( isQueueFull() ) {
                if (queueIsClosed) {
                    throw new QueueIsClosedExecption();
                }
                waiting_jobs++;
                notFull.await();
                waiting_jobs--;
            }
            if (queueIsClosed) {
                throw new QueueIsClosedExecption();
            }
            addItemToQueue(data);
            notEmpty.signal();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        finally {
            lock.unlock();
        }
    }

    public PrintItem Consume() throws QueueIsClosedExecption{
        PrintItem item = null;
        lock.lock();
        try {
            while ( isQueueEmpty() ) {
                if (queueIsClosed && curCount==0) {
                    throw new QueueIsClosedExecption();
                }
                waiting_printers++;
                notEmpty.await();
                waiting_printers--;
            }
            if (queueIsClosed && curCount==0) {
                throw new QueueIsClosedExecption();
            }
            item = getItemFromQueue();
            notFull.signal();

        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        finally {
            lock.unlock(); // Release the lock
        }
        return item;
    }

    public int RemainingSize() {

        int remainingSize;
        lock.lock();
        remainingSize = maxElementCount - curCount;
        lock.unlock();

        return remainingSize;
    }

    public void CloseQueue() {
        // queue is closed
        lock.lock();
        try  {
            queueIsClosed = true;
            // terminate all waiting threads here
            if (waiting_jobs>0) notFull.signalAll();
            if ((curCount == 0) && (waiting_printers>0)) notEmpty.signalAll();
        } finally {
            lock.unlock(); // Release the lock
        }
    }

    private PrintItem getItemFromQueue() {
        PrintItem item = teachersQueue.poll();
        if(item == null) {
            item = studentsQueue.poll();
        }
        if(item != null) {
            curCount--;
        }
        return item;
    }

    private void addItemToQueue(PrintItem p) {
        if (p.getPrintType() == PrintItem.PrintType.INSTRUCTOR){
            teachersQueue.add(p);
        } else {
            studentsQueue.add(p);
        }
        curCount++;
    }
}
