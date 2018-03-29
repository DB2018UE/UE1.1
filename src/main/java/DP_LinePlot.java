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

/**
 * Dual Axis plot for the given task: statistic correlation between the given statistic entities
 * DISCLAIMER: Credit goes to the team of JFreeChart, it's classes are
 * used/extended.
 */
public class DP_LinePlot extends ApplicationFrame{

    /**
     * data sets to be read from files
     */
    private static XYSeriesCollection dataset1;
    private static XYSeriesCollection dataset2;


    public DP_LinePlot(String title) {
        super(title);
        loadData();
        setContentPane(createDPPanel());
    }

    /**
     * method for creating a panel out of the chart, to use it in a window frame
     * @return finished panel
     */
    private JPanel createDPPanel() {
        JFreeChart chart = createChart();
        chart.setBackgroundPaint(Color.WHITE);
        JPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }

    /**
     * loads the raw data from the specific csv files and filters out all data which is out
     * of the investigated time frame. They are located in
     * src/main/resources.
     * DISCLAIMER: credit goes to the team opencsv, it's reader/parsers are used to handle the
     * csv input.
     */
    private void loadData() {
        XYSeries temperature = new XYSeries("temperature");
        XYSeries unemployment = new XYSeries("unemployment");
        //additional parser to exclude default ',' separator (european comma)
        final CSVParser parser = new CSVParserBuilder().withSeparator(';').build();

        try (
                Reader reader =
                        Files.newBufferedReader(Paths.get("src/main/resources/labour-szg.csv"));
                CSVReader csvReader =
                        new CSVReaderBuilder(reader).withSkipLines(3).withCSVParser(parser).build()
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
                        new CSVReaderBuilder(reader).withSkipLines(3).withCSVParser(parser).build()
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
                    //Exception occurs at the last line due to a data source statement
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

    /**
     * method for creating a chart with the necessary settings to display both Y-axis
     * in a readable way
     * @return finished chart
     */
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
