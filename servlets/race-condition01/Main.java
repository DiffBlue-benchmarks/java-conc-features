// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

// Extend HttpServlet class
public class Main  extends HttpServlet 
{
   private Stack<String> astack = new Stack<>();
   private String message;

   public void init() throws ServletException {
      // Do required initialization
      message = "Hello World";
      astack.push(message);
   }

   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException 
    {
      // Set response content type
      int cdi = Integer.parseInt(request.getQueryString());
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      if(astack.empty())
      {
        out.println("<h1>" + "stack empty: "+  cdi + "</h1>");
        return;
      }
      if(cdi==1)
      {
        // Delay thread
        try
        {
          Thread.sleep(3000);
        }
        catch(Exception e)
        {

        }
      }
      astack.pop();
      out.println("<h1>" + cdi + "</h1>");
   }

   public void destroy() 
   {
      // do nothing.
   }
}
