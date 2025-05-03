module com.example.payrollmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;
    requires kernel;
    requires layout;
    requires io;
    requires org.slf4j;
    requires java.desktop;
    requires javafx.swing; // Add this line if it's not already there

    opens com.example.payrollmanagementsystem.controller to javafx.fxml;
    opens com.example.payrollmanagementsystem to javafx.fxml;

    exports com.example.payrollmanagementsystem;
    exports com.example.payrollmanagementsystem.controller;
}