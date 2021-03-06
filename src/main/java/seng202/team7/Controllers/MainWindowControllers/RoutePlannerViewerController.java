package seng202.team7.Controllers.MainWindowControllers;

import com.sun.javafx.webkit.WebConsoleListener;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import seng202.team7.DataTypes.Location;
import seng202.team7.DataTypes.Retailer;
import seng202.team7.DataTypes.StaticVariables;
import seng202.team7.DataTypes.Wifi;
import seng202.team7.Database.DatabaseEmptyException;
import seng202.team7.Database.DatabaseRetriever;
import seng202.team7.JSHandling.RouteHandler;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller for the route planner window that holds the buttons' functions
 * and the javascript bridge
 * @author Joshua Meneghini
 */
public class RoutePlannerViewerController implements Initializable{

    @FXML private WebView webViewMap1;
    @FXML private ComboBox<String> wifiTypeCB;
    @FXML private ComboBox<String> retailerTypeCB;
    @FXML private Text errorLabel;

    private WebEngine webEngine1;
    private JSObject jsBridge1;
    private RouteHandler routeHandler;

    /**
     * Initialises the widgets and bridge in the route planner
     * @param url Required parameter that is not used
     * @param rb Required parameter that is not used
     */
    public void initialize(URL url, ResourceBundle rb) {
        String[] wifiTypes = {"All", "Free", "Limited Free", "Partner Site"};
        wifiTypeCB.getItems().addAll(wifiTypes);
        wifiTypeCB.getSelectionModel().select(0);

        String[] retailerTypes = {"All", "Food", "Nightlife", "Shopping", "Personal/Professional Services", "Visitor Services", "Community Resources"};
        retailerTypeCB.getItems().addAll(retailerTypes);
        retailerTypeCB.getSelectionModel().select(0);

        webEngine1 = webViewMap1.getEngine();

        webEngine1.setJavaScriptEnabled(true);
        webEngine1.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                jsBridge1 = (JSObject) webEngine1.executeScript("window");
                routeHandler = new RouteHandler();
                jsBridge1.setMember("bridge", routeHandler);
                //jsBridge1 = (JSObject) webEngine1.executeScript("getJsConnector();");
            }
        });
        webEngine1.load(getClass().getClassLoader().getResource("HTMLFiles/RoutePlanner.html").toExternalForm());

        WebConsoleListener.setDefaultListener(new WebConsoleListener() {
            @Override
            public void messageAdded(WebView webView, String message, int lineNumber, String sourceId) {
                //System.out.println("Console: [" + sourceId + ":" + lineNumber + "] " + message);
            }
        });
    }

    /**
     * Clears the wifi markers from the map
     */
    public void clearWifi() {
        errorLabel.setVisible(false);
        jsBridge1.call("loadWifiType", "Clear");
    }

    /**
     * Clears the retailer markers from the map
     */
    public void clearRetailer() {
        errorLabel.setVisible(false);
        jsBridge1.call("loadRetailerType", "Clear");
    }

    /**
     * Displays the Wi-Fi locations of the type selected in the combobox
     */
    public void displayWifis() {
        errorLabel.setVisible(false);
        jsBridge1.call("loadWifiType", wifiTypeCB.getSelectionModel().getSelectedItem());
    }

    /**
     * Displays the retailer locations of the type selected in the combobox
     */
    public void displayRetailers() {
        errorLabel.setVisible(false);
        jsBridge1.call("loadRetailerType", retailerTypeCB.getSelectionModel().getSelectedItem());
    }

    /**
     * Adds the nearest Wi-Fi to the selected Location(s) on the map
     */
    public void addWifi() {
        errorLabel.setVisible(false);
        Integer points = (Integer) jsBridge1.call("getPoints");
        if (points == 0) {
            errorLabel.setText("Please select a location on the map first.");
            errorLabel.setVisible(true);
        } else if (points == 1) {
            String start = (String) jsBridge1.call("getStart");
            Location startLocation = new Location(Double.valueOf(start.split(",")[0]), Double.valueOf(start.split(",")[1]));
            try {
                Wifi nearestWifi = findNearestWifi(startLocation);
                routeHandler.setLocationToAdd(nearestWifi);
                jsBridge1.call("addWifiToRoute");
            } catch (DatabaseEmptyException e) {
                errorLabel.setText("Please load some Wi-Fi locations to the database.");
                errorLabel.setVisible(true);
            }
        } else {
            String start = (String) jsBridge1.call("getStart");
            String end = (String) jsBridge1.call("getEnd");
            Location startLocation = new Location(Double.valueOf(start.split(",")[0]), Double.valueOf(start.split(",")[1]));
            Location endLocation = new Location(Double.valueOf(end.split(",")[0]), Double.valueOf(end.split(",")[1]));
            try {
                Wifi nearestWifi = findNearestWifi(startLocation, endLocation);
                routeHandler.setLocationToAdd(nearestWifi);
                jsBridge1.call("addWifiToRoute");
            } catch (DatabaseEmptyException e) {
                errorLabel.setText("Please load some Wi-Fi locations to the database.");
                errorLabel.setVisible(true);
            }
        }
    }

    /**
     * Adds the nearest retailer to the selected Location(s) on the map
     */
    public void addRetailer() {
        errorLabel.setVisible(false);
        Integer points = (Integer) jsBridge1.call("getPoints");
        if (points == 0) {
            errorLabel.setText("Please select a location on the map first.");
            errorLabel.setVisible(true);
        } else if (points == 1) {
            String start = (String) jsBridge1.call("getStart");
            Location startLocation = new Location(Double.valueOf(start.split(",")[0]), Double.valueOf(start.split(",")[1]));
            try {
                Retailer nearestRetailer = findNearestRetailer(startLocation);
                routeHandler.setLocationToAdd(nearestRetailer);
                jsBridge1.call("addRetailerToRoute");
            } catch (DatabaseEmptyException e) {
                errorLabel.setText("Please load some Retailer locations to the database.");
                errorLabel.setVisible(true);
            }
        } else {
            String start = (String) jsBridge1.call("getStart");
            String end = (String) jsBridge1.call("getEnd");
            Location startLocation = new Location(Double.valueOf(start.split(",")[0]), Double.valueOf(start.split(",")[1]));
            Location endLocation = new Location(Double.valueOf(end.split(",")[0]), Double.valueOf(end.split(",")[1]));
            try {
                Retailer nearestRetailer = findNearestRetailer(startLocation, endLocation);
                routeHandler.setLocationToAdd(nearestRetailer);
                jsBridge1.call("addRetailerToRoute");
            } catch (DatabaseEmptyException e) {
                errorLabel.setText("Please load some Retailer locations to the database.");
                errorLabel.setVisible(true);
            }
        }
    }

    /**
     * Finds the nearest wifi location to a retailer object
     * @param point The point to search around
     * @return The nearest wifi object
     * @throws DatabaseEmptyException thrown when there are no wifi objects in the database
     */
    private Wifi findNearestWifi(Location point) throws DatabaseEmptyException{
        if (wifiInDatabase()) {
            DatabaseRetriever dbRetriever = new DatabaseRetriever();
            ArrayList<Wifi> wifiList = dbRetriever.queryWifi(StaticVariables.wifiByLocation(point.getLatitude(), point.getLongitude()));
            ArrayList<Wifi> filteredWifiList = new ArrayList<>();
            if (wifiTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                filteredWifiList = wifiList;
            } else {
                for (Wifi wifi : wifiList) {
                    if (wifi.getType() != null) {
                        if (wifi.getType().equals(wifiTypeCB.getSelectionModel().getSelectedItem())) {
                            filteredWifiList.add(wifi);
                        }
                    }
                }
            }
            double defaultDist = StaticVariables.defaultDist;
            while (filteredWifiList.isEmpty()) {
                defaultDist *= 2;
                wifiList = dbRetriever.queryWifi(StaticVariables.wifiByLocation(point.getLatitude(), defaultDist, point.getLongitude(), defaultDist));
                if (wifiTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                    filteredWifiList = wifiList;
                } else {
                    for (Wifi wifi : wifiList) {
                        if (wifi.getType() != null) {
                            if (wifi.getType().equals(wifiTypeCB.getSelectionModel().getSelectedItem())) {
                                filteredWifiList.add(wifi);
                            }
                        }
                    }
                }
            }
            double minDistance = -1;
            double currentDistance;
            int minIndex = 0;
            for (Wifi wifi : filteredWifiList) {
                currentDistance = StaticVariables.calculateDistance(point.getLatitude(), point.getLongitude(), wifi.getLatitude(), wifi.getLongitude());
                if (minDistance == -1) {
                    minDistance = currentDistance;
                } else if (minDistance > currentDistance) {
                    minDistance = currentDistance;
                    minIndex = filteredWifiList.indexOf(wifi);
                }
            }
            return filteredWifiList.get(minIndex);
        } else {
            throw new DatabaseEmptyException();
        }
    }

    /**
     * Finds the nearest wifi object to a straight line route
     * @param start The start location of the route
     * @param end The end location of the route
     * @return The nearest wifi object to the route
     * @throws DatabaseEmptyException thrown when there are no wifi objects in the database
     */
    private Wifi findNearestWifi(Location start, Location end) throws DatabaseEmptyException{
        if (wifiInDatabase()) {
            Location centre = new Location(((start.getLatitude() + end.getLatitude())/2), ((start.getLongitude() + end.getLongitude())/2));
            double latOffset = Math.abs(centre.getLatitude() - start.getLatitude()) + StaticVariables.defaultDist;
            double longOffset = Math.abs(centre.getLongitude() - start.getLongitude()) + StaticVariables.defaultDist;
            DatabaseRetriever dbRetriever = new DatabaseRetriever();
            ArrayList<Wifi> wifiList = dbRetriever.queryWifi(StaticVariables.wifiByLocation(centre.getLatitude(), latOffset, centre.getLongitude(), longOffset));
            ArrayList<Wifi> filteredWifiList = new ArrayList<>();
            double defaultDist = StaticVariables.defaultDist;
            if (wifiTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                filteredWifiList = wifiList;
            } else {
                for (Wifi wifi : wifiList) {
                    if (wifi.getType() != null) {
                        if (wifi.getType().equals(wifiTypeCB.getSelectionModel().getSelectedItem())) {
                            filteredWifiList.add(wifi);
                        }
                    }
                }
            }
            while (filteredWifiList.isEmpty()) {
                wifiList = dbRetriever.queryWifi(StaticVariables.wifiByLocation(centre.getLatitude(), latOffset + defaultDist, centre.getLongitude(), longOffset + defaultDist));
                defaultDist *= 2;
                if (wifiTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                    filteredWifiList = wifiList;
                } else {
                    for (Wifi wifi : wifiList) {
                        if (wifi.getType() != null) {
                            if (wifi.getType().equals(wifiTypeCB.getSelectionModel().getSelectedItem())) {
                                filteredWifiList.add(wifi);
                            }
                        }
                    }
                }
            }
            double minDistance = -1;
            double currentDistance;
            int minIndex = 0;
            for (Wifi wifi : filteredWifiList) {
                currentDistance = StaticVariables.calculateDistance(start, end, wifi.getLatitude(), wifi.getLongitude());
                if (minDistance == -1) {
                    minDistance = currentDistance;
                } else if (minDistance > currentDistance) {
                    minDistance = currentDistance;
                    minIndex = filteredWifiList.indexOf(wifi);
                }
            }
            return filteredWifiList.get(minIndex);
        } else {
            throw new DatabaseEmptyException();
        }
    }

    /**
     * Finds the nearest retailer object to a straight line route
     * @param start The start location of the route
     * @param end The end location of the route
     * @return The nearest retailer object to the route
     * @throws DatabaseEmptyException thrown when there are no retailer objects in the database
     */
    private Retailer findNearestRetailer(Location start, Location end) throws DatabaseEmptyException{
        if (retailerInDatabase()) {
            Location centre = new Location(((start.getLatitude() + end.getLatitude())/2), ((start.getLongitude() + end.getLongitude())/2));
            double latOffset = Math.abs(centre.getLatitude() - start.getLatitude()) + StaticVariables.defaultDist;
            double longOffset = Math.abs(centre.getLongitude() - start.getLongitude()) + StaticVariables.defaultDist;
            DatabaseRetriever dbRetriever = new DatabaseRetriever();
            ArrayList<Retailer> retailerList = dbRetriever.queryRetailer(StaticVariables.retailerByLocation(centre.getLatitude(), latOffset, centre.getLongitude(), longOffset));
            ArrayList<Retailer> filteredRetailerList = new ArrayList<>();
            double defaultDist = StaticVariables.defaultDist;
            if (retailerTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                filteredRetailerList = retailerList;
            } else {
                for (Retailer retailer : retailerList) {
                    if (retailer.getTypeID() != null) {
                        if (retailer.getTypeID().equals(retailerTypeCB.getSelectionModel().getSelectedItem())) {
                            filteredRetailerList.add(retailer);
                        }
                    }
                }
            }
            while (filteredRetailerList.isEmpty()) {
                retailerList = dbRetriever.queryRetailer(StaticVariables.retailerByLocation(centre.getLatitude(), latOffset + defaultDist, centre.getLongitude(), longOffset + defaultDist));
                defaultDist *= 2;
                if (retailerTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                    filteredRetailerList = retailerList;
                } else {
                    for (Retailer retailer : retailerList) {
                        if (retailer.getTypeID() != null) {
                            if (retailer.getTypeID().equals(retailerTypeCB.getSelectionModel().getSelectedItem())) {
                                filteredRetailerList.add(retailer);
                            }
                        }
                    }
                }
            }
            double minDistance = -1;
            double currentDistance;
            int minIndex = 0;
            for (Retailer retailer : filteredRetailerList) {
                currentDistance = StaticVariables.calculateDistance(start, end, retailer.getLatitude(), retailer.getLongitude());
                if (minDistance == -1) {
                    minDistance = currentDistance;
                } else if (minDistance > currentDistance) {
                    minDistance = currentDistance;
                    minIndex = retailerList.indexOf(retailer);
                }
            }
            return retailerList.get(minIndex);
        } else {
            throw new DatabaseEmptyException();
        }
    }

    /**
     * Finds the nearest retailer location to a retailer object
     * @param point The point to search around
     * @return The nearest retailer object
     * @throws DatabaseEmptyException thrown when there are no retailer objects in the database
     */
    private Retailer findNearestRetailer(Location point) throws DatabaseEmptyException{
        if (retailerInDatabase()) {
            DatabaseRetriever dbRetriever = new DatabaseRetriever();
            ArrayList<Retailer> retailerList = dbRetriever.queryRetailer(StaticVariables.retailerByLocation(point.getLatitude(), point.getLongitude()));
            ArrayList<Retailer> filteredRetailerList = new ArrayList<>();
            double defaultDist = StaticVariables.defaultDist;
            if (retailerTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                filteredRetailerList = retailerList;
            } else {
                for (Retailer retailer : retailerList) {
                    if (retailer.getTypeID() != null) {
                        if (retailer.getTypeID().equals(retailerTypeCB.getSelectionModel().getSelectedItem())) {
                            filteredRetailerList.add(retailer);
                        }
                    }
                }
            }
            while (filteredRetailerList.isEmpty()) {
                defaultDist *= 2;
                retailerList = dbRetriever.queryRetailer(StaticVariables.retailerByLocation(point.getLatitude(), defaultDist, point.getLongitude(), defaultDist));
                if (retailerTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
                    filteredRetailerList = retailerList;
                } else {
                    for (Retailer retailer : retailerList) {
                        if (retailer.getTypeID() != null) {
                            if (retailer.getTypeID().equals(retailerTypeCB.getSelectionModel().getSelectedItem())) {
                                filteredRetailerList.add(retailer);
                            }
                        }
                    }
                }
            }
            double minDistance = -1;
            double currentDistance;
            int minIndex = 0;
            for (Retailer retailer : filteredRetailerList) {
                currentDistance = StaticVariables.calculateDistance(point.getLatitude(), point.getLongitude(), retailer.getLatitude(), retailer.getLongitude());
                if (minDistance == -1) {
                    minDistance = currentDistance;
                } else if (minDistance > currentDistance) {
                    minDistance = currentDistance;
                    minIndex = retailerList.indexOf(retailer);
                }
            }
            return retailerList.get(minIndex);
        } else {
            throw new DatabaseEmptyException();
        }
    }

    /**
     * Checks whether there are any wifi records in the database
     * @return true if there are wifi records in the database
     */
    private boolean wifiInDatabase() {
        DatabaseRetriever dbRetriever = new DatabaseRetriever();
        ArrayList<Wifi> wifiList = dbRetriever.queryWifi(StaticVariables.steppedQuery(Wifi.tableName, 0));
        if (wifiList.isEmpty()) {
            return false;
        } else if (wifiTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
            return true;
        } else {
            wifiList = dbRetriever.queryWifi(StaticVariables.singleStringQuery(Wifi.tableName, "type", wifiTypeCB.getSelectionModel().getSelectedItem()));
            return !wifiList.isEmpty();
        }
    }

    /**
     * Checks whether there are any retailer records in the database
     * @return true if there are retailer records in the database
     */
    private boolean retailerInDatabase() {
        DatabaseRetriever dbRetriever = new DatabaseRetriever();
        ArrayList<Retailer> retailerList = dbRetriever.queryRetailer(StaticVariables.steppedQuery(Retailer.tableName, 0));
        if (retailerList.isEmpty()) {
            return false;
        } else if (retailerTypeCB.getSelectionModel().getSelectedItem().equals("All")) {
            return true;
        } else {
            retailerList = dbRetriever.queryRetailer(StaticVariables.singleStringQuery(Retailer.tableName, "typeID", retailerTypeCB.getSelectionModel().getSelectedItem()));
            return !retailerList.isEmpty();
        }
    }
}