package org.migdb.migdbclient.main;

import java.net.URL;
import java.net.URLClassLoader;

import org.migdb.migdbclient.config.Configurations;
import org.migdb.migdbclient.config.FxmlPath;
import org.migdb.migdbclient.config.ImagePath;
import org.migdb.migdbclient.models.dao.SqliteDAO;
import org.migdb.migdbclient.config.fromResources.threads.CheckInternetConnectivity;
import org.migdb.migdbclient.config.fromResources.threads.ConnectivityIsShowInstance;
import org.migdb.migdbclient.views.root.RootLayoutController;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class MainApp extends Application {

	private BorderPane rootLayout;
	private AnchorPane mainAnchorpane = new AnchorPane();
	private VBox splashLayout;
	private VBox progressTextLayout;
	private ProgressBar loadProgress;
	private Label progressText;
	private Stage primaryStage;
	private static final int SPLASH_WIDTH = 520;

	CheckInternetConnectivity runner = new CheckInternetConnectivity();

	@FXML
	private Button internetConnectivityOkButton;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void init() {
		System.out.println("Initializing application...");
		ImageView splash = new ImageView(new Image(ImagePath.SPLASHIMAGE.getPath()));
		loadProgress = new ProgressBar();
		loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
		progressText = new Label("Will find friends for peanuts . . .");
		progressText.setStyle("-fx-text-fill : white");
		splashLayout = new VBox();
		progressTextLayout = new VBox();
		splashLayout.getChildren().addAll(splash, loadProgress);
		progressTextLayout.getChildren().add(progressText);
		progressText.setAlignment(Pos.CENTER);
		splashLayout.setStyle("-fx-background-color: transparent; ");
		progressTextLayout.setLayoutX(5);
		progressTextLayout.setLayoutY(280);
		mainAnchorpane.getChildren().addAll(splashLayout, progressTextLayout);
		splashLayout.setEffect(new DropShadow());
		ConnectivityIsShowInstance.INSTANCE.setShow(false);


		Configurations config = new Configurations();
		System.out.println("Creating app folder...");
		config.createAppFolder();
		System.out.println("Creating connection table...");
		config.createConnectionTable();
		System.out.println("Inserting operators...");
		config.insertOperators();
	}

	@Override
	public void start(final Stage initStage) throws Exception {
		final Task<ObservableList<String>> jarTask = new Task<ObservableList<String>>() {
			@Override
			protected ObservableList<String> call() throws InterruptedException {
				ObservableList<String> foundJars = FXCollections.<String>observableArrayList();
				ObservableList<String> availableJars = FXCollections.observableArrayList();

				ClassLoader loader = ClassLoader.getSystemClassLoader();
				System.out.println("ClassLoader: " + loader.getClass());
				if (loader instanceof URLClassLoader) {
					URL[] urls = ((URLClassLoader) loader).getURLs();
					for (URL url : urls) {
						String[] jars = (url.getFile()).split("/");
						availableJars.add((jars[jars.length - 1]).toString());
					}
				} else {
					System.err.println("ClassLoader is not a URLClassLoader.");
				}

				updateMessage("Finding . . .");
				for (int i = 0; i < availableJars.size(); i++) {
					Thread.sleep(400);
					updateProgress(i + 1, availableJars.size());
					String nextFriend = availableJars.get(i);
					foundJars.add(nextFriend);
					updateMessage("Loading . . . " + nextFriend);
				}
				Thread.sleep(400);
				updateMessage("MigDB loaded.");

				return foundJars;
			}
		};

		jarTask.setOnFailed(event -> {
			System.err.println("Error occurred during task execution:");
			jarTask.getException().printStackTrace();
		});

		showSplash(initStage, jarTask, () -> showDbPathChooserStage());
		new Thread(jarTask).start();
	}

	public void showMainStage() {
		try {
			System.out.println("Starting main stage...");

			primaryStage = new Stage(StageStyle.DECORATED);
			primaryStage.setTitle("MigDB");
			primaryStage.getIcons().add(new Image(ImagePath.FAVICON.getPath()));

			FXMLLoader loader = new FXMLLoader();
			System.out.println("FXML path: " + FxmlPath.ROOTLAYOUT.getPath());
			System.out.println("Resolved URL: " + MainApp.class.getResource(FxmlPath.ROOTLAYOUT.getPath()));
			loader.setLocation(MainApp.class.getResource(FxmlPath.ROOTLAYOUT.getPath()));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					event.consume();
					RootLayoutController rootCntrlr = new RootLayoutController();
					rootCntrlr.closePlatform();
					runner.stop();
				}
			});
		} catch (Exception e) {
			System.err.println("Error occurred in showMainStage:");
			e.printStackTrace();
		}
	}

	private void showSplash(final Stage initStage, Task<?> task, InitCompletionHandler initCompletionHandler) {
		progressText.textProperty().bind(task.messageProperty());
		loadProgress.progressProperty().bind(task.progressProperty());
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				loadProgress.progressProperty().unbind();
				loadProgress.setProgress(1);
				initStage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), mainAnchorpane);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> initStage.hide());
				fadeSplash.play();

				initCompletionHandler.complete();
			}
		});

		task.setOnFailed(event -> {
			System.err.println("Splash task failed:");
			task.getException().printStackTrace();
		});

		Scene splashScene = new Scene(mainAnchorpane, Color.TRANSPARENT);
		splashScene.getStylesheets().add(getClass().getResource("/css/custom.css").toExternalForm());
		initStage.setScene(splashScene);
		initStage.getIcons().add(new Image(ImagePath.FAVICON.getPath()));
		initStage.centerOnScreen();
		initStage.initStyle(StageStyle.TRANSPARENT);
		initStage.setAlwaysOnTop(true);
		initStage.show();
	}

	public interface InitCompletionHandler {
		void complete();
	}

	private void showDbPathChooserStage() {
		try {
			SqliteDAO dao = new SqliteDAO();
			System.out.println("Checking MongoDbPath existence...");
			if (!dao.isPathExist("MongoDbPath")) {
				System.out.println("MongoDbPath not found. Opening path chooser...");
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(MainApp.class.getResource(FxmlPath.MONGODBPATHBROWSE.getPath()));
				rootLayout = loader.load();
				Scene dbPathChooser = new Scene(rootLayout);
				primaryStage = new Stage();
				primaryStage.setScene(dbPathChooser);
				primaryStage.setResizable(false);
				primaryStage.show();
			} else {
				System.out.println("MongoDbPath found. Proceeding to main stage...");
				showMainStage();
			}
		} catch (Exception e) {
			System.err.println("Error in showDbPathChooserStage:");
			e.printStackTrace();
		}
	}

	@FXML
	private void internetConnectivityOkButtonClickEvent() {
		Stage stage = (Stage) internetConnectivityOkButton.getScene().getWindow();
		stage.close();
		ConnectivityIsShowInstance.INSTANCE.setShow(false);
	}
}
