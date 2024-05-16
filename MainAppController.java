import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.DialogPane;

public class MainAppController {

    
    @FXML
    private BorderPane mainContainer;
    
    @FXML
    private Button quitButton, forwardButton, backButton;
    
    @FXML
    private Button instructionButton;
    
    @FXML
    private DatePicker fromDatePicker, toDatePicker;
    
    private LocalDate finalMinDate, finalMaxDate, fromDate, toDate;   
    //List of fxml file paths
    private List<String> panelPaths = List.of("WelcomePanel.fxml","MapPanel.fxml", "StatPanel.fxml", "CompPanel.fxml");
    
    private int currentPanelIndex = 0;

    
    private CovidDataLoader loader = new CovidDataLoader(); 
    private List<CovidData> currentData;
    
    private DataSorter dataSorter;
    
    private MapController mapController;
    private StatPanelController statController;
    private CompPanelController compController;

    private boolean canChangePanel = false;
    


    // Method to initialize listeners or any setup needed
    @FXML
    private void initialize() {
        // set up button actions here
        quitButton.setOnAction(event -> handleQuitAction());
        forwardButton.setOnAction(event -> handleForwardAction());
        backButton.setOnAction(event -> handleBackAction());
        instructionButton.setOnAction(evenet -> handleInstructionAction());
        setupDatePickers();
        calculateMinMaxRange();
        
        //load first panel initially
        loadPanel(panelPaths.get(currentPanelIndex));
          
        
    }

    public void setDataSorter(DataSorter dataSorter) {
        this.dataSorter = dataSorter;
    }
    
    public boolean canChangePanel(){
        return canChangePanel;
    }
    
    public void setChangePanel(Boolean value){
        canChangePanel = value;
    }
    
    //Loads design from fxml file, and calles respective update methods to the relevant panel
    public void loadPanel(String fxmlFile) {
    
        fromDate = fromDatePicker.getValue();
        toDate = toDatePicker.getValue();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Node panel = loader.load();
            mainContainer.setCenter(panel);
          if(fxmlFile.equals("MapPanel.fxml")){
                mapController = loader.getController();
                mapController.setDataSorter(dataSorter);
                mapController.setDateRange(fromDate,toDate);
                mapController.updateButtonColors();

          } 
          else if(fxmlFile.equals("StatPanel.fxml")) {
                statController = loader.getController();
                statController.setDataSorter(dataSorter);
                statController.setDateRange(fromDate, toDate);
                statController.updateStatisticsDisplay() ;
          }
          else if(fxmlFile.equals("CompPanel.fxml")){
                compController = loader.getController();
                compController.setDataSorter(dataSorter);
                compController.setDateRange(fromDate, toDate);
                compController.updateChartData();
          }
        } catch (IOException e) {
          e.printStackTrace(); // Handle exceptions properly
        }
    }
    
    // Method to handle the quit action
    private void handleQuitAction() {
      Stage stage = (Stage) quitButton.getScene().getWindow();
      stage.close();
    }
    
    // Method to handle forward action
    public void handleBackAction(){
        if(canChangePanel()){
            currentPanelIndex = (currentPanelIndex - 1 + panelPaths.size()) % panelPaths.size(); // Move backward and wrap around
            loadPanel(panelPaths.get(currentPanelIndex));  
          }else{
            showAlert("Inappropriate Dates", "You must select appropriate dates before going to the previous panel, between "+finalMinDate+" and "+finalMaxDate);
        }
    }
    
    // Method to handle back action
    public void handleForwardAction(){
        if(canChangePanel()){
            currentPanelIndex = (currentPanelIndex + 1) % panelPaths.size();
            loadPanel(panelPaths.get(currentPanelIndex)); 
        }else{
            showAlert("Inappropriate Dates", "You must select appropriate dates before going to the next panel, between "+finalMinDate+" and "+finalMaxDate);
        }
    
    }
     // Method to handle instructions action
    public void handleInstructionAction() {
        try {
            // Load the FXML file for the new stage
            FXMLLoader loader = new FXMLLoader(getClass().getResource("instructionsPanel.fxml"));
            Parent root = loader.load();
            // Create a new stage
            Stage instructionStage = new Stage();
            instructionStage.setTitle("Instructions");
    
            // Set the scene for the stage
            Scene scene = new Scene(root);
            instructionStage.setScene(scene);
    
            // Show the stage
            instructionStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void setupDatePickers() {
        // Listener for the 'from' DatePicker
        fromDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateDateRange();
        });
        fromDatePicker.setShowWeekNumbers(false);
    
        // Listener for the 'to' DatePickergetTotalCasesByDateRange
        toDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            validateDateRange();
        });
        toDatePicker.setShowWeekNumbers(false);
    }
    
    //Any time a date is picked, will check for validity, enabling/disablign ability to traverse through panels
 private void validateDateRange() {
    fromDate = fromDatePicker.getValue();
    toDate = toDatePicker.getValue();
    
    boolean validFromDate = validateDate(fromDate, "From");
    boolean validToDate = validateDate(toDate, "To");
    
    if (validFromDate && validToDate) {
        if (fromDate.isAfter(toDate)) {
            showAlert("Invalid Date Range", "The 'From' date cannot be after the 'To' date.");
            resetDatePickers();
            setChangePanel(false);
        } else {
            setChangePanel(true);
            updateControllers(fromDate, toDate);
        }
    } else {
        setChangePanel(false);
    }
}

private boolean validateDate(LocalDate date, String type) {
    if (date == null) {
        return false;
    }
    
    if (date.isBefore(finalMinDate) || date.isAfter(finalMaxDate)) {
        showAlert("Invalid " + type + " Date", "Selected '" + type + "' date is out of range.");
        if (type.equals("From")) {
            fromDatePicker.setValue(null);
        } else {
            toDatePicker.setValue(null);
        }
        return false;
    }
    
    return true;
}

private void resetDatePickers() {
    fromDatePicker.setValue(null);
    toDatePicker.setValue(null);
}

private void updateControllers(LocalDate fromDate, LocalDate toDate) {
    if (mapController != null) {
        mapController.setDateRange(fromDate, toDate);
        mapController.updateButtonColors();
    }
    if (statController != null) {
        statController.setDateRange(fromDate, toDate);
        statController.updateStatisticsDisplay();
    }
    if (compController != null) {
        compController.setDateRange(fromDate, toDate);
        compController.updateChartData();
    }
}

    
    private void showAlert(String title, String content) {
                
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 12px; -fx-font-weight: bold;");
        
        alert.showAndWait();
    }
    
    //Method used to have the min and max date within the csv file
    private void calculateMinMaxRange(){
        ArrayList<CovidData> covidDataList = loader.load();
    
        LocalDate minDate = LocalDate.MAX;
        LocalDate maxDate = LocalDate.MIN;
        for (CovidData data : covidDataList) {
            LocalDate date = LocalDate.parse(data.getDate());
            if (date.isBefore(minDate)) {
                minDate = date;
            }
            if (date.isAfter(maxDate)) {
                maxDate = date;
            }
        }
    
        finalMinDate = minDate.minusDays(1);
        finalMaxDate = maxDate.plusDays(1);
        
    }
}
