package com.anirudhr.timeMan;

import java.util.HashMap;

public class History {
	private HashMap<String, String> map;
	public History(){
		map = new HashMap<String, String>();
	}
	public String get(String key) {
		return map.get(key);
	}
	public void put(String key, String value) {
		map.put(key, value);
	}
}
