import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static DataSorter dataSorter;

    @Override
    public void start(Stage primaryStage) throws Exception {
        dataSorter = new DataSorter();
        // Load the FXML file
        FXMLLoader mainWindow = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Parent root = mainWindow.load();

        // pass the dataSorter to the main controller
        MainAppController mainController = mainWindow.getController();
        mainController.setDataSorter(dataSorter);

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Binary Baes' Covid Data Explorer");
        primaryStage.show();
    }

    public static DataSorter getDataSorter() {
        return dataSorter;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
