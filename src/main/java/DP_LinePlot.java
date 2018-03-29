import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DP_LinePlot extends ApplicationFrame{

    private static XYSeriesCollection dataset1;
    private static XYSeriesCollection dataset2;


    public DP_LinePlot(String title) {
        super(title);
        loadData();
        setContentPane(createDPPanel( ));
    }

    private JPanel createDPPanel() {
        JFreeChart chart = createChart();
        chart.setBackgroundPaint(Color.WHITE);
        JPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }

    private void loadData() {
        XYSeries temperature = new XYSeries("temperature");
        XYSeries unemployment = new XYSeries("unemployment");
        final CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (
                Reader reader =
                        Files.newBufferedReader(Paths.get("src/main/resources/labour-szg.csv"));
                CSVReader csvReader =
                        new CSVReaderBuilder(reader).withSkipLines(3).withCSVParser(parser).build();
        ){
            String[] nextRecord;
            int currentYear;
            while ((nextRecord = csvReader.readNext()) != null) {

                currentYear = Integer.parseInt(nextRecord[2]);
                if(currentYear<2002||currentYear>2013) continue;
                else {
                    unemployment.add(currentYear, Double.parseDouble(nextRecord[3].replace(',','.')));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                Reader reader =
                        Files.newBufferedReader(Paths.get("src/main/resources/tab_1.3.1_klimaundwetter_.csv"));
                CSVReader csvReader =
                        new CSVReaderBuilder(reader).withSkipLines(3).withCSVParser(parser).build();
        ){
            String[] nextRecord;
            int currentYear;
            while ((nextRecord = csvReader.readNext()) != null) {

                try {
                    currentYear = Integer.parseInt(nextRecord[0]);
                    if (currentYear < 2002 || currentYear > 2013) continue;
                    else {
                        temperature.add(currentYear, Double.parseDouble(nextRecord[1].replace(',', '.')));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Could not convert to number: " + nextRecord.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dataset1 = new XYSeriesCollection();
        dataset2 = new XYSeriesCollection();
        dataset1.addSeries(temperature);
        dataset2.addSeries(unemployment);
    }

    private static JFreeChart createChart() {
        XYPlot plot = new XYPlot();
        plot.setDataset(0, dataset1);
        plot.setDataset(1, dataset2);

        plot.setRenderer(0, new XYSplineRenderer());
        XYSplineRenderer splineRenderer = new XYSplineRenderer();
        splineRenderer.setSeriesFillPaint(0, Color.BLUE);

        plot.setRenderer(1, splineRenderer);
        NumberAxis axisTemp = new NumberAxis("Temperature");
        axisTemp.setRange(7,14);
        plot.setRangeAxis(0, axisTemp);
        NumberAxis axisUnemp = new NumberAxis("Unemployment");
        axisUnemp.setRange(3,6);
        plot.setRangeAxis(1, axisUnemp);
        NumberAxis axisYear = new NumberAxis("Year");
        axisYear.setRange(2002,2013);
        plot.setDomainAxis(axisYear);

        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);
        
        JFreeChart chart = new JFreeChart("Correlation between Annual avg. temperature of Vienna/Unemployment in Salzburg", Font.getFont("Serif"), plot, true);
        return chart;
    }
    

    public static void main(String[] args) {
        DP_LinePlot linePlot = new DP_LinePlot("Digital Preservation 2018 ue1.1");
        linePlot.setSize( 800 , 600 );
        linePlot.setVisible( true );
    }
}
