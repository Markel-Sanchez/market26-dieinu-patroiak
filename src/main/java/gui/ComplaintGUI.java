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

public class ComplaintGUI extends JFrame {
	private JFrame thisFrame;
	private String sellerMail;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.Close"));

	public ComplaintGUI(String email) {
		this.sellerMail = email;
		this.thisFrame = this;
		this.setSize(700, 500);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("Profile.Complaint") + ": " + sellerMail);

		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});
		this.getContentPane().add(jButtonClose);

		JButton jButtonReceivedComp = new JButton(
				ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.ReceivedComplaints")); //$NON-NLS-1$ //$NON-NLS-2$

		jButtonReceivedComp.setBounds(79, 178, 223, 69);
		getContentPane().add(jButtonReceivedComp);

		JButton jButtonSentComp = new JButton(
				ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.SentComplaints")); //$NON-NLS-1$ //$NON-NLS-2$
		jButtonSentComp.setBounds(356, 178, 250, 69);
		getContentPane().add(jButtonSentComp);

		jButtonReceivedComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				Registered user = facade.getUserByEmail(sellerMail);
				if (user != null) {
					JFrame a = new ShowComplaintsGUI(email, "Jasotakoak");
					a.setVisible(true);
				}
			}
		});

		jButtonSentComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				Registered user = facade.getUserByEmail(sellerMail);
				if (user != null) {
					JFrame a = new ShowComplaintsGUI(email, "Bidalitakoak");
					a.setVisible(true);
				}
			}
		});
	}
}