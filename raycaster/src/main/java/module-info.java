module com.perseus.raycaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    
    opens com.perseus.raycaster to javafx.fxml;
    exports com.perseus.raycaster;
}
