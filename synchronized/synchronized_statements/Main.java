
// Unlike synchronized methods, synchronized statements must specify the object that provides the intrinsic lock
class SynchronizedCounter 
{
  private int c = 0;

  public void increment() 
  {
    synchronized(this)
    {
      c++;
    }
  }

  public void decrement() 
  {
    synchronized(this)
    {
      c--;
    }
  }

  public int value() 
  {
    synchronized(this)
    {
      return c;
    }
  }
}

public class Main
{

  public static void main(String[] args)
  {
    Main test = new Main();
    test.test01();
  }

  public void test01()
  {
    SynchronizedCounter counter = new SynchronizedCounter();
    counter.increment();
    counter.decrement();
  }
}
