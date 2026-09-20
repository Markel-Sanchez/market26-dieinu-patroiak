package gui;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Order;

public class QueryOrdersGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private String email;
	private JFrame thisFrame;

	private JButton jButtonSearch = new JButton(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Search"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	
	private JScrollPane scrollPanelOrders = new JScrollPane();
	private JTable tableOrders = new JTable();
	private DefaultTableModel tableModelOrders;

	private JTextField jTextFieldSearch = new JTextField();
	private JLabel jLabelOrders = new JLabel();

	private String[] columnNamesOrders = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Title"), 
			"Min Price", 
			"Max Price",
			"Object"
	};

	public QueryOrdersGUI(String email) {
		this.email = email;
		this.thisFrame = this;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().add(jButtonClose);
		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));
		this.setTitle("Query Orders: " + email);

		jLabelOrders.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelOrders);

		jTextFieldSearch.setBounds(52, 56, 357, 26);
		this.getContentPane().add(jTextFieldSearch);
		jTextFieldSearch.setColumns(10);

		jButtonSearch.setBounds(427, 56, 117, 29);
		this.getContentPane().add(jButtonSearch);

		scrollPanelOrders.setBounds(new Rectangle(52, 137, 459, 150));
		
		tableModelOrders = new DefaultTableModel(null, columnNamesOrders) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		tableOrders.setModel(tableModelOrders);
		this.actualizarColumnas(); 
		
		scrollPanelOrders.setViewportView(tableOrders);
		this.getContentPane().add(scrollPanelOrders);

		jButtonClose.setBounds(new Rectangle(200, 376, 130, 30));
		this.getContentPane().add(jButtonClose);


		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});

		jButtonSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					tableModelOrders.setDataVector(null, columnNamesOrders);
					tableModelOrders.setColumnCount(4); 

					BLFacade facade = MainGUI.getBusinessLogic();
					List<Order> orders = facade.getAllOrders(); 
					System.out.println(orders.size());

					if (orders.isEmpty()) {
						jLabelOrders.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.NoProducts"));
					} else {
						jLabelOrders.setText(ResourceBundle.getBundle("Etiquetas").getString("QuerySalesGUI.Products"));
					}

					for (Order order : orders) {
						if (jTextFieldSearch.getText().isEmpty() || order.getTitle().toLowerCase().contains(jTextFieldSearch.getText().toLowerCase())) {
							Vector<Object> row = new Vector<Object>();
							row.add(order.getTitle());
							row.add(order.getPriceMin());
							row.add(order.getPriceMax());
							row.add(order);
							tableModelOrders.addRow(row);
						}
					}
					actualizarColumnas(); 
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		tableOrders.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					Point point = mouseEvent.getPoint();
					int row = tableOrders.rowAtPoint(point);
					if (row != -1) {
						Order o = (Order) tableModelOrders.getValueAt(row, 3);
						ShowOrdersGUI s = new ShowOrdersGUI(o,email);
						s.setVisible(true);
					}
				}
			}
		});
	}

	/**
	 * Método auxiliar para configurar las columnas de la tabla
	 */
	private void actualizarColumnas() {
		tableOrders.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableOrders.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableOrders.getColumnModel().getColumn(2).setPreferredWidth(70);
		
		if (tableOrders.getColumnCount() == 4) {
			tableOrders.getColumnModel().removeColumn(tableOrders.getColumnModel().getColumn(3));
		}
	}
}