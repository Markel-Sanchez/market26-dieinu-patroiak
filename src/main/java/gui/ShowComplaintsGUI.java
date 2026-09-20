package gui;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import businessLogic.BLFacade;
import domain.Complaint;
import domain.Registered;
import domain.Sale;

public class ShowComplaintsGUI extends JFrame {

	private JTable table;
	private String email;
	private List<Complaint> currentComplaints;
	private DefaultTableModel tableModel;
	private JFrame thisFrame;
	private String[] columnNames = { ResourceBundle.getBundle("Etiquetas").getString("ShowComplaint.Title"),
			ResourceBundle.getBundle("Etiquetas").getString("ShowComplaint.Date"),
			ResourceBundle.getBundle("Etiquetas").getString("ShowComplaint.Description") };

	public ShowComplaintsGUI(String email, String title) {
		this.email = email;
		thisFrame = this;
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("ComplaintGUI.Complaints") + ": " + email);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout());

		tableModel = new DefaultTableModel(null, columnNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		BLFacade facade = MainGUI.getBusinessLogic();
		Registered user = facade.getUserByEmail(email);
		if (title.equals("Admin")) {
			this.currentComplaints = facade.getAdminComplaints();

		} else if (title.equals("Jasotakoak")) {
			this.currentComplaints = user.getReceivedComplaintList();
		} else {
			this.currentComplaints = user.getSentComplaintList();
		}

		for (Complaint c : currentComplaints) {
			Vector<Object> row = new Vector<Object>();

			if (c.getSale() != null) {
				row.add(c.getSale().getTitle());
			} else {
				row.add("Sin producto");
			}
			row.add(c.getData());
			row.add(c.getZergatia());
			tableModel.addRow(row);
		}

		JPanel panelBoton = new JPanel();
		JButton btnCerrar = new JButton("Cerrar");

		table = new JTable(tableModel);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					if (row != -1) {
						Complaint selectedComplaint = currentComplaints.get(row);
						Sale s = selectedComplaint.getSale();
						if (s != null) {
							String emaila = email;
							JFrame a = new ShowSaleGUI(s, emaila, true);
							a.setVisible(true);
							thisFrame.setVisible(false);
						} else {
							JOptionPane.showMessageDialog(null, "Error");
						}
					}
				}
			}
		});

		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(450);

		btnCerrar.addActionListener(e -> dispose());

		panelBoton.add(btnCerrar);
		this.getContentPane().add(panelBoton, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane(table);
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
}