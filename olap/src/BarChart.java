

import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset; 
import org.jfree.data.category.DefaultCategoryDataset; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 

public class BarChart extends ApplicationFrame
{
   public BarChart( String applicationTitle , String chartTitle, ArrayList <String> listDrugs, ArrayList <Integer> listAges, ArrayList <Integer> listValues )
   {
      super( applicationTitle );        
      JFreeChart barChart = ChartFactory.createBarChart(
         chartTitle,           
         "Category",            
         "Score",            
         createDataset(listDrugs, listAges, listValues),          
         PlotOrientation.VERTICAL,           
         true, true, false);
         
      ChartPanel chartPanel = new ChartPanel( barChart );        
      chartPanel.setPreferredSize(new java.awt.Dimension( 560 , 367 ) );        
      setContentPane( chartPanel ); 
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
   }
   
   private CategoryDataset createDataset(ArrayList <String> listDrugs, ArrayList <Integer> listAges, ArrayList <Integer> listValues )
   {   
      final DefaultCategoryDataset dataset = 
      new DefaultCategoryDataset( );  
      
      int i, j, m=0;
      // values to the graph
      for (i=0;i<listAges.size();i++) {
    	  for (j=0;j<listDrugs.size();j++) {
    			  dataset.addValue(listValues.get(m), listDrugs.get(j), listAges.get(i));
    			  m++;
    	  }
      }


      return dataset; 
   }
   public static void main( String[ ] args )
   {

   }
}