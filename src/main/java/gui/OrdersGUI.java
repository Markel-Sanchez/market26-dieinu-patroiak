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

public class OrdersGUI extends JFrame {
	private String email;
	private final String ETIKETEN_IZENA="Etiquetas";
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle(ETIKETEN_IZENA).getString("Close"));
	private JFrame thisFrame;

	public OrdersGUI(String email) {
		this.email = email;
		this.thisFrame = this;
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().add(jButtonClose);
		this.getContentPane().setLayout(null);
		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));

		JButton jButtonQueryOrders = new JButton(ResourceBundle.getBundle(ETIKETEN_IZENA).getString("OrdersGUI.QueryOrders")); //$NON-NLS-1$ //$NON-NLS-2$
		jButtonQueryOrders.setBounds(100, 177, 179, 49);
		getContentPane().add(jButtonQueryOrders);
		jButtonQueryOrders.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QueryOrdersGUI(email);
				a.setVisible(true);
			}
		});

		JButton jButtonMakeOrder = new JButton(ResourceBundle.getBundle(ETIKETEN_IZENA).getString("OrdersGUI.MakeOrder")); //$NON-NLS-1$ //$NON-NLS-2$
		jButtonMakeOrder.setBounds(396, 177, 179, 49);
		getContentPane().add(jButtonMakeOrder);
		jButtonMakeOrder.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new MakeOrderGUI(email);
				a.setVisible(true);
			}
		});

		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});

	}
}