package jserver;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class CounterMap<T> {
	private Map<T, Integer> counter = new TreeMap<>();

	public void put(T key) {
		if (counter.containsKey(key)) {
			int count = 1 + counter.get(key);
			counter.put(key, count);
		} else {
			counter.put(key, 1);
		}
	}

	public void printSorted() {
		counter.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(System.out::println);
	}

	public int getSum() {
		return counter.values().stream().reduce(0, Integer::sum);
	}

	public int max() {
		return counter.values().stream().max(Comparator.naturalOrder()).get();
	}

	public Entry<T, Integer> maxEntry() {
		return counter.entrySet().stream().sorted(Map.Entry.comparingByValue()).reduce((first, second) -> second).get();
	}

	public int size() {
		return counter.size();
	}
	
	public Set<Entry<T, Integer>> getSet() {
	 return counter.entrySet();
	}
}
