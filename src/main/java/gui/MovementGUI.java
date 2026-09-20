package gui;

import businessLogic.BLFacade;
import domain.Movement;
import domain.Registered;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class MovementGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private final JLabel jLabelMovements = new JLabel(
			ResourceBundle.getBundle("Etiquetas").getString("MovementGUI.Movements"));
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JScrollPane scrollPanelMovements = new JScrollPane();
	private JTable tableMovements = new JTable();
	private DefaultTableModel tableModelMovements;
	private JFrame thisFrame;
	private String[] columnNamesMovements = new String[] {
			ResourceBundle.getBundle("Etiquetas").getString("MovementGUI.Concept"),
			ResourceBundle.getBundle("Etiquetas").getString("MovementGUI.Amount"),
			ResourceBundle.getBundle("Etiquetas").getString("MovementGUI.Date"), };
	private String sellerMail;

	public MovementGUI(String email) {
		this.sellerMail = email;
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		tableMovements.setEnabled(false);
		thisFrame = this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("Profile.Movement"));

		jLabelMovements.setBounds(52, 108, 427, 16);
		this.getContentPane().add(jLabelMovements);

		jButtonClose.setBounds(new Rectangle(220, 379, 130, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.setVisible(false);
			}
		});
		this.getContentPane().add(jButtonClose, null);

		scrollPanelMovements.setBounds(new Rectangle(52, 137, 459, 150));
		scrollPanelMovements.setViewportView(tableMovements);

		tableModelMovements = new DefaultTableModel(null, columnNamesMovements);
		tableMovements.setModel(tableModelMovements);
		tableModelMovements.setDataVector(null, columnNamesMovements);

		tableModelMovements.setColumnCount(4);
		tableMovements.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableMovements.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableMovements.getColumnModel().getColumn(2).setPreferredWidth(70);

		tableMovements.getColumnModel().removeColumn(tableMovements.getColumnModel().getColumn(3));

		this.getContentPane().add(scrollPanelMovements, null);

		this.updateTable();
	}

	private void updateTable() {
		try { //Erroreak sahiesteko
			tableModelMovements.setDataVector(null, columnNamesMovements);
			tableModelMovements.setColumnCount(4);

			BLFacade facade = MainGUI.getBusinessLogic();
			Registered user = facade.getUserByEmail(sellerMail);
			List<domain.Movement> movements = user.getMovementList();

			if (movements == null || movements.isEmpty()) {
				jLabelMovements.setText(ResourceBundle.getBundle("Etiquetas").getString("MovementGUI.NoMovements"));
			} else {
				jLabelMovements.setText(ResourceBundle.getBundle("Etiquetas").getString("MovementGUI.Movements"));
				for (domain.Movement m : movements) {
					Vector<Object> row = new Vector<Object>();
					row.add(m.getDescription());
					row.add(m.getAmount());
					row.add(new SimpleDateFormat("dd-MM-yyyy").format(m.getMovementDate()));
					row.add(m);
					tableModelMovements.addRow(row);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tableMovements.getColumnModel().getColumn(0).setPreferredWidth(200);
		tableMovements.getColumnModel().getColumn(1).setPreferredWidth(70);
		tableMovements.getColumnModel().getColumn(2).setPreferredWidth(70);
		tableMovements.getColumnModel().removeColumn(tableMovements.getColumnModel().getColumn(3));
	}
}