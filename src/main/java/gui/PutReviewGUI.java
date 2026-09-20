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
import domain.Sale;
import domain.User;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.awt.event.ActionEvent;

public class PutReviewGUI extends JFrame {
	private String email;
	private Sale sale;
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JFrame thisFrame;
	private JTextField jTextFieldStars;
	private JTextArea jTextFieldComment;
	private JLabel labelComment;
	
	public PutReviewGUI (String email, Sale sale) {
		this.email = email;
		this.sale = sale;
		this.thisFrame=this;
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.getContentPane().add(jButtonClose);
		this.getContentPane().setLayout(null);
		jButtonClose.setBounds(new Rectangle(270, 354, 130, 30));
	
		jTextFieldStars = new JTextField();
		jTextFieldStars.setBounds(155, 49, 121, 45);
		getContentPane().add(jTextFieldStars);
		jTextFieldStars.setColumns(10);
		
		jTextFieldComment = new JTextArea();
		jTextFieldComment.setLineWrap(true);
		jTextFieldComment.setWrapStyleWord(true);
		jTextFieldComment.setBounds(155, 116, 342, 80);
		jTextFieldComment.setBorder(new javax.swing.border.LineBorder(java.awt.Color.LIGHT_GRAY));
		getContentPane().add(jTextFieldComment);
		
		
		JLabel labelStars = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("Stars"));
		labelStars.setBounds(35, 51, 110, 15);
		getContentPane().add(labelStars);
		
		JButton jButtonCreateReview = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateReview"));
		jButtonCreateReview.setBounds(428, 294, 130, 38);
		getContentPane().add(jButtonCreateReview);
		
		labelComment = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("Description")); //$NON-NLS-1$ //$NON-NLS-2$
		labelComment.setBounds(35, 119, 96, 12);
		getContentPane().add(labelComment);
		

		jButtonCreateReview.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	
		        try {
		        	int stars = Integer.parseInt(jTextFieldStars.getText());
		        	String comment = jTextFieldComment.getText();
		            if(stars >= 1 && stars <= 5 && !comment.equals("")) {
			            BLFacade facade = MainGUI.getBusinessLogic();
			            facade.createReview(sale, sale.getUser().getEmail(), email, stars, comment);
			            JOptionPane.showMessageDialog(null, "Review has been put! / La review se ha puesto! / Review-a jarri da!");
			            thisFrame.dispose();
		            }else {
		            	JOptionPane.showMessageDialog(null, "Cannot put that stars / No se pueden poner las estrellas / Ezin dira izarrak jarri");
		            }
		            
		        } catch (NumberFormatException ex) {
		            JOptionPane.showMessageDialog(null, "Number format exception");
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