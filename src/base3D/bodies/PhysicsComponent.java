package base3D.bodies;

import base3D.definitions.Force;
import base3D.definitions.Updatable;
import base3D.threads.ThreadController;
import base3D.Useful;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;

public class PhysicsComponent implements Updatable {
    private boolean active;
    public final PVector value;
    public final PVector velocity;
    public final PVector acceleration;

    public final List<Force> forces;
    public float mass;

    //-----------------------------------------
    //constructors
    public PhysicsComponent() {
        this.value = new PVector();
        this.velocity = new PVector(); // unit per second
        this.acceleration = new PVector(); // unit per second^2
        this.active = true;

        this.forces = new ArrayList<>();
        this.mass = 1;
    }

    public PhysicsComponent(PVector value) {
        this();
        this.value.set(value);
    }

    public PhysicsComponent(PVector value, PVector velocity) {
        this(value);
        this.velocity.set(velocity);
    }



    // copy constructor
    public PhysicsComponent(PhysicsComponent m) {this(); set(m);}

    public void set(PhysicsComponent m) {
        this.value.set(m.value);
        this.velocity.set(m.velocity);
        this.acceleration.set(m.acceleration);
        this.active = m.isActive();

        this.mass = m.mass;
        this.forces.clear();
        this.forces.addAll(m.forces);
    }

    public void set(PVector position, PVector velocity, PVector acceleration) {
        this.value.set(position);
        this.velocity.set(velocity);
        this.acceleration.set(acceleration);
    }

    public PhysicsComponent copy() {
        return new PhysicsComponent(this);
    }

    //-----------------------------------------
    // Methods
    @Override
    public String toString() {
        return "Motion<" + Useful.objectToHex(this) + ">(Value:" + Useful.pvectoString(value)
                + ", \tVelocity:" + Useful.pvectoString(velocity) + ", \tAcceleration:" + Useful.pvectoString(acceleration) + ")";
    }

    @Override
    public void updateElement() {if (active) update();}

    private void update() {
        float dTime = ThreadController.getObjectManagerTimeKeeperDeltaTime();

        acceleration.set(0,0,0);

        for (Force f : forces)
            f.addToAcceleration(this);


        velocity.add(PVector.mult(acceleration, dTime));
        value.add(PVector.mult(velocity, dTime));
    }


    //-----------------------------------------
    // Getters & Setters
    @Override
    public void setActive(boolean b) {active = b;}

    @Override
    public boolean isActive() {return active;}
}
