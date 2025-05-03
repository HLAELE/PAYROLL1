// ReportController.java
package com.example.payrollmanagementsystem.controller;

import com.example.payrollmanagementsystem.dao.ReportDAO;
import com.example.payrollmanagementsystem.model.Report;
import com.example.payrollmanagementsystem.util.ReportExporter;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportController {

    @FXML
    private PieChart salaryPieChart;

    @FXML
    private BarChart<String, Number> departmentBarChart;

    @FXML
    private LineChart<String, Number> salaryTrendLineChart;

    @FXML
    private Label totalPayrollLabel;

    @FXML
    private Label avgSalaryLabel;

    @FXML
    private Label topDeptLabel;

    @FXML
    private Button printReportButton; // New button

    private final ReportDAO reportDAO = new ReportDAO();

    @FXML
    public void initialize() {
        displayPayrollSummary();
        loadSalaryPieChart();
        loadDepartmentBarChart();
        loadSalaryTrendLineChart();
        setupChartAxisLabels();
        addHoverEffectToCharts();
    }

    private void displayPayrollSummary() {
        Map<String, Double> summary = reportDAO.getPayrollSummary();
        if (summary != null) {
            totalPayrollLabel.setText(String.format("Total Payroll: M%.2f", summary.getOrDefault("Total Payroll", 0.0)));
            avgSalaryLabel.setText(String.format("Average Salary: M%.2f", summary.getOrDefault("Average Salary", 0.0)));
        } else {
            totalPayrollLabel.setText("Total Payroll: M0.00");
            avgSalaryLabel.setText("Average Salary: M0.00");
        }

        String highestDept = reportDAO.getHighestPayingDept();
        topDeptLabel.setText(highestDept != null ? highestDept : "N/A");
    }

    private void loadSalaryPieChart() {
        List<Report> data = reportDAO.getPayrollByDepartment();
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

        for (Report report : data) {
            if (report != null && report.getLabel() != null) {
                pieData.add(new PieChart.Data(report.getLabel(), report.getValue()));
            }
        }

        salaryPieChart.setData(pieData);

        // Fade and translate animation for PieChart
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), salaryPieChart);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        TranslateTransition translateIn = new TranslateTransition(Duration.seconds(1), salaryPieChart);
        translateIn.setFromY(-100);
        translateIn.setToY(0);
        translateIn.setCycleCount(1);

        fadeIn.play();
        translateIn.play();
    }

    private void loadDepartmentBarChart() {
        departmentBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Payroll by Department");

        List<Report> data = reportDAO.getPayrollByDepartment();

        for (Report report : data) {
            if (report != null && report.getLabel() != null) {
                series.getData().add(new XYChart.Data<>(report.getLabel(), report.getValue()));
            }
        }

        departmentBarChart.getData().add(series);

        // Fade and translate animation for BarChart
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), departmentBarChart);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        TranslateTransition translateIn = new TranslateTransition(Duration.seconds(1), departmentBarChart);
        translateIn.setFromX(-100);
        translateIn.setToX(0);
        translateIn.setCycleCount(1);

        fadeIn.play();
        translateIn.play();
    }

    private void loadSalaryTrendLineChart() {
        salaryTrendLineChart.getData().clear();
        salaryTrendLineChart.setCreateSymbols(true);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Payroll");

        List<Report> data = reportDAO.getMonthlySalaryTrends();

        for (Report report : data) {
            if (report != null && report.getLabel() != null) {
                XYChart.Data<String, Number> point = new XYChart.Data<>(report.getLabel(), report.getValue());
                series.getData().add(point);

                // Highlight current month
                if (report.getLabel().equalsIgnoreCase(getCurrentMonth())) {
                    point.setNode(new StackPane() {{
                        setStyle("-fx-background-color: #ff5722; -fx-background-radius: 5px;");
                        setPrefSize(10, 10);
                    }});
                }
            }
        }

        salaryTrendLineChart.getData().add(series);

        // Format Y-axis to show currency values
        NumberAxis yAxis = (NumberAxis) salaryTrendLineChart.getYAxis();
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, null, null));

        // Fade and translate animation for LineChart
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), salaryTrendLineChart);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        TranslateTransition translateIn = new TranslateTransition(Duration.seconds(1), salaryTrendLineChart);
        translateIn.setFromY(100);
        translateIn.setToY(0);
        translateIn.setCycleCount(1);

        fadeIn.play();
        translateIn.play();
    }

    private String getCurrentMonth() {
        return java.time.Month.from(java.time.LocalDate.now()).toString();
    }

    private void setupChartAxisLabels() {
        salaryTrendLineChart.getXAxis().setLabel("Month");
        salaryTrendLineChart.getYAxis().setLabel("Salary (M)");

        departmentBarChart.getXAxis().setLabel("Department");
        departmentBarChart.getYAxis().setLabel("Payroll (M)");
    }

    private void addHoverEffectToCharts() {
        // Hover effect for PieChart
        salaryPieChart.setOnMouseEntered(event -> {
            salaryPieChart.setOpacity(0.8);
        });

        salaryPieChart.setOnMouseExited(event -> {
            salaryPieChart.setOpacity(1.0);
        });

        // Hover effect for BarChart
        departmentBarChart.setOnMouseEntered(event -> {
            departmentBarChart.setOpacity(0.8);
        });

        departmentBarChart.setOnMouseExited(event -> {
            departmentBarChart.setOpacity(1.0);
        });

        // Hover effect for LineChart
        salaryTrendLineChart.setOnMouseEntered(event -> {
            salaryTrendLineChart.setOpacity(0.8);
        });

        salaryTrendLineChart.setOnMouseExited(event -> {
            salaryTrendLineChart.setOpacity(1.0);
        });
    }

    @FXML
    private void handleBackButtonClick(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/payrollmanagementsystem/fxml/AdminDashboardView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlePrintReport() {
        try {
            ReportExporter.exportToPdf(totalPayrollLabel.getText(), avgSalaryLabel.getText(), topDeptLabel.getText(),
                    salaryPieChart, departmentBarChart, salaryTrendLineChart);
            showAlert("Success", "Reports and charts exported to PDF successfully.");
        } catch (IOException e) {
            showAlert("Error", "Failed to export reports to PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}