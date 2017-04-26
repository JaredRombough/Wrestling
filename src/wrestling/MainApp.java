package wrestling;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import wrestling.file.Import;
import org.objenesis.strategy.StdInstantiatorStrategy;
import wrestling.view.FinancialScreenController;
import static javafx.application.Application.launch;

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
    private AnchorPane financialPane;
    private FinancialScreenController financialController;

    private final int WINDOW_MIN_WIDTH = 1200;
    private final int WINDOW_MIN_HEIGHT = 900;

    public MainApp() {
        this.cssEnabled = true;

    }

    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Wrestling");
        this.primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        this.primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        this.primaryStage.centerOnScreen();

        showTitleScreen();

    }

    //starts a new random game
    public void newRandomGame() throws IOException {
        this.gameController = new GameController();
        initRootLayout();
        showStartGameScreen();
    }

    //starts a new game from imported data
    public void newImportGame() throws IOException {
        Import importer = new Import();
        this.gameController = importer.importController();
        initRootLayout();
        showStartGameScreen();
    }

    //continues the last saved game, jumps to browser
    public void continueGame() throws ClassNotFoundException, IOException {
        this.gameController = loadGame();
        initRootLayout();
        startGame();
    }

    //shows initial title screen
    private void showTitleScreen() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TitleScreen.fxml"));
            AnchorPane titleScreen = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(titleScreen);

            if (cssEnabled) {
                scene.getStylesheets().add(getClass().getResource("view/style.css").toExternalForm());
            }

            primaryStage.setScene(scene);
            primaryStage.show();

            TitleScreenController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
        }

    }

    public void startGame() throws IOException {
        prepareScreens();
        showBrowser();
        updateLabels();

        //number of days to run automatically at start of game
        int preRunDays = 10;

        for (int i = 0; i < preRunDays; i++) {
            nextDay();
            System.out.println("day: " + gameController.date());

        }

        setButtonsDisable(false);
    }

    private final boolean cssEnabled;

    private void saveGame() throws FileNotFoundException, IOException {

        Kryo kryo = new Kryo();

        Output output = new Output(new FileOutputStream("file.bin"));

        kryo.writeObject(output, gameController);
        output.close();
    }

    private GameController loadGame() throws ClassNotFoundException, FileNotFoundException, IOException {

        Kryo kryo = new Kryo();

        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        Input input = new Input(new FileInputStream("file.bin"));
        GameController gc = kryo.readObject(input, GameController.class);
        input.close();

        return gc;
    }

    private void prepareScreens() throws IOException {
        //this will load into memory all the screens that we will be switching between
        //so we aren't creating a new screen each time
        loadWorkerOverview();
        loadEventScreen();
        loadBrowser();
        loadFinancial();
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
            //primaryStage.setMinWidth(rootLayoutController.rootLayoutMinWidth() * 3);
            //primaryStage.setMinHeight(rootLayoutController.rootLayoutMinWidth() * 2);
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
    loads the financial overview screen
     */
    private void loadFinancial() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/FinancialScreen.fxml"));
        financialPane = (AnchorPane) loader.load();

        financialController = loader.getController();

        financialController.setMainApp(this);
        financialController.setGameController(this.gameController);
    }

    /*
    shows the browser
     */
    public void showBrowser() {

        rootLayout.setCenter(browserPane);
    }

    /*
    shows the financial screen
     */
    public void showFinancial() {
        rootLayout.setCenter(financialPane);
        financialController.updateLabels();
    }

    /*
    shows the browser and selects the most recent event
     */
    public void showLastEvent() {
        showBrowser();

        browserController.showLastEvent();

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
        }
    }

    /*
    loads and shows the title screen
     *//*
    private void showTitleScreen() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TitleScreen.fxml"));
            AnchorPane titleScreen = (AnchorPane) loader.load();

            rootLayout.setCenter(titleScreen);

            TitleScreenController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
        }

    }*/

 /*
    calls the root layout to update the labels
    most/all controllers have a link to the main app
    so if they cause any change that needs to be reflected 
    in labels outside of their screens it can be handled here
     */
    private void updateLabels() {
        rootLayoutController.updateLabels();
        browserController.updateLabels();
        eventScreenController.updateLabels();
        financialController.updateLabels();

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
    private void setButtonsDisable(boolean disable) {
        rootLayoutController.setButtonsDisable(disable);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
