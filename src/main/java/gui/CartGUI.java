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

public class CartGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private final JLabel jLabelProducts = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products")); 

	private JButton jButtonSearch = new JButton(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search")); 
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JButton jButtonBuyAll = new JButton(ResourceBundle.getBundle("Etiquetas").getString("BuyAll"));

	private JScrollPane scrollPanelProducts = new JScrollPane();
	private JTable tableProducts= new JTable();

	private DefaultTableModel tableModelProducts;

	private JFrame thisFrame; 

	private String[] columnNamesProducts = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"),
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.PublicationDate"),
	};
	private JTextField jTextFieldSearch;
	
	private String sellerMail;
	private Registered user;

	public CartGUI(String email) {
		this.sellerMail = email; 
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		tableProducts.setEnabled(false);
		thisFrame=this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("Profile.Cart") + ": " + sellerMail);
		
		jLabelProducts.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelProducts);

		jButtonClose.setBounds(new Rectangle(200, 376, 130, 30));
		jButtonClose.addActionListener(e -> thisFrame.dispose());	
		this.getContentPane().add(jButtonClose, null);
		
		

		scrollPanelProducts.setBounds(new Rectangle(52, 137, 459, 150));
		scrollPanelProducts.setViewportView(tableProducts);
		tableModelProducts = new DefaultTableModel(null, columnNamesProducts);
		tableProducts.setModel(tableModelProducts);

		tableModelProducts.setDataVector(null, columnNamesProducts);
		tableModelProducts.setColumnCount(4); 

		tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
		tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3));

		this.getContentPane().add(scrollPanelProducts, null);
		
		jTextFieldSearch = new JTextField();
		jTextFieldSearch.setBounds(52, 56, 357, 26);
		getContentPane().add(jTextFieldSearch);
		jTextFieldSearch.setColumns(10);
		
		jButtonSearch.addActionListener(new ActionListener() {
		 	public void actionPerformed(ActionEvent e) {
		 		try {
					tableModelProducts.setDataVector(null, columnNamesProducts);
					tableModelProducts.setColumnCount(4); 

					BLFacade facade = MainGUI.getBusinessLogic();
					
					
					user = facade.getUserByEmail(email);
					List<domain.Sale> sales = user.getCart();

					if (sales.isEmpty()) jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					else jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					
					for (domain.Sale sale : sales) {
						if (jTextFieldSearch.getText().isEmpty() || sale.getTitle().toLowerCase().contains(jTextFieldSearch.getText().toLowerCase())) {
							Vector<Object> row = new Vector<Object>();
							row.add(sale.getTitle());
							row.add(sale.getPrice());
							row.add(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPublicationDate()));
							row.add(sale);
							tableModelProducts.addRow(row);		
						}
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				tableProducts.getColumnModel().getColumn(0).setPreferredWidth(200);
				tableProducts.getColumnModel().getColumn(1).setPreferredWidth(10);
				tableProducts.getColumnModel().getColumn(1).setPreferredWidth(70);
				tableProducts.getColumnModel().removeColumn(tableProducts.getColumnModel().getColumn(3));
		 	}
		});

		jButtonSearch.setBounds(427, 56, 117, 29);
		getContentPane().add(jButtonSearch);
		
		tableProducts.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if(mouseEvent.getClickCount() == 2) {
					JTable table =(JTable) mouseEvent.getSource();
					Point point = mouseEvent.getPoint();
					int row = table.rowAtPoint(point);
					Sale s=(Sale) tableModelProducts.getValueAt(row, 3);
					new ShowSaleGUI(s, sellerMail, false);
					
		            // dena kendu erositakoa/kendutakoa ez erakusteko
					tableModelProducts.setRowCount(0); 
					
		            jLabelProducts.setText(ResourceBundle.getBundle("Etiquetas").getString("UpdateAfterAction"));
		            jLabelProducts.setForeground(Color.RED);
				}
			}
		});
		
		jButtonBuyAll.setBounds(new Rectangle(381, 376, 130, 30));
		jButtonBuyAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				
				user = facade.getUserByEmail(email);
				List<domain.Sale> sales = user.getCart();
				if(sales != null && !sales.isEmpty()) {
					boolean eragiketaEmaitza = facade.buyCart(email, sales);
					if(eragiketaEmaitza) {
						JOptionPane.showMessageDialog(null,"Purchase done!/¡Compra realizada!/Erosketa eginda!");
						thisFrame.dispose();
					}else{
						JOptionPane.showMessageDialog(null,"There was a problem, cannot buy!/!Ha habido un probmlema, no se puede comprar!/Arazo bat egon da, ezin da erosi! ");
					}
				}else {
					JOptionPane.showMessageDialog(null,"No sales in the cart!/¡No hay productos en el carrito!/Ez daude produktuak erosket-organ");
				}	
			}
		});		
		this.getContentPane().add(jButtonBuyAll, null);
	}
}