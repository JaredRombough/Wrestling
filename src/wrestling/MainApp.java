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
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
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
import wrestling.view.TitleScreenController;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;

public class MainApp extends Application {

    private static final int WINDOW_MIN_WIDTH = 1200;
    private static final int WINDOW_MIN_HEIGHT = 900;
    private static final int PRE_RUN_DAYS = 0;
    private static final String CONTACT = "OpenWrestling@gmail.com";
    private static final String VERSION = "0.0.2";

    public static void main(String[] args) {

        launch(args);

    }

    private final transient Logger logger;

    private Stage primaryStage;
    private GameController gameController;
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
        try {
            gameController = new GameController(true);
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Problem creating gameController, setting gameController null", ex);
            gameController = null;
            throw ex;
        }
        if (gameController != null) {
            initRootLayout();
            showStartGameScreen();
        }

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

                generateAlert("Import error", "Resources could not be validated.", error).showAndWait();

            } else {
                this.gameController = importer.getGameController();
                initRootLayout();
                showStartGameScreen();
            }
        } catch (Exception ex) {

            logger.log(Level.ERROR, error, ex);

            generateAlert("Import error", "Resources could not be validated.", error + "\n" + ex.getMessage()).showAndWait();

            throw ex;
        }

    }

    public Alert generateAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("style.css");
        return alert;
    }

    //continues the last saved game, jumps to browser
    public void continueGame() throws IOException {
        this.gameController = loadGame();

        if (gameController != null) {
            try {
                initRootLayout();
            } catch (IOException ex) {
                logger.log(Level.ERROR, "initRootLayout() call failed in continueGame()", ex);
                throw ex;
            }
            try {
                startGame();
            } catch (IOException ex) {
                logger.log(Level.ERROR, "startGame() call failed in continueGame()", ex);
                throw ex;
            }
        }
    }

    //shows initial title screen
    private void showTitleScreen() throws IOException {

        try {
            Screen titleScreen = ViewUtils.loadScreenFromResource(ScreenCode.TITLE, this, gameController);

            // Show the scene containing the root layout.
            Scene scene = new Scene(titleScreen.pane);

            if (cssEnabled) {
                scene.getStylesheets().add("style.css");
            }

            getPrimaryStage().setScene(scene);
            getPrimaryStage().show();

            ((TitleScreenController) titleScreen.controller).setImage(loadTitleScreenImage());
        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            throw ex;
        }

    }

    private Image loadTitleScreenImage() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream("title.jpg"));
        return SwingFXUtils.toFXImage(bufferedImage, null);
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
        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            generateAlert("Error", "Error while saving the game", ex.getLocalizedMessage()).showAndWait();
            throw ex;
        }
    }

    private GameController loadGame() {

        Kryo kryo = new Kryo();
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

        GameController gc = null;

        try (Input input = new Input(new FileInputStream("saveGame.bin"))) {
            gc = kryo.readObject(input, GameController.class);
        } catch (IOException ex) {
            logger.log(Level.ERROR, "IOException in loadGame(), returning null gameController", ex);
            gc = null;
        }

        return gc;
    }

    private void prepareScreens() throws IOException {
        //this will load into memory all the screens that we will be switching between
        //so we aren't creating a new screen each time
        List<ScreenCode> screensToLoad = new ArrayList<>(Arrays.asList(ScreenCode.FINANCIAL,
                ScreenCode.CALENDAR,
                ScreenCode.BROWSER,
                ScreenCode.EVENT,
                ScreenCode.WORKER_OVERVIEW
        ));

        for (ScreenCode screen : screensToLoad) {
            screens.add(ViewUtils.loadScreenFromResource(screen, this, gameController));
        }
    }

    /**
     * Initializes the root layout.
     *
     * @throws java.io.IOException
     */
    public void initRootLayout() throws IOException {
        try {

            screens.add(ViewUtils.loadScreenFromResource(ScreenCode.ROOT, this, gameController));

            // Show the scene containing the root layout.
            Scene scene = new Scene(ViewUtils.getByCode(screens, ScreenCode.ROOT).pane);

            if (cssEnabled) {
                scene.getStylesheets().add("style.css");
            }
            getPrimaryStage().setScene(scene);
            getPrimaryStage().show();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "initRootLayout() failed with IOException", ex);
            throw ex;
        }
    }

    public void show(ScreenCode code) {
        Screen screen = ViewUtils.getByCode(screens, code);
        ((BorderPane) ViewUtils.getByCode(screens, ScreenCode.ROOT).pane).setCenter(screen.pane);
        screen.controller.updateLabels();

    }

    /*
    shows the browser and selects the most recent event
     */
    public void showLastEvent() {
        show(ScreenCode.BROWSER);
        ((BrowserController) ViewUtils.getByCode(screens, ScreenCode.BROWSER).controller).showLastEvent();
    }

    /*
    loads and shows a start game screen
    currently these methods are combined because we only do it once
     */
    public void showStartGameScreen() throws IOException {
        try {
            Screen startGameScreen = ViewUtils.loadScreenFromResource(ScreenCode.START, this, gameController);
            ((BorderPane) ViewUtils.getByCode(screens, ScreenCode.ROOT).pane).setCenter(startGameScreen.pane);
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Error in showStartGameScreen()", ex);
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
        for (Screen screen : screens) {
            if (screen.code.alwaysUpdate()) {
                updateLabels(screen.code);
            }
        }
    }

    private void updateLabels(ScreenCode code) {
        ViewUtils.getByCode(screens, code).controller.updateLabels();
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
        ((RootLayoutController) ViewUtils.getByCode(screens, ScreenCode.ROOT).controller).setButtonsDisable(disable);
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

        return dataFolder == null ? new File(System.getProperty("user.dir") + "/DATA/") : dataFolder;
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
