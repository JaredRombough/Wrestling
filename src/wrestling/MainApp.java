package wrestling;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;
import wrestling.file.Import;
import wrestling.model.controller.GameController;
import wrestling.view.BrowserController;
import wrestling.view.RootLayoutController;
import wrestling.view.StartGameScreenController;
import wrestling.view.TitleScreenController;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;

public class MainApp extends Application {

    private static final int WINDOW_MIN_WIDTH = 1200;
    private static final int WINDOW_MIN_HEIGHT = 900;
    private static final int PRE_RUN_DAYS = 0;
    private static final String CONTACT = "OpenWrestling@gmail.com";
    private static final String VERSION = "0.0.1";

    public static void main(String[] args) {

        launch(args);

    }

    private transient Logger logger;

    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootLayoutController rootLayoutController;
    private GameController gameController;

    private AnchorPane workerOverviewPane;
    private final List<Screen> screens;

    private File picsFolder;
    private File logosFolder;
    private File dataFolder;

    private final boolean cssEnabled;

    public MainApp() {
        this.screens = new ArrayList<>();

        this.cssEnabled = true;
        logger = LogManager.getLogger(getClass());
        logger.log(Level.INFO, "Logger online");
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        this.primaryStage = primaryStage;
        this.getPrimaryStage().setTitle("Wrestling");
        this.getPrimaryStage().setMinWidth(WINDOW_MIN_WIDTH);
        this.getPrimaryStage().setMinHeight(WINDOW_MIN_HEIGHT);
        this.getPrimaryStage().centerOnScreen();
        showTitleScreen();

    }

    //starts a new random game
    public void newRandomGame() throws IOException {
        this.gameController = new GameController(true);
        initRootLayout();
        showStartGameScreen();
    }

    //starts a new game from imported data
    public void newImportGame(File dataFolder, File picsFolder, File logosFolder) throws Exception {
        this.dataFolder = dataFolder;
        this.picsFolder = picsFolder;
        this.logosFolder = logosFolder;

        Import importer = new Import();

        String error = "";
        try {

            error = importer.tryImport(dataFolder);

            if (!error.isEmpty()) {

                logger.log(Level.ERROR, error);

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Import error");
                alert.setHeaderText("Resources could not be validated.");
                alert.setContentText(error);

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add("style.css");

                alert.showAndWait();

            } else {
                this.gameController = importer.getGameController();
                initRootLayout();
                showStartGameScreen();
            }
        } catch (Exception ex) {

            logger.log(Level.ERROR, ex);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import error");
            alert.setHeaderText("Resources could not be validated.");
            alert.setContentText(error + "\n" + ex.getMessage());

            throw ex;
        }

    }

    //continues the last saved game, jumps to browser
    public void continueGame() throws ClassNotFoundException, IOException {
        this.gameController = loadGame();
        initRootLayout();
        startGame();
    }

