import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;

public class MapController {

    @FXML
    private Button Haringey, Camden, Newham, Barking_And_Dagenham, Tower_Hamlets, Harrow, Enfield, Barnet, Kensington_And_Chelsea, Redbridge, Havering, Hackney, Islington, Waltham_Forest, Brent, Hillingdon, Westminster, Ealing, Greenwich, Hounslow, Hammersmith_And_Fulham, Wandsworth, City_Of_London, Bexley, Richmond_Upon_Thames, Merton, Lambeth, Southwark, Lewisham, Kingston_Upon_Thames, Sutton, Croydon, Bromley;
    
    private LocalDate fromDate,toDate;
    
    private DataSorter dataSorter;
    
    @FXML
    private TableView<CovidData> covidTableView;
    @FXML
    private TableColumn<CovidData, String> dateColumn;
    @FXML
    private TableColumn<CovidData, Number> retailRecreationColumn,groceryPharmacyColumn,parksColumn,transitColumn,residentialColumn,workplacesColumn,newCasesColumn,totalCasesColumn,newDeathsColumn,totalDeathsColumn;    
    //Changes shapes and prepares buttons to be clicked and open a new window with records
    public void initialize() {
        applyHexagonShape(Enfield);
        applyHexagonShape(Barnet);
        applyHexagonShape(Haringey);
        applyHexagonShape(Waltham_Forest);
        applyHexagonShape(Harrow);
        applyHexagonShape(Brent);
        applyHexagonShape(Camden);
        applyHexagonShape(Islington);
        applyHexagonShape(Hackney);
        applyHexagonShape(Redbridge);
        applyHexagonShape(Havering);
        applyHexagonShape(Hillingdon);
        applyHexagonShape(Ealing);      
        applyHexagonShape(Kensington_And_Chelsea);
        applyHexagonShape(Westminster);
        applyHexagonShape(Tower_Hamlets);
        applyHexagonShape(Newham);
        applyHexagonShape(Barking_And_Dagenham);
        applyHexagonShape(Hounslow);
        applyHexagonShape(Hammersmith_And_Fulham);
        applyHexagonShape(Wandsworth);
        applyHexagonShape(City_Of_London);
        applyHexagonShape(Greenwich);
        applyHexagonShape(Bexley);
        applyHexagonShape(Richmond_Upon_Thames);
        applyHexagonShape(Merton);
        applyHexagonShape(Lambeth);
        applyHexagonShape(Southwark);
        applyHexagonShape(Lewisham);
        applyHexagonShape(Kingston_Upon_Thames);
        applyHexagonShape(Sutton);
        applyHexagonShape(Croydon);
        applyHexagonShape(Bromley);                
        
        setButtonClickHandlers();
        
    }

    private void applyHexagonShape(Button button)
    {
        if (button != null) {
            Shape hexagonShape = hexagonShape(55); //55 is the input radius into the function below
            button.setShape(hexagonShape);
            button.setMinSize(55,55);
            button.setMaxSize(55,55);
        } else {
            System.out.println("Button is null, cannot apply hexagon shape.");
        }
    }
    
