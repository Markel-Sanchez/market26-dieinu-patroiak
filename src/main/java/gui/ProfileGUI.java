package gui;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.Sale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ProfileGUI extends JFrame {
	private JPanel jContentPane = null;
	private JFrame thisFrame;
	private String sellerMail;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JButton jButtonMovement = null;
	private JButton jButtonMoney = null;
	private JButton jButtonComplaint = null;
	protected JLabel jLabelSelectOption;
	private JButton jButtonCart = null;
	private JButton jButtonFavourite = null;
	private JButton jButtonPurchasedSales = null;

	public ProfileGUI(String email) {
		this.sellerMail = email;
		this.thisFrame = this;
		this.setSize(700, 500);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);

		jButtonMovement = new JButton();
		jButtonMovement.setText(ResourceBundle.getBundle("Etiquetas").getString("Profile.Movement"));
		jButtonMovement.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new MovementGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jButtonMoney = new JButton();
		jButtonMoney.setText(ResourceBundle.getBundle("Etiquetas").getString("Profile.Money"));
		jButtonMoney.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new MoneyGUI(sellerMail);
				a.setVisible(true);
			}
		});
		
		jButtonCart = new JButton();
		jButtonCart.setText(ResourceBundle.getBundle("Etiquetas").getString("Profile.Cart"));
		jButtonCart.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CartGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jButtonComplaint = new JButton();
		jButtonComplaint.setText(ResourceBundle.getBundle("Etiquetas").getString("Profile.Complaint"));
		jButtonComplaint.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new ComplaintGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});

		jButtonFavourite = new JButton();
		jButtonFavourite.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Favourite"));
		jButtonFavourite.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new FavoriteGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jButtonPurchasedSales = new JButton();
		jButtonPurchasedSales.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Purchased"));
		jButtonPurchasedSales.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new PurchasedSalesGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jContentPane = new JPanel();
		jContentPane.setLayout(new GridLayout(8, 1, 10, 10));
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonMoney);
		jContentPane.add(jButtonCart);
		jContentPane.add(jButtonFavourite);
		jContentPane.add(jButtonPurchasedSales);
		jContentPane.add(jButtonMovement);
		jContentPane.add(jButtonComplaint);
		jContentPane.add(jButtonClose);

		setContentPane(jContentPane);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Profile") + ": " + sellerMail);
	}
}