
public class Main
{
  public static void main (String args[]) throws Exception
  {
    test1 ();
  }

  public static void test1 () throws InterruptedException
  {
    System.out.println ("test1: == begin ==");

    Runnable r1 = new Runnable1 ();
    Thread t1 = new Thread (r1);

    System.out.println ("test1: starting t1");
    t1.start ();
    System.out.println ("test1: joining t1");
    t1.join ();
    System.out.println ("test1: == end ==");
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

