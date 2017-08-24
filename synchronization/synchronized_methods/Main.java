
/// When a thread invokes a synchronized method, it automatically acquires the 
/// intrinsic lock for that method's object and releases it when the method returns. 
/// The lock release occurs even if the return was caused by an uncaught exception.
/// By making the methods in this class syncrhnoized. we get two effects.
///
/// First, it is not possible for two invocations of synchronized 
/// methods on the same object to interleave. When one thread is executing a 
/// synchronized method for an object, all other threads that invoke 
/// synchronized methods for the same object block (suspend execution) until the first 
/// thread is done with the object.
///
/// Second, when a synchronized method exits, it automatically establishes a happens-before relationship with 
/// any subsequent invocation of a synchronized method for the same object. 
/// This guarantees that changes to the state of the object are visible to all threads.
class SynchronizedCounter 
{
  private int c = 0;

  public synchronized void increment() 
  {
    c++;
  }

  public synchronized void decrement() 
  {
    c--;
  }

  public synchronized int value() 
  {
    return c;
  }
}

public class Main
{

  public static void main(String[] args)
  {
    test01();
  }

  public static void test01()
  {
    SynchronizedCounter counter = new SynchronizedCounter();
    counter.increment();
    counter.decrement();
  }
}
