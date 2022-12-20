import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main
{
    static class Producer implements Runnable
    {
        private PrinterRoom room;
        private int producerID;
        private PrintItem.PrintType type;

        public Producer(PrinterRoom room, int id, PrintItem.PrintType type)
        {
            this.room = room;
            this.producerID = id;
            this.type = type;
            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerID,
                    String.format(SyncLogger.FORMAT_PRODUCER_LAUNCH, producerID));
        }

        @Override
        public void run() {
            while (true)
            {
                PrintItem item = new PrintItem(new Random().nextInt(1000), type, producerID);
                if(!room.SubmitPrint(item, producerID))
                {
                    break;
                }
            }
            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerID,
                    String.format(SyncLogger.FORMAT_TERMINATING, producerID));
        }
    }

    public static void main(String args[]) throws InterruptedException
    {
        PrinterRoom room = new PrinterRoom(2, 8);
        List<Producer> producers = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            PrintItem.PrintType type = (i % 2 == 0) ? PrintItem.PrintType.STUDENT : PrintItem.PrintType.INSTRUCTOR;
            producers.add(new Producer(room, i, type));
        }
        for (Producer p : producers) {
            new Thread(p).start();
        }

        Thread.sleep((long)(3 * 1000));

        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                "Closing Room");
        room.CloseRoom();

        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                "Room is Closed");
    }
}