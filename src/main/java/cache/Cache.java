package cache;

import java.util.*;

public class Cache {
    private static Map<String, List<String>> gob_dataCacheMap;
    private static Cache gob_cache;

    /**
     * Singleton instance of the data cache
     *
     * @return instance of the data cache
     */
    public static Cache getIpCache() {
        if (gob_cache == null) {
            gob_cache = new Cache();
        }

        return gob_cache;
    }

    private Cache() {
        gob_dataCacheMap = new HashMap<>();
    }

    /**
     * Store a value in the cache
     *
     * @param iva_key key
     * @param iva_value value
     */
    public void put(String iva_key, String iva_value) {
        List<String> lob_ipList = get(iva_key);

        if (lob_ipList == null) {
            lob_ipList = new ArrayList<>();
        }

        lob_ipList.add(iva_value);
        gob_dataCacheMap.put(iva_key, lob_ipList);
    }

    /**
     * Get a value from the data cache
     *
     * @param iva_key key
     * @return value
     */
    public List<String> get(String iva_key) {
        return gob_dataCacheMap.get(iva_key);
    }

    public void removeEntry(String iva_key, String iva_value) {
        List<String> lob_ipList = get(iva_key);

        if (lob_ipList != null) {
            lob_ipList.remove(iva_value);
            gob_dataCacheMap.replace(iva_key, lob_ipList);
        }
    }
}
