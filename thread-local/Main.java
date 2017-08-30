
public class Main
{
  public static void main (String args[]) throws Exception
  {
    test1 ();
    test2 ();
  }

  /** Creates MAX threads and waits for their termination */
  public static void test1 () throws InterruptedException
  {
    System.out.println ("m: == begin test 1 ==");

    final int MAX = 3;
    Thread1[] t = new Thread1[MAX];

    for (int i = 0; i < MAX; i++)
    {
      t[i] = new Thread1 (i * 100);

      System.out.printf ("m: starting thread %d\n", t[i].getId());
      t[i].start ();
    }

    for (int i = 0; i < MAX; i++)
    {
      System.out.printf ("m: joining thread %d\n", t[i].getId());
      t[i].join ();
    }
    System.out.println ("m: == end ==\n");
  }

  /** Creates MAX threads and waits for their termination */
  public static void test2 () throws InterruptedException
  {
    System.out.println ("m: == begin test 2 ==");

    final int MAX = 3;
    Thread2[] t = new Thread2[MAX];

    for (int i = 0; i < MAX; i++)
    {
      t[i] = new Thread2 (i * 100);

      System.out.printf ("m: starting thread %d\n", t[i].getId());
      t[i].start ();
    }

    for (int i = 0; i < MAX; i++)
    {
      System.out.printf ("m: joining thread %d\n", t[i].getId());
      t[i].join ();
    }
    System.out.println ("m: == end ==\n");
  }
}

/** A thread that increments a totally private field count, which is initialized
 * using the init parameter of the constructor. */
class Thread1 extends Thread
{
  // private variable (field), not shared with any other thread, and not using
  // any thread local whatsoever
  int count;

  Thread1 (int init)
  {
    count = init;
  }

  @Override
  public void run ()
  {
    long tid = Thread.currentThread().getId();
    System.out.printf ("t%d: starting!\n", tid);

    for (int i = 0; i < 5; i++)
    {
      count++;
      System.out.printf ("t%d: count %d\n", tid, count);
    }

    System.out.printf ("t%d: finishing!\n", tid);
  }
}

/** A thread that increments a ThreadLocal static field "shared" between
 * instances of this class, but all initialized and containing a different local
 * value. This mimics the behavior of Thread1 but using a ThreadLocal object.
 */
class Thread2 extends Thread
{
  // A ThreadLocal static, shared value, which however maintains a different
  // value for each thread using it.  We could define and use the method
  // initialValue() of ThreadLocal<T>, but we don't :)
  static ThreadLocal<Integer> count = new ThreadLocal<Integer> ();
  int init;

  Thread2 (int init)
  {
    // Calling "count.set (init)" has no relevant effect, as it only achieves to
    // set the value of the counter for the calling thread, which is the main
    // thread, and not the Thread that we are trying to construct!!

    // So the only possibility we are left with, if we don't want to overload
    // the method initialValue() is to pass the initial value using a private
    // variable ...
    this.init = init;
  }

  @Override
  public void run ()
  {
    long tid = Thread.currentThread().getId();
    System.out.printf ("t%d: starting!\n", tid);

    // ... and then initialize the counter here when we are already running in
    // the thread
    count.set (init);

    for (int i = 0; i < 5; i++)
    {
      count.set (count.get() + 1);
      System.out.printf ("t%d: count %d\n", tid, count.get());
    }

    System.out.printf ("t%d: finishing!\n", tid);
  }
}

