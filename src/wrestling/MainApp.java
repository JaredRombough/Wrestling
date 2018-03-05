package wrestling;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objenesis.strategy.StdInstantiatorStrategy;
import wrestling.file.Import;
import wrestling.model.controller.GameController;
import wrestling.view.NextDayScreenController;
import wrestling.view.RootLayoutController;
import wrestling.view.calendar.CalendarController;
import wrestling.view.start.TitleScreenController;
import wrestling.view.utility.Screen;
import wrestling.view.utility.ScreenCode;
import wrestling.view.utility.ViewUtils;

public class MainApp extends Application {

    private static final int WINDOW_MIN_WIDTH = 1200;
    private static final int WINDOW_MIN_HEIGHT = 900;
    private static final int PRE_RUN_DAYS = 0;
    private static final String CONTACT = "OpenWrestling@gmail.com";
    private static final String VERSION = "0.1.1";

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

    private boolean preRun = false;
    private boolean randomGame;
    private final boolean cssEnabled;

    private final ResourceBundle resx;

    private Screen currentScreen;

    private double currentStageWidth;
    private double currentStageHeight;

    public MainApp() {
        this.screens = new ArrayList<>();

        this.cssEnabled = true;
        logger = LogManager.getLogger(getClass());
        logger.log(Level.INFO, "Logger online. Running version " + VERSION);

        Locale locale = new Locale("en", "US");

        resx = ResourceBundle.getBundle("wrestling.Language", locale);

        currentStageWidth = WINDOW_MIN_WIDTH;
    }

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        primaryStage.setTitle("Wrestling");
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        primaryStage.centerOnScreen();

        ChangeListener<Number> stageHeightListener = ((observable, oldValue, newValue) -> {
            currentStageHeight = newValue.doubleValue();
            if (currentScreen != null) {
                updateLabels(currentScreen.code);
            }

        });
        ChangeListener<Number> stageWidthListener = ((observable, oldValue, newValue) -> {
            currentStageWidth = newValue.doubleValue();
            if (currentScreen != null) {

                updateLabels(currentScreen.code);
            }

        });

        primaryStage.widthProperty().addListener(stageWidthListener);
        primaryStage.heightProperty().addListener(stageHeightListener);

