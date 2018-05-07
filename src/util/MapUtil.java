package util;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class MapUtil {

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V> Map<K, V> sort(Map<K, V> map){

		Map<K, V> sortedMap = new TreeMap<>(
                (o1, o2) -> ((Comparable) o1).compareTo(o2));
		
		sortedMap.putAll(map);
		return sortedMap;
	}

	public static double[] getValues(Map<Integer, Double> data, double coeff){

        if(data.isEmpty()){
            return null;
        }

        double[] array = new double[data.size()];
        int i = 0;
        for(Map.Entry<Integer, Double> entry : data.entrySet()){
            double value = entry.getValue();
            array[i++] = coeff * value;
        }

        return array;
	}

    public static double[] getValues(Map<Integer, Double> data){
        return getValues(data, 1.0);
    }
}
