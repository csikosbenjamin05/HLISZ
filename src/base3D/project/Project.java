package base3D.project;

import base3D.project.scene.SceneManager;

import java.io.File;
import java.util.*;

public class Project {

    public enum ProjectStructure {
        textureFolder("textures", true),
        audioFolder("audio", true),
        shapesFolder("shapes", true),
        resourceListFile("resources.xml", false);

        public final String name;
        private final boolean isDirectory;
        ProjectStructure(String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
        }

        public static List<ProjectStructure> getDirectories() {
            List<ProjectStructure> directories = new ArrayList<>();

            for (ProjectStructure ps : ProjectStructure.values())
                if (ps.isDirectory) directories.add(ps);

            return directories;
        }

        public static List<ProjectStructure> getNotDirectories() {
            List<ProjectStructure> directories = new ArrayList<>();

            for (ProjectStructure ps : ProjectStructure.values())
                if (!ps.isDirectory) directories.add(ps);

            return directories;
        }
    }
    
    private static final List<ProjectStructure> projectDirectories = ProjectStructure.getDirectories();
    private static final List<ProjectStructure> projectRootFiles = ProjectStructure.getNotDirectories();


    public final File root;

    public final SceneManager sceneManager;
    public final Map<ProjectStructure, File> projectFiles = new HashMap<>();


    public Project(String path) {

        root = new File(path);

        sceneManager = new SceneManager(root);

        System.out.println(root.getName());
        System.out.println(root.getAbsolutePath());
        System.out.println(root.exists());


        if (!root.isDirectory()) throw new NullPointerException("Root folder invalid");

        if (isFolderStructureValid()) {
            for(ProjectStructure ps : ProjectStructure.values()) {
                projectFiles.put(ps, Objects.requireNonNull(root.listFiles(f -> f.isDirectory() == ps.isDirectory && f.getName().equals(ps.name)))[0]); // TODO optimize
            }
            sceneManager.loadXML(projectFiles.get(ProjectStructure.resourceListFile));
        }

        else
            throw new NullPointerException("Project structure invalid.");



    }

    private boolean isFolderStructureValid() {

        List<File> files = List.of(Objects.requireNonNull(root.listFiles()));

        Map<String, Boolean> containsDir = new HashMap<>();
        for (ProjectStructure s : projectDirectories) containsDir.put(s.name, false);
        
        Map<String, Boolean> containsFile = new HashMap<>();
        for (ProjectStructure s : projectRootFiles) containsFile.put(s.name, false);
        
        for (File f : files) {
            if (f.isDirectory()) {
                if (containsDir.containsKey(f.getName())) containsDir.put(f.getName(), true);
            } else {
                if (containsFile.containsKey(f.getName())) containsFile.put(f.getName(), true);
            }
        }
        
        for (String k : containsDir.keySet()) {
            if (!containsDir.get(k)) return false;
        }
        
        for (String k : containsFile.keySet()) {
            if (!containsFile.get(k)) return false;
        }


        return true;
    }

}
