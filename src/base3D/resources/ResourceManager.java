package base3D.resources;

import base3D.project.EngineProject;
import base3D.project.Project;

import java.util.*;

// =======================================================
// Resource Manager:
//      - singleton
//      - loads the textures    THIS IS HELL        * WAS
// =======================================================


public class ResourceManager {


    //////////////////
    // Static fields
    /////////////////

    private static ResourceManager instance = null;


    //////////////////
    // Instance fields
    /////////////////
    private EngineProject engineProject;
    private Project project;


    private final Map<Resource, List<Runnable>> subscribers;

    private boolean subscribersLocked = false;

    //////////////////
    // Initialising & Get instance
    /////////////////

    public static Project init(String projectPath) {
        if (instance == null) {
            instance = new ResourceManager();
            EngineProject.init();

            instance.engineProject = EngineProject.getInstance();

            instance.project = new Project(projectPath);
            //instance.project.loadResources();

            return instance.project;

        } else
            throw new RuntimeException("[EngineProject] Double initialisation ");
    }

    public static ResourceManager getInstance() {
        if (instance != null) {
            return instance;
        }
        throw new NullPointerException("[Resource Manager] No instance found. Have you called the init(path) method?");
    }

    private ResourceManager() {
        subscribers = new HashMap<>();
    }

    public static Texture getTexture(String name, Runnable redrawMethod) {
        if (instance != null) {
            if (instance.project.sceneManager.isResourcePresent(name)) {

                while (instance.subscribersLocked) {}

                instance.subscribersLocked = true;
                Texture texture = instance.project.sceneManager.getTexture(name);
                if (!instance.subscribers.containsKey(texture))
                    instance.subscribers.put(texture, new ArrayList<>());

                instance.subscribers.get(texture).add(redrawMethod);

                instance.subscribersLocked = false;

                return texture;

            }

            else
                throw new RuntimeException("[Resource Manager] Texture " + name + " doesn't exist.");

        }
        throw new NullPointerException("[Resource Manager] No instance found. Have you called the init(path) method?");
    }

    public static void resourceUpdated(Resource resource) {

        while (instance.subscribersLocked) {}

        instance.subscribersLocked = true;
        try {
                if (instance.subscribers.containsKey(resource))
                    for (Runnable method : instance.subscribers.get(resource))
                        method.run();

        }catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }

        instance.subscribersLocked = false;


    }


}