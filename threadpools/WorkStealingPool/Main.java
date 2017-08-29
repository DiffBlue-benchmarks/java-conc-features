import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;

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
    ForkJoinPool sch=new ForkJoinPool(10, ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
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
          sch.submit(aRunnable2);
          // Sleep for 5 sec
          Thread.sleep(5 * 1000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
      }
    };
    sch.shutdown();
    sch.submit(aRunnable);
  }
}