        showTitleScreen();

    }

    //starts a new random game
    public void newRandomGame() throws IOException {
        randomGame = true;
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
        randomGame = false;
        this.dataFolder = dataFolder;
        this.picsFolder = picsFolder;
        this.logosFolder = logosFolder;

        Import importer = new Import();

        String error = "";
        try {

            error = importer.tryImport(dataFolder);

            if (!error.isEmpty()) {

                logger.log(Level.ERROR, error);

                ViewUtils.generateAlert("Import error", "Resources could not be validated.", error).showAndWait();

            } else {
                this.gameController = importer.getGameController();
                initRootLayout();
                showStartGameScreen();
            }
        } catch (Exception ex) {

            logger.log(Level.ERROR, error, ex);

            ViewUtils.generateAlert("Import error", "Resources could not be validated.", error + "\n" + ex.getMessage()).showAndWait();

            throw ex;
        }

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

        Screen titleScreen = ViewUtils.loadScreenFromResource(ScreenCode.TITLE, this, gameController);

        // Show the scene containing the root layout.
        Scene scene = new Scene(titleScreen.pane);

        if (cssEnabled) {
            scene.getStylesheets().add("style.css");
        }

        getPrimaryStage().setScene(scene);
        getPrimaryStage().show();

        ((TitleScreenController) titleScreen.controller).setImage(loadImageFromPath("images/title.jpg"));

    }

    public Image loadImageFromPath(String imagePath) {
//        BufferedImage bufferedImage = ImageIO.read(getClass().getResourceAsStream("images/title.jpg"));
//        return SwingFXUtils.toFXImage(bufferedImage, null);
        return ViewUtils.loadImage(getClass().getResourceAsStream(imagePath));
    }

    public void startGame() throws IOException {
        Runnable task = this::startGameTask;
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private void startGameTask() {

        Platform.runLater(() -> {
            primaryStage.getScene().setCursor(Cursor.WAIT);
        });

        gameController.initializeEvents();

        try {
            loadScreens();
        } catch (IOException ex) {
            logger.log(Level.ERROR, "Error preparing screens", ex);
        }

        Platform.runLater(() -> {
            show(ScreenCode.BROWSER);
            updateLabels();
        });

        preRun();

        setRootLayoutButtonDisable(false);
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    private void preRun() {
        preRun = PRE_RUN_DAYS > 0;

        //number of days to run automatically at start of game
        for (int i = 0; i < PRE_RUN_DAYS; i++) {
            try {
                nextDay();
            } catch (IOException ex) {
                logger.log(Level.ERROR, "Error during pre run days", ex);
            }

        }

        preRun = false;
    }

    private void saveGame() throws IOException {

        Kryo kryo = new Kryo();

        try (Output output = new Output(new FileOutputStream("saveGame.bin"))) {
            kryo.writeObject(output, gameController);
        } catch (IOException ex) {
            logger.log(Level.ERROR, ex);
            ViewUtils.generateAlert("Error", "Error while saving the game", ex.getLocalizedMessage()).showAndWait();
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

    private void loadScreens() throws IOException {
        //this will load into memory all the screens that we will be switching between
        //so we aren't creating a new screen each time
        List<ScreenCode> screensToLoad = new ArrayList<>(Arrays.asList(ScreenCode.FINANCIAL,
                ScreenCode.CALENDAR,
                ScreenCode.BROWSER,
                ScreenCode.EVENT,
                ScreenCode.WORKER_OVERVIEW,
                ScreenCode.NEXT_DAY_SCREEN,
                ScreenCode.RESULTS
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
        screens.add(ViewUtils.loadScreenFromResource(ScreenCode.ROOT, this, gameController));
        Scene scene = new Scene(ViewUtils.getByCode(screens, ScreenCode.ROOT).pane, currentStageWidth, currentStageHeight);
        if (cssEnabled) {
            scene.getStylesheets().add("style.css");
        }
        getPrimaryStage().setScene(scene);
        getPrimaryStage().show();
    }

    public Screen show(ScreenCode code) {
        if (currentScreen != null) {
            currentScreen.controller.focusLost();
        }

        Screen screen = ViewUtils.getByCode(screens, code);
        currentScreen = screen;

        ((BorderPane) ViewUtils.getByCode(screens, ScreenCode.ROOT).pane).setCenter(screen.pane);
        ((RootLayoutController) ViewUtils.getByCode(screens, ScreenCode.ROOT).controller).updateSelectedButton(code);

        screen.controller.updateLabels();

        if (code.equals(ScreenCode.CALENDAR)) {
            ((CalendarController) screen.controller).setCurrent(gameController.getDateManager().today());
        }

        updateLabels();
        return screen;

    }

    public void show(ScreenCode code, Object obj) {
        Screen screen = show(code);
        screen.controller.setCurrent(obj);
    }

    /*
    loads and shows a start game screen
    currently these methods are combined because we only do it once
     */
    public void showStartGameScreen() throws IOException {

        Screen startGameScreen = ViewUtils.loadScreenFromResource(ScreenCode.START, this, gameController);
        ((BorderPane) ViewUtils.getByCode(screens, ScreenCode.ROOT).pane).setCenter(startGameScreen.pane);
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

    public void updateLabels(ScreenCode code) {
        Screen screen = ViewUtils.getByCode(screens, code);
        if (screen != null) {
            screen.controller.updateLabels();
        }

    }

    public void nextDay() throws IOException {

        if (preRun) {
            gameController.nextDay();
            saveGame();
        } else {
            NextDayScreenController nextDay = (NextDayScreenController) ViewUtils.getByCode(screens, ScreenCode.NEXT_DAY_SCREEN).controller;
            RootLayoutController root = (RootLayoutController) ViewUtils.getByCode(screens, ScreenCode.ROOT).controller;

            nextDay.setLoadingMessage("Loading...");
            show(ScreenCode.NEXT_DAY_SCREEN);
            root.setButtonsDisable(true);
            primaryStage.getScene().setCursor(Cursor.WAIT);

            Thread thread = new Thread(nextDayTask(nextDay, root));
            thread.setDaemon(true);
            thread.start();

        }

        logger.log(Level.INFO, "day: " + gameController.getDateManager().today());
    }

    private Task<Void> nextDayTask(NextDayScreenController nextDay, RootLayoutController root) {

        Task<Void> task = new Task<Void>() {

            @Override
            public Void call() throws InterruptedException {

                gameController.nextDay();

                try {
                    saveGame();
                } catch (IOException ex) {
                    logger.log(Level.ERROR, "Problem saving Game", ex);

                }
                return null;
            }
        };

        task.setOnSucceeded((WorkerStateEvent t) -> {

            nextDay.setLoadingMessage("Loading...\nComplete!");
            updateLabels();
            root.setButtonsDisable(false);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
        });

        return task;
    }

    /*
    disable buttons, for when we're starting up the game
    and don't yet want to allow screen switching freedom
     */
    public void setRootLayoutButtonDisable(boolean disable) {
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

    /**
     * @return the resx
     */
    public ResourceBundle getResx() {
        return resx;
    }

    /**
     * @return the currentStageSize
     */
    public double getCurrentStageWidth() {
        return currentStageWidth;
    }

    /**
     * @return the currentStageHeight
     */
    public double getCurrentStageHeight() {
        return currentStageHeight;
    }

    /**
     * @return the randomGame
     */
    public boolean isRandomGame() {
        return randomGame;
    }

}
