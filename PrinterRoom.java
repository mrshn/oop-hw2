import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrinterRoom
{
    private class Printer implements Runnable
    {
        // TODO: Implement
        // ....

        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            // TODO: Implement
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
    }

    public void CloseRoom()
    {
        // TODO: Implement
    }
}
