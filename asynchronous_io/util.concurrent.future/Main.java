import java.io.IOException;
import java.lang.InterruptedException;
import java.util.concurrent.ExecutionException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class Main
{

  public static void main(String[] args)
  {
    try
    {
      test01();
    }
    catch(ExecutionException e)
    {
      /// Exception thrown when attempting to retrieve the result of a 
      /// task that aborted by throwing an exception. This exception can
      /// be inspected using the Throwable.getCause() method.
    }
    catch(InterruptedException e)
    {
      /// Thrown when a thread is waiting, sleeping, or otherwise occupied (i.e result.get())
      /// and the thread is interrupted, either before or during the activity
    }
    catch(IOException e)
    {

    }
  }

  public static ByteBuffer getDataBuffer() 
  {
    String lineSeparator = System.getProperty("line.separator");

    StringBuilder sb = new StringBuilder();
    sb.append("test");
    sb.append(lineSeparator);
    String str = sb.toString();
    Charset cs = Charset.forName("UTF-8");
    ByteBuffer bb = ByteBuffer.wrap(str.getBytes(cs));

    return bb;
  }

  public static void test01() throws ExecutionException, InterruptedException, IOException
  {
    Path path = Paths.get("../example.txt");
    AsynchronousFileChannel afc   = AsynchronousFileChannel.open(path, WRITE,  CREATE);
    ByteBuffer dataBuffer = getDataBuffer();
    Future<Integer> result = afc.write(dataBuffer, 0);
    // wait for I/O to finish and get result.
    int written_bytes = result.get();
    System.out.format("%s bytes written to %s%n", written_bytes, path.toAbsolutePath());
  }
}
