package gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import businessLogic.BLFacade;
import domain.Order;

public class ShowOrdersGUI extends JFrame {
	String email;
	Order currentOrder;
	private JTextField txtTitle, txtMin, txtMax;
	private JTextArea txtDesc;
	private JButton btnGoToSale;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));

	public ShowOrdersGUI(Order order, String userEmail) {
		this.email = userEmail;
		this.currentOrder = order;
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("OrdersGUI.QueryOrders"));
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		jButtonClose.setBounds(new Rectangle(185, 385, 130, 30));
		this.getContentPane().add(jButtonClose);
		this.getContentPane().setLayout(null);

		JLabel lbl1 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.Title"));
		lbl1.setBounds(30, 30, 100, 20);
		getContentPane().add(lbl1);
		txtTitle = new JTextField(order.getTitle());
		txtTitle.setBounds(123, 30, 227, 25);
		txtTitle.setEditable(false);
		getContentPane().add(txtTitle);

		JLabel lbl2 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.Desc"));
		lbl2.setBounds(30, 80, 100, 20);
		getContentPane().add(lbl2);

		txtDesc = new JTextArea(order.getDescription());
		txtDesc.setLineWrap(true);
		txtDesc.setWrapStyleWord(true);
		txtDesc.setEditable(false);

		txtDesc.setBorder(new javax.swing.JTextField().getBorder());
		txtDesc.setBounds(120, 90, 250, 150);

		getContentPane().add(txtDesc);

		JLabel lbl3 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.PMin"));
		lbl3.setBounds(378, 30, 100, 20);
		getContentPane().add(lbl3);
		txtMin = new JTextField(String.valueOf(order.getPriceMin()));
		txtMin.setBounds(450, 28, 105, 25);
		txtMin.setEditable(false);
		getContentPane().add(txtMin);

		JLabel lbl4 = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.PMax"));
		lbl4.setBounds(378, 80, 78, 20);
		getContentPane().add(lbl4);
		txtMax = new JTextField(String.valueOf(order.getPriceMax()));
		txtMax.setBounds(455, 78, 100, 25);
		txtMax.setEditable(false);
		getContentPane().add(txtMax);

		String ownerEmail = order.getRegistered().getEmail();

		if (!ownerEmail.equals(userEmail)) {
			btnGoToSale = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.CreateSale"));
			btnGoToSale.setBounds(185, 311, 250, 30);
			getContentPane().add(btnGoToSale);

			btnGoToSale.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						BLFacade facade = MainGUI.getBusinessLogic();
						facade.removeOrder(currentOrder);
						
						dispose();
						JFrame a = new CreateSaleGUI(userEmail);
						a.setVisible(true);
					} catch (Exception ex) {
						System.out.println("Error: " + ex.getMessage());
					}
				}
			});
		}
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		this.getContentPane().add(jButtonClose);
	}
}