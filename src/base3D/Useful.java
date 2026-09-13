package base3D;

import processing.core.PVector;
import java.text.DecimalFormat;

public class Useful {
    private static final DecimalFormat df = new DecimalFormat("#.000");

    public static void keepInBound(boolean loop, PVector vec, float bMin, float bMax) {
        if (loop) {
            vec.x = (vec.x > bMax) ? bMin : (vec.x < bMin ? bMax : vec.x);
            vec.y = (vec.y > bMax) ? bMin : (vec.y < bMin ? bMax : vec.y);
            vec.z = (vec.z > bMax) ? bMin : (vec.z < bMin ? bMax : vec.z);
        } else {
            vec.x = (vec.x > bMax) ? bMax : (Math.max(vec.x, bMin));
            vec.y = (vec.y > bMax) ? bMax : (Math.max(vec.y, bMin));
            vec.z = (vec.z > bMax) ? bMax : (Math.max(vec.z, bMin));
        }
    }

    public static String pvectoString(PVector v) {
        return "[x:" + df.format(v.x) + " y:" + df.format(v.y) + " z:" + df.format(v.z) + "]";
    }

    public static String objectToString(Object obj) {
        return getClassOfObject(obj) + "<" + objectToHex(obj) + ">";
    }

    public static String objectToHex(Object obj) {
        return Integer.toHexString(obj.hashCode());
    }

    public static String getClassOfObject(Object obj) {
        return obj.getClass().toString();
    }
}
