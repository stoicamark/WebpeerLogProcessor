package util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MapUtil {
	public static <K, V> Map<K, V> sort(Map<K, V> map){
		Map<K, V> sortedMap = new TreeMap<K, V>(
                new Comparator<K>() {

                    @SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
                    public int compare(K o1, K o2) {
                        return ((Comparable) o1).compareTo(o2);
                    }

                });
		
		sortedMap.putAll(map);
		return sortedMap;
	}
}
