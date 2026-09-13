package base3D.ui;

import base3D.shapeLibrary.brushes.ShapeBrush;
import base3D.ui.core.Area;
import base3D.ui.core.UIElement;
import base3D.ui.core.Text;
import base3D.ui.core.UIVector;
import processing.core.PGraphics;


public class Label extends UIElement {
    //-----------------------------------------
    // Attributes:
    private Text text;
    private Box box;


    //-----------------------------------------
    // Constructors:

    public Label(Area area) {
        super(area);
        this.text = new Text();
        this.box = new Box(area); // this.area = box.area

        area.size = new UIVector(0,0); // absolute
        setText("Empty");
    }

    public Label(Area area, String strText) {
        this(area);
        setText(strText);
    }

    public Label(Area area, ShapeBrush brush) {
        this(area);
        this.box.setBrush(brush);
    }

    //copy
    public Label(Label lb) {
        super(lb.getAreaCp());
        set(lb);
    }

    public void set(Label lb) {
        set((UIElement) lb); // area, visible, active
        setAreaRef(lb.getAreaCp());
        this.text = lb.getTextCp();
        this.box = lb.getBoxCp();
        this.box.setAreaRef(getAreaRef());

        setText(text.getText());
    }

    public Label copy() {
        return new Label(this);
    }


    //-----------------------------------------
    // Methods:

    public void render(PGraphics canvas) {
        UIVector position = getAreaRef().cachedPosition;

        box.render(canvas);
        text.render(position.cacheX, position.cacheY);

    }

    public void update() {
        getAreaRef().update(); // this.area = box.area
    }

    //-----------------------------------------
    // Getters & Setters:

    public void setText(String str) {
        text.setText(str);

        UIVector size = getAreaRef().size;

        size.ensureXYAreAbsolute();

        size.xCoord.set(text.getTextArea()[2]);
        size.yCoord.set(text.getTextArea()[3]);
    }

    public Text getTextRef() {return text;}
    public Box getBoxRef() {return box;}

    public Text getTextCp() {return text.copy();}
    public Box getBoxCp() {return box.copy();}
    public void setBox(Box box) {this.box = box;}
    public void setAreaRef(Area ref) {box.setAreaRef(ref); ((UIElement) this).setAreaRef(ref);}

}
