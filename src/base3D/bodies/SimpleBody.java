package base3D.bodies;

import processing.core.PGraphics;
import processing.core.PVector;

public class SimpleBody extends SpatialBody {
    //-----------------------------------------
    // Constructors:

    public SimpleBody() {getNewObjectId();}

    public SimpleBody(PVector position) {
        this();
        getPosition().value.set(position);
    }

    //------------------------
        public SimpleBody(SimpleBody other) {super(); set(other);}
        public SimpleBody copy() {return new SimpleBody(this);}


    public void set(SimpleBody other) {set((SpatialBody) other);}
    //------------------------


    //-----------------------------------------
    // Methods:

    @Override
    public void render(PGraphics canvas, float y0) {renderShape(canvas, y0);}

    @Override
    public void update() {updatePositionAndRotation();}

}
