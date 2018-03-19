package ar.edu.utn.sigmaproject.util;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.Chart;

public class GanttChartEngine extends JFreeChartEngine {
	private static final long serialVersionUID = 1L;

	public Color task1Color;
	public Color task2Color;
	public Color shadowColor;
	public Color completeColor;
	public Color incompleteColor;
	public boolean gradientBar = false;

	public GanttChartEngine(){
		setColorSet(1);
	}

	public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
		if (task1Color == null)
			return false;
		CategoryPlot plot = (CategoryPlot) jfchart.getPlot();
		GanttRenderer renderer = (GanttRenderer) plot.getRenderer();
		renderer.setBarPainter(gradientBar ? new GradientBarPainter() : new StandardBarPainter());
		renderer.setIncompletePaint(incompleteColor);
		renderer.setCompletePaint(completeColor);
		renderer.setShadowPaint(shadowColor);
		renderer.setSeriesPaint(0, task1Color);
		renderer.setSeriesPaint(1, task2Color);
		return false;
	}

	public void setColorSet(int value) {
		switch (value) {
		case 1:
			gradientBar = false;
			task1Color = new Color(0x083643);
			task2Color = new Color(0xB1E001);
			shadowColor = new Color(0x1D3C42);
			completeColor = new Color(0xCEF09D);
			incompleteColor = new Color(0x476C5E);
			break;
		case 2:
			gradientBar = false;
			task1Color = new Color(0xADCF4F);
			task2Color = new Color(0xF2EC99);
			shadowColor = new Color(0x84815B);
			completeColor = new Color(0x4A1A2C);
			incompleteColor = new Color(0x8E3557);
			break;
		case 0:
		default: // Default Color
			task1Color = null;
		}
	}
}