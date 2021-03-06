package seng202.team7.Controllers.HelpWindowControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import seng202.team7.Windows.HelpWindow.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is the controller for the Help Window.
 * @author Connor McEwan-McDowall
 */
public class HelpWindowController implements Initializable{
    @FXML private TreeView<String> helpNavigationTree;
    @FXML private AnchorPane helpCenterAnchorPane;

    private HomeHelp homeHelp;
    private RoutePlannerHelp routePlannerHelp;
    private MapViewerHelp mapViewerHelp;
    private RetailerDataViewerHelp retailerHelp;
    private WifiDataViewerHelp wifiHelp;
    private TripDataViewerHelp tripHelp;
    private DataEntryHelp dataEntryHelp;
    private MapAnalyticHelp mapAnalyticHelp;
    private TripAnalyticHelp graphHelp;
    private String currentWindow;

    /**
     * Runs as the program initially starts. Initialises each of the custom
     * JavaFX panels (loads them into memory) and calls for the help navigation bar
     * to be populated. The help home screen is shown initially as default.
     * @param location Unused parameter (There to fit signature)
     * @param resources Unused parameter (There to fit signature)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set home window as initial window
        homeHelp = new HomeHelp();
        helpCenterAnchorPane.getChildren().add(homeHelp);
        currentWindow = "Home";

        // Initialize different custom panels (Saves loading panel again every time panel is changed)
        routePlannerHelp = new RoutePlannerHelp();
        mapViewerHelp = new MapViewerHelp();
        retailerHelp = new RetailerDataViewerHelp();
        wifiHelp = new WifiDataViewerHelp();
        tripHelp = new TripDataViewerHelp();
        dataEntryHelp = new DataEntryHelp();
        mapAnalyticHelp = new MapAnalyticHelp();
        graphHelp = new TripAnalyticHelp();

        populateNavigationBar();
    }

    /**
     * Populates the navigation bar with all necessary tree items. Also creates
     * an event listener for whenever a tree item in the navigation bar is clicked.
     * This listener calls the changeMainScreen function when tree selection changes.
     */
    private void populateNavigationBar() {
        // Tree root
        TreeItem<String> root = new TreeItem<>();
        root.setExpanded(true);

        // Home branch
        makeBranch("Home", root);

        // Route Planner branch
        makeBranch("Route Planning", root);

        // Map Viewer branch
        makeBranch("Map Viewer", root);

        // Analytics branch
        TreeItem<String> analyticsBranch = makeBranch("Analytics", root);
        makeBranch("Map View", analyticsBranch);
        makeBranch("Graph View", analyticsBranch);

        // Raw Data Viewer branch
        TreeItem<String> dataViewerBranch = makeBranch("Data Viewer", root);
        makeBranch("Retailer", dataViewerBranch);
        makeBranch("Trip", dataViewerBranch);
        makeBranch("Wifi", dataViewerBranch);

        // Data Entry branch
        makeBranch("Data Entry", root);

        helpNavigationTree.setRoot(root);
        helpNavigationTree.setShowRoot(false);

        helpNavigationTree.getSelectionModel().selectedItemProperty()
                .addListener((v, oldValue, newValue) -> {
                    String currentSelection = newValue.getValue();
                    if (!(helpNavigationTree.toString().equals(currentSelection))) {
                        changeMainScreen(currentSelection);
                    }
                });
    }

    /**
     * Creates a new TreeItem with Strings representing a branch. The new branch has the
     * text title in it and is a child of parent.
     * @param title A String representing the text title for the branch.
     * @param parent The parent TreeItem of new branch to be created.
     * @return The newly created branch (TreeItem).
     */
    private TreeItem<String> makeBranch(String title, TreeItem<String> parent) {
        TreeItem<String> item = new TreeItem<>(title);
        parent.getChildren().add(item);
        return item;
    }

    /**
     * Changes the window currently shown in the helpCenterAnchorPane to be newScreen
     * @param newScreen The new window that will replace the current window.
     */
    private void changeMainScreen(String newScreen) {
        if (!newScreen.equals("Analytics") && !newScreen.equals("Data Viewer")) {
            // Removes the current window to make room for the new window
            switch (currentWindow) {
                case "Home": removeMainScreen(homeHelp); break;
                case "Route Planning": removeMainScreen(routePlannerHelp); break;
                case "Map Viewer": removeMainScreen(mapViewerHelp); break;
                case "Analytics": break; // No screen change
                case "Map View": removeMainScreen(mapAnalyticHelp); break;
                case "Graph View": removeMainScreen(graphHelp); break;
                case "Data Viewer": break; // No screen change
                case "Retailer": removeMainScreen(retailerHelp); break;
                case "Trip": removeMainScreen(tripHelp); break;
                case "Wifi": removeMainScreen(wifiHelp); break;
                case "Data Entry": removeMainScreen(dataEntryHelp); break;
                default: System.out.println("ERROR: No such removal handle exists");
            }
            // Add the new window where the old one was
            switch (newScreen) {
                case "Home": setMainScreen(homeHelp); break;
                case "Route Planning": setMainScreen(routePlannerHelp); break;
                case "Map Viewer": setMainScreen(mapViewerHelp); break;
                case "Analytics": break; // No screen change
                case "Map View": setMainScreen(mapAnalyticHelp); break;
                case "Graph View": setMainScreen(graphHelp); break;
                case "Data Viewer": break; // No screen change
                case "Retailer": setMainScreen(retailerHelp); break;
                case "Trip": setMainScreen(tripHelp); break;
                case "Wifi": setMainScreen(wifiHelp); break;
                case "Data Entry": setMainScreen(dataEntryHelp); break;
                default: System.out.println("ERROR: No such set handle exists");
            }
            // Update window tracker
            currentWindow = newScreen;
        }
    }

    /**
     * Removes the current window inside helpCenterAnchorPane
     * @param window The window to be removed
     */
    private void removeMainScreen(Node window) {
        helpCenterAnchorPane.getChildren().remove(window);
    }

    /**
     * Adds the current window inside helpCenterAnchorPane
     * @param window The window to be added
     */
    private void setMainScreen(Node window) {
        helpCenterAnchorPane.getChildren().add(window);
    }
}
