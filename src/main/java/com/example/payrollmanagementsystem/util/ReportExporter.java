package com.example.payrollmanagementsystem.util;

import com.example.payrollmanagementsystem.dao.ReportDAO;
import com.example.payrollmanagementsystem.model.Report;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter; // Import FileWriter
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ReportExporter {

    private final ReportDAO reportDAO = new ReportDAO();

    // Export payroll by department to CSV
    public void exportPayrollByDepartmentToCSV(String filePath) {
        List<Report> reports = reportDAO.getPayrollByDepartment();
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            writer.append("Department,Total Payroll\n");
            for (Report report : reports) {
                writer.append(report.getLabel()).append(",").append(String.valueOf(report.getValue())).append("\n");
            }
            System.out.println("Exported Payroll By Department to CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Export monthly salary trends to CSV
    public void exportSalaryTrendsToCSV(String filePath) {
        List<Report> reports = reportDAO.getMonthlySalaryTrends();
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            writer.append("Month,Total Payroll\n");
            for (Report report : reports) {
                writer.append(report.getLabel()).append(",").append(String.valueOf(report.getValue())).append("\n");
            }
            System.out.println("Exported Monthly Salary Trends to CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to export total payroll to CSV
    public void exportTotalPayrollToCSV(String filePath) {
        double total = reportDAO.getTotalPayrollExpenses();
        try (FileWriter writer = new FileWriter(new File(filePath))) {
            writer.append("Total Payroll\n");
            writer.append(String.valueOf(total)).append("\n");
            System.out.println("Exported Total Payroll to CSV.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportToPdf(String totalPayroll, String avgSalary, String topDept,
                                   PieChart pieChart, BarChart<String, Number> barChart,
                                   LineChart<String, Number> lineChart) throws IOException {
        Stage primaryStage = new Stage(); // Dummy stage for FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.setInitialFileName("payroll_report.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file == null) {
            return; // User cancelled the save dialog
        }

        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph("Payroll Report").setBold().setFontSize(18));
            document.add(new Paragraph("\n"));

            // Summary Table
            Table summaryTable = new Table(new float[]{1, 2});
            summaryTable.setWidth(UnitValue.createPercentValue(50));
            summaryTable.addCell("Total Payroll:");
            summaryTable.addCell(totalPayroll);
            summaryTable.addCell("Average Salary:");
            summaryTable.addCell(avgSalary);
            summaryTable.addCell("Top Department:");
            summaryTable.addCell(topDept);
            document.add(summaryTable);
            document.add(new Paragraph("\n"));

            // Add Charts as Images
            if (pieChart.getData() != null && !pieChart.getData().isEmpty()) {
                Image pieChartImage = convertChartToImage(pieChart);
                document.add(pieChartImage);
                document.add(new Paragraph("\n"));
            }

            if (barChart.getData() != null && !barChart.getData().isEmpty()) {
                Image barChartImage = convertChartToImage(barChart);
                document.add(barChartImage);
                document.add(new Paragraph("\n"));
            }

            if (lineChart.getData() != null && !lineChart.getData().isEmpty()) {
                Image lineChartImage = convertChartToImage(lineChart);
                document.add(lineChartImage);
            }

        } catch (IOException e) {
            throw new IOException("Failed to create PDF: " + e.getMessage(), e);
        }
    }

    private static Image convertChartToImage(Chart chart) throws IOException {
        WritableImage writableImage = chart.snapshot(null, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        javafx.scene.image.Image fxImage = writableImage;
        ImageIO.write(SwingFXUtils.fromFXImage(fxImage, null), "png", outputStream);
        ImageData imageData = ImageDataFactory.create(outputStream.toByteArray());
        return new Image(imageData).scaleToFit(500, 500);
    }
}