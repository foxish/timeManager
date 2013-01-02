package com.anirudhr.timeMan;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class PieChart {
	private Random random;

public Intent execute(Context context, HashMap<String, Long> map, String date) {
	random = new Random(); //once for the intent
	DefaultRenderer renderer = buildCategoryRenderer(map.size());
	//title
    renderer.setChartTitle(date);
    
	//sizes
	renderer.setChartTitleTextSize(35);
	renderer.setLabelsTextSize(20);
	renderer.setLegendTextSize(20);
    
    //background
    renderer.setApplyBackgroundColor(true);
    renderer.setBackgroundColor(Color.BLACK);
    
    //zoom & pan
    renderer.setPanEnabled(false);
    renderer.setZoomEnabled(false);

	//build the chart
	CategorySeries categorySeries = new CategorySeries("Time Utilization Chart");
	for (Map.Entry<String, Long> entry : map.entrySet()) {
		categorySeries.add(entry.getKey(), entry.getValue());
		//System.out.println("key=" + entry.getKey() + ", value=" + entry.getValue());
	}
	return ChartFactory.getPieChartIntent(context, categorySeries, renderer, "Time Utilization");
}

protected DefaultRenderer buildCategoryRenderer(int numColors) {
	DefaultRenderer renderer = new DefaultRenderer();
		for (int i=0; i<numColors; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(findRandomColor());
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

private int findRandomColor() {
	int r = 255 - random.nextInt(128);
	int g = 255 - random.nextInt(128);
	int b = 255 - random.nextInt(128);
	return Color.argb(255, r, g, b);
}
}