import java.util.List;
import java.util.Arrays;

public class Main
{
   static class Producer implements Runnable
   {
       private int tId;

       // TODO: You may want to implement this class to test your code

       private PrinterRoom room;
       private List<PrintItem> jobs;
       private List<Integer> sleep;
       public Producer(int id, PrinterRoom room, List<PrintItem> jobs, List<Integer> sleep){
           this.room = room;
           this.jobs = jobs;
           this.sleep = sleep;
           this.tId = id;
       }

       public void join()
       {
           // TODO: Provide a thread join functionality for the main thread
       }

       public void run()
       {
           try {
               for (int i = 0; i < this.jobs.size(); i++)
               {
                   Thread.sleep(this.sleep.get(i));
                   // Printing and display the elements in ArrayList
                   SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, this.tId,
                           String.format(SyncLogger.FORMAT_ADD, this.jobs.get(i)));
                   if(!room.SubmitPrint(this.jobs.get(i), this.tId))
                   {
                       SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, this.tId,
                               String.format(SyncLogger.FORMAT_ROOM_CLOSED, this.jobs.get(i)));
                   }
               }
           } catch (InterruptedException e) {
               return;
           } finally {
               // TODO: check logger
                System.out.println("Job " + tId + " is exiting...");
           }
       }
   }

    public static void main(String args[]) throws InterruptedException
    {
        PrinterRoom room = new PrinterRoom(2, 8);
/*
        Producer producer = new Producer(0, room, Arrays.asList(
                new PrintItem(300, PrintItem.PrintType.STUDENT, 0),
                new PrintItem(300, PrintItem.PrintType.STUDENT, 1),
                new PrintItem(300, PrintItem.PrintType.STUDENT, 2),
                new PrintItem(300, PrintItem.PrintType.INSTRUCTOR, 3)),
                Arrays.asList(0, 0, 5000, 0)
        );
        Thread t1 = new Thread(producer);

        t1.start();
        t1.join();
*/
        while(true)
        {
            PrintItem item0 = new PrintItem(100, PrintItem.PrintType.STUDENT, 0);
            PrintItem item1 = new PrintItem(50, PrintItem.PrintType.INSTRUCTOR,1);
            PrintItem item2 = new PrintItem(66, PrintItem.PrintType.STUDENT, 2);
            if(!room.SubmitPrint(item0, 0))
            {
                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item0));
                break;
            }
            if(!room.SubmitPrint(item1, 0))
            {
                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item1));
                break;
            }
            if(!room.SubmitPrint(item2, 0))
            {
                SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, 0,
                                          String.format(SyncLogger.FORMAT_ROOM_CLOSED, item2));
                break;
            }
            break;
        }

        // Wait a little we are doing produce on the same thread that will do the close
        // actual tests won't do this.
        Thread.sleep((long)(3 * 1000));
        // Log before close
        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                                  "Closing Room");
        room.CloseRoom();
        // This should print only after all elements are closed (here we wait 3 seconds so it should be immediate)
        SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                                  "Room is Closed");
    }
}