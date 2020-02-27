package openwrestling;

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
import lombok.Getter;
import openwrestling.database.Database;
import openwrestling.file.Import;
import openwrestling.model.SegmentItem;
import openwrestling.model.controller.GameController;
import openwrestling.model.segmentEnum.Gender;
import openwrestling.view.RootLayoutController;
import openwrestling.view.calendar.controller.CalendarController;
import openwrestling.view.news.controller.NewsScreenController;
import openwrestling.view.start.controller.TitleScreenController;
import openwrestling.view.utility.GameScreen;
import openwrestling.view.utility.ScreenCode;
import openwrestling.view.utility.ViewUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {

    public static final String CONTACT = "OpenWrestling@gmail.com or /u/OpenWrestling on Reddit";
    public static final String VERSION = "0.4.0";

    private static final int WINDOW_MIN_WIDTH = 1600;
    private static final int WINDOW_MIN_HEIGHT = 900;
    private static final int PRE_RUN_DAYS = 0;
    private final transient Logger logger;
    @Getter
    private Stage primaryStage;
    private GameController gameController;
    private final List<GameScreen> screens;
    private File picsFolder;
    private File logosFolder;
    private File dataFolder;
    private boolean preRun = false;
    @Getter
    private boolean randomGame;
    private final boolean cssEnabled;
    @Getter
    private final ResourceBundle resx;
    private GameScreen currentScreen;
    @Getter
    private double currentStageWidth;
    private double currentStageHeight;

    public static String dbURL;

    public static void main(String[] args) {
        launch(args);
    }

    public MainApp() {
        this.screens = new ArrayList<>();

        this.cssEnabled = true;
        logger = LogManager.getLogger(getClass());
        Configurator.setRootLevel(Level.DEBUG);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("Caught " + e);
            System.out.println(ExceptionUtils.getStackTrace(e));
        });
        logger.log(Level.INFO, "Logger online. Running version " + VERSION);

        Locale locale = new Locale("en", "US");

        resx = ResourceBundle.getBundle("openwrestling.Language", locale);

        currentStageWidth = WINDOW_MIN_WIDTH;
    }

    @Override
    public void start(Stage stage) throws IOException {

        primaryStage = stage;
        primaryStage.setTitle("Open Wrestling " + VERSION);
        primaryStage.setMinWidth(WINDOW_MIN_WIDTH);
        primaryStage.setMinHeight(WINDOW_MIN_HEIGHT);
        //primaryStage.setResizable(false);

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
        if (connectToNewDatabase()) {
            randomGame = true;
            gameController = new GameController(true);
            initRootLayout();
            showStartGameScreen();
        }
    }

    public void newImportGame(File dataFolder, File picsFolder, File logosFolder) throws Exception {
        if (connectToNewDatabase()) {
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
    }

    public void continueGame(File dbFile) {
        Database.setDbFile(dbFile);
        gameController = new GameController(false);
        gameController.loadGameDataFromDatabase();
        initRootLayout();
        continueGame();
    }


    private void showTitleScreen() {

        GameScreen titleScreen = ViewUtils.loadScreenFromFXML(ScreenCode.TITLE, this, gameController);

        // Show the scene containing the root layout.
        Scene scene = new Scene(titleScreen.pane);

        if (cssEnabled) {
            scene.getStylesheets().add("style.css");
        }

        primaryStage.setScene(scene);
        primaryStage.show();

        ((TitleScreenController) titleScreen.controller).setImage(loadImageFromPath("images/title.jpg"));

    }

    public Image loadImageFromPath(String imagePath) {
        return ViewUtils.loadImage(getClass().getResourceAsStream(imagePath));
    }

    public Image getDefaultWorkerImage(SegmentItem segmentItem) {
        if (segmentItem.getGender().equals(Gender.MALE)) {
            return loadImageFromPath("images/workerDefaultMale.jpg");
        } else {
            return loadImageFromPath("images/workerDefaultFemale.jpg");
        }

    }

    public void continueGame() {
        Runnable task = this::continueGameTask;
        Thread.UncaughtExceptionHandler h = (th, ex) -> {
            System.out.println("Uncaught exception: " + ex);
            System.out.println(ExceptionUtils.getStackTrace(ex));
        };
        Thread backgroundThread = new Thread(task);
        backgroundThread.setUncaughtExceptionHandler(h);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    public void startGame() {
        Runnable task = this::startGameTask;
        Thread.UncaughtExceptionHandler h = (th, ex) -> {
            System.out.println("Uncaught exception: " + ex);
            System.out.println(ExceptionUtils.getStackTrace(ex));
        };
        Thread backgroundThread = new Thread(task);
        backgroundThread.setUncaughtExceptionHandler(h);
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private void startGameTask() {

        Platform.runLater(() -> {
            primaryStage.getScene().setCursor(Cursor.WAIT);
        });

        loadScreens();

        gameController.initializeGameData();
        preRun();

        Platform.runLater(() -> {
            show(ScreenCode.BROWSER);
            updateLabels();
        });

        setRootLayoutButtonDisable(false);
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    private void continueGameTask() {

        Platform.runLater(() -> {
            primaryStage.getScene().setCursor(Cursor.WAIT);
        });

        loadScreens();

        Platform.runLater(() -> {
            show(ScreenCode.BROWSER);
            updateLabels();
        });

        setRootLayoutButtonDisable(false);
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    private void preRun() {
        long time = System.currentTimeMillis();
        preRun = PRE_RUN_DAYS > 0;
        List<Long> dayTimes = new ArrayList<>();

        //number of days to run automatically at start of game
        for (int i = 0; i < PRE_RUN_DAYS; i++) {
            long dayStartTime = System.currentTimeMillis();
            nextDay();
            dayTimes.add(System.currentTimeMillis() - dayStartTime);
        }

        preRun = false;
        logger.log(Level.DEBUG, "pre run for " + PRE_RUN_DAYS + " days took " +
                (System.currentTimeMillis() - time));
        if (PRE_RUN_DAYS > 0) {
            Profile profile = new Profile();
            profile.printEval(gameController, dayTimes);
        }
    }

    private void loadScreens() {
        //this will load into memory all the screens that we will be switching between
        //so we aren't creating a new screen each time
        List<ScreenCode> screensToLoad = new ArrayList<>(Arrays.asList(ScreenCode.FINANCIAL,
                ScreenCode.CALENDAR,
                ScreenCode.BROWSER,
                ScreenCode.EVENT,
                ScreenCode.WORKER_OVERVIEW,
                ScreenCode.NEWS,
                ScreenCode.RESULTS
        ));

        for (ScreenCode screen : screensToLoad) {
            screens.add(ViewUtils.loadScreenFromFXML(screen, this, gameController));
        }
    }

    /**
     * Initializes the root layout.
     *
     * @throws java.io.IOException
     */
    public void initRootLayout() {
        screens.add(ViewUtils.loadScreenFromFXML(ScreenCode.ROOT, this, gameController));
        Scene scene = new Scene(ViewUtils.getByCode(screens, ScreenCode.ROOT).pane, currentStageWidth, currentStageHeight);
        if (cssEnabled) {
            scene.getStylesheets().add("style.css");
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public GameScreen show(ScreenCode code) {
        if (currentScreen != null) {
            currentScreen.controller.focusLost();
        }

        GameScreen screen = ViewUtils.getByCode(screens, code);
        currentScreen = screen;

        ((BorderPane) ViewUtils.getByCode(screens, ScreenCode.ROOT).pane).setCenter(screen.pane);
        ((RootLayoutController) ViewUtils.getByCode(screens, ScreenCode.ROOT).controller).updateSelectedButton(code);

        //screen.controller.updateLabels();
        if (code.equals(ScreenCode.CALENDAR)) {
            ((CalendarController) screen.controller).setCurrent(gameController.getDateManager().today());
        }

        updateLabels();
        return screen;

    }

    public void show(ScreenCode code, Object obj) {
        GameScreen screen = show(code);
        screen.controller.setCurrent(obj);
    }

    public void showStartGameScreen() {

        GameScreen startGameScreen = ViewUtils.loadScreenFromFXML(ScreenCode.START, this, gameController);
        ((BorderPane) ViewUtils.getByCode(screens, ScreenCode.ROOT).pane).setCenter(startGameScreen.pane);
    }

    /*
    calls the root layout to update the labels
    most/all controllers have a link to the main app
    so if they cause any change that needs to be reflected
    in labels outside of their screens it can be handled here
     */
    private void updateLabels() {
        logger.log(Level.DEBUG, "updateLabels");
        for (GameScreen screen : screens) {
            if (screen.code.alwaysUpdate()) {
                updateLabels(screen.code);
            }
        }
    }

    public void updateLabels(ScreenCode code) {
        GameScreen screen = ViewUtils.getByCode(screens, code);
        GameScreen root = ViewUtils.getByCode(screens, ScreenCode.ROOT);
        if (screen != null) {
            screen.controller.updateLabels();
            root.controller.updateLabels();
        }

    }

    public void nextDay() {

        if (preRun) {
            gameController.nextDay();
        } else {
            NewsScreenController nextDay = (NewsScreenController) ViewUtils.getByCode(screens, ScreenCode.NEWS).controller;
            RootLayoutController root = (RootLayoutController) ViewUtils.getByCode(screens, ScreenCode.ROOT).controller;

            show(ScreenCode.NEWS);
            root.setButtonsDisable(true);
            primaryStage.getScene().setCursor(Cursor.WAIT);

            Thread.UncaughtExceptionHandler h = (th, ex) -> {
                System.out.println("Uncaught exception: " + ex);
                System.out.println(ExceptionUtils.getStackTrace(ex));
            };
            Thread thread = new Thread(nextDayTask(nextDay, root));
            thread.setUncaughtExceptionHandler(h);
            thread.setDaemon(true);
            thread.start();

        }

        logger.log(Level.INFO, "day: " + gameController.getDateManager().today());
    }

    private Task<Void> nextDayTask(NewsScreenController nextDayScreenController, RootLayoutController root) {

        Task<Void> task = new Task<>() {

            @Override
            public Void call() {
                try {
                    gameController.nextDay();
                } catch (Exception ex) {
                    logger.log(Level.ERROR, "Problem saving Game", ex);
                    throw ex;
                }
                return null;
            }
        };

        task.setOnSucceeded((WorkerStateEvent t) -> {
            logger.log(Level.DEBUG, "onSucceeded");
            root.setButtonsDisable(false);
            nextDayScreenController.nextDay();
            updateLabels();
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

    private boolean connectToNewDatabase() {
        File fileName = ViewUtils.createDatabaseDialog(primaryStage);
        if (fileName == null) {
            return false;
        }
        dbURL = Database.createNewDatabase(fileName);
        Database.connect(dbURL);
        return true;
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

}
