import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;

public class Main
{

  public static void main(String[] args)
  {
    try
    {
      test01();
    }
    catch(Exception e)
    {
    }
  }

  public static void test01() throws InterruptedException
  {
    /// Creates a thread pool with two threads that can schedule
    /// commands to run after a given delay, or to execute periodically.
    ScheduledExecutorService schExService= Executors.newScheduledThreadPool(2);

    // Create a Runnable object of anonymous class
    Runnable aRunnable = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          // Print a value
          System.out.println("Runnable at work !");
          // Sleep for 5 sec
          Thread.sleep(5 * 1000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    };

    // Create a Runnable object of anonymous class
    Runnable aRunnable2 = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          // Print a value
          System.out.println("Runnable 2 at work !");
          // Sleep for 5 sec
          Thread.sleep(5 * 1000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    };
  
    // Creates and executes a one-shot action that becomes enabled after the given delay.
    // 1st param - the task to execute
    // 2nd param - the time from now to delay execution
    // 3rd param - the time unit of the delay parameter
    schExService.scheduleWithFixedDelay(aRunnable2, 1, 2, TimeUnit.SECONDS);
    schExService.scheduleWithFixedDelay(aRunnable, 1, 2, TimeUnit.SECONDS);

    // waits for termination for 30 seconds only
    schExService.awaitTermination(10, TimeUnit.SECONDS);
    schExService.shutdown();
  }
}
