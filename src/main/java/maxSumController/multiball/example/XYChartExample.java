package maxSumController.multiball.example;

import java.awt.Font;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import util.StatisticsList;

public class XYChartExample {
	public static void createChart(List<List<Double>> values,
			List<String> titles, String title) {

		// Add the series to your data set
		XYSeriesCollection dataset = new XYSeriesCollection();

		for (int i = 0; i < values.size(); i++) {
			List<Double> valueList = values.get(i);

			// Create a simple XY chart
			XYSeries series = new XYSeries(titles.get(i));

			int index = 0;
			for (Double value : valueList) {
				series.add(index++, value);
			}

			dataset.addSeries(series);

		}

		// Generate the graph
		JFreeChart chart = ChartFactory.createXYLineChart(title, "Iterations", // x-axis
				// Label
				"Global Utility", // y-axis Label
				dataset, // Dataset
				PlotOrientation.VERTICAL, // Plot Orientation
				true, // Show Legend
				true, // Use tooltips
				false // Configure chart to generate URLs?
				);
		ChartPanel panel = new ChartPanel(chart);

		JFrame frame = new JFrame();
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void createChart(List<Double> values, String title) {
		createChart(Collections.singletonList(values), Collections
				.singletonList(title), title);
	}

	public static JFrame createBoxAndWhiskers(List<StatisticsList> values,
			List<String> titles, String plotTitle) {

		final ChartPanel chartPanel = createBoxAndWhiskersPanel(values, titles,
				plotTitle);

		JFrame frame = new JFrame();
		frame.getContentPane().add(chartPanel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		return frame;
	}

	public static ChartPanel createBoxAndWhiskersPanel(
			List<StatisticsList> values, List<String> titles, String plotTitle) {
		DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();

		for (int i = 0; i < values.size(); i++) {
			StatisticsList statisticsList = values.get(i);
			String title = titles.get(i);

			dataset.add(statisticsList, title, "");
		}

		final CategoryAxis xAxis = new CategoryAxis("Algorithm");
		final NumberAxis yAxis = new NumberAxis("Solution Quality");
		yAxis.setAutoRangeIncludesZero(false);
		final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
		renderer.setFillBox(false);

		final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis,
				renderer);

		final JFreeChart chart = new JFreeChart(plotTitle, new Font(
				"SansSerif", Font.BOLD, 14), plot, true);
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));
		return chartPanel;
	}
}
