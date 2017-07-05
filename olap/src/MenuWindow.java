import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuWindow frame = new MenuWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/
	}

	/**
	 * Create the frame.
	 */
	public MenuWindow() {
		int i;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 719, 452);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel questionsLbl = new JLabel("QUESTIONS");
		GridBagConstraints gbc_questionsLbl = new GridBagConstraints();
		gbc_questionsLbl.insets = new Insets(0, 0, 5, 5);
		gbc_questionsLbl.gridx = 5;
		gbc_questionsLbl.gridy = 1;
		contentPane.add(questionsLbl, gbc_questionsLbl);
		
		JComboBox comboQuestions = new JComboBox();
		GridBagConstraints gbc_comboQuestions = new GridBagConstraints();
		gbc_comboQuestions.insets = new Insets(0, 0, 5, 5);
		gbc_comboQuestions.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboQuestions.gridx = 5;
		gbc_comboQuestions.gridy = 3;
		contentPane.add(comboQuestions, gbc_comboQuestions);
			
		// Add the questions to the Combo Box
		String items[] = { "1-Evolution of symptom over time", "2-Absolute Frequency of a Symptom for a given Age Group and Drug", "3-Ingredients which are more likely to cause severe symptoms"};
		for (i=0;i<items.length; i++)
			comboQuestions.addItem(items[i]);
		
		
		
		
		// *** --- CHECK FINAL RESULT --- ***
				
		JButton btnSearch = new JButton("SEARCH");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				setVisible(false);
				// check user choice
				int userChoice;
				userChoice = comboQuestions.getSelectedIndex()+1;
				if (userChoice == 1) {
					Query1Filters newWin1 = new Query1Filters();
					newWin1.setVisible(true);
				}
				else if (userChoice == 2) {
					Query2Filters newWin2 = new Query2Filters();
					newWin2.setVisible(true);
				}
				
				else if (userChoice == 3) {
					Query3Filters newWin3 = new Query3Filters();
					newWin3.setVisible(true);
				}
				
			}
		});
		GridBagConstraints gbc_btnSearch = new GridBagConstraints();
		gbc_btnSearch.insets = new Insets(0, 0, 0, 5);
		gbc_btnSearch.gridx = 5;
		gbc_btnSearch.gridy = 9;
		contentPane.add(btnSearch, gbc_btnSearch);
		

		
		
	}

}
