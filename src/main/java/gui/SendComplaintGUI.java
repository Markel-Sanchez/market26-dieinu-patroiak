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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import businessLogic.BLFacade;

public class SendComplaintGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	private JLabel jLabelTitle = new JLabel();
	private JLabel jLabelDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("SendComplaintGUI.Description"));
	private JTextArea jTextAreaComplaint = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane();
	private JButton jButtonSend = new JButton(ResourceBundle.getBundle("Etiquetas").getString("SendComplaintGUI.Send"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("SendComplaintGUI.Close"));
	private String userEmail;
	private Sale sale;

	public SendComplaintGUI(Sale sale, String email) {
		this.userEmail = email;
		this.sale = sale;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("SendComplaintGUI.Title"));
		this.setLocationRelativeTo(null);

		jLabelTitle.setText("Complaint for: " + sale.getUser().getEmail());
		jLabelTitle.setHorizontalAlignment(SwingConstants.CENTER);
		jLabelTitle.setBounds(new Rectangle(20, 20, 400, 20));
		this.getContentPane().add(jLabelTitle);

		jLabelDescription.setBounds(new Rectangle(40, 60, 200, 20));
		this.getContentPane().add(jLabelDescription);

		scrollPane.setBounds(new Rectangle(40, 85, 350, 150));
		jTextAreaComplaint.setLineWrap(true);
		jTextAreaComplaint.setWrapStyleWord(true);
		scrollPane.setViewportView(jTextAreaComplaint);
		this.getContentPane().add(scrollPane);

		jButtonSend.setBounds(new Rectangle(60, 260, 130, 30));
		jButtonSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = jTextAreaComplaint.getText();
				if (text.trim().isEmpty()) {
					jLabelTitle.setText("The description cannot be blank!");
					jLabelTitle.setForeground(Color.RED);
				} else {
					BLFacade facade = MainGUI.getBusinessLogic();
					facade.putComplaint(userEmail, sale, text);
					sale.setAlreadyComplaint(true);
					System.out.println("Sending complaint: " + text);
					dispose();
				}
			}
		});
		this.getContentPane().add(jButtonSend);

		jButtonClose.setBounds(new Rectangle(240, 260, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		this.getContentPane().add(jButtonClose);
	}
}