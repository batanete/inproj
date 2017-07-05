import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Font;

public class FirstWindow extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FirstWindow frame = new FirstWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FirstWindow() {
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		setBounds(100, 100, 575, 377);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblDisclaimerAllData = new JLabel("DISCLAIMER: All data will be used responsibly.");
		lblDisclaimerAllData.setFont(new Font("Tahoma", Font.PLAIN, 18));
		
		JButton btnIAccept = new JButton("I ACCEPT");
		
		btnIAccept.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				setVisible(false);
				try {
					MenuWindow frameMenu = new MenuWindow();
					frameMenu.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(107, Short.MAX_VALUE)
					.addComponent(lblDisclaimerAllData)
					.addGap(100))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(208)
					.addComponent(btnIAccept)
					.addContainerGap(232, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(104)
					.addComponent(lblDisclaimerAllData)
					.addGap(18)
					.addComponent(btnIAccept)
					.addContainerGap(140, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
	}

}
