package base3D.definitions;

import base3D.bodies.PhysicsComponent;
import processing.core.PVector;

public interface Force {
    void addToAcceleration(PhysicsComponent physicsComponentRef);

}
