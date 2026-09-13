package base3D.resources;

import base3D.ENV;
import processing.core.PImage;

public class Texture extends Resource {
    private PImage texture;

    public Texture(ResourceType type, String collection, String fileName) {
        super(type, collection, fileName);

        assert(type == ResourceType.CUBE_TEXTURE || type == ResourceType.IMAGE || type == ResourceType.SPHERE_TEXTURE);
        texture = null;

        loadMethodLambda = (p) -> texture = ENV.p.loadImage(p);
    }

    public PImage get() {
        if (isLoaded())
            return texture;

        return ((Texture)type.getAlternative()).get();
    }

}
