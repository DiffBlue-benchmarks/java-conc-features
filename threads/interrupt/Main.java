
public class Main
{
  public static void main (String args[]) throws Exception
  {
    test1 ();
    test2 ();
    test3 ();
    test4 ();
  }

  /** Creates one thread, interrupts it and waits for its termination. */
  public static void test1 () throws InterruptedException
  {
    System.out.println ("m: == begin test 1 ==");

    Runnable1 r1 = new Runnable1 ();
    Thread t = new Thread (r1);

    System.out.println ("m: starting thread");
    t.start ();

    System.out.println ("m: sleeping 350ms");
    Thread.sleep (350);

    System.out.println ("m: interrupting thread");
    t.interrupt ();

    System.out.println ("m: joining thread");
    t.join ();

    System.out.println ("m: == end ==\n");
  }

  /** Creates one thread, sleeps for 450ms and interrupts the thread. */
  public static void test2 () throws InterruptedException
  {
    System.out.println ("m: == begin test 2 ==");

    Runnable2 r2 = new Runnable2 ();
    Thread t = new Thread (r2);

    System.out.println ("m: starting thread");
    t.start ();

    System.out.println ("m: sleeping 450ms");
    Thread.sleep (450);

    System.out.println ("m: interrupting thread");
    t.interrupt ();

    System.out.println ("m: joining thread");
    t.join ();

    System.out.println ("m: == end ==\n");
  }

  /** Creates one thread, sleeps for 450ms and interrupts it */
  public static void test3 () throws InterruptedException
  {
    System.out.println ("m: == begin test 3 ==");

    Thread t = new Thread (new Runnable3 ());

    System.out.println ("m: starting thread");
    t.start ();

    System.out.println ("m: sleeping 450ms");
    Thread.sleep (450);

    System.out.println ("m: interrupting thread");
    t.interrupt ();

    System.out.println ("m: joining thread");
    t.join ();

    System.out.println ("m: == end ==\n");
  }

  /** Creates one thread, sleeps for 450ms and interrupts it */
  public static void test4 () throws InterruptedException
  {
    System.out.println ("m: == begin test 4 ==");

    Thread t = new Thread (new Runnable4 ());

    System.out.println ("m: starting thread");
    t.start ();

    System.out.println ("m: sleeping 450ms");
    Thread.sleep (450);

    System.out.println ("m: interrupting thread");
    t.interrupt ();

    System.out.println ("m: joining thread");
    t.join ();

    System.out.println ("m: == end ==\n");
  }
}

/** Waits forever on its own lock to be interrupted. Terminates afterwards */
class Runnable1 implements Runnable
{
  Object lock = new Object ();

  @Override
  public void run ()
  {
    System.out.println ("t: starting!");

    synchronized (lock)
    {
      System.out.println ("t: waiting indefinitely...");
      try {
        lock.wait ();
      }
      catch (InterruptedException e)
      {
        assert !Thread.interrupted();
        System.out.printf ("t: interrupted: e: %s\n", e);
        System.out.println ("t: exiting");
        return;
      }
    }
    System.out.println ("t: finishing!");
  }
}

/** Waits to be interrupted for up to 2 seconds using Object.wait(ms).
 * Terminates afterwards */
class Runnable2 implements Runnable
{
  Object lock = new Object ();

  @Override
  public void run ()
  {
    System.out.println ("t: starting!");

    synchronized (lock)
    {
      for (int i = 0; i < 20; i++)
      {
        System.out.println ("t: waiting on lock for 100ms!");
        try {
          lock.wait (100);
        }
        catch (InterruptedException e)
        {
          assert !Thread.interrupted();
          System.out.printf ("t: interrupted: e: %s\n", e);
          System.out.println ("t: exiting");
          return;
        }
      }
    }

    System.out.println ("t: finishing!");
  }
}

/** Waits to be interrupted for up to 2 seconds using Thread.sleep(ms).
 * Terminates afterwards */
class Runnable3 implements Runnable
{
  @Override
  public void run ()
  {
    System.out.println ("t: starting!");

    System.out.println ("t: sleeping for 2s");
    try {
      Thread.sleep (2000);
    }
    catch (InterruptedException e)
    {
      assert !Thread.interrupted();
      System.out.printf ("t: interrupted: e: %s\n", e);
      System.out.println ("t: exiting");
      return;
    }
    System.out.println ("t: finishing!");
  }
}

/** Enters a spinlock which exits only when the thread is interrupted. */
class Runnable4 implements Runnable
{
  @Override
  public void run ()
  {
    System.out.println ("t: starting!");

    System.out.println ("t: entering a spinlock ....");
    int i = 0;
    while (true)
    {
      i++;
      if (i % 50000000 == 0)
        System.out.printf ("t: iterating, i %d\n", i);
      if (Thread.interrupted())
      {
        System.out.printf ("t: interruption, breaking, i %d\n", i);
        break;
      }
    }
    System.out.println ("t: finishing!");
  }
}

