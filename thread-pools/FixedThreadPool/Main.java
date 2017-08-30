import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main
{

  public static void main(String[] args)
  {
    test01();
  }

  public static void test01()
  {
    /// Ceates a thread pool that reuses a fixed number of threads(2)operating off
    /// a shared unbounded queue, using the provided ThreadFactory to create new threads when needed.
    /// At any point, at most nThreads threads will be active processing tasks. If additional tasks
    /// are submitted when all threads are active, they will wait in the queue until a thread is
    /// available. If any thread terminates due to a failure during execution prior to shutdown,
    /// a new one will take its place if needed to execute subsequent tasks.
    /// The threads in the pool will exist until it is explicitly shutdown.
    ExecutorService fixedPool = Executors.newFixedThreadPool(2);

    // Create a Callable object of anonymous class
    Callable<String> aCallable = new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        // Print a value
        System.out.println("Callable at work !");
        // Sleep for 5 sec
        Thread.sleep(5 * 1000);
        return "Callable done!";
      }
    };

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

    Future<String> callableFuture = fixedPool.submit(aCallable);
    Future<?> runnableFuture = fixedPool.submit(aRunnable);

    fixedPool.shutdown();
  }
}
