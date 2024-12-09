module com.perseus.raycaster {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires java.xml;
    
    opens com.perseus.raycaster to javafx.fxml;
    exports com.perseus.raycaster;
}
