package base3D.bodies.forces;

import base3D.bodies.PhysicsComponent;
import base3D.definitions.Force;
import processing.core.PApplet;
import processing.core.PVector;

public class Drag implements Force {

    // plane
    public Field field;
    public float k;

    public Drag(Field fieldRef, float k) {
        this.field = fieldRef;
        this.k = k;
    }


    private float getDirection(float f) {
        return (f >= 0 ? -1 : 1);
    }


    public void addToAcceleration1(PhysicsComponent physicsComponentRef) {

        if (field.isObjectInside(physicsComponentRef.value)) {

            float c = physicsComponentRef.mass * 100;
            PVector velocity = physicsComponentRef.velocity;

/*

            physicsComponentRef.acceleration.add(
                    k * getDirection(velocity.x) * velocity.x * velocity.x / c,
                    k * getDirection(velocity.y) * velocity.y * velocity.y / c,
                    k * getDirection(velocity.z) * velocity.z * velocity.z / c
            );

                          */

            float sqrtPart = PApplet.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);

            physicsComponentRef.acceleration.add(
                    k * getDirection(velocity.x) * velocity.x * sqrtPart / c,
                    k * getDirection(velocity.y) * velocity.y * sqrtPart  / c,
                    k * getDirection(velocity.z) * velocity.z * velocity.z / c
            );


        }

    }
    @Override
    public void addToAcceleration(PhysicsComponent physicsComponentRef) {

        if (field.isObjectInside(physicsComponentRef.value)) {

            float c = physicsComponentRef.mass * 100;
            PVector velocity = physicsComponentRef.velocity;
            float r = PApplet.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);

            physicsComponentRef.acceleration.add(
                    -k * r * velocity.x / c,
                    -k * r * velocity.y / c,
                    0
            );
        }

    }
}
