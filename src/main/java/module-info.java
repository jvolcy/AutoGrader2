module AutoGraderApp {
    requires javafx.controls;
    requires javafx.fxml;

    opens AutoGraderApp to javafx.fxml;
    exports AutoGraderApp;
}