package base3D.threads.updateThreads;


import base3D.ui.Window;
import java.util.LinkedList;
import java.util.List;

public class UIUpdateThread extends UpdateThread {


    //////////////////////////
    // Attributes
    private final List<Window> windows;


    //////////////////////////
    // Constructors
    public UIUpdateThread(int frequency) {
        super(frequency, "UIUpdateThread");
        this.windows = new LinkedList<>();
    }


    //////////////////////////
    //Methods

    public void clearWindows() {
        windows.clear();
    }

    public void addWindow(Window w) {windows.add(w);}
    public void removeWindow(Window w) {windows.remove(w);}

    public void targetFunction() {
        for (Window w : windows) w.updateElement();
    }
    public void startThread() {
        started = true;
        Thread t = new Thread(this);
        t.start();
    }

}
