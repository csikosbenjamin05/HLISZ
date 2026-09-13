package base3D.bodies.forces;

import base3D.bodies.PhysicsComponent;
import base3D.definitions.Force;
import processing.core.PVector;

public class LinearGravity implements Force {

    // plane
    public Field field;
    public PVector gravitationalAcceleration;

    public boolean resetVelocityIfOutside;

    public LinearGravity(Field fieldRef, PVector gravitationalAcceleration) {
        this.field = fieldRef;
        this.gravitationalAcceleration = gravitationalAcceleration;
        this.resetVelocityIfOutside = false;
    }

    public LinearGravity(Field fieldRef, PVector gravitationalAcceleration, boolean resetVelocityIfOutside) {
        this(fieldRef, gravitationalAcceleration);
        this.resetVelocityIfOutside = resetVelocityIfOutside;
    }


    public LinearGravity(Field fieldRef, float ax, float ay, float az) {
        this.field = fieldRef;
        this.gravitationalAcceleration = new PVector(ax, ay, az);
    }

    @Override
    public void addToAcceleration(PhysicsComponent physicsComponentRef) {

        if (field.isObjectInside(physicsComponentRef.value)) {
            physicsComponentRef.acceleration.add(gravitationalAcceleration);
        } else if (resetVelocityIfOutside) {
                physicsComponentRef.velocity.set(0,0,0);
        }

    }
}
