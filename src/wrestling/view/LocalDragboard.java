
package wrestling.view;

import java.util.HashMap;
import java.util.Map;

public class LocalDragboard {
    
    

    private final Map<Class<?>, Object> contents;

    private static final LocalDragboard INSTANCE = new LocalDragboard();
    
    

    private LocalDragboard() {
        this.contents = new HashMap<>();
    }

    public static LocalDragboard getINSTANCE() {
        return INSTANCE;
    }

    public <T> void putValue(Class<T> type, T value) {
        contents.put(type, type.cast(value));
    }

    public <T> T getValue(Class<T> type) {
        return type.cast(contents.get(type));
    }

    public boolean hasType(Class<?> type) {
        return contents.keySet().contains(type);
    }

    public void clear(Class<?> type) {
        contents.remove(type);
    }

    public void clearAll() {
        contents.clear();
    }
}
