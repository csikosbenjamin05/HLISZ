import HLISZ.scenes.FreeFall;
import HLISZ.scenes.MainMenu;
import HLISZ.scenes.Satellite;
import HLISZ.scenes.Throw;
import base3D.project.Project;
import base3D.ENV;
import base3D.EventHandler;
import base3D.project.scene.Scene;
import base3D.resources.ResourceManager;
import base3D.threads.ThreadController;
import processing.core.PApplet;
import processing.event.MouseEvent;


public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main(new String[] {Main.class.getName()});
    }

    Project project;
    Scene scene1;
    Scene scene2;
    Scene scene3;
    Scene scene4;
    ThreadController threadController;

    @Override
    public void settings() {
        size(1000, 600, P3D);
        ENV.set(this);

        ThreadController.init(30, 240);
        threadController = ThreadController.getInstance();
    }

    @Override
    public void setup() {
        surface.setLocation(100, 100);
        surface.setTitle("Ha a legellenallas is szamit");
        surface.setResizable(true);
        smooth();

        /*
        project =  ResourceManager.init("/home/beni/IdeaProjects/base3D/TestProject");
        scene1 = new MyScene(project.sceneManager);
        project.sceneManager.setActiveScene(scene1);
        scene1.init(1000*100);

         */

        project =  ResourceManager.init("data/HLISZ");
        scene1 = new MainMenu(project.sceneManager);
        scene2 = new FreeFall(project.sceneManager);
        scene3 = new Throw(project.sceneManager);
        scene4 = new Satellite(project.sceneManager);

        project.sceneManager.setActiveScene(scene1);


        println("setup");

        EventHandler.giveEventCalls();

        threadController.startAllThreads();

    }

    @Override
    public void draw() {
        EventHandler.giveEventCalls(); // handle events
        project.sceneManager.render();
        threadController.printState();
    }

    @Override
    public void keyPressed() {
        EventHandler.inputEvent(EventHandler.events.KEY_PRESSED, key);
    }

    @Override
    public void keyReleased() {
        EventHandler.inputEvent(EventHandler.events.KEY_RELEASED, key);
    }

    @Override
    public void mouseWheel(MouseEvent event) {
        int v = event.getCount();
        EventHandler.inputEvent((v==1) ? EventHandler.events.WHEEL_UP : EventHandler.events.WHEEL_DOWN, v);
    }

    @Override
    public void mousePressed() {
        EventHandler.inputEvent(EventHandler.events.MOUSE_PRESSED, mouseButton);
    }

    @Override
    public void mouseReleased() {
        EventHandler.inputEvent(EventHandler.events.MOUSE_RELEASED, mouseButton);
    }

    @Override
    public void exit() {
        threadController.stopAllThreads();
        super.exit();
    }
}
