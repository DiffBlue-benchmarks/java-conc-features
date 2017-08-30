
public class Main
{
  public static void main (String args[]) throws Exception
  {
    test1 ();
    test2 ();
  }

  /** Creates one thread passing an instance of Runnable1 to the constructor of
   * java.lang.Thread */
  public static void test1 () throws InterruptedException
  {
    System.out.println ("test1: == begin ==");

    Runnable r1 = new Runnable1 ();
    Thread t1 = new Thread (r1);

    System.out.println ("test1: starting t1");
    t1.start ();
    System.out.println ("test1: joining t1");
    t1.join ();
    System.out.println ("test1: == end ==\n");
  }

  /** Creates one thread using a class extending java.lang.Thread */
  public static void test2 ()
      throws InterruptedException, IllegalThreadStateException
  {
    System.out.println ("test2: == begin ==");

    Thread2 t2 = new Thread2 ();

    System.out.println ("test2: starting t2");
    t2.start ();
    System.out.println ("test2: joining t2");
    t2.join ();
    System.out.println ("test2: == end ==\n");
  }
}

class Runnable1 implements Runnable
{
  @Override
  public void run ()
  {
    System.out.println ("t1: starting!");
    System.out.println ("t1: finishing!");
  }
}

class Thread2 extends Thread
{
  @Override
  public void run ()
  {
    System.out.println ("t2: starting!");
    System.out.println ("t2: finishing!");
  }

  /* Overriding method start makes this class not to start a thread */
  // public void start () throws IllegalThreadStateException
  // {
  //   System.out.println ("t2: method start!");
  //   try
  //   {
  //     sleep (1000);
  //   }
  //   catch (InterruptedException e)
  //   {}
  // }
}

