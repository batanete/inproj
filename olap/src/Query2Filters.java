import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.ui.RefineryUtilities;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class Query2Filters extends JFrame {

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
	public Query2Filters() {
		int i;
		DatabaseOperator db = new DatabaseOperator();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 714, 546);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblAbsoluteFrequencyOf = new JLabel("ABSOLUTE FREQUENCY OF A SYMPTOM");
		lblAbsoluteFrequencyOf.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JLabel lblSymptom = new JLabel("Symptom");
		
		JLabel lblAge = new JLabel("Age");
		
		JLabel lblDrug = new JLabel("Drug");
		
		JComboBox comboBox_Drug1 = new JComboBox();
		
		JComboBox comboBox_Drug2 = new JComboBox();
		
		JComboBox comboBox_Drug3 = new JComboBox();
		
		JComboBox comboBox_Age1 = new JComboBox();
		
		JComboBox comboBox_Age2 = new JComboBox();
		
		JComboBox comboBox_Age3 = new JComboBox();
		
		JComboBox comboBox_Age4 = new JComboBox();
		
		// Add items to ComboBoxes
		//Symptoms
//		ArrayList <String> listSymptoms = db.getSymptoms();
//		for (i=0;i<listSymptoms.size();i++) {
//			comboBox_Symptom.addItem(listSymptoms.get(i));
//		}
		
		// Drugs
		ArrayList <String> listDrugs = db.getDrugs();
		comboBox_Drug1.addItem("-");
		comboBox_Drug2.addItem("-");
		comboBox_Drug3.addItem("-");
		for (i=0;i<listDrugs.size();i++) {
			comboBox_Drug1.addItem(listDrugs.get(i));
			comboBox_Drug2.addItem(listDrugs.get(i));
			comboBox_Drug3.addItem(listDrugs.get(i));
		}
		
		//Age
		String[] itemsAge = {"-", "1", "2", "3", "4"};
		for (i=0;i<itemsAge.length;i++) {
			comboBox_Age1.addItem(itemsAge[i]);
			comboBox_Age2.addItem(itemsAge[i]);
			comboBox_Age3.addItem(itemsAge[i]);
			comboBox_Age4.addItem(itemsAge[i]);
		}	
		
		JTextArea txtrArea = new JTextArea();
		txtrArea.setText("The graph represents the frequency of the symptom, \ngrouped by the selected Age groups for each drug.");
		
		textFieldSymptom = new JTextField();
		textFieldSymptom.setColumns(10);
		
		JButton btnSearch = new JButton("SEARCH");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				
				// get chosen filters
				//int indexChosenSymptom = comboBox_Symptom.getSelectedIndex();
				int indexChosenDrug1 = comboBox_Drug1.getSelectedIndex();
				int indexChosenDrug2 = comboBox_Drug2.getSelectedIndex();
				int indexChosenDrug3 = comboBox_Drug3.getSelectedIndex();
				int indexChosenAge1 = comboBox_Age1.getSelectedIndex();
				int indexChosenAge2 = comboBox_Age2.getSelectedIndex();
				int indexChosenAge3 = comboBox_Age3.getSelectedIndex();
				int indexChosenAge4 = comboBox_Age4.getSelectedIndex();
				
				//String chosenSymptom = (String) comboBox_Symptom.getItemAt(indexChosenSymptom);
				String chosenDrug1 = (String) comboBox_Drug1.getItemAt(indexChosenDrug1);
				String chosenDrug2 = (String) comboBox_Drug2.getItemAt(indexChosenDrug2);
				String chosenDrug3 = (String) comboBox_Drug3.getItemAt(indexChosenDrug3);
				
				String chosenSymptom2 = textFieldSymptom.getText();
				
				// decide parameters for Bar Chart
				ArrayList <String> chosenDrugs = new ArrayList <String>();
				chosenDrugs.add(chosenDrug1);
				chosenDrugs.add(chosenDrug2);
				chosenDrugs.add(chosenDrug3);
				
				ArrayList <Integer> chosenAges = new ArrayList <Integer>();
				chosenAges.add(indexChosenAge1);
				chosenAges.add(indexChosenAge2);
				chosenAges.add(indexChosenAge3);
				chosenAges.add(indexChosenAge4);

				
				ArrayList <Integer> listValues = new ArrayList <Integer>();
				listValues = db.runQuery2(chosenSymptom2, chosenDrugs, chosenAges);
				
				BarChart chart = new BarChart("Statistics", "Frequency of "+chosenSymptom2, chosenDrugs, chosenAges, listValues);
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
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(168)
							.addComponent(lblAbsoluteFrequencyOf))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(85)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblAge)
										.addComponent(lblSymptom, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblDrug))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(comboBox_Drug3, 0, 363, Short.MAX_VALUE)
										.addGroup(gl_contentPane.createSequentialGroup()
											.addComponent(comboBox_Age1, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(comboBox_Age2, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox_Age3, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(comboBox_Age4, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
										.addComponent(comboBox_Drug1, 0, 363, Short.MAX_VALUE)
										.addComponent(comboBox_Drug2, 0, 363, Short.MAX_VALUE)
										.addComponent(textFieldSymptom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(62)
									.addComponent(btnSearch))
								.addComponent(txtrArea, GroupLayout.PREFERRED_SIZE, 480, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(27)
							.addComponent(btnCancel)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(23)
					.addComponent(lblAbsoluteFrequencyOf)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSymptom)
						.addComponent(textFieldSymptom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDrug)
						.addComponent(comboBox_Drug3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_Drug1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearch))
					.addGap(11)
					.addComponent(comboBox_Drug2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAge)
						.addComponent(comboBox_Age1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBox_Age2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBox_Age3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBox_Age4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(97)
					.addComponent(txtrArea, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addGap(60)
					.addComponent(btnCancel)
					.addGap(39))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
