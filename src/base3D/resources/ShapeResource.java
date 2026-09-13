package base3D.resources;

public class ShapeResource extends Resource {

    public ShapeResource(ResourceType type, String collection, String fileName) {
        super(type, collection, fileName);
        assert(type == ResourceType.SHAPE);
        loadMethodLambda = (f) -> {};

    }
}