    //shows initial title screen
    private void showTitleScreen() throws IOException {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TitleScreen.fxml"));
            AnchorPane titleScreen = (AnchorPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(titleScreen);
            BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream("title.jpg"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

            if (cssEnabled) {
                scene.getStylesheets().add("style.css");
            }

            getPrimaryStage().setScene(scene);
            getPrimaryStage().show();

            TitleScreenController controller = loader.getController();
            controller.setDependencies(this, gameController);
            controller.setImage(image);
        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            throw ex;
        }

    }

    public void startGame() throws IOException {
        prepareScreens();
        show(ScreenCode.BROWSER);
        updateLabels();

        //number of days to run automatically at start of game
        for (int i = 0; i < PRE_RUN_DAYS; i++) {
            nextDay();
            logger.log(Level.INFO, "day: " + gameController.getDateManager().today());
        }

        setButtonsDisable(false);
    }

    private void saveGame() throws IOException {

        Kryo kryo = new Kryo();

        try (Output output = new Output(new FileOutputStream("saveGame.bin"))) {
            kryo.writeObject(output, gameController);
        } catch (Exception ex) {
            logger.log(Level.ERROR, ex);
            throw ex;
        }
    }

    private GameController loadGame() throws ClassNotFoundException, IOException {

        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        GameController gc = null;

        try (Input input = new Input(new FileInputStream("saveGame.bin"))) {
            gc = kryo.readObject(input, GameController.class);
        } catch (Exception ex) {
            logger.log(Level.ERROR, ex);
            throw ex;
        }

        return gc;
    }

    private void prepareScreens() throws IOException {
        //this will load into memory all the screens that we will be switching between
        //so we aren't creating a new screen each time
        List<ScreenCode> screensToLoad = new ArrayList<>(Arrays.asList(ScreenCode.FINANCIAL,
                ScreenCode.CALENDAR,
                ScreenCode.BROWSER,
                ScreenCode.EVENT_SCREEN,
                ScreenCode.WORKER_OVERVIEW
        ));

        for (ScreenCode screen : screensToLoad) {
            screens.add(loadScreenFromResource(screen));
        }
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() throws IOException {
        try {

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);

            if (cssEnabled) {
                scene.getStylesheets().add("style.css");
            }

            getPrimaryStage().setScene(scene);
            getPrimaryStage().show();

            //load and store the controller
            rootLayoutController = loader.getController();
            rootLayoutController.setDependencies(this, gameController);

        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            throw ex;
        }
    }

    /**
     * Shows the worker overview
     */
    public void showWorkerOverview() {
        rootLayout.setCenter(workerOverviewPane);

    }

    private Screen loadScreenFromResource(ScreenCode code) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        Screen screen = new Screen();
        loader.setLocation(MainApp.class.getResource(code.resource()));
        screen.anchorPane = (AnchorPane) loader.load();
        screen.controller = loader.getController();
        screen.controller.setDependencies(this, gameController);
        screen.code = code;
        return screen;
    }

    public void show(ScreenCode code) {
        Screen screen = getByCode(code);
        rootLayout.setCenter(screen.anchorPane);
        screen.controller.updateLabels();

    }

    private Screen getByCode(ScreenCode code) {
        for (Screen screen : screens) {
            if (screen.code == code) {
                return screen;
            }
        }
        return null;
    }

    /*
    shows the browser and selects the most recent event
     */
    public void showLastEvent() {
        show(ScreenCode.BROWSER);
        ((BrowserController) getByCode(ScreenCode.BROWSER).controller).showLastEvent();
    }

    /*
    loads and shows a start game screen
    currently these methods are combined because we only do it once
     */
    public void showStartGameScreen() throws IOException {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/StartGameScreen.fxml"));
            AnchorPane startGameScreen = (AnchorPane) loader.load();

            StartGameScreenController controller = loader.getController();
            controller.setDependencies(this, gameController);

            rootLayout.setCenter(startGameScreen);

        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            throw ex;
        }
    }


    /*
    calls the root layout to update the labels
    most/all controllers have a link to the main app
    so if they cause any change that needs to be reflected 
    in labels outside of their screens it can be handled here
     */
    private void updateLabels() {
        rootLayoutController.updateLabels();
        for (Screen screen : screens) {
            if (screen.code.alwaysUpdate()) {
                updateLabels(screen.code);
            }
        }

    }

    private void updateLabels(ScreenCode code) {
        getByCode(code).controller.updateLabels();
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

    /**
     * @return the primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @return the picsFolder
     */
    public File getPicsFolder() {
        return (dataFolder == null) ? new File(System.getProperty("user.dir") + "/PICS/") : picsFolder;
    }

    /**
     * @return the logosFolder
     */
    public File getLogosFolder() {
        return (dataFolder == null) ? new File(System.getProperty("user.dir") + "/LOGOS/") : logosFolder;
    }

    /**
     * @return the dataFolder
     */
    public File getDataFolder() {

        return (dataFolder == null) ? new File(System.getProperty("user.dir") + "/DATA/") : dataFolder;
    }

    /**
     * @return the VERSION
     */
    public String getVERSION() {
        return VERSION;
    }

    /**
     * @return the CONTACT
     */
    public String getCONTACT() {
        return CONTACT;
    }

}
