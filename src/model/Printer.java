package model;

import java.util.Map;

public class Printer {
	public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key : " + entry.getKey() 
				+ " Value : " + entry.getValue());
        }
    }

	public static void printPeerResults(Map<String, TestResult> map) {
		for (Map.Entry<String, TestResult> entry : map.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