    // methods used to rotate the hexagon and make each line separately, returning a hexagon polygon
    private Shape hexagonShape(double radius) {
        Polygon hexagon = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angleRad = Math.toRadians(60 * i - 30); 
            double x = radius + radius * Math.cos(angleRad);
            double y = radius + radius * Math.sin(angleRad);
            hexagon.getPoints().addAll(x, y);
        }
        return hexagon;
    }
    
    //Using data withitn time range, creates a dynamic heat map, which will change the colors of buttons using relative calculation, rather than using solid if > x statements
    public void updateButtonColors() {
        CovidDataLoader loader = new CovidDataLoader();
        ArrayList<CovidData> filteredData = loader.getDataByDateRange(fromDate, toDate);

        // Calculate total deaths for each borough
        HashMap<String, Integer> boroughDeaths = new HashMap<>();
        for (CovidData data : filteredData) {
            String borough = data.getBorough();
            int deaths = data.getNewDeaths();
            boroughDeaths.put(borough, boroughDeaths.getOrDefault(borough, 0) + deaths);
        }
        
        // Find minimum and maximum death counts
        int minDeaths = Integer.MAX_VALUE;
        int maxDeaths = Integer.MIN_VALUE;
        for (int deaths : boroughDeaths.values()) {
            if (deaths < minDeaths) minDeaths = deaths;
            if (deaths > maxDeaths) maxDeaths = deaths;
        }

        // Define start and end colors for the gradient 
        Color startColor = Color.web("#8ffbff");
        Color endColor = Color.web("#006d9c");

        // Set button colors based on total deaths
        for (Button button : Arrays.asList(Haringey, Camden, Newham, Barking_And_Dagenham, Tower_Hamlets, Harrow, Enfield, Barnet, Kensington_And_Chelsea, Redbridge, Havering, Hackney, Islington, Waltham_Forest, Brent, Hillingdon, Westminster, Ealing, Greenwich, Hounslow, Hammersmith_And_Fulham, Wandsworth, City_Of_London, Bexley, Richmond_Upon_Thames, Merton, Lambeth, Southwark, Lewisham, Kingston_Upon_Thames, Sutton, Croydon, Bromley)) {
            String borough = button.getId().replace("_"," "); // Assuming button id matches borough name
            if (boroughDeaths.containsKey(borough)) {
                int deaths = boroughDeaths.get(borough);
                // Scale the death count to a fraction between 0 and 1
                double fraction = (double) (deaths - minDeaths) / (maxDeaths - minDeaths);
                // Interpolate the color based on the fraction
                Color interpolatedColor = interpolateColor(startColor, endColor, fraction);
                // Apply the interpolated color to the button
                button.setStyle("-fx-background-color: " + toHexString(interpolatedColor) + ";");
            }
        }
    }

    // Method to interpolate between two colors
    private Color interpolateColor(Color start, Color end, double fraction) {
        double r = start.getRed() + (end.getRed() - start.getRed()) * fraction;
        double g = start.getGreen() + (end.getGreen() - start.getGreen()) * fraction;
        double b = start.getBlue() + (end.getBlue() - start.getBlue()) * fraction;
        return new Color(r, g, b, 1.0); // 1 does full opacity
    }

    // Method to convert Color object to hex string
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    
    private void setButtonClickHandlers() {
        ArrayList<Button> buttons = new ArrayList<>(Arrays.asList(Haringey, Camden, Newham, Barking_And_Dagenham, Tower_Hamlets, Harrow, Enfield, Barnet, Kensington_And_Chelsea, Redbridge, Havering, Hackney, Islington, Waltham_Forest, Brent, Hillingdon, Westminster, Ealing, Greenwich, Hounslow, Hammersmith_And_Fulham, Wandsworth, City_Of_London, Bexley, Richmond_Upon_Thames, Merton, Lambeth, Southwark, Lewisham, Kingston_Upon_Thames, Sutton, Croydon, Bromley));
        for (Button button : buttons) {
            button.setOnAction(event -> {
                //As variable names cant have empty spaces, used underscores, which get replaced when fetching data 
                String boroughName = button.getId().replace("_"," ");
                displayDataInTable(dataSorter.getDataByBorough(fromDate,toDate, boroughName));
            });
        }
    }
    
    public void setDataSorter(DataSorter dataSorter){
        this.dataSorter = dataSorter;
    }
    
    public void setDateRange(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
    
    //Gets the fxml file with these columns, and populates them with the values obtained from filtered data
    public void displayDataInTable(ArrayList<CovidData> filteredBoroughData) {
        try {
            FXMLLoader loader = new FXMLLoader(MapController.class.getResource("secondWindow.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            String windowTitle = filteredBoroughData.get(0).getBorough();
            dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
            retailRecreationColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRetailRecreationGMR()));
            groceryPharmacyColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getGroceryPharmacyGMR()));
            parksColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getParksGMR()));
            transitColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTransitGMR()));    
            workplacesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getWorkplacesGMR()));
            residentialColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getResidentialGMR())); 
            newCasesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNewCases()));
            totalCasesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalCases()));
            newDeathsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNewDeaths()));
            totalDeathsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalDeaths()));
            
            //Disables some unappealing highlighting, as well as being able to sort the tables upside down, they can still be sorted by click hold on the title, and sliding left/right
            covidTableView.setSortPolicy(param -> false);
            covidTableView.setFocusTraversable(false);
            covidTableView.setSelectionModel(null);
            
            covidTableView.getItems().clear();
            covidTableView.getItems().addAll(filteredBoroughData);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Records of " + windowTitle);
            stage.show();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            alert.setContentText("Unable to display the data. Please try again later.");
            alert.showAndWait();
    }
    }
}

    
