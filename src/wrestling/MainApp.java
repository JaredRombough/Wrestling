/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wrestling;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import wrestling.model.GameController;
import wrestling.view.RootLayoutController;
import wrestling.view.TitleScreenController;
import wrestling.model.Event;
import wrestling.view.BrowserController;
import wrestling.view.EventScreenController;
import wrestling.view.StartGameScreenController;
import wrestling.view.WorkerOverviewController;
import static javafx.application.Application.launch;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootLayoutController;
    private GameController gameController;

    private AnchorPane eventScreenPane;
    private EventScreenController eventScreenController;
    private AnchorPane workerOverviewPane;
    private AnchorPane eventArchiveScreenPane;
    private AnchorPane browserPane;
    private BrowserController browserController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Wrestling");
        this.primaryStage.setMaximized(true);
        gameController = new GameController();

        initRootLayout();
        showTitleScreen();

    }

    public void saveGame() {
        try {

            FileOutputStream fileOutputStream = new FileOutputStream("wrestlingsavegame.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(gameController);
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadGame() throws ClassNotFoundException {
        try {

            FileInputStream fileInputStream = new FileInputStream("wrestlingsavegame.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            gameController = null;
            gameController = (GameController) objectInputStream.readObject();
            System.out.println("after load");
            System.out.println(gameController.promotions);
            System.out.println(gameController.playerPromotion());
            objectInputStream.close();

            //now we need to update the rootlayoutcontroller with the newly
            //loaded gameController or else it won't have the correct controller
            rootLayoutController.setGameController(gameController);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void prepareScreens() {
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
            primaryStage.setScene(scene);
            primaryStage.show();

            rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);
            rootLayoutController.setGameController(this.gameController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showWorkerOverview() {
        rootLayout.setCenter(workerOverviewPane);

    }

    /*
    loads the worker overview
     */
    public void loadWorkerOverview() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/WorkerOverview.fxml"));
            workerOverviewPane = (AnchorPane) loader.load();

            WorkerOverviewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setGameController(this.gameController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    loads the browser
     */
    public void loadBrowser() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Browser.fxml"));
            browserPane = (AnchorPane) loader.load();

            browserController = loader.getController();

            browserController.setMainApp(this);
            browserController.setGameController(this.gameController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    shows the browser
     */
    public void showBrowser() {
        //System.out.println(browserPane);
        rootLayout.setCenter(browserPane);
    }

    /*
    shows the browser with a specific event displayed
    such as when we have just run an event and go straight to the
    results of it
     */
    public void showBrowser(Event event) {
        showBrowser();
        //push the event button or something
        //or have a method that finds the last evnt properly?
        browserController.showEvent(event);

    }

    public void showEventScreen() {
        rootLayout.setCenter(eventScreenPane);

    }

    /*
    loads the event screen (for booking events)
     */
    public void loadEventScreen() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/EventScreen.fxml"));
            eventScreenPane = (AnchorPane) loader.load();

            EventScreenController controller = loader.getController();
            controller.setMainApp(this);
            controller.setGameController(this.gameController);

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
        rootLayoutController.updateCurrentDateLabel();
        rootLayoutController.updateCurrentFundsLabel();
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
