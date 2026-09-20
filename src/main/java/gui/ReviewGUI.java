package gui;

import businessLogic.BLFacade;

import domain.Registered;

import domain.Review;

import javax.swing.*;

import java.awt.*;

import java.awt.event.*;

import java.util.*;

import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ReviewGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private final JLabel jLabelReviews = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Reviews"));
	private JButton jButtonSearch = new JButton(
			ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JScrollPane scrollPanelReviews = new JScrollPane();
	private JTable tableReviews = new JTable();
	private DefaultTableModel tableModelReviews;
	private JFrame thisFrame;
	private String[] columnNamesReviews = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("Stars"),
			ResourceBundle.getBundle("Etiquetas").getString("Description"),
			ResourceBundle.getBundle("Etiquetas").getString("Sender"),
			ResourceBundle.getBundle("Etiquetas").getString("Receiver")
	};

	private JTextField jTextFieldSearch;
	private String sellerMail;

	public ReviewGUI(String email) {
		this.sellerMail = email;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tableReviews.setEnabled(false);
		thisFrame = this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Reviews") + ": " + sellerMail);
		jLabelReviews.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelReviews);
		
		jButtonClose.setBounds(new Rectangle(335, 379, 130, 30));
		jButtonClose.addActionListener(e -> thisFrame.dispose());
		this.getContentPane().add(jButtonClose);

		scrollPanelReviews.setBounds(new Rectangle(30, 137, 630, 220));
		scrollPanelReviews.setViewportView(tableReviews);

		tableModelReviews = new DefaultTableModel(null, columnNamesReviews);
		tableReviews.setModel(tableModelReviews);

		tableReviews.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		tableReviews.getColumnModel().getColumn(0).setPreferredWidth(50);
		tableReviews.getColumnModel().getColumn(1).setPreferredWidth(345); 
		tableReviews.getColumnModel().getColumn(2).setPreferredWidth(125); 
		tableReviews.getColumnModel().getColumn(3).setPreferredWidth(125);
		
		this.getContentPane().add(scrollPanelReviews);

		jTextFieldSearch = new JTextField();
		jTextFieldSearch.setBounds(52, 56, 357, 26);
		getContentPane().add(jTextFieldSearch);
		jButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					tableModelReviews.setRowCount(0);
					BLFacade facade = MainGUI.getBusinessLogic();
					List<Review> reviews = facade.getAllReviews();

					if (reviews.isEmpty())
						jLabelReviews.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.NoReviews"));

					else
						jLabelReviews.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Reviews"));

					for (Review r : reviews) {
						String search = jTextFieldSearch.getText().toLowerCase();

						if (search.isEmpty() || r.getSale().getTitle().toLowerCase().contains(search)
								|| r.getComment().toLowerCase().contains(search)) {

							Vector<Object> row = new Vector<Object>();
							row.add(r.getStars() + " ⭐"); 
							row.add(r.getComment()); 
							row.add(r.getReviewer().getEmail()); 
							row.add(r.getReviewee().getEmail()); 
							row.add(r);
							tableModelReviews.addRow(row);
						}

					}
					tableReviews.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN); 
					tableReviews.getColumnModel().getColumn(0).setPreferredWidth(50);
					tableReviews.getColumnModel().getColumn(1).setPreferredWidth(345); 
					tableReviews.getColumnModel().getColumn(2).setPreferredWidth(125);
					tableReviews.getColumnModel().getColumn(3).setPreferredWidth(125);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		jButtonSearch.setBounds(427, 56, 117, 29);
		getContentPane().add(jButtonSearch);
	}
}