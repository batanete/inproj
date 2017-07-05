
import org.jfree.chart.ChartPanel;

import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart extends ApplicationFrame
{
   public LineChart( String applicationTitle , String chartTitle, ArrayList <Integer> frequencySymptoms, ArrayList <String> months)
   {
      super(applicationTitle);
      JFreeChart lineChart = ChartFactory.createLineChart(
         chartTitle,
         "Months","Number of Patients",
         createDataset(frequencySymptoms, months),
         PlotOrientation.VERTICAL,
         true,true,false);
         
      ChartPanel chartPanel = new ChartPanel( lineChart );
      chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
      setContentPane( chartPanel );
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }
   
   private DefaultCategoryDataset createDataset(ArrayList <Integer> frequencySymptoms, ArrayList <String> months)
   {
      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
      int i;
      for (i=0; i< frequencySymptoms.size();i++) {
    	  dataset.addValue( frequencySymptoms.get(i), "frequencySymptom" , months.get(i) );
      }
      return dataset;
   }

//   private DefaultCategoryDataset createDataset( )
//   {
//      DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
//      dataset.addValue( 15 , "no symptoms" , "1970" );
//      dataset.addValue( 30 , "no symptoms" , "1980" );
//      dataset.addValue( 60 , "no symptoms" ,  "1990" );
//      dataset.addValue( 120 , "no symptoms" , "2000" );
//      dataset.addValue( 240 , "no symptoms" , "2010" );
//      dataset.addValue( 300 , "no symptoms" , "2014" );
//      return dataset;
//   }
   public static void main( String[ ] args ) 
   {
      /*LineChart chart = new LineChart(
      "School Vs Years" ,
      "Numer of Schools vs years");

      chart.pack( );
      RefineryUtilities.centerFrameOnScreen( chart );
      chart.setVisible( true );*/
   }
}