import java.util.Collections;
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

        private int pId;
        private IMPMCQueue<PrintItem> roomQ;
        private Thread printerThread ;


        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            // TODO: Implement
            pId = id;
            roomQ = roomQueue;

            this.launch();
        }

        public void run()
        {
            SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, this.pId,
                    String.format(SyncLogger.FORMAT_PRINTER_LAUNCH, pId));
            PrintItem item = null;
            try {
                while (true)
                {
                    item = roomQ.Consume();
                    item.print();
                    SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, this.pId,
                            String.format(SyncLogger.FORMAT_PRINT_DONE, item));
                }
            }
            catch (QueueIsClosedExecption e) {
                // TODO: check if queue is closed
                if (item == null) {
                    SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, -1,
                            String.format(SyncLogger.FORMAT_TERMINATING, -1));
                }
                SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, this.pId,
                        String.format(SyncLogger.FORMAT_TERMINATING, item));
            }
            finally {}
        }

        private void launch(){
            this.printerThread = new Thread(this);
            this.printerThread.start();
        }

        public void waitTermination() throws InterruptedException {
            this.printerThread.join();
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
        try {
            roomQueue.Add(item);
            return true;
        } catch (QueueIsClosedExecption e) {
            return false;
        }

    }

    public void CloseRoom() throws InterruptedException
    {
        // TODO: Implement

        // reject additions to queue
        roomQueue.CloseQueue();
        Iterator<Printer> iterator = this.printers.iterator();
        while (iterator.hasNext()) {
            iterator.next().waitTermination();
        }

    }
}
