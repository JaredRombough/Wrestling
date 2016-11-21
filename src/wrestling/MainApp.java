/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import wrestling.model.GameController;
import wrestling.view.RootLayoutController;
import wrestling.view.TitleScreenController;
import wrestling.view.BrowserController;
import wrestling.view.EventScreenController;
import wrestling.view.StartGameScreenController;
import wrestling.view.WorkerOverviewController;
import static javafx.application.Application.launch;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import wrestling.file.Import;
import wrestling.model.EventArchive;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootLayoutController;
    private GameController gameController;

    private AnchorPane eventScreenPane;
    private EventScreenController eventScreenController;
    private AnchorPane workerOverviewPane;
    private AnchorPane browserPane;
    private BrowserController browserController;

    public MainApp() {
        this.cssEnabled = true;

    }

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Wrestling");

        do {

            showOptionDialogue();
        } while (gameController == null);

        initRootLayout();
        showTitleScreen();

    }

    public void startGame() throws IOException {
        prepareScreens();
        showBrowser();
        updateLabels();

        setButtonsDisable(false);
    }

    private final boolean cssEnabled;


    /*
    show the initial dialogue to choose random or imported game
     */
    private void showOptionDialogue() throws IOException, ClassNotFoundException {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle("Wrestling");
        alert.setHeaderText("Wrestling!");
        alert.setContentText("Randomly generate game or import from file?");
        ButtonType randomButton = new ButtonType("Random");
        ButtonType importButton = new ButtonType("Import");
        ButtonType loadButton = new ButtonType("Load Game");

        if (cssEnabled) {
            //add css
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
            dialogPane.getStyleClass().add("myDialog");
        }

        alert.getButtonTypes().setAll(randomButton, importButton, loadButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == randomButton) {
            gameController = new GameController();

            alert.close();
        } else if (result.get() == importButton) {
            Import importer = new Import();
            gameController = importer.importController();
            alert.close();

        } else if (result.get() == loadButton) {

            loadGame();

        }

    }

    public void saveGame() throws FileNotFoundException, IOException {

        FileOutputStream fileOutputStream = new FileOutputStream("wrestlingsavegame.ser");
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(gameController);
        }

    }

    public void loadGame() throws ClassNotFoundException, FileNotFoundException, IOException {

        FileInputStream fileInputStream = new FileInputStream("wrestlingsavegame.ser");
        try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            gameController = null;
            gameController = (GameController) objectInputStream.readObject();

        } catch (Exception e) {

            gameController = null;
            System.out.println("load game exception");
            e.printStackTrace();
        }

    }

    public void prepareScreens() throws IOException {
        //this will load into memory all the screens that we will be switching between
        //so we aren't creating a new screen each time
        loadWorkerOverview();
        loadEventScreen();
        loadBrowser();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);

            if (cssEnabled) {
                scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.show();

            //load and store the controller
            rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);
            rootLayoutController.setGameController(this.gameController);

            //set the minum size of the main window based on the root layout
            primaryStage.setMinWidth(rootLayoutController.rootLayoutMinWidth() * 3);
            primaryStage.setMinHeight(rootLayoutController.rootLayoutMinWidth() * 2);

            primaryStage.centerOnScreen();
            //this.primaryStage.setMaximized(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the worker overview
     */
    public void showWorkerOverview() {
        rootLayout.setCenter(workerOverviewPane);

    }

    /*
    loads the worker overview
     */
    private void loadWorkerOverview() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/WorkerOverview.fxml"));
            workerOverviewPane = (AnchorPane) loader.load();

            WorkerOverviewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setGameController(this.gameController);

        } catch (IOException e) {
        }
    }

    /*
    loads the browser
     */
    private void loadBrowser() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/Browser.fxml"));
        browserPane = (AnchorPane) loader.load();

        browserController = loader.getController();

        browserController.setMainApp(this);
        browserController.setGameController(this.gameController);

    }

    /*
    shows the browser
     */
    public void showBrowser() {

        rootLayout.setCenter(browserPane);
    }

    /*
    shows the browser with a specific event displayed
    such as when we have just run an event and go straight to the
    results of it
     */
    public void showBrowser(EventArchive event) {
        showBrowser();
        browserController.showEvent(event);

    }

    public void showEventScreen() {
        rootLayout.setCenter(eventScreenPane);

    }

    /*
    loads the event screen (for booking events)
     */
    private void loadEventScreen() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EventScreen.fxml"));
            eventScreenPane = (AnchorPane) loader.load();

            EventScreenController controller = loader.getController();
            controller.setMainApp(this);
            controller.setGameController(this.gameController);
            eventScreenController = controller;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    loads and shows a start game screen
    currently these methods are combined because we only do it once
     */
    public void showStartGameScreen() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StartGameScreen.fxml"));
            AnchorPane startGameScreen = (AnchorPane) loader.load();

            StartGameScreenController controller = loader.getController();
            controller.setMainApp(this);
            controller.setGameController(this.gameController);

            rootLayout.setCenter(startGameScreen);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    loads and shows the title screen
     */
    public void showTitleScreen() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TitleScreen.fxml"));
            AnchorPane titleScreen = (AnchorPane) loader.load();

            rootLayout.setCenter(titleScreen);

            TitleScreenController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    calls the root layout to update the labels
    most/all controllers have a link to the main app
    so if they cause any change that needs to be reflected 
    in labels outside of their screens it can be handled here
     */
    public void updateLabels() {
        rootLayoutController.updateLabels();
        browserController.updateLabels();
        eventScreenController.updateLabels();

    }

    public void nextDay() throws IOException {
        gameController.nextDay();
        saveGame();
        updateLabels();
    }

    /*
    disable buttons, for when we're starting up the game
    and don't yet want to allow screen switching freedom
     */
    public void setButtonsDisable(boolean disable) {
        rootLayoutController.setButtonsDisable(disable);
    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
