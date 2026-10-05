package gui;

import businessLogic.BLFacade;
import domain.Registered;
import domain.Sale;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class PurchasedSalesGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String MAIN_GUI_PURCHASED = "MainGUIErregistratua_Purchased";
	private final JLabel jLabelProducts = new JLabel(
			ResourceBundle.getBundle("Etiquetas").getString(MAIN_GUI_PURCHASED));
	private JButton jButtonSearch = new JButton(
			ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts = new JTable();
	private DefaultTableModel tableModelProducts;
	private JTextField jTextFieldSearch = new JTextField();
	private JFrame thisFrame;
	private String sellerMail;
	private String[] columnNamesProducts = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"), };

	public PurchasedSalesGUI(String email) {
		this.sellerMail = email;
		this.thisFrame = this;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(null);

		tableProducts.setEnabled(false);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString(MAIN_GUI_PURCHASED) + ": " + sellerMail);

		jLabelProducts.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelProducts);

		jTextFieldSearch.setBounds(52, 56, 357, 26);
		this.getContentPane().add(jTextFieldSearch);

		jButtonClose.setBounds(new Rectangle(220, 379, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});
		this.getContentPane().add(jButtonClose);

		scrollPanelProducts.setBounds(new Rectangle(52, 137, 459, 150));
		tableModelProducts = new DefaultTableModel(null, columnNamesProducts);
		tableProducts.setModel(tableModelProducts);

		tableModelProducts.setColumnCount(4);
		updateColumWidth();

		scrollPanelProducts.setViewportView(tableProducts);
		this.getContentPane().add(scrollPanelProducts);

		jButtonSearch.setBounds(427, 56, 117, 29);
		jButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try { //Erroreak sahiesteko
					jLabelProducts.setForeground(Color.BLACK);
					tableModelProducts.setRowCount(0);

					BLFacade facade = MainGUI.getBusinessLogic();
					Registered user = facade.getUserByEmail(email);
					List<domain.Sale> purchasedSales = user.getPurchasedSales();

					if (purchasedSales == null || purchasedSales.isEmpty()) {
						jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.NoPurchased"));
					} else {
						jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString(MAIN_GUI_PURCHASED));

						for (domain.Sale sale : purchasedSales) {
							if (jTextFieldSearch.getText().isEmpty() || sale.getTitle().toLowerCase()
									.contains(jTextFieldSearch.getText().toLowerCase())) {

								Vector<Object> row = new Vector<Object>();
								row.add(sale.getTitle());
								row.add(sale.getPrice());
								row.add(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPublicationDate()));
								row.add(sale);
								tableModelProducts.addRow(row);
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				updateColumWidth();
			}
		});
		this.getContentPane().add(jButtonSearch);

		// DOBLE KLIK
		tableProducts.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					int row = tableProducts.rowAtPoint(mouseEvent.getPoint());
					if (row != -1) {
						Sale s = (Sale) tableModelProducts.getValueAt(row, 3);
						new ShowSaleGUI(s, sellerMail, true);
						
						// dena kendu erositakoa/kendutakoa ez erakusteko
						tableModelProducts.setRowCount(0); 
						
			            jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("UpdateAfterAction"));
			            jLabelProducts.setForeground(Color.RED);
					}
				}
			}
		});
	}

	private void updateColumWidth() {
		if (tableProducts.getColumnModel().getColumnCount() >= 3) {
			tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
			tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);
			tableProducts.getColumnModel().getColumn(2).setPreferredWidth(150);
			if (tableProducts.getColumnModel().getColumnCount() == 4) {
				tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3));
			}
		}
	}
}