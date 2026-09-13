package base3D.ui;

import base3D.ENV;
import base3D.EventHandler;
import base3D.project.scene.Scene;
import base3D.ui.core.Area;
import base3D.ui.core.Clickable;
import base3D.ui.core.UIElement;
import base3D.ui.core.UIVector;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.LinkedList;
import java.util.List;

public abstract class Window extends UIElement {
    //-----------------------------------------
    // Attributes:
    private final List<UIElement> elements;
    private final Scene parentScene;


    //-----------------------------------------
    // Constructors:


    public Window(Area area, Scene parent) {
        super(area);
        this.parentScene = parent;
        elements = new LinkedList<>();
        EventHandler.subscribe(EventHandler.events.MOUSE_PRESSED, ENV.p.LEFT, this::clicked);
    }

    //-----------------------------------------
    // Methods:

    public abstract void setup();

    public void addElement(UIElement e) {
        elements.add(e);
        e.setParent(this);
    }

    public void removeElement(UIElement e) {
        elements.remove(e);
    }

    public void clicked() {
        PVector clickLocation = new PVector(ENV.p.mouseX, ENV.p.mouseY);
        if( isActive() && getAreaRef().isInside(clickLocation))

            for (UIElement e : elements)
                if(e instanceof Clickable)
                    ((Clickable)e).clicked(clickLocation);

    }

    @Override
    public void update() {
        getAreaRef().update();
        for (UIElement e : elements) e.updateElement();
    }

    @Override
    public void render(PGraphics canvas) {
        for (UIElement e : elements) e.renderElement(canvas, 0);
    }



    //-----------------------------------------
    // Getters & Setters:

    public Scene getParentScene() {return parentScene;}

    public void switchScene(String sceneName) {
        parentScene.sceneManager.setActiveScene(sceneName);
    }



    public LabelButton generateLabelButton(String text, float xRel, float yRel, int textSize, boolean centered) {
        LabelButton labelButton1 = new LabelButton(
                new Area(
                        getAreaRef(),
                        new UIVector(getAreaRef().size, xRel, yRel),
                        new UIVector(0,0)
                ),
                "-"
        );
        labelButton1.getBoxRef().getBrushRef().drawBorder = false;
        labelButton1.getBoxRef().getBrushRef().faceColor = ENV.p.color(0,200,50);
        labelButton1.getTextRef().textSize = textSize;
        labelButton1.setText(text);
        if (centered) {
            labelButton1.getAreaRef().offSet.xCoord.set(-labelButton1.getAreaRef().size.xCoord.get() / 2);
            labelButton1.getAreaRef().offSet.yCoord.set(-labelButton1.getAreaRef().size.yCoord.get() / 2);
        }
        labelButton1.update();

        addElement(labelButton1);
        return labelButton1;
    }


    public Label generateLabel(String text, float xRel, float yRel, int textSize, boolean centered) {
        Label label1 = new LabelButton(
                new Area(
                        getAreaRef(),
                        new UIVector(getAreaRef().size, xRel, yRel),
                        new UIVector(0,0)
                ),
                "-"
        );
        label1.getBoxRef().getBrushRef().drawBorder = false;
        label1.getBoxRef().getBrushRef().faceColor = ENV.p.color(0,200,50);
        label1.getTextRef().textSize = textSize;
        label1.setText(text);
        if (centered) {
            label1.getAreaRef().offSet.xCoord.set(-label1.getAreaRef().size.xCoord.get() / 2);
            label1.getAreaRef().offSet.yCoord.set(-label1.getAreaRef().size.yCoord.get() / 2);
        }
        label1.update();

        addElement(label1);
        return label1;
    }


    public LabelButton generateCenteredLabelButton(String text, float yRel) {
        LabelButton labelButton1 = new LabelButton(
                new Area(
                        getAreaRef(),
                        new UIVector(getAreaRef().size, (float) 0.8, yRel),
                        new UIVector(0,0)
                ),
                "-"
        );
        labelButton1.getBoxRef().getBrushRef().drawBorder = false;
        labelButton1.getBoxRef().getBrushRef().faceColor = ENV.p.color(0,200,50);
        labelButton1.getTextRef().textSize = 50;
        labelButton1.setText(text);
        labelButton1.getAreaRef().offSet.xCoord.set(-labelButton1.getAreaRef().size.xCoord.get() / 2);
        labelButton1.getAreaRef().offSet.yCoord.set(-labelButton1.getAreaRef().size.yCoord.get() / 2);
        labelButton1.update();

        addElement(labelButton1);
        return labelButton1;
    }
}
