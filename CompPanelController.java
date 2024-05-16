import java.time.LocalDate;
import java.util.List;
import java.util.Locale.Category;
import java.util.Map;
import javafx.scene.Node;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ComboBox;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CompPanelController {

    private LocalDate fromDate, toDate;
    private String[] lineChartStats = {"New Deaths", "New Cases"};

    private CovidDataLoader loader = new CovidDataLoader();
    private DataSorter dataSorter;
    
    @FXML
    private ComboBox<String> selectBorough1, selectBorough2, selectStat;

    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private NumberAxis barYAxis;

    @FXML
    private CategoryAxis barXAxis;

    // list of London Boroughs alphabetically
    private String[] boroughs = {"Barking and Dagenham", "Barnet", "Bexley", "Brent", "Bromley", "Camden", "Croydon", "Ealing", "Enfield", "Greenwich", "Hackney", "Hammersmith and Fulham", "Haringey", "Harrow", "Havering", "Hillingdon", "Hounslow", "Islington", "Kensington and Chelsea", "Kingston upon Thames", "Lambeth", "Lewisham", "Merton", "Newham", "Redbridge", "Richmond upon Thames", "Southwark", "Sutton", "Tower Hamlets", "Waltham Forest", "Wandsworth", "Westminster"};

    // list of statistics to compare
    private String[] stats = {"Grocery and Pharmacy GMR", "New Cases", "New Deaths", "Parks GMR", "Residential GMR", "Retail and Recreation GMR", "Transit Stations GMR", "Workplace GMR"};

    private String borough1,borough2,selectedStat;
    
    @FXML
    private void initialize() {
        // Populate ComboBoxes with data
        
        selectBorough1.getItems().addAll(boroughs);
        selectBorough2.getItems().addAll(boroughs);
        selectStat.getItems().addAll(stats);
        selectStat.getSelectionModel().selectFirst();
        selectBorough1.getSelectionModel().select(0);
        selectBorough2.getSelectionModel().select(1);


        lineChart.setTitle("");
        lineChart.lookup(".chart-title").setStyle("-fx-font-size: 15px;");
        xAxis.setTickLabelsVisible(false);
        yAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 12));

        barChart.lookup(".chart-title").setStyle("-fx-font-size: 15px;");
        barXAxis.setTickLabelsVisible(false);
        barYAxis.setTickLabelFont(Font.font("Arial", FontWeight.BOLD, 12));
        setupComboBoxes();
    }
    
    //Adds the listeners to update chart data anytime a combox box changes
    private void setupComboBoxes() {
        selectStat.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateChartData();
        });
        
        selectBorough1.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateChartData();
        });
        
        selectBorough2.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateChartData();
        });
    }

    public void updateChartData() {
        borough1 = selectBorough1.getValue();
        borough2 = selectBorough2.getValue();
        selectedStat = selectStat.getValue();
       
       
        xAxis.setLabel(getDateRange());
        yAxis.setLabel(selectedStat);
        barYAxis.setLabel(selectedStat);
        barXAxis.setLabel(borough1 + " vs " + borough2);

        lineChart.getData().clear(); // Clear previous data
        barChart.getData().clear(); // Clear previous data
        
        //Displays the barChart/LineChart depending 
        if(lineChartStats[0].equals(selectedStat) || lineChartStats[1].equals(selectedStat))
        {
            lineChart.setVisible(true);
            lineChart.setManaged(true);
            barChart.setVisible(false);
            barChart.setManaged(false);
        }
        else
        {
            lineChart.setVisible(false);
            lineChart.setManaged(false);
            barChart.setVisible(true);
            barChart.setManaged(true);
        }

        if((borough1!= null && borough2 != null))
        {
            XYChart.Series<String, Number> series1 = new XYChart.Series<>();
            series1.setName(borough1);
            XYChart.Series<String, Number> series2 = new XYChart.Series<>();
            series2.setName(borough2);
            //USing switch logic to display the data
            switch(selectedStat)
            {
                case "New Deaths":
                    lineChart.setTitle("Cumulative New Deaths");
                    displayCumulativeDeaths(series1, series2);
                    break;
                case "New Cases":
                    lineChart.setTitle("Cumulative New Cases");
                    displayCumulativeCases(series1, series2);
                    break;
                case "Grocery and Pharmacy GMR":
                    displayGroceryGMR(series1, series2);
                    break;
                case "Retail and Recreation GMR":
                    displayRetailGMR(series1, series2);
                    break;
                case "Workplace GMR":
                    displayWorkplaceGMR(series1, series2);
                    break;
                case "Parks GMR":
                    displayParksGMR(series1, series2);
                    break;
                case "Residential GMR":
                    displayResidentialGMR(series1, series2);
                    break;
                case "Transit Stations GMR":
                    displayTransitGMR(series1, series2);
                    break;
                default:
                    break;
            }
        }
    }
    
    //The next methods are just different forms of displaying data, all using differing forms of logic
    private void displayCumulativeDeaths(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        Map<LocalDate, Integer> deathsData1 = dataSorter.getCumulativeDeathByIntervals(fromDate, toDate, borough1);
        Map<LocalDate, Integer> deathsData2 = dataSorter.getCumulativeDeathByIntervals(fromDate, toDate, borough2);
        for (Map.Entry<LocalDate, Integer> entry : deathsData1.entrySet()) {
            series1.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        for (Map.Entry<LocalDate, Integer> entry : deathsData2.entrySet()) {
            series2.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        lineChart.getData().addAll(series1, series2);
    }

    private void displayCumulativeCases(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        Map<LocalDate, Integer> totalCases1 = dataSorter.getCumulativeCasesByIntervals(fromDate, toDate, borough1);
        Map<LocalDate, Integer> totalCases2 = dataSorter.getCumulativeCasesByIntervals(fromDate, toDate, borough2);
        for (Map.Entry<LocalDate, Integer> entry : totalCases1.entrySet()) {
            series1.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }
        for (Map.Entry<LocalDate, Integer> entry : totalCases2.entrySet()) {
            series2.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        lineChart.getData().addAll(series1, series2);
    }

    //display GMR data averages in a barchart, various methods created for the various types of GMR data
    private void displayGroceryGMR(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        double gmr1 = dataSorter.getBoroughGroceryGMR(fromDate, toDate, borough1);
        double gmr2 = dataSorter.getBoroughGroceryGMR(fromDate, toDate, borough2);

        series1.getData().add(new XYChart.Data<>(borough1, gmr1));
        series2.getData().add(new XYChart.Data<>(borough2, gmr2));

        barChart.getData().addAll(series1, series2);
    }

    private void displayRetailGMR(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        double gmr1 = dataSorter.getBoroughRetailGMR(fromDate, toDate, borough1);
        double gmr2 = dataSorter.getBoroughRetailGMR(fromDate, toDate, borough2);

        series1.getData().add(new XYChart.Data<>(borough1, gmr1));
        series2.getData().add(new XYChart.Data<>(borough2, gmr2));

        barChart.getData().addAll(series1, series2);
    }

    private void displayWorkplaceGMR(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        double gmr1 = dataSorter.getBoroughWorkplaceGMR(fromDate, toDate, borough1);
        double gmr2 = dataSorter.getBoroughWorkplaceGMR(fromDate, toDate, borough2);

        series1.getData().add(new XYChart.Data<>(borough1, gmr1));
        series2.getData().add(new XYChart.Data<>(borough2, gmr2));

        barChart.getData().addAll(series1, series2);
    }

    private void displayParksGMR(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        double gmr1 = dataSorter.getBoroughParksGMR(fromDate, toDate, borough1);
        double gmr2 = dataSorter.getBoroughParksGMR(fromDate, toDate, borough2);

        series1.getData().add(new XYChart.Data<>(borough1, gmr1));
        series2.getData().add(new XYChart.Data<>(borough2, gmr2));

        barChart.getData().addAll(series1, series2);
    }

    private void displayResidentialGMR(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        double gmr1 = dataSorter.getBoroughResidentialGMR(fromDate, toDate, borough1);
        double gmr2 = dataSorter.getBoroughResidentialGMR(fromDate, toDate, borough2);

        series1.getData().add(new XYChart.Data<>(borough1, gmr1));
        series2.getData().add(new XYChart.Data<>(borough2, gmr2));

        barChart.getData().addAll(series1, series2);
    }

    private void displayTransitGMR(XYChart.Series<String, Number> series1, XYChart.Series<String, Number> series2) {
        double gmr1 = dataSorter.getBoroughTransitGMR(fromDate, toDate, borough1);
        double gmr2 = dataSorter.getBoroughTransitGMR(fromDate, toDate, borough2);

        series1.getData().add(new XYChart.Data<>(borough1, gmr1));
        series2.getData().add(new XYChart.Data<>(borough2, gmr2));

        barChart.getData().addAll(series1, series2);
    }

    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public String getDateRange() {
        return fromDate + " to " + toDate;
    }

    public void setDataSorter(DataSorter dataSorter) {
        this.dataSorter = dataSorter;
    }

    
}
