package com.vivaio_felice.vivaio_hibernate;

import java.util.List;
import java.util.Map;

public class CanvasjsChartData {

	// lavorare per SETTARE LE LISTE esternamente!!

	static List<List<Map<Object, Object>>> list;

	public CanvasjsChartData() {
	}

	public static List<List<Map<Object, Object>>> getList() {
		return list;
	}

	public void setList(List<List<Map<Object, Object>>> list) {
		this.list = list;
	}

}
