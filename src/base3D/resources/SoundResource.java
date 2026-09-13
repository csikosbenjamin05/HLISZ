package base3D.resources;

public class SoundResource extends Resource {

    public SoundResource(ResourceType type, String collection, String fileName) {
        super(type, collection, fileName);
        assert(type == ResourceType.AUDIO);
        loadMethodLambda = (f) -> {};
    }
}
