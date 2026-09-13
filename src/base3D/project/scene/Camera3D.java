package base3D.project.scene;

import base3D.ENV;
import base3D.EventHandler;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PApplet.*;
import static processing.core.PConstants.PI;


public class Camera3D {

    public static final Runnable2 HUD_drawText = (PGraphics canvas, String s) -> {
        canvas.rect(300, 300, 100, 100);
        canvas.textSize(16);
        canvas.text(s, 20, 20);
    };
    

    public interface HUD {
        void render(PGraphics canvas, float y0);
    }
    public interface Runnable2 {
        void render(PGraphics canvas, String text);
    }

    public HUD HUDRenderMethod;

    private final PApplet environment;
    private final PGraphics canvas;

    public final PVector camPos;
    private final PVector camLookAt;
    private final PVector camUp;

    public final PVector previousMouseCoordinates;
    public final PVector initialMouseCoordinates;
    private boolean isAngleFromMouse = false;
    private boolean initialMouseCoordinatesSet;

    private float movementSpeed;
    private float angleLeftRight, angleUpDown;

    private final PVector screenDimensions;

    public boolean mouseEnabled;


    // constructor I - without parameters
    public Camera3D(PApplet environment, PGraphics canvas) {
        this.canvas = canvas;
        this.environment = environment;
        
        // set vectors
        camPos    = new PVector(canvas.width/2f, canvas.height/2f, 990);
        camLookAt = new PVector(canvas.width/2f,canvas.height/2f, -600);
        camUp     = new PVector( 0, 1, 0 );

        //
        movementSpeed = 2;



        // angles & ground
        angleLeftRight = 1.5f*PApplet.PI;
        angleUpDown = 0;

        //Vectors for mouse control
        //initialMouseCoordinates = new PVector(canvas.width/2f, canvas.height/2f);

        mouseEnabled = true;

        initialMouseCoordinates = new PVector(0,0,0);
        previousMouseCoordinates = new PVector(0,0,0);

        screenDimensions = new PVector(environment.width, environment.height);

        initialMouseCoordinatesSet = true;

        // Set up event calls

        EventHandler.subscribe(EventHandler.events.KEY_HELD, 'w', this::ForwardPressed);
        EventHandler.subscribe(EventHandler.events.KEY_HELD, 's', this::BackwardPressed);
        EventHandler.subscribe(EventHandler.events.KEY_HELD, 'a', this::LeftPressed);
        EventHandler.subscribe(EventHandler.events.KEY_HELD, 'd', this::RightPressed);
        EventHandler.subscribe(EventHandler.events.KEY_HELD, 'c', this::DownPressed);
        EventHandler.subscribe(EventHandler.events.KEY_HELD, ' ', this::UpPressed);



        EventHandler.subscribe(EventHandler.events.WHEEL_UP, 1, () -> setMovementSpeed(movementSpeed * (1f -0.1f) - 1));
        EventHandler.subscribe(EventHandler.events.WHEEL_DOWN, -1, () -> setMovementSpeed(movementSpeed * (1f + 0.1f) + 1));

        EventHandler.subscribe(EventHandler.events.MOUSE_PRESSED, ENV.p.RIGHT, () -> setAngleFromMouse(true));

        // fix the perspective if the size of the window has changed.
        EventHandler.subscribe(EventHandler.events.WINDOW_RESIZED, 0, this::windowSizeChanged);

        // debug
        EventHandler.subscribe(EventHandler.events.KEY_HELD, 'i', () -> System.out.println(this));

    }

    // constructor II - with parameters for POS
    public Camera3D (PApplet environment, PGraphics canvas, float x1, float y1, float z1) {
        this(environment, canvas);
        camPos.set(x1, y1, z1);
    }

    @Override
    public String toString() {
        return  "CAMERA3D(\n  movementSpeed: " + getMovementSpeed() +
                "\n  camLookAt: " + getLookAtRef().toString() +
                "\n  camPos: " + camPos.toString() +
                "\n  leftRight: " + angleLeftRight +
                "\n  UpDown " + angleUpDown +
                "\n  initialMouseCoordinates " + initialMouseCoordinates.toString() +
                "\n  previousMouseCoordinates " + previousMouseCoordinates.toString() +
                "\n)";

    }

    //-----------------------------------------
    // Methods:

    public void updateCamera() {

        if (mouseEnabled) {
            if (!environment.mousePressed) setAngleFromMouse(false);
            if (isAngleFromMouse) changeLookFromMouse();
        }

        setLookAt();

        // apply internal class vectors to actual camera
        canvas.camera (camPos.x, camPos.y, camPos.z,
                camLookAt.x, camLookAt.y, camLookAt.z,
                camUp.x, camUp.y, camUp.z);
    }


