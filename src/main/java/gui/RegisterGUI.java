package gui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import businessLogic.BLFacade;
import domain.Registered;
import domain.User;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class RegisterGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField; // email
	private JTextField textField_1;// user
	private JPasswordField passwordField1;
	private JPasswordField passwordField2;
	private JFrame mainGui;
	private JFrame nirePantaila;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RegisterGUI frame = new RegisterGUI();
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
	public RegisterGUI() {
		nirePantaila = this;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		//mail
		textField = new JTextField();
		textField.setBounds(279, 75, 144, 19);
		contentPane.add(textField);
		textField.setColumns(10);
		//user
		textField_1 = new JTextField();
		textField_1.setBounds(279, 131, 144, 18);
		contentPane.add(textField_1);
		textField_1.setColumns(10);

		//password1
		passwordField1 = new JPasswordField();
		passwordField1.setBounds(279, 187, 144, 18);
		contentPane.add(passwordField1);

		//password2
		passwordField2 = new JPasswordField();
		passwordField2.setBounds(279, 244, 144, 18);
		contentPane.add(passwordField2);

		JLabel lblNewLabel = new JLabel("Mail");
		lblNewLabel.setBounds(63, 78, 67, 12);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Username");
		lblNewLabel_1.setBounds(63, 134, 67, 12);
		contentPane.add(lblNewLabel_1);

		JLabel lblPass1 = new JLabel("Password");
		lblPass1.setBounds(63, 190, 104, 12);
		contentPane.add(lblPass1);

		JLabel lblPass2 = new JLabel("Repeat Pass");
		lblPass2.setBounds(63, 247, 104, 12);
		contentPane.add(lblPass2);

		JButton btnNewButton = new JButton();
		btnNewButton.setText(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Register"));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BLFacade facade = MainGUI.getBusinessLogic();
				String mail = textField.getText();
				String user = textField_1.getText();
				String pass1 = new String(passwordField1.getPassword());
				String pass2 = new String(passwordField2.getPassword());
				if (pass1.equals(pass2) && !mail.equals("")) {
					User s = facade.isLogged(mail, pass1);
					if (s == null) {
						User b = facade.register(mail, user, pass1);
						new MainGUIErregistratua(b.getEmail()).setVisible(true);
						nirePantaila.setVisible(false);
					}
				} else {
					if (mail.equals("")) {
						JOptionPane.showMessageDialog(nirePantaila, "Have to enter an email!/¡Tiene que meter un email!/Email bat sartu bhar duzu!");
					} else {
						JOptionPane.showMessageDialog(nirePantaila, "Passwords do not match!/¡Las contraseñas no coinciden!/Pasahitzak ez dute bat egiten! ");
					}
				}
			}
		});
		btnNewButton.setBounds(279, 331, 144, 29);
		contentPane.add(btnNewButton);
		btnNewButton.setBounds(246, 300, 176, 35);

		JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("RegisterGUI.Close")); //$NON-NLS-1$ //$NON-NLS-2$
		jButtonClose.setBounds(294, 377, 89, 23);
		contentPane.add(jButtonClose);

		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RegisterGUI.this.setVisible(false);
				if (mainGui == null) {
					mainGui = new MainGUI("");
					mainGui.setVisible(true);
				} else {
					mainGui.setVisible(true);
				}
			}
		});
	}
}