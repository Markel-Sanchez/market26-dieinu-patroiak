package gui;

import businessLogic.BLFacade;
import configuration.UtilDate;
import domain.Registered;
import domain.Sale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class MoneyGUI extends JFrame {
	private JFrame thisFrame;
	private String sellerMail;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JButton jButtonAdd = null;
	private JButton jButtonQuit = null;
	private float actualMoney = 0;
	private JPanel jContentPane;
	private JLabel jLabelSaldo = new JLabel();
	private JLabel jLabelCuanto = new JLabel();
	private JTextField jTextFieldZenbat = new JTextField();

	public MoneyGUI(String email) {
		BLFacade facade = MainGUI.getBusinessLogic();
		this.sellerMail = email;
		Registered user = facade.getUserByEmail(email);
		this.actualMoney = user.getMoneyKop();
		this.thisFrame = this;
		this.setSize(700, 500);
		this.setLocationRelativeTo(null);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("Profile.Money") + ": " + sellerMail);

		jButtonAdd = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.jButtonAdd.text")); //$NON-NLS-1$ //$NON-NLS-2$
		jButtonAdd.setBounds(132, 291, 130, 30);

		jButtonQuit = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.jButtonQuit.text")); //$NON-NLS-1$ //$NON-NLS-2$
		jButtonQuit.setBounds(403, 291, 130, 30);

		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});

		jContentPane = new JPanel(new BorderLayout(0, 20));
		jContentPane.setBorder(new javax.swing.border.EmptyBorder(20, 50, 20, 50));

		jLabelSaldo.setHorizontalAlignment(SwingConstants.CENTER);
		jContentPane.add(jLabelSaldo, BorderLayout.NORTH);

		JPanel pnlErdia = new JPanel(new GridLayout(4, 1, 10, 10));
		pnlErdia.add(jLabelCuanto);
		pnlErdia.add(jTextFieldZenbat);

		JPanel pnlBotonesAccion = new JPanel(new GridLayout(1, 2, 10, 10));
		pnlBotonesAccion.add(jButtonAdd);
		pnlBotonesAccion.add(jButtonQuit);
		pnlErdia.add(pnlBotonesAccion);

		jContentPane.add(pnlErdia, BorderLayout.CENTER);
		JPanel pnlSur = new JPanel();
		pnlSur.add(jButtonClose);
		jContentPane.add(pnlSur, BorderLayout.SOUTH);

		this.setContentPane(jContentPane);

		updateTexts();

		jButtonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { //Erroreak sahiesteko
					float cantidad = Float.parseFloat(jTextFieldZenbat.getText());
					if (cantidad > 0) {
						facade.addMoney(sellerMail, cantidad);
						actualMoney += cantidad; // aldagai lokala eguneratu
						updateTexts();
						jTextFieldZenbat.setText("");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(thisFrame, "Please, introduce a number/Por favor, introoduce un número/Mesedez, sartu zenbaki bat.");
				}
			}
		});

		jButtonQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					float cantidad = Float.parseFloat(jTextFieldZenbat.getText());
					if (cantidad > 0 && actualMoney >= cantidad) {
						facade.quitMoney(sellerMail, cantidad);
						actualMoney -= cantidad;// aldagai lokala eguneratu
						updateTexts();
						jTextFieldZenbat.setText("");
					} else if (actualMoney < cantidad) {
						JOptionPane.showMessageDialog(thisFrame, "You have got not enough money/No tienes suficiente dinero/Ez duzu nahiko diru.");
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(thisFrame, "Please, introduce a valid number/Por favor, introduce un número válido/Mesedez, sartu zenbaki bat.");
				}
			}
		});
	}

	public void updateTexts() {
		ResourceBundle bundle = ResourceBundle.getBundle("Etiquetas");
		jLabelSaldo.setText(bundle.getString("Money.Balance") + ": " + actualMoney + "€");
		jLabelCuanto.setText(bundle.getString("Money.Amount"));
		jButtonAdd.setText(bundle.getString("MoneyGUI.jButtonAdd.text"));
		jButtonQuit.setText(bundle.getString("MoneyGUI.jButtonQuit.text"));
		jButtonClose.setText(bundle.getString("Close"));
	}
}