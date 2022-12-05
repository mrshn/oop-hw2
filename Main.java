public class Main
{
   static class Producer implements Runnable
   {

       // TODO: You may want to implement this class to test your code

       public void join()
       {
           // TODO: Provide a thread join functionality for the main thread
       }
   }

    public static void main(String args[]) throws InterruptedException
    {
        PrinterRoom room = new PrinterRoom(2, 8);
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