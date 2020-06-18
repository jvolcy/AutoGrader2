module AutoGraderApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens AutoGraderApp to javafx.fxml;
    exports AutoGraderApp;
}