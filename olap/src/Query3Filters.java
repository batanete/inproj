import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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

public class Query3Filters extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

	}

	/**
	 * Create the frame.
	 */
	public Query3Filters() {
		int i;
		DatabaseOperator db = new DatabaseOperator();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 671, 431);
		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblIngredientsWhichCause = new JLabel("INGREDIENTS WHICH CAUSE SEVERE SYMPTOMS");
		lblIngredientsWhichCause.setFont(new Font("Tahoma", Font.PLAIN, 18));
		

		
		

		
		JLabel lblOptionalFilters = new JLabel("Optional Filters");
		
		JLabel lblSex = new JLabel("Sex");
		
		JLabel lblWeight = new JLabel("Weight");
		
		JLabel lblAge = new JLabel("Age");
		
		JComboBox comboBox_Sex = new JComboBox();
		
		JComboBox comboBox_Weight = new JComboBox();
		
		JComboBox comboBox_Age = new JComboBox();
		
		// Specify parameters for each box
		// Sex
		String[] itemsSex = {"-", "M", "F"};
		for (i=0;i<itemsSex.length;i++) {
			comboBox_Sex.addItem(itemsSex[i]);
		}
		
		//Age
		String[] itemsAge = {"-", "1", "2", "3", "4"};
		for (i=0;i<itemsAge.length;i++) {
			comboBox_Age.addItem(itemsAge[i]);
		}
		
		//Weight
		String[] itemsWeight = {"-", "1", "2", "3", "4"};
		for (i=0;i<itemsWeight.length;i++) {
			comboBox_Weight.addItem(itemsWeight[i]);
		}
		
		
		JButton btnSearch = new JButton("SEARCH");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				
				// get the checked 
				int chosenSex = comboBox_Sex.getSelectedIndex();
				int chosenAge = comboBox_Age.getSelectedIndex();
				int chosenWeight = comboBox_Weight.getSelectedIndex();
				
				
				ArrayList <String> listIngredients = new ArrayList <String>();
				listIngredients = db.runQuery3(chosenSex, chosenWeight, chosenAge);
				
				Query3Results q3 = new Query3Results(listIngredients);
				q3.setVisible(true);
				//setVisible(false);
			}
		});
		
		JButton btnCancel = new JButton("CANCEL");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				MenuWindow mw = new MenuWindow();
				mw.setVisible(true);
			}
		});
		
		
		
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(81, Short.MAX_VALUE)
					.addComponent(lblIngredientsWhichCause)
					.addGap(131))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(242)
					.addComponent(btnSearch)
					.addContainerGap(331, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(40)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblOptionalFilters)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addComponent(lblAge)
							.addComponent(lblWeight)
							.addComponent(lblSex)))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(comboBox_Sex, 0, 65, Short.MAX_VALUE)
						.addComponent(comboBox_Weight, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(comboBox_Age, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap(428, Short.MAX_VALUE))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnCancel)
					.addContainerGap(562, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblIngredientsWhichCause)
					.addGap(34)
					.addComponent(lblOptionalFilters)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBox_Sex, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblSex))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblWeight)
							.addPreferredGap(ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
							.addComponent(btnSearch)
							.addGap(67))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(comboBox_Weight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboBox_Age, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblAge))
							.addGap(152)
							.addComponent(btnCancel)
							.addGap(10)))
					.addGap(37))
		);
		contentPane.setLayout(gl_contentPane);
	}
}
