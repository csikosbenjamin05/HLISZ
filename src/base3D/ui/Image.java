package base3D.ui;

import base3D.ENV;
import base3D.resources.ResourceManager;
import base3D.definitions.ResourceUser;
import base3D.resources.Texture;
import base3D.ui.core.Area;
import base3D.ui.core.UIElement;
import base3D.ui.core.UIVector;
import processing.core.PGraphics;

public class Image extends UIElement implements ResourceUser {
    //-----------------------------------------
    // Attributes:
    private Texture img;


    //-----------------------------------------
    // Constructors:
    public Image(Area area) {
        super(area);
        this.img = null;
    }

    public Image(Area area, String imgName) {
        super(area);
        setImage(imgName);
    }

    //copy
    public Image(Image imgObj) {
        super(imgObj.getAreaCp());
        set(imgObj);
    }

    public Image copy() {return new Image(this);}

    public void set(Image imgObj) {
        set((UIElement) imgObj); // area, visible, active
        setAreaRef(imgObj.getAreaCp());
        this.img = imgObj.getImage();
    }

    //-----------------------------------------
    // Methods:

    public void render(PGraphics canvas) {
        UIVector position = getAreaRef().cachedPosition;
        UIVector size = getAreaRef().size;

        canvas.image(img.get(), position.cacheX, position.cacheY, size.cacheX, size.cacheY);
    }

    public void matchImageSize() {
        UIVector size = getAreaRef().size;

        size.ensureXYAreAbsolute();

        size.xCoord.set(img.get().width);
        size.yCoord.set(img.get().height);
    }

    //-----------------------------------------
    // Getters & Setters:

    public Texture getImage() {return img;}
    public void setImage(String imgName) {
        this.img = ResourceManager.getTexture(imgName, this::reGenerateElement);
    }


    @Override
    public void reGenerateElement() {
        matchImageSize();
    }
}
