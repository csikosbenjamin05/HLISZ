package base3D.shapeLibrary.brushes;

import base3D.ENV;

public class BrushCollection {

    public static ShapeBrush defaultShapeBrush;
    public static ShapeBrush defaultMeshBrush;

    static {
        defaultShapeBrush = new ShapeBrush();
        defaultMeshBrush = new MeshBrush();
    }

}
