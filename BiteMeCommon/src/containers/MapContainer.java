package containers;

import java.util.Map;

public class MapContainer<K, V> {
    private Map<K, V> map;

    public MapContainer(Map<K, V> map) {
        this.map = map;
    }

    public Map<K, V> getMap() {
        return map;
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }
}

