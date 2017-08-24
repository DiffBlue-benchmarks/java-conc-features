import java.lang.InterruptedException;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

class Attachment 
{
  public Path path;
  public ByteBuffer buffer;
  public AsynchronousFileChannel asyncChannel;
}

class WriteHandler implements CompletionHandler<Integer, Attachment> 
{
  @Override
  public void completed(Integer result, Attachment attach) 
  {
    System.out.format("%s bytes written  to  %s%n", result,attach.path.toAbsolutePath());
    try 
    {
      attach.asyncChannel.close();
    } 
    catch (IOException e) 
    {
      e.printStackTrace();
    }
  }

  @Override
  public void failed(Throwable e, Attachment attach) 
  {
    try 
    {
      attach.asyncChannel.close();
    }
    catch (IOException e1) 
    {
      e1.printStackTrace();
    }
  }
}

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
    AsynchronousFileChannel afc = AsynchronousFileChannel.open(path, WRITE, CREATE);
    ByteBuffer dataBuffer = getDataBuffer();
    WriteHandler writeHandle = new WriteHandler();

    Attachment attach = new Attachment();
    attach.asyncChannel = afc;
    attach.buffer = dataBuffer;
    attach.path = path;

    afc.write(dataBuffer, 0, attach, writeHandle);

    while(attach.asyncChannel.isOpen())
      Thread.sleep(100);
  }
}
