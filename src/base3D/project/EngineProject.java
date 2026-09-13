package base3D.project;

import base3D.ENV;
import base3D.project.scene.EngineScene;
import base3D.resources.ResourceCollection;
import base3D.resources.ResourceList;
import base3D.resources.ResourceType;

// Singleton
public class EngineProject extends Project {

    private static EngineProject instance = null;

    public static void init() {
        if (instance == null) {
            instance = new EngineProject();
        } else
            throw new RuntimeException("[EngineProject] Double initialisation ");
    }

    public static EngineProject getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new NullPointerException("[EngineProject] No instance found. Have you called the init() method?");
    }
    private EngineProject() {
        super(ENV.p.dataPath("engineSources"));

        EngineScene engineScene = new EngineScene(sceneManager);
        sceneManager.setActiveScene(engineScene);

        ResourceCollection resourceCollection = engineScene.resourceCollection;

        ResourceList textureAtlas = resourceCollection.textureAtlas;
        ResourceList soundAtlas = resourceCollection.textureAtlas;
        ResourceList shapeAtlas = resourceCollection.textureAtlas;

        //load all resources for the engine
        resourceCollection.loadResourcesDirectly();

        ResourceType.IMAGE.setAlternative(textureAtlas.getResource("missing/texture_image.png"));
        ResourceType.CUBE_TEXTURE.setAlternative(textureAtlas.getResource("missing/texture_cube.png"));
        ResourceType.SPHERE_TEXTURE.setAlternative(textureAtlas.getResource("missing/texture_sphere.png"));

        ResourceType.AUDIO.setAlternative(soundAtlas.getResource("missing/audio.mp3"));
        ResourceType.SHAPE.setAlternative(shapeAtlas.getResource("missing/shape.shp"));


    }
}
