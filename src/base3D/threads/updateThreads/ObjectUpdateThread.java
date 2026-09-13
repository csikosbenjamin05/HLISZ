package base3D.threads.updateThreads;


import base3D.bodies.SpatialBody;
import base3D.threads.TimeKeeper;
import processing.core.PGraphics;

import java.sql.Time;
import java.util.*;

public class ObjectUpdateThread  extends UpdateThread{

    //////////////////////////
    // Attributes
    private final ArrayList<SpatialBody> backObjects; // these get calculated on a separate thread
    private final ArrayList<SpatialBody> frontObjects;// these are copies of the updated ones from the backObjects.

    public final TimeKeeper timeKeeper;

    //////////////////////////
    // Constructors

    public ObjectUpdateThread(int updateFrequency) {
        super(updateFrequency, "ObjectUpdateThread");
        this.backObjects = new ArrayList<>();
        this.frontObjects = new ArrayList<>();
        this.timeKeeper = new TimeKeeper();
    }


    //////////////////////////
    //Methods

    public void addObject(SpatialBody o) {
        if (getThreadState() != UpdateThreadStates.STOPPED) {
            pause();
            waitForPause();
        }

        backObjects.add(o);
        frontObjects.add(o.copy());

        if (getThreadState() != UpdateThreadStates.STOPPED) unpause();
    }

    public void clearObjects() {
        backObjects.clear();
        frontObjects.clear();
        timeKeeper.startNewUpdate();
    }

    public void remove(SpatialBody o) {
        if (getThreadState() != UpdateThreadStates.STOPPED) {
            pause();
            waitForPause();
        }

        frontObjects.remove(backObjects.indexOf(o));
        backObjects.remove(o);

        if (getThreadState() != UpdateThreadStates.STOPPED) unpause();
    }

    public SpatialBody getObjectWithID(int id) {
        for (SpatialBody b : backObjects) if (b.getId() == id) return b;
        return null;
    }

    public List<Integer> getObjectIds() {
        List<Integer> ids = new LinkedList<>();
        for(SpatialBody b : backObjects) ids.add(b.getId());
        return ids;
    }

    public void updateObjects() {
        for(SpatialBody o : backObjects) o.updateElement();
    }

    public void merge() {
        ListIterator<SpatialBody> backIt = backObjects.listIterator();
        // iterators used in the merge method
        ListIterator<SpatialBody> frontIt = frontObjects.listIterator();
        SpatialBody ptr;

        while (backIt.hasNext()) {
            ptr = frontIt.next();
            ptr.getClass().cast(ptr).setToSimpleBody(backIt.next());
        }

    }

    public void renderObjects(PGraphics canvas, float y0) {
        for(SpatialBody o : frontObjects) o.renderElement(canvas, y0);
    }

    public void startThread() {
        timeKeeper.currentUpdateTime = System.nanoTime();
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void targetFunction() {
        timeKeeper.startNewUpdate(); // calc dTime
        updateObjects();
        merge();
    }

}
