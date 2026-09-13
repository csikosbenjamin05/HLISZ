package base3D;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class EventHandler {
    //-----------------------------------------
    // Interfaces:
    public interface Runnable {
        void run();
    }


    //-----------------------------------------
    // Classes:



    //-----------------------------------------
    // Attributes:
    static private final HashSet<Integer> pressedKeys;
    static private final HashSet<Integer> pressedMouseButtons;
    static private final HashMap<Integer, HashMap<Integer, LinkedList<Runnable>>> eventCallMap;
    static private final int[] windowSize;

    public enum events {
        KEY_PRESSED(0),
        KEY_RELEASED(1),
        KEY_HELD(2),

        MOUSE_PRESSED(3),
        MOUSE_RELEASED(4),
        MOUSE_HELD(5),

        WHEEL_UP(6),
        WHEEL_DOWN(7),

        WINDOW_RESIZED(8);


        private final int value;

        events(int value) {this.value = value;}

        private int get() {return value;}
    }

    static {
        pressedKeys = new HashSet<>();
        pressedMouseButtons = new HashSet<>();
        eventCallMap = new HashMap<>();

        for (events e : events.values())  eventCallMap.put(e.get(), new HashMap<>());

        windowSize = new int[]{-1,-1};

    }


    //-----------------------------------------
    // Methods:


    /////////////
    // Input //
    // processing calls -->> EventHandler/pressedKeys, pressedMouseButtons
    public static void inputEvent(events e, int v) {
        switch (e) {
            case KEY_PRESSED -> pressedKeys.add(v);
            case KEY_RELEASED -> pressedKeys.remove(v);
            case MOUSE_PRESSED -> pressedMouseButtons.add(v);
            case MOUSE_RELEASED -> pressedMouseButtons.remove(v);
            case WHEEL_UP -> {
                pressedMouseButtons.remove(v);
                pressedMouseButtons.remove(-1); // remove wheel_down event
                pressedMouseButtons.add(v);
            }
            case WHEEL_DOWN -> {
                pressedMouseButtons.remove(v);
                pressedMouseButtons.remove(1); // remove wheel_up event
                pressedMouseButtons.add(v);
            }
            default -> {
            }
        }
        executeEvents(e, v);
    }

    public static void executeEvents(events e, int v) {
        HashMap<Integer, LinkedList<Runnable>> eventList = eventCallMap.get(e.get());
        if (eventList.containsKey(v)) {
            for(Runnable r : eventList.get(v)) r.run();
        }
    }

    public static void subscribe(events e, int activationValue, Runnable method) {
        HashMap<Integer, LinkedList<Runnable>> eventList = eventCallMap.get(e.get());

        if (! eventList.containsKey(activationValue)) eventList.put(activationValue, new LinkedList<>());
        eventList.get(activationValue).add(method);

    }

    public static void giveEventCalls() { // -> draw
        // runs the HOLD events
        events[] holdEvents = {events.KEY_HELD, events.MOUSE_HELD};

        HashMap<Integer, LinkedList<Runnable>> eventList;
        for (events e : holdEvents) {
            eventList = eventCallMap.get(e.get());
            for (int v : eventList.keySet()) {
                if (pressedKeys.contains(v)) for(Runnable r : eventList.get(v)) r.run();
            }
        }

        // WindowSizeChangedEvent
        if(windowSize[0] != ENV.p.width || windowSize[1] != ENV.p.height) {

            windowSize[0] = ENV.p.width;
            windowSize[1] = ENV.p.height;
			
			if (eventCallMap.get(events.WINDOW_RESIZED.get()).size() > 0)
				for (Runnable r : eventCallMap.get(events.WINDOW_RESIZED.get()).get(0)) r.run();
        }
    }


    public static void clearEventCallMap() {
        for (int key : eventCallMap.keySet()) {
            eventCallMap.get(key).clear();
        }
    }

    //-----------------------------------------
    // Getters & Setters:

    public static boolean isKeyPressed(char c) {return pressedKeys.contains((int)c);}
    public static boolean isKeyReleased(char c) {return !pressedKeys.contains((int)c);}

    public static HashSet<Integer> getPressedKeys() {return pressedKeys;}
    public static HashSet<Integer> getPressedMouseButtons() {return pressedMouseButtons;}

    public static boolean isMouseClicked() {return ENV.p.mousePressed;}
    public static boolean isMouseButtonClicked(int button) {return pressedMouseButtons.contains(button);}

    public static boolean isMouseWheelTurned() {return isMouseWheelTurnedDown() || isMouseWheelTurnedUp();}
    public static boolean isMouseWheelTurnedUp() {return pressedMouseButtons.contains(1);}
    public static boolean isMouseWheelTurnedDown() {return pressedMouseButtons.contains(-1);}

}
