package base3D.resources;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceCollection {

    private static final Map<String, ResourceType> resourceTypeNameMap = new HashMap<>();
    static {
        for (ResourceType rt : ResourceType.values())
            resourceTypeNameMap.put(rt.name, rt);
    }

    public final ResourceList textureAtlas;
    public final ResourceList soundAtlas;
    public final ResourceList shapeAtlas;

    private final File root;
    private final NodeList XMLRoot;

    private boolean loaded;


    public ResourceCollection(File root, Node XMLRoot, ResourceCollection loadedResourcesGlobal) {
        this.textureAtlas = new ResourceList();
        this.soundAtlas = new ResourceList();
        this.shapeAtlas = new ResourceList();

        this.root = root;
        this.XMLRoot = XMLRoot.getChildNodes();

        softLoadResourcesFromSceneNode(loadedResourcesGlobal);
        this.loaded = false;
    }

    public ResourceCollection() {
        this.textureAtlas = new ResourceList();
        this.soundAtlas = new ResourceList();
        this.shapeAtlas = new ResourceList();
        this.root = null;
        this.XMLRoot = null;
    }


    private boolean isResourceFormatValid(String s, String[] p) {
        // "collection/filename"
        // only a-z, A-Z, 1-9
        // exactly 1 '/'

        return s.matches("[a-zA-Z0-9/_.]+") && p.length == 2;

    }


    private void loadResourceList(Node first, ResourceList destination, int type, ResourceCollection loadedResourcesGlobal) {

        String[] p;
        String tagName;
        String resource;

        Node current;
        NodeList childNodes = first.getChildNodes();

        for(int i = 0; i < childNodes.getLength(); i++) {

            current = childNodes.item(i);

            if (current.getNodeType() == Node.ELEMENT_NODE) {
                tagName = current.getNodeName();

                resource = current.getTextContent();

                p = resource.split("/");
                if (isResourceFormatValid(resource, p))

                    if (resourceTypeNameMap.containsKey(tagName)) {

                        Resource newResource;
                        switch (type) {

                            case 1 -> {
                                if (loadedResourcesGlobal.textureAtlas.isResourcePresent(p[0], p[1]))
                                    destination.add(loadedResourcesGlobal.textureAtlas.getResource(p[0], p[1]));
                                else {
                                    newResource = new Texture(resourceTypeNameMap.get(tagName), p[0], p[1]);
                                    destination.add(newResource);
                                    loadedResourcesGlobal.textureAtlas.add(newResource);
                                }
                            }

                            case 2 -> {
                                if (loadedResourcesGlobal.soundAtlas.isResourcePresent(p[0], p[1]))
                                    destination.add(loadedResourcesGlobal.soundAtlas.getResource(p[0], p[1]));
                                else {
                                    newResource = new SoundResource(resourceTypeNameMap.get(tagName), p[0], p[1]);
                                    destination.add(newResource);
                                    loadedResourcesGlobal.soundAtlas.add(newResource);
                                }
                            }

                            case 3 -> {
                                if (loadedResourcesGlobal.shapeAtlas.isResourcePresent(p[0], p[1]))
                                    destination.add(loadedResourcesGlobal.shapeAtlas.getResource(p[0], p[1]));
                                else {
                                    newResource = new ShapeResource(resourceTypeNameMap.get(tagName), p[0], p[1]);
                                    destination.add(newResource);
                                    loadedResourcesGlobal.shapeAtlas.add(newResource);
                                }
                            }

                            default -> throw new RuntimeException("[ResourceCollection] Invalid resource type.");

                        }

                    } else

                        throw new RuntimeException("[ResourceAtlas] \"" + tagName + "\" for " + resource + " is not a valid resource type");

                else

                    throw new RuntimeException("[ResourceAtlas] \"" + resource + "\" is not a valid format for a resource.");

            }
        }

    }

    public void softLoadResourcesFromSceneNode(ResourceCollection loadedResourcesGlobal) {

        HashMap<String, Node> nodeFinder = new HashMap<>();
        nodeFinder.put("textures", null);
        nodeFinder.put("sounds", null);
        nodeFinder.put("shapes", null);

        Node current;
        String nodeName;
        for (int i = 0; i < XMLRoot.getLength(); i++) {
            current = XMLRoot.item(i);
            nodeName = current.getNodeName();

            if (nodeFinder.containsKey(nodeName)) nodeFinder.put(nodeName, current);
        }

        // check if textures, sounds and shapes were provided

        for (String n : nodeFinder.keySet()) {
            if (nodeFinder.get(n) == null)
                throw new RuntimeException("[ResourceCollection] Invalid XML format. " +
                        "The <scene> tag has to contain the following tags: <textures>, <sounds>, <shapes>");
        }

        Node textures = nodeFinder.get("textures");
        Node sounds   = nodeFinder.get("sounds");
        Node shapes   = nodeFinder.get("shapes");

        loadResourceList(textures, textureAtlas, 1, loadedResourcesGlobal);
        loadResourceList(sounds, soundAtlas, 2, loadedResourcesGlobal);
        loadResourceList(shapes, shapeAtlas ,3, loadedResourcesGlobal);


    }

    public void loadResources() {
        textureAtlas.loadResources(root);
        soundAtlas.loadResources(root);
        shapeAtlas.loadResources(root);
    }

    public void loadResourcesDirectly() {
        textureAtlas.loadResourcesDirectly(root);
        soundAtlas.loadResourcesDirectly(root);
        shapeAtlas.loadResourcesDirectly(root);
    }

    public boolean isResourcePresent(ResourceList resourceList, String resource) {
        String[] split = resource.split("/");
        if (split.length != 2) return false;

        String collection = split[0];
        String fileName = split[1];

        if (resourceList.resources.containsKey(collection))
            return resourceList.resources.get(collection).containsKey(fileName);
        return false;
    }
}
