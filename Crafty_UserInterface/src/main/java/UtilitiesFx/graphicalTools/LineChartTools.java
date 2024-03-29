package UtilitiesFx.graphicalTools;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import dataLoader.CellsLoader;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * @author Mohamed Byari
 *
 */

public class LineChartTools {

	public void lineChart(CellsLoader M, Pane box, LineChart<Number, Number> lineChart,
			HashMap<String, double[]> hash) {
		lineChart.getData().clear();
		Series<Number, Number>[] series = new XYChart.Series[hash.size()];

		AtomicInteger i = new AtomicInteger();
		hash.forEach((key, value) -> {
			if (value != null) {
				series[i.get()] = new XYChart.Series<Number, Number>();
				series[i.get()].setName(key);
				lineChart.getData().add(series[i.get()]);
				i.getAndIncrement();
			}
		});
		AtomicInteger k = new AtomicInteger();
		hash.forEach((key, value) -> {
			if (value != null) {
				for (int j = 1; j < value.length; j++) {
					series[k.get()].getData().add(new XYChart.Data<>(
							j + ((NumberAxis) lineChart.getXAxis()).getLowerBound(), (Number) (value[j])));
				}
				k.getAndIncrement();
			}
		});
		if (hash.size() > 8) {
			AtomicInteger K = new AtomicInteger();
			hash.forEach((key, value) -> {
				if (value != null) {
					for (int j = 1; j < value.length; j++) {
						series[K.get()].getData().add(new XYChart.Data<>(j, +value[j]));
						series[K.get()].getNode().lookup(".chart-series-line").setStyle(
								"-fx-stroke: " + ColorsTools.getStringColor(ColorsTools.colorlist(K.get())) + ";");
					}
					K.getAndIncrement();
				}
			});
			if (M != null)
				labelcolor(M, lineChart);
			lineChart.setCreateSymbols(false);
		}
		if (box != null) {
			MousePressed.mouseControle(box, lineChart);
		}
	}

	public void labelcolor(CellsLoader M, LineChart<Number, Number> lineChart) {
		int m = 0;
		for (Node item : lineChart.lookupAll("Label.chart-legend-item")) {
			Label label = (Label) item;
			Color co = M.AFtsSet.getAftHash().get(label.getText()) != null
					? M.AFtsSet.getAftHash().get(label.getText()).getColor()
					: ColorsTools.colorlist(m);
			final Rectangle rectangle = new Rectangle(10, 10, co);
			label.setGraphic(rectangle);
			m++;
		}
	}

}