    private void ForwardPressed() {
        // run forward / running towards lookAt
        float x1, z1;
        x1 = camPos.x + PApplet.cos(angleLeftRight) * movementSpeed;
        z1 = camPos.z + PApplet.sin(angleLeftRight) * movementSpeed;
        camPos.set(x1, camPos.y, z1);
    }
    private void BackwardPressed() {
        // run backward
        float x1, z1;
        x1 = camPos.x - PApplet.cos(angleLeftRight) * movementSpeed;
        z1 = camPos.z - PApplet.sin(angleLeftRight) * movementSpeed;
        camPos.set(x1, camPos.y, z1);
    }
    private void RightPressed() {
        // left / sideways
        float x1, z1;
        x1 = camPos.x - PApplet.cos(angleLeftRight - PApplet.HALF_PI) * movementSpeed;
        z1 = camPos.z - PApplet.sin(angleLeftRight - PApplet.HALF_PI) * movementSpeed;
        camPos.set(x1, camPos.y, z1);
    }
    private void LeftPressed() {
        // right
        float x1, z1;
        x1 = camPos.x + PApplet.cos(angleLeftRight - PApplet.HALF_PI) * movementSpeed;
        z1 = camPos.z + PApplet.sin(angleLeftRight - PApplet.HALF_PI) * movementSpeed;
        camPos.set(x1, camPos.y, z1);
    }
    private void DownPressed() {
        camPos.y += movementSpeed;
    }
    private void UpPressed() {
        camPos.y -= movementSpeed;
    }

    public void renderHUD() {
        // HUD text upper left corner - this must be called at the very end of draw()
        // this is a 2D HUD

        canvas.camera();
        canvas.hint(canvas.DISABLE_DEPTH_TEST);
        canvas.noLights();

        // ------------------

        HUDRenderMethod.render(ENV.p.g, 0);

        // ------------------
        // reset all parameters to defaults
        canvas.textAlign(canvas.LEFT, canvas.BASELINE);
        canvas.rectMode(canvas.CORNER);
        canvas.textSize(32);
        canvas.hint(canvas.ENABLE_DEPTH_TEST); // no HUD anymore
    }


    //--------------------------------
    // events


    void changeLookFromMouse() {

        angleLeftRight = PApplet.map(initialMouseCoordinates.x - previousMouseCoordinates.x + environment.mouseX,
                                0, canvas.width, 0, PApplet.PI*3);

        angleUpDown = initialMouseCoordinates.y - previousMouseCoordinates.y + environment.mouseY;

        angleUpDown = PApplet.map(angleUpDown,
                0, canvas.height, 0, PApplet.PI);

        angleUpDown %= PI;
        angleUpDown += HALF_PI;


        //tempAngleUpDown =PApplet.map(initialMouseCoordinates.y -  previousMouseCoordinates.y + environment.mouseY,
        //                    -canvas.height/2f, canvas.height*3, -50, 50);


        //System.out.println(angleLeftRight + " " +  angleUpDown/PI*180);

    }

    public void setAngleFromMouse(boolean inp) {
        if (inp) {

            System.out.println(initialMouseCoordinates + " " + previousMouseCoordinates + " " + camPos);

            previousMouseCoordinates.set(environment.mouseX, environment.mouseY);
            initialMouseCoordinatesSet = false;
        } else {
            if (!initialMouseCoordinatesSet) {

                changeLookFromMouse();

                initialMouseCoordinates.x += environment.mouseX - previousMouseCoordinates.x;
                initialMouseCoordinates.y += environment.mouseY - previousMouseCoordinates.y;

                initialMouseCoordinatesSet = true;
            }
        }
        isAngleFromMouse = inp;
    }

    private void windowSizeChanged() {
        // avoid clipping : https : // forum.processing.org/two/discussion/4128/quick-q-how-close-is-too-close-why-when-do-3d-objects-disappear
        canvas.perspective(PApplet.PI/3f, (float) canvas.width/canvas.height, 1, 1000000);

        initialMouseCoordinates.x = initialMouseCoordinates.x / screenDimensions.x * environment.width;
        initialMouseCoordinates.y = initialMouseCoordinates.y / screenDimensions.y * environment.height;

        screenDimensions.set(environment.width, environment.height);

    }

    //-----------------------------------------
    // Getters & Setters:

    public void setLookAt() {
        float x1 = camPos.x + PApplet.cos(angleLeftRight) *10;
        //float y1 = camPos.y + angleUpDown /2;
        float y1 = camPos.y + PApplet.cos(angleUpDown + PI*0.5f)*10;
        float z1 = camPos.z + PApplet.sin(angleLeftRight) *10;
        camLookAt.set(x1, y1, z1);
    }

    public void setLookAt (float horizontal, float vertical) {
        angleLeftRight = horizontal;
        angleUpDown = vertical;
        setLookAt();
    }
    
    public PVector getLookAtRef() {return camLookAt;}

    public void setMovementSpeed(float sp) {
        movementSpeed = sp;
        if (movementSpeed < 0)
        {
            movementSpeed = 0;
        }
        else if (movementSpeed > 100)
        {
            movementSpeed = 100;
        }
    }

    public float getMovementSpeed() {return movementSpeed;}
}
