package base3D.project.scene;

import base3D.EventHandler;
import base3D.resources.ResourceCollection;
import base3D.resources.Texture;
import base3D.threads.ThreadController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SceneManager {

    private final ResourceCollection loadedResourcesGlobal;
    private final HashMap<String, ResourceCollection> sceneResources;
    private final HashMap<String, Scene> sceneList;
    private Scene activeScene;

    private final File root;

    public SceneManager(File root) {
        loadedResourcesGlobal = new ResourceCollection();
        sceneList = new HashMap<>();
        sceneResources = new HashMap<>();
        this.root = root;
    }

    public void render() {
        activeScene.render();
    }

    protected void addScene(Scene scene) {
        sceneList.put(scene.sceneName, scene);
    }

    public void setActiveScene(Scene scene) {
        setActiveScene(scene.sceneName);
    }

    public void setActiveScene(String sceneName) {
        if (sceneList.containsKey(sceneName)) {

            EventHandler.clearEventCallMap();
            ThreadController.getInstance().objMan.clearObjects();
            ThreadController.getInstance().uiMan.clearWindows();

            Scene temp = sceneList.get(sceneName);
            activeScene = temp;

            System.gc();

            temp.resourceCollection.loadResources();
            temp.init(200000);
            temp.setup();


        } else {
            throw  new RuntimeException("[SceneManager] Scene '" + sceneName + "' is not present in the list of scenes controlled by the sceneManager.");
        }


    }

    public void loadXML(File resourcesXML) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // optional, but recommended
            // process XML securely, avoid attacks like XML External Entities (XXE)
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(resourcesXML);

            // optional, but recommended
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList sceneTags = doc.getElementsByTagName("scene");
            Node current;
            String sceneName;

            for (int i = 0; i < sceneTags.getLength(); i++) {
                current = sceneTags.item(i);

                sceneName = current.getAttributes().getNamedItem("name").getTextContent();

                sceneResources.put(sceneName, new ResourceCollection(root, current, loadedResourcesGlobal));
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    protected ResourceCollection getResourceCollection(String name) {
        if (!sceneResources.containsKey(name))
            throw new RuntimeException("[SceneManager] No collection exists with the following name : " + name);
        return sceneResources.get(name);
    }

    public boolean isResourcePresent(String name) {
        return activeScene.resourceCollection.isResourcePresent(activeScene.resourceCollection.textureAtlas, name);
    }

    public Texture getTexture(String name) {
        return (Texture) activeScene.resourceCollection.textureAtlas.getResource(name);
    }
}
