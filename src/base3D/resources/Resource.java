package base3D.resources;

import java.io.File;
import java.nio.file.Paths;

public abstract class Resource {

    // Resource type
    final ResourceType type;


    // Is the resource loaded?
    private boolean loaded;

    public boolean isLoaded() {
        return loaded;
    }



    interface LoadMethodLambda {
        void load(String path);
    }

    LoadMethodLambda loadMethodLambda = null;
    public void LoadResource(File root) {
        if (!loaded) {
            File target = new File(Paths.get(root.getPath(), type.parentFolder.name, collection, fileName).toUri());
            if (!target.exists())
                throw new RuntimeException("[ResourceAtlas] " + collection + "/" + fileName + " resource doesn't exist (" + target.getPath() + ")");

            loadMethodLambda.load(target.getPath());

            System.out.println("[ResourceAtlas] " + target.getPath() + " loaded.");

            loaded = true;
            ResourceManager.resourceUpdated(this);
        }
    }


    // Collection and fileName

    public final String collection;
    public final String fileName;

    // Subscriptions

    public Resource(ResourceType type, String collection, String fileName) {
        this.type = type;
        this.collection = collection;
        this.fileName = fileName;
        this.loaded = false;
    }

    public String getName() {
        return collection + "/" + fileName;
    }


}
