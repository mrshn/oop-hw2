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

    private int waitingProducerCount ;
    private int waitingConsumerCount ;


    public PrinterQueue(int maxElementCount)
    {
        // TODO: Implement

        // You can change this signature but also don't forget to
        // change the instantiation signature on the
        // Printer room
        this.maxElementCount = maxElementCount;
        curCount = 0;
        queueIsClosed = false;

        waitingProducerCount = 0;
        waitingConsumerCount = 0;

        teachersQueue = new LinkedList<>();
        studentsQueue = new LinkedList<>();

    }

    /**
     * Only called with lock
     * @return if Queue is full
     */
    private boolean isQueueFull()
    {
        return (maxElementCount == curCount);
    }

    /**
     * Only called with lock
     * @return if Queue is empty
     */
    private boolean isQueueEmpty()
    {
        return (0 == curCount);
    }

    public void Add(PrintItem data) throws QueueIsClosedExecption
    {
        lock.lock();
        try
        {
            while( isQueueFull() )
            {
                if (queueIsClosed)
                {
                    throw new QueueIsClosedExecption();
                }
                waitingProducerCount++;
                notFull.await();
                waitingProducerCount--;
            }
            if (queueIsClosed)
            {

                throw new QueueIsClosedExecption();
            }

            addHelper(data);

        }
        catch (InterruptedException ie)
        {
            ie.printStackTrace();
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Only called with lock
     * @param p is the item to be added
     */
    private void addHelper(PrintItem p)
    {
        if (p.getPrintType() == PrintItem.PrintType.INSTRUCTOR)
        {
            teachersQueue.add(p);
        } else
        {
            studentsQueue.add(p);
        }
        curCount++;
        notEmpty.signal();
    }

    public PrintItem Consume() throws QueueIsClosedExecption
    {
        PrintItem item = null;
        lock.lock();
        try
        {
            while ( isQueueEmpty() )
            {
                if ( queueIsClosed )
                {
                    throw new QueueIsClosedExecption();
                }
                waitingConsumerCount++;
                notEmpty.await();
                waitingConsumerCount--;
            }

            item = consumeHelper();

        }
        catch ( InterruptedException ie )
        {
            ie.printStackTrace();
        }
        finally
        {
            lock.unlock();
        }
        return item;
    }

    /**
     * Only called with lock
     * First tries to consume from the teachersQueue
     * Secondly tries to consume from the studentsQueue if not already consumed from the teachersQueue
     * @return the item consumed
     */
    private PrintItem consumeHelper()
    {
        // Try polling from teachers first
        PrintItem item = teachersQueue.poll();

        if( item == null )
        {
            // if teachers were empty -> poll from students
            item = studentsQueue.poll();
        }

        if( item != null )
        {
            // if polling was succesfull
            curCount--;
            notFull.signal();
        }

        return item;
    }

    public int RemainingSize()
    {
        int remainingSize;
            remainingSize = maxElementCount - curCount;
        return remainingSize;
    }

    public void CloseQueue()
    {
        lock.lock();
        try
        {
            queueIsClosed = true;
            if ( waitingConsumerCount > 0 )
            {
                notEmpty.signalAll();
            }
            if ( waitingProducerCount > 0 )
            {
                notFull.signalAll();
            }
        }
        catch (IllegalMonitorStateException imse)
        {
            imse.printStackTrace();
        }
        finally
        {
            lock.unlock(); // Release the lock
        }
    }




}
