package base3D.project.scene;


import base3D.ENV;
import base3D.resources.ResourceCollection;
import base3D.threads.ThreadController;

public abstract class Scene {

    protected Camera3D cam;
    protected SkyBox skyBox;

    public final ResourceCollection resourceCollection;
    public final String sceneName;

    public final SceneManager sceneManager;

    public String skyBoxName = "skybox/skybox.png";

    public Scene(SceneManager sceneManager, String sceneName) {
        this.sceneName = sceneName;
        this.sceneManager = sceneManager;
        sceneManager.addScene(this);
        resourceCollection = sceneManager.getResourceCollection(sceneName);
    }

    public void init(int renderDistance) {
        this.cam = new Camera3D(ENV.p, ENV.p.g, 0, 0, 0);
        this.skyBox = new SkyBox(cam.camPos, skyBoxName, renderDistance);
    }

    public abstract void setup();         // initialise the objects
    public abstract void displayScene();  // Drawing of the scene / decoration

    public void setCanvasAndEnvironment() {
        ENV.p.background(111);
        cam.updateCamera();
        setSkyBox();
        setLights();
    }

    public void setSkyBox() {skyBox.drawSkyBox(ENV.p.g, cam.camPos);}

    public void renderObjects() {
        ThreadController.getInstance().objMan.renderObjects(ENV.p.g, -cam.camPos.y);
    }

    public void setLights() {
        ENV.p.pushMatrix();
        ENV.p.ambientLight(150, 150, 150);
        ENV.p.directionalLight(255, 255, 255, 0, 1, 0);
        ENV.p.directionalLight(200, 200, 200, 0, -1, 0);
        ENV.p.popMatrix();
    }

    public void render() {

        setCanvasAndEnvironment();
        displayScene();

        ENV.p.stroke(255, 100, 0);
        renderObjects();

        ENV.p.stroke(0);
        cam.renderHUD();



    }

}
