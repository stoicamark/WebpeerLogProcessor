package application;

import java.io.IOException;
import java.util.Map;

import base.LogProcessor;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Controller {
	
	@FXML
    public BarChart<String,Number> barchart1;
	
	@FXML
    public BarChart<String,Number> barchart2;
	
	public Controller() throws IOException{
		barchart1 = buildChart("src/logs/with", "with", "");
		barchart2 = buildChart("src/logs/without", "without", "");
	}
	
	private BarChart<String, Number> buildChart(String folderPath, String serieName, String chartTitle) throws IOException{
		final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = 
            new BarChart<String,Number>(xAxis,yAxis);
        bc.setTitle(chartTitle);
        xAxis.setLabel("Capacity (mAh)");  
        yAxis.setLabel("Result (mAh/s)");
		
		LogProcessor logProcessor = new LogProcessor();
		Map<Integer, Double> plotData = logProcessor.execute(folderPath);
		
		XYChart.Series serie = new XYChart.Series();
		
		serie.setName(serieName);
		
		for(Map.Entry<Integer, Double> data : plotData.entrySet()) {
			Integer capacity = data.getKey();
			Double result = data.getValue();
			serie.getData().add(new XYChart.Data<String, Double>(String.valueOf(capacity), result));
		}
		
		bc.getData().add(serie);
		bc.setBarGap(1);
		bc.setCategoryGap(0);
		
		return bc;
	}

}
