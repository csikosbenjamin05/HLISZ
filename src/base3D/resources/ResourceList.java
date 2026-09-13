package base3D.resources;

import base3D.threads.taskThreads.ResourceLoaderThread;

import java.io.File;
import java.util.HashMap;

public class ResourceList {

    public final HashMap<String, HashMap<String, Resource>> resources;

    public ResourceList() {
        resources = new HashMap<>();
    }

    public void add(Resource r) {

        if (! resources.containsKey(r.collection))
            resources.put(r.collection, new HashMap<>());

        if (resources.get(r.collection).containsKey(r.fileName))
            throw new RuntimeException("[ResourceList] Duplicate resource: " + r.collection + "/" + r.fileName);

        resources.get(r.collection).put(r.fileName, r);
    }

    public Resource getResource(String name) {
        String[] p = name.split("/");
        assert(p.length == 2);

        return getResource(p[0], p[1]);
    }

    public Resource getResource(String collection, String fileName) {
        return resources.get(collection).get(fileName);
    }

    public void loadResources(File root) {
        ResourceLoaderThread resourceLoaderThread = new ResourceLoaderThread();
        HashMap<String, Resource> collectionMap;

        for (String collection : resources.keySet()) {
            collectionMap = resources.get(collection);

            for (String fileName : collectionMap.keySet())
                resourceLoaderThread.addItem(collectionMap.get(fileName));


        }

        resourceLoaderThread.start(root);
    }

    public void loadResourcesDirectly(File root) {
        HashMap<String, Resource> collectionMap;

        for (String collection : resources.keySet()) {

            collectionMap = resources.get(collection);

            for (String fileName : collectionMap.keySet())
                collectionMap.get(fileName).LoadResource(root);

        }

    }

    public boolean isResourcePresent(String collection, String fileName) {
        return resources.containsKey(collection) && resources.get(collection).containsKey(fileName);
    }

}
