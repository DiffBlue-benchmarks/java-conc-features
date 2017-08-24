import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

// Simple GUI:
// AWT create an event dispatching thread (EDT). This is a background thread used in Java to
// process events from the Abstract Window Toolkit (AWT) graphical user interface event queue
// The events are primarily update events that cause user interface components to redraw themselves, 
// or input events from input devices such as the mouse or keyboard.
// AWT uses a single-threaded painting model in which all screen updates must be performed from a single thread. 
// The event dispatching thread is the only valid thread to update the visual state of visible user interface components. 
// NOTE: Updating visible components from other threads is the source of many common bugs in Java programs.
public class Main  extends Frame
{  
  private Label statusLabel;
  public Main()
  {  
    statusLabel =new Label();
    Button b=new Button("click me");  
    b.setBounds(30,100,80,30);// setting button position  
    add(b);//adding button into frame  
    add(statusLabel);//adding label to frame
    statusLabel.setText("Thread ID: " + Thread.currentThread().getId());
    statusLabel.setSize(350,100);
    setLayout(new GridLayout(3, 1));
    b.addActionListener(new ActionListener() 
    {
      public void actionPerformed(ActionEvent e) 
      {
        statusLabel.setText("Thread ID: " + Thread.currentThread().getId());
      }
    });

    setSize(300,300);//frame size 300 width and 300 height  
    setVisible(true);//now frame will be visible, by default not visible  
  }  
  public static void main(String args[])
  {  
    Main f=new Main();  
  }
}  
