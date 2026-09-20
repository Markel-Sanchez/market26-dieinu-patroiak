package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import businessLogic.BLFacade;
import domain.Admin;
import domain.Registered;
import domain.User;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;

public class MakeOrderGUI extends JFrame {
	private String email;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JFrame thisFrame;
	private JTextField jTextFieldTitle;
	private JTextArea jTextFieldDesc;
	private JTextField jTextFieldMin;
	private JTextField jTextFieldMax;
	private JLabel labelDesc;
	private JLabel labelPMin;
	private JLabel labelPMax;
	
	public MakeOrderGUI (String email) {
		this.email = email;
		this.thisFrame=this;
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().add(jButtonClose);
		this.getContentPane().setLayout(null);
		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));
	
		jTextFieldTitle = new JTextField();
		jTextFieldTitle.setBounds(155, 49, 121, 45);
		getContentPane().add(jTextFieldTitle);
		jTextFieldTitle.setColumns(10);
		
		jTextFieldDesc = new JTextArea();
		jTextFieldDesc.setLineWrap(true);
		jTextFieldDesc.setWrapStyleWord(true);     
		jTextFieldDesc.setBounds(155, 116, 342, 80);
		jTextFieldDesc.setBorder(new javax.swing.border.LineBorder(java.awt.Color.LIGHT_GRAY));
		getContentPane().add(jTextFieldDesc);
		
		jTextFieldMin = new JTextField();
		jTextFieldMin.setBounds(155, 230, 96, 18);
		getContentPane().add(jTextFieldMin);
		jTextFieldMin.setColumns(10);
		
		jTextFieldMax = new JTextField();
		jTextFieldMax.setBounds(155, 275, 96, 18);
		getContentPane().add(jTextFieldMax);
		jTextFieldMax.setColumns(10);
		
		JLabel labelTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.Title"));
		labelTitle.setBounds(35, 51, 110, 15);
		getContentPane().add(labelTitle);
		
		JButton jButtonCreateOrder = new JButton(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.CreateOrder"));
		jButtonCreateOrder.setBounds(428, 294, 130, 38);
		getContentPane().add(jButtonCreateOrder);
		
		labelDesc = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.Desc")); //$NON-NLS-1$ //$NON-NLS-2$
		labelDesc.setBounds(35, 119, 96, 12);
		getContentPane().add(labelDesc);
		
		labelPMin = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.PMin")); //$NON-NLS-1$ //$NON-NLS-2$
		labelPMin.setBounds(35, 232, 110, 15);
		getContentPane().add(labelPMin);
		
		labelPMax = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MakeOrderGUI.PMax")); //$NON-NLS-1$ //$NON-NLS-2$
		labelPMax.setBounds(35, 278, 110, 12);
		getContentPane().add(labelPMax);
		jButtonCreateOrder.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        try {
		            String title = jTextFieldTitle.getText();
		            String desc = jTextFieldDesc.getText();
		            float min = Float.parseFloat(jTextFieldMin.getText());
		            float max = Float.parseFloat(jTextFieldMax.getText());
		            
		            BLFacade facade = MainGUI.getBusinessLogic();
		            facade.createOrder(desc, title, min, max, email);
		            
		            JOptionPane.showMessageDialog(null, "Order created succesfully / Orden creada correctamente / Eskaera ondo sortu da");
		            thisFrame.dispose();
		            
		        } catch (NumberFormatException ex) {
		            JOptionPane.showMessageDialog(null, "Please, introduce a valid price (numbers)/Por favor, introduce precios válidos (números)/Mesedez, sartu prezio egoki bat(Zenbakia)");
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
		    }
		});
		
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thisFrame.dispose();
			}
		});
	}
}