package application;
	
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import base.LogProcessor;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;


public class Main extends Application {
	
	private BarChart<String, Number> buildChart(String folderPath, String chartTitle) throws IOException{

	    final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final BarChart<String,Number> bc =
            new BarChart<>(xAxis,yAxis);
        bc.setTitle(chartTitle);

        xAxis.setLabel("Peer capacities");
        yAxis.setLabel("Average battery drain");
        
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(0.15);
        yAxis.setTickUnit(0.05);
        
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
            	NumberFormat formatter = new DecimalFormat("#0.0");
                return (formatter.format(object.doubleValue() * 100)) + "%";
            }

            @Override
            public Number fromString(String string) {
                return 0;
            }
        });
		
		LogProcessor logProcessor = new LogProcessor();
		Map<Integer, Double> plotData = logProcessor.execute(folderPath);
		
		XYChart.Series serie = new XYChart.Series();
		
		for(Map.Entry<Integer, Double> data : plotData.entrySet()) {
			Integer capacity = data.getKey();
			Double result = data.getValue();
			serie.getData().add(new XYChart.Data<>(String.valueOf(capacity), result));
		}
		
		bc.getData().add(serie);
		bc.setBarGap(1);
		bc.setCategoryGap(0);
		bc.setPrefWidth(700);
		bc.setPrefHeight(350);
		bc.setPadding(new Insets(20));
		bc.setLegendVisible(false);

        return bc;
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		
		stage.setTitle("Bar Chart Sample");
        
        
		BarChart<String, Number> bc1 = buildChart("src/logs/with", "Aggregated result with Benchmarker");
		BarChart<String, Number> bc2 = buildChart("src/logs/without", "Aggregated result without Benchmarker");
		
		bc1.lookupAll(".default-color0.chart-bar")
        .forEach(n -> n.setStyle("-fx-bar-fill: #8BC34A;"));
		
		bc2.lookupAll(".default-color0.chart-bar")
        .forEach(n -> n.setStyle("-fx-bar-fill: #78909C;"));

        FlowPane flowPane = new FlowPane();
        flowPane.getChildren().addAll(bc1, bc2);

        Group group = new Group();
        group.getChildren().addAll(flowPane);
		
		Scene scene = new Scene(group, 750, 750, Color.WHITE);
		stage.setScene(scene);
		stage.show();

	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
