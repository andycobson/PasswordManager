module org.mgrs {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.commons.codec;

    opens org.mgrs to javafx.fxml;
    exports org.mgrs;
}