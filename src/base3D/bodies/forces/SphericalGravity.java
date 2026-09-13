package base3D.bodies.forces;

import base3D.bodies.PhysicsComponent;
import base3D.bodies.SpatialBody;
import base3D.definitions.Force;
import processing.core.PVector;

public class SphericalGravity implements Force {

    // TODO

    static float G = 1e8f;

    PhysicsComponent center;
    Field area;

    boolean stopIfOutside;

    public SphericalGravity(SpatialBody center, Field area) {
        this(center,area, false);
    }


    public SphericalGravity(SpatialBody center, Field area, boolean stopIfOutside) {
        this.center = center.getPosition();
        this.area = area;
        this.stopIfOutside = stopIfOutside;
    }

    @Override
    public void addToAcceleration(PhysicsComponent physicsComponentRef) {


        if (area.isObjectInside(physicsComponentRef.value)) {

            PVector vRadius = PVector.sub(center.value, physicsComponentRef.value);

            float r = vRadius.mag();
            float f = (G * center.mass * physicsComponentRef.mass) / (r * r * r);


            // G * (m*m)/r^3 * rv

            physicsComponentRef.acceleration.add(
                    f * vRadius.x,
                    f * vRadius.y,
                    f * vRadius.z
            );
        } else if (stopIfOutside) physicsComponentRef.velocity.set(0,0,0);
    }
}
