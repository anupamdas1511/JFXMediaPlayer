module com.example.jmediaplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    // add icon pack modules
    requires org.kordamp.ikonli.fontawesome;

    opens com.example.jmediaplayer to javafx.fxml;
    exports com.example.jmediaplayer;
}