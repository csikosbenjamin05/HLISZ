package base3D.ui;

import base3D.ENV;
import base3D.EventHandler;
import base3D.shapeLibrary.brushes.ShapeBrush;
import base3D.ui.core.*;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class Slider extends UIElement implements Clickable {
    //-----------------------------------------
    // Attributes:
    private float lowerBound;
    private float upperBound;
    private float handlePosition;

    public float value;

    public String valueSuffix = "";

    private ShapeBrush sliderLineShapeBrush;
    private ShapeBrush sliderHandleShapeBrush;
    private ShapeBrush sliderBoundsShapeBrush;
    private ShapeBrush sliderValueShapeBrush;

    private Label labelLowerBound;
    private Label labelUpperBound;
    private Label labelValue;

    private Area clickArea;

    private Box handle;

    private int roundPrecision = 3;


    //-----------------------------------------
    // Constructors:

    public Slider(Area area, float lowerBound, float upperBound) {
        this(area, lowerBound, upperBound, "");
    }
    public Slider(Area area, float lowerBound, float upperBound, String valueSuffix) {
        super(area);

        this.valueSuffix = valueSuffix;

        if(getAreaRef().size.yCoord.isRelative())
            getAreaRef().size.yCoord = new AbsoluteCoordinate(getAreaRef().size.cacheY);

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        this.sliderLineShapeBrush = new ShapeBrush();
        sliderLineShapeBrush.borderColor = ENV.p.color(100);
        this.sliderHandleShapeBrush = new ShapeBrush();
        this.sliderBoundsShapeBrush = new ShapeBrush();
        this.sliderValueShapeBrush = new ShapeBrush();

        this.labelLowerBound = new Label(
                new Area(
                        getAreaRef(),
                        getAreaRef().cachedPosition,
                        new UIVector(10,10)
                ),
                sliderBoundsShapeBrush
        );
        setLabelValue(labelLowerBound, lowerBound);
        this.labelUpperBound = new Label(
                new Area(
                        getAreaRef(),
                        getAreaRef().cachedPosition,
                        new UIVector(10,10)
                ),
                sliderBoundsShapeBrush
        );
        setLabelValue(labelUpperBound, upperBound);


        this.labelValue = new Label(
                new Area(
                        getAreaRef(),
                        getAreaRef().cachedPosition,
                        new UIVector(10,10)
                ),
                sliderValueShapeBrush
        );



        this.handlePosition = 0f;
        this.handle = new Box(
                new Area(
                        getAreaRef(),
                        getAreaRef().cachedPosition,
                        new UIVector(5,10)
                ),
                sliderHandleShapeBrush
        );

        setHandlePosition();
        value = calculateValue();
        setLabelValueWithSuffix(labelValue, value);

        this.clickArea = new Area(
                getAreaRef(),
                getAreaRef().cachedPosition,
                new UIVector(0,0)
        );

        //setClickAction(this::sliderClicked);

        EventHandler.subscribe(EventHandler.events.WINDOW_RESIZED, 0, this::windowSizeChanged);
    }

    //copy
    //------------------------
    public Slider(Slider other) {super(other.getAreaCp()); set(other);}

    public Slider copy() {return new Slider(this);}

    public void set(Slider other) {
        this.set((UIElement) other);

        this.lowerBound = other.getLowerBound();
        this.upperBound = other.getUpperBound();

        this.sliderLineShapeBrush = other.getSliderLineBrush();
        this.sliderHandleShapeBrush = other.getSliderHandleBrush();
        this.sliderBoundsShapeBrush = other.getSliderBoundsBrush();
        this.sliderValueShapeBrush = other.getSliderValueBrush();

        this.labelLowerBound = other.getLabelLowerBound().copy();
        this.labelUpperBound = other.getLabelUpperBound().copy();
        this.labelValue = other.getLabelValue().copy();

        this.handle = other.getHandle().copy();
        this.handlePosition = 0;
        this.roundPrecision = other.getRoundPrecision();
    }
    //------------------------


    //-----------------------------------------
    // Methods:

    private void sliderClicked(PVector click) {
        float lineStart = getLineStart();
        float lineEnd = getLineEnd();


        if (click.x < lineStart) handlePosition = 0;
        else if (click.x > lineEnd) handlePosition = lineEnd - lineStart;
        else handlePosition = click.x - lineStart;

        setHandlePosition();
        value = calculateValue();
        setLabelValueWithSuffix(labelValue, value);
    }

    private void windowSizeChanged() {
        setUpperBoundPosition();
        setHandlePosition();
    }

    private void setUpperBoundPosition() {
        labelUpperBound.getAreaRef().offSet.xCoord.set(getLabelWidth(labelLowerBound) + getLineWidth());
    }


    private void setHandlePosition() {

        UIVector handleOffset = handle.getAreaRef().offSet;

        handleOffset.ensureXYAreAbsolute();

        handleOffset.xCoord.set(getLabelWidth(labelLowerBound) + handlePosition);
        handleOffset.yCoord.set(labelLowerBound.getHeight()/2 - handle.getHeight());

        UIVector lbValueOffset = labelValue.getAreaRef().offSet;

        lbValueOffset.ensureXYAreAbsolute();

        lbValueOffset.xCoord.set(handleOffset.xCoord.get());
        lbValueOffset.yCoord.set(labelLowerBound.getHeight());

    }

    private float calculateValue() {
        return (upperBound - lowerBound) * (handlePosition / getLineWidth()) + lowerBound;
    }

    public void setHandlePositionFromValue(float value) {
        this.value = value;
        handlePosition = (value - lowerBound) / (upperBound - lowerBound) * getLineWidth();
        setHandlePosition();
        setLabelValueWithSuffix(labelValue, value);
    }


    @Override
    public void render(PGraphics canvas) {

        //clickArea.drawOutline(canvas);


        getAreaRef().update();

        getAreaRef().drawOutline(canvas);

        labelValue.render(canvas);
        labelUpperBound.render(canvas);
        labelLowerBound.render(canvas);

        handle.render(canvas);

        float yValue =  getYCoord() +  labelLowerBound.getHeight()/2;
        sliderLineShapeBrush.setLocalEnvironment();
        canvas.line(getLineStart(), yValue, getLineEnd(), yValue);


    }

    @Override
    public void update() {

        getAreaRef().update();

        setUpperBoundPosition();

        labelValue.update();

        labelLowerBound.getAreaRef().offSet.yCoord.set(labelLowerBound.getHeight()/4);
        labelUpperBound.getAreaRef().offSet.yCoord.set(labelUpperBound.getHeight()/4);

        labelUpperBound.update();
        labelLowerBound.update();


        handle.update();

        clickArea.offSet.xCoord.set(getLabelWidth(labelLowerBound));
        clickArea.size.xCoord.set(getLineWidth());
        clickArea.size.yCoord.set(labelLowerBound.getHeight());
        clickArea.update();

        //System.out.println(clickArea);
    }

    private void setLabelValue(Label lb, float f) {
        String str = String.valueOf(f);
        int roundPos = str.indexOf('.')+roundPrecision+1;
        lb.setText(str.substring(0, Math.min(roundPos, str.length())));
    }
    private void setLabelValueWithSuffix(Label lb, float f) {
        String str = String.valueOf(f);
        int roundPos = str.indexOf('.')+roundPrecision+1;
        lb.setText(str.substring(0, Math.min(roundPos, str.length())) + valueSuffix);
    }


    //-----------------------------------------
    // Getters & Setters:

    private float getXCoord() {
        return getAreaRef().cachedPosition.xCoord.get();
    }

    private float getYCoord() {
        return getAreaRef().cachedPosition.yCoord.get();
    }

    private float getLineStart() {
        return getXCoord() + getLabelWidth(labelLowerBound);
    }
    private float getLineEnd() {
        return getXCoord() + getWidth() - getLabelWidth(labelUpperBound);
    }

    private float getLabelWidth(Label lb) {
        return lb.getWidth();
    }

    private float getLineWidth() {
        return getWidth() - (getLabelWidth(labelLowerBound) + getLabelWidth(labelUpperBound));
    }



    public float getLowerBound() {
        return lowerBound;
    }
    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
    }

    public float getUpperBound() {
        return upperBound;
    }
    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
    }

    public ShapeBrush getSliderLineBrush() {
        return sliderLineShapeBrush;
    }
    public void setSliderLineBrush(ShapeBrush sliderLineShapeBrush) {
        this.sliderLineShapeBrush = sliderLineShapeBrush;
    }

    public ShapeBrush getSliderHandleBrush() {
        return sliderHandleShapeBrush;
    }
    public void setSliderHandleBrush(ShapeBrush sliderHandleShapeBrush) {
        this.sliderHandleShapeBrush = sliderHandleShapeBrush;
    }

    public ShapeBrush getSliderBoundsBrush() {
        return sliderBoundsShapeBrush;
    }
    public void setSliderBoundsBrush(ShapeBrush sliderBoundsShapeBrush) {
        this.sliderBoundsShapeBrush = sliderBoundsShapeBrush;
    }

    public ShapeBrush getSliderValueBrush() {
        return sliderValueShapeBrush;
    }
    public void setSliderValueBrush(ShapeBrush sliderValueShapeBrush) {
        this.sliderValueShapeBrush = sliderValueShapeBrush;
    }

    public Label getLabelLowerBound() {
        return labelLowerBound;
    }
    public Label getLabelUpperBound() {
        return labelUpperBound;
    }
    public Label getLabelValue() {
        return labelValue;
    }

    public Box getHandle() {
        return handle;
    }

    public int getRoundPrecision() {
        return roundPrecision;
    }

    public void setRoundPrecision(int roundPrecision) {
        this.roundPrecision = roundPrecision;
    }



    @Override
    public void clicked(PVector clickLocation) {
        if(isActive() && clickArea.isInside(clickLocation)) sliderClicked(clickLocation);
    }

    @Override
    public Runnable getClickAction() {return null;}
    @Override
    public void setClickAction(Runnable clickAction) {}
}
