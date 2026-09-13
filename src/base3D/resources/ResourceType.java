package base3D.resources;

import base3D.project.Project;

public enum ResourceType {

    IMAGE("image", Project.ProjectStructure.textureFolder, (Resource r) -> true),
    CUBE_TEXTURE("cube", Project.ProjectStructure.textureFolder, (Resource r) -> ((Texture) r).get().width / ((Texture) r).get().height == 6),
    SPHERE_TEXTURE("sphere", Project.ProjectStructure.textureFolder, (Resource r) -> true),
    SKYBOX("skybox", Project.ProjectStructure.textureFolder, CUBE_TEXTURE.checker),
    AUDIO("audio", Project.ProjectStructure.audioFolder, (Resource r) -> true),
    SHAPE("shape", Project.ProjectStructure.shapesFolder, (Resource r) -> true);

    private interface Checker {
        boolean run(Resource r);
    }
    public static boolean typeMatches(Resource r, ResourceType type) {
        return r.type == type;
    }

    private final Checker checker;
    private Resource alternative;
    public final String name;

    public final Project.ProjectStructure parentFolder;
    ResourceType(String name, Project.ProjectStructure parentFolder, Checker r) {
        this.checker = r;
        this.alternative = null;
        this.name = name;
        this.parentFolder = parentFolder;
    }

    public boolean isResourceValid(Resource r) {
        return checker.run(r) && typeMatches(r, this);
    }

    public void setAlternative(Resource r) {
        alternative = r;
    }
    public Resource getAlternative() {
        return alternative;
    }



}
