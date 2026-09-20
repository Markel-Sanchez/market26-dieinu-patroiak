package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import businessLogic.BLFacade;
import domain.Admin;
import domain.Registered;
import domain.User;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;

public class LoginGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JFrame maingui;
	private JPasswordField textField_1;
	private JFrame nirePantaila;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Close"));

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginGUI frame = new LoginGUI();
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
	public LoginGUI() {
		nirePantaila = this;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);

		textField = new JTextField();
		textField.setBounds(246, 126, 196, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		textField_1 = new JPasswordField();
		textField_1.setBounds(246, 182, 196, 18);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		JLabel lblNewLabel = new JLabel("Mail");
		lblNewLabel.setBounds(54, 129, 76, 12);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Password");
		lblNewLabel_1.setBounds(54, 185, 76, 12);
		contentPane.add(lblNewLabel_1);
		JButton btnNewButton = new JButton();
		btnNewButton.setText(ResourceBundle.getBundle("Etiquetas").getString("LoginGUI.Login"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
				String sartutakoEmail = textField.getText();
				String sartutakoPass = new String(textField_1.getPassword());
				User s = facade.isLogged(sartutakoEmail, sartutakoPass);
				if (s != null) {
					if (s instanceof Registered) {
						new MainGUIErregistratua(sartutakoEmail).setVisible(true);
					} else if (s instanceof Admin) {
						new MainAdminGUI(sartutakoEmail).setVisible(true);
					}

					nirePantaila.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(nirePantaila, "User doesn't exist/El usuario o existe/Erabiltzailea ez da existitzen");
				}

			}
		});
		jButtonClose.setBounds(new Rectangle(220, 379, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LoginGUI.this.setVisible(false);
				if (maingui == null) {
					maingui = new MainGUI("");
					maingui.setVisible(true);
				} else {
					maingui.setVisible(true);
				}
			}
		});
		this.getContentPane().add(jButtonClose, null);
		btnNewButton.setBounds(246, 300, 176, 35);
		contentPane.add(btnNewButton);

		jButtonClose.setBounds(289, 397, 89, 23);
		contentPane.add(jButtonClose);
	}
}