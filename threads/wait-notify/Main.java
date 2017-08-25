
public class Main
{
  public static void main (String[] args) throws Exception
  {
    Main m = new Main ();
    m.test1 (1, false);
    m.test1 (2, true);
  }

  Object lock = new Object();
  boolean available = false;
  long data;

  // Thread 1: waits until data is available using a wait WITHOUT timeout
  Thread t1 = new Thread () {
    @Override
    public void run ()
    {
      System.out.println ("t1: starting");

      // lock the monitor
      synchronized (lock)
      {
        // wait until data is available
        while (!available)
        {
          System.out.println ("t1: !available, waiting");
          try { lock.wait (); } catch (InterruptedException e) {}
        }

        // print the data and unlock
        System.out.printf ("t1: data available: %d\n", data);
      }

      System.out.println ("t1: finishing");
      available = false;
    }
  };

  // Thread 2: waits until data is available using a wait WITH timeout
  Thread t2 = new Thread () {
    @Override
    public void run ()
    {
      System.out.println ("t2: starting");

      // lock the monitor
      synchronized (lock)
      {
        // wait for 100ms until data is available, then retry
        while (!available)
        {
          System.out.println ("t2: !available, waiting");
          try { lock.wait (100); } catch (InterruptedException e) {}
        }

        // print the data and unlock
        System.out.printf ("t2: data available: %d\n", data);
        available = false;
      }

      System.out.println ("t2: finishing");
    }
  };


  /** Illustrates the use of Object.{wait,notify} to signal a synchronization
   * condition (data sent from the main thread to the thread is now available)
   */
  public void test1 (int num, boolean wantTimeout) throws InterruptedException
  {
    System.out.printf ("test%d: == begin ==\n", num);

    System.out.printf ("test%d: starting thread\n", num);
    Thread t = wantTimeout ? t1 : t2;
    t.start ();

    // occasionally we are slow, making the thread to wait()
    if (System.currentTimeMillis() % 2 == 1) Thread.sleep (600);

    synchronized (lock)
    {
      // if data was not available, signal the thread
      if (!available)
      {
        System.out.printf ("test%d: notifying\n", num);
        lock.notify();
      }

      // make data available (we still hold the lock)
      available = true;
      data = System.currentTimeMillis();
    }

    System.out.printf ("test%d: joining\n", num);
    t.join ();
    System.out.printf ("test%d: == end ==\n\n", num);
  }
}
