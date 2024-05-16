import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.time.LocalDate;


public class StatPanelController{
    @FXML
    private Label statName, statValue;
    @FXML
    private Button nextButton, previousButton;

    private LocalDate fromDate, toDate;

    private CovidDataLoader loader = new CovidDataLoader();
    private DataSorter dataSorter;

    private String[] statisticNames = {"Total Deaths", "Average Cases", "Retail and Recreation GMR", "Grocery and Pharmacy GMR"};
    
    private int currentStatisticIndex = 0;

    @FXML 
    private void initialize(){
        // set up action buttons
        nextButton.setOnAction(event -> handleNextAction());
        previousButton.setOnAction(event -> handlePreviousAction());
    }

    private void handleNextAction() {
        // Cycle to the next statistic
        currentStatisticIndex = (currentStatisticIndex + 1) % statisticNames.length;
        updateStatisticsDisplay();
    }

    private void handlePreviousAction() {
        // Cycle to the previous statistic
        currentStatisticIndex = (currentStatisticIndex - 1 + statisticNames.length) % statisticNames.length;
        updateStatisticsDisplay();
    }

    public void setDataSorter(DataSorter dataSorter){
        this.dataSorter = dataSorter;
    }
    
    //Usees switch logic to update statistics display, obtaining statistic names from a defined string array
    public void updateStatisticsDisplay() {
        if (dataSorter != null) {
            switch (statisticNames[currentStatisticIndex]) {
                case "Total Deaths":
                    int totalDeaths = dataSorter.getTotalDeathsByDateRange(fromDate, toDate);
                    statValue.setText(String.valueOf(totalDeaths));
                    break;
                case "Average Cases":
                    double averageCases = dataSorter.getAverageCases(fromDate, toDate);
                    statValue.setText(String.valueOf(averageCases));
                    break;
                case "Retail and Recreation GMR":
                    double retailRecreationGMR = dataSorter.getAverageRetailGMR(fromDate, toDate);
                    statValue.setText(String.valueOf(retailRecreationGMR));
                    break;
                case "Grocery and Pharmacy GMR":
                    double groceryPharmacyGMR = dataSorter.getAverageGroceryGMR(fromDate, toDate);
                    statValue.setText(String.valueOf(groceryPharmacyGMR));
                    break;
                default:
                    break;
            }
            statName.setText(statisticNames[currentStatisticIndex]); // Update the label with the current statistic name
        }
    }

    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
