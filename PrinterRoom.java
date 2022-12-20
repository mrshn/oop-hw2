import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Iterator;

public class PrinterRoom
{
    private class Printer implements Runnable
    {
        // TODO: Implement
        // ....

        private final Thread printerThread ;
        private final IMPMCQueue<PrintItem> roomQ;
        private final int pId;

        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            // TODO: Implement
            this.pId = id;
            this.roomQ = roomQueue;
            // When instantiated, it creates its own thread and
            // runs over the shared queue of the PrinterRoom class
            this.printerThread = new Thread(this);
            this.printerThread.start();
        }

        public void joinPrinter() throws InterruptedException
        {
            this.printerThread.join();
        }

        private void logHelper(String s)
        {
            SyncLogger.Instance().Log( SyncLogger.ThreadType.CONSUMER, this.pId, s );
        }

        public void run()
        {
            PrintItem item = null;
            logHelper( String.format(SyncLogger.FORMAT_PRINTER_LAUNCH, pId) );
            try
            {
                while (true)
                {
                    item = roomQ.Consume();
                    item.print();
                    logHelper( String.format(SyncLogger.FORMAT_PRINT_DONE, item) );
                }
            }
            catch (QueueIsClosedExecption exception)
            {
                logHelper( String.format(SyncLogger.FORMAT_TERMINATING, item) );
            }
        }

    }

    private IMPMCQueue<PrintItem> roomQueue;
    private final List<Printer> printers;

    public PrinterRoom(int printerCount, int maxElementCount)
    {
        // Instantiating the shared queue
        roomQueue = new PrinterQueue(maxElementCount);

        // Let's try streams
        // Printer creation automatically launches its thread
        printers = Collections.unmodifiableList(IntStream.range(0, printerCount)
                                                         .mapToObj(i -> new Printer(i, roomQueue))
                                                         .collect(Collectors.toList()));
        // Printers are launched using the same queue
    }

    public boolean SubmitPrint(PrintItem item, int producerId)
    {
        // TODO: Implement
        try
        {
            // Logging submitting effort, before potentially waiting on the queue.
            SyncLogger.Instance().Log( SyncLogger.ThreadType.PRODUCER, producerId, String.format(SyncLogger.FORMAT_ADD, item) );
            roomQueue.Add(item);
            return true;
        }
        catch (QueueIsClosedExecption e)
        {
            // Logging if room is closed and submitting is skipped
            SyncLogger.Instance().Log( SyncLogger.ThreadType.PRODUCER, producerId, String.format(SyncLogger.FORMAT_ROOM_CLOSED, item) );
            return false;
        }
    }

    public void CloseRoom()
    {
        // TODO: Implement
        roomQueue.CloseQueue();
        Iterator<Printer> iterator = this.printers.iterator();
        try
        {
            while (iterator.hasNext())
            {
                iterator.next().joinPrinter();
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ConcurrentModificationException e)
        {
            // Should not happen since printers list is final ?
            e.printStackTrace();
        }
    }
}
