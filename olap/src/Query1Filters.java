import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.ui.RefineryUtilities;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class Query1Filters extends JFrame {

	private JPanel contentPane;
	private JTextField textFieldSymptom;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	/**
	 * Create the frame.
	 */
	public Query1Filters() {
		int i;
		DatabaseOperator db = new DatabaseOperator();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 742, 502);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("Evolution of symptom over time");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JLabel lblSymptom = new JLabel("Symptom");
		
		JLabel lblTimespan = new JLabel("TimeSpan");
		
		JLabel lblYearBetween = new JLabel("YEAR: between");
		
		JComboBox comboBox_YearI = new JComboBox();
		
		JLabel lblAnd = new JLabel("and");
		
		JComboBox comboBox_YearF = new JComboBox();
		
		JLabel lblMonthBetween = new JLabel("MONTH: between");
		
		JComboBox comboBox_MonthI = new JComboBox();
		
		JLabel lblAnd_1 = new JLabel("and");
		
		JComboBox comboBox_MonthF = new JComboBox();
		
		JLabel lblOptionalFilters = new JLabel("Optional Filters");
		
		JLabel lblSex = new JLabel("Sex");
		
		JComboBox comboBox_Sex = new JComboBox();
		
		JLabel lblWeightGroup = new JLabel("Weight Group");
		
		JLabel lblAgeGroup = new JLabel("Age Group");
		
		JLabel lblDrug = new JLabel("Drug");
		
		JComboBox comboBox_Weight = new JComboBox();
		
		JComboBox comboBox_Age = new JComboBox();
		
		JComboBox comboBox_Drug = new JComboBox();
		
		
		/* ADD ITEMS TO COMBOBOXES */
		
		// Symptoms
//		ArrayList <String> listSymptoms = db.getSymptoms();
//		
//		for (i=0;i<listSymptoms.size();i++) {
//			comboBox_Symptom.addItem(listSymptoms.get(i));
//		}
		
		// YearIni + YearFin
		String[] listYears = {"2013", "2014", "2015"};
		
		for (i=0;i<listYears.length;i++) {
			comboBox_YearI.addItem(listYears[i]);
			comboBox_YearF.addItem(listYears[i]);
		}
		
		// Months
		String[] listMonths = {"Jan", "Fev", "Mar", "Ap", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		for (i=0;i<listMonths.length;i++) {
			comboBox_MonthI.addItem(listMonths[i]);
			comboBox_MonthF.addItem(listMonths[i]);
		}
		
		// Sex
		String[] itemsSex = {"-", "M", "F"};
		for (i=0;i<itemsSex.length;i++) {
			comboBox_Sex.addItem(itemsSex[i]);
		}
		
		//Age
		String[] itemsAge = {"-", "1(0-12)", "2(13-17)", "3(18-65)", "4(66+)"};
		for (i=0;i<itemsAge.length;i++) {
			comboBox_Age.addItem(itemsAge[i]);
		}
		
		//Weight
		String[] itemsWeight = {"-", "1(0-34)", "2(35-49)", "3(50-69)", "4(70+)"};
		for (i=0;i<itemsWeight.length;i++) {
			comboBox_Weight.addItem(itemsWeight[i]);
		}
		
		
		// Drugs
		ArrayList <String> listDrugs = db.getDrugs();
		comboBox_Drug.addItem("-");
		for (i=0;i<listDrugs.size();i++) {
			comboBox_Drug.addItem(listDrugs.get(i));
		}
		
		textFieldSymptom = new JTextField();
		textFieldSymptom.setColumns(10);
		
		JCheckBox exactBox = new JCheckBox("exact");
		
		/* CHECK THE FILTERS THAT HAVE BEEN SELECTED */
		
		JButton btnSearch = new JButton("SEARCH");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				
				// get chosen filters
				//int indexChosenSymptom = comboBox_Symptom.getSelectedIndex();
				int indexChosenYearI = comboBox_YearI.getSelectedIndex();
				int indexChosenYearF = comboBox_YearF.getSelectedIndex();
				int indexChosenMonthI = comboBox_MonthI.getSelectedIndex();
				int indexChosenMonthF = comboBox_MonthF.getSelectedIndex();
				int indexChosenSex = comboBox_Sex.getSelectedIndex();
				int indexChosenAge = comboBox_Age.getSelectedIndex();
				int indexChosenWeight = comboBox_Weight.getSelectedIndex();
				int indexChosenDrug = comboBox_Drug.getSelectedIndex();
								
				//String chosenSymptom = (String) comboBox_Symptom.getItemAt(indexChosenSymptom);
				String chosenSymptom = textFieldSymptom.getText();
				String chosenYearI = (String) comboBox_YearI.getItemAt(indexChosenYearI);
				String chosenYearF = (String) comboBox_YearF.getItemAt(indexChosenYearF);
				String chosenMonthI = (String) comboBox_MonthI.getItemAt(indexChosenMonthI);
				String chosenMonthF = (String) comboBox_MonthF.getItemAt(indexChosenMonthF);
				String chosenDrug = (String) comboBox_Drug.getItemAt(indexChosenDrug);
				
				// get the graph points
				int j=0;
				ArrayList <String> chosenMonths = new ArrayList <String>();
				
				// calculate the n� of months (n� of graph points)
				int numberMonths = db.getNumberMonths(indexChosenMonthI, indexChosenMonthF, Integer.parseInt(chosenYearI), Integer.parseInt(chosenYearF));
			
				for (j=1;j<=numberMonths;j++) {
					chosenMonths.add(Integer.toString(j));
				}
				
				ArrayList <Integer> frequencySymptoms = new ArrayList <Integer>();
				
				
				if(!exactBox.isSelected())
					frequencySymptoms = db.runQuery1(chosenSymptom, numberMonths, Integer.parseInt(chosenYearI), Integer.parseInt(chosenYearF), indexChosenMonthI, indexChosenMonthF, indexChosenSex, indexChosenWeight, indexChosenAge, chosenDrug);
				else
					frequencySymptoms = db.runQuery1Exact(chosenSymptom, numberMonths, Integer.parseInt(chosenYearI), Integer.parseInt(chosenYearF), indexChosenMonthI, indexChosenMonthF, indexChosenSex, indexChosenWeight, indexChosenAge, chosenDrug);
				LineChart chart = new LineChart("Evolution of Symptom Nausea In Months","Evolution of Symptom Nausea in months", frequencySymptoms, chosenMonths);
			    chart.pack( );
			    RefineryUtilities.centerFrameOnScreen( chart );
			    chart.setVisible( true );
	
				
			}
		});
		
		JButton btnCancel = new JButton("CANCEL");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				setVisible(false);
				MenuWindow mw = new MenuWindow();
				mw.setVisible(true);
			}
		});
		
		
		
		
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(48)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblOptionalFilters)
								.addComponent(lblTimespan)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(211)
							.addComponent(lblNewLabel))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(77)
							.addComponent(lblSex)
							.addGap(18)
							.addComponent(comboBox_Sex, GroupLayout.PREFERRED_SIZE, 57, GroupLayout.PREFERRED_SIZE)
							.addGap(38)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblWeightGroup)
								.addComponent(lblAgeGroup))
							.addGap(16)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(comboBox_Age, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(comboBox_Weight, 0, 82, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblDrug)
							.addGap(18)
							.addComponent(comboBox_Drug, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnSearch)
							.addGap(55))))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(645, Short.MAX_VALUE)
					.addComponent(btnCancel))
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(49)
							.addComponent(lblSymptom)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textFieldSymptom))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(142)
							.addComponent(lblMonthBetween)
							.addGap(5)
							.addComponent(comboBox_MonthI, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(lblAnd_1)
							.addGap(5)
							.addComponent(comboBox_MonthF, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(158)
							.addComponent(lblYearBetween)
							.addGap(5)
							.addComponent(comboBox_YearI, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(lblAnd)
							.addGap(5)
							.addComponent(comboBox_YearF, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)))
					.addGap(33)
					.addComponent(exactBox)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(96, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addGap(38)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblSymptom)
								.addComponent(textFieldSymptom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(exactBox))
							.addGap(38)
							.addComponent(lblTimespan)
							.addGap(5)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(3)
									.addComponent(lblYearBetween))
								.addComponent(comboBox_YearI, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(3)
									.addComponent(lblAnd))
								.addComponent(comboBox_YearF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(35))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(btnSearch)
							.addGap(7)))
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(3)
									.addComponent(lblMonthBetween))
								.addComponent(comboBox_MonthI, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(3)
									.addComponent(lblAnd_1)))
							.addGap(35)
							.addComponent(lblOptionalFilters)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblSex)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(comboBox_Sex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblWeightGroup)
									.addComponent(lblDrug)
									.addComponent(comboBox_Weight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(comboBox_Drug, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
						.addComponent(comboBox_MonthF, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblAgeGroup)
						.addComponent(comboBox_Age, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(8)
					.addComponent(btnCancel)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
				
	}
}
