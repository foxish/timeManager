package com.anirudhr.timeMan;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class PieChart {

public Intent execute(Context context) {
	int[] colors = new int[] { Color.RED, Color.YELLOW, Color.BLUE };
	DefaultRenderer renderer = buildCategoryRenderer(colors);
	
	CategorySeries categorySeries = new CategorySeries("Time Utilization Chart");
	categorySeries.add("Productive ", 30);
	categorySeries.add("Unproductive", 20);
	categorySeries.add("Unknown ", 60);
	
	return ChartFactory.getPieChartIntent(context, categorySeries, renderer, "Time Utilization");
}

protected DefaultRenderer buildCategoryRenderer(int[] colors) {
	DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}
}