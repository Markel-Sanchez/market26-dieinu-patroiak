package gui;

/**
 * @author Software Engineering teachers
 */

import javax.swing.*;

import businessLogic.BLFacade;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainGUIErregistratua extends JFrame {

	private String sellerMail;
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButtonCreateQuery = null;
	private JButton jButtonQueryQueries = null;
	private JButton jButtonLogOut = null;
	private JButton jButtonProfile = null;
	private JFrame nirePantaila;
	private JButton jButtonReviews=null; 
	private JFrame logout;
	private static BLFacade appFacadeInterface;
	private JButton jButtonOrders=null; 

	public static BLFacade getBusinessLogic() {
		return appFacadeInterface;
	}

	public static void setBussinessLogic(BLFacade facade) {
		appFacadeInterface = facade;
	}

	protected JLabel jLabelSelectOption;
	private JRadioButton rdbtnNewRadioButton;
	private JRadioButton rdbtnNewRadioButton_1;
	private JRadioButton rdbtnNewRadioButton_2;
	private JPanel panel;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * This is the default constructor
	 */
	public MainGUIErregistratua(String email) {
		super();
		nirePantaila = this;
		this.sellerMail = email;

		this.setSize(700, 500); 
		this.setLocationRelativeTo(null);
		jLabelSelectOption = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jLabelSelectOption.setFont(new Font("Tahoma", Font.BOLD, 13));
		jLabelSelectOption.setForeground(Color.BLACK);
		jLabelSelectOption.setHorizontalAlignment(SwingConstants.CENTER);

		rdbtnNewRadioButton = new JRadioButton("English");
		rdbtnNewRadioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("en"));
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton);

		rdbtnNewRadioButton_1 = new JRadioButton("Euskara");
		rdbtnNewRadioButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Locale.setDefault(new Locale("eus"));
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_1);

		rdbtnNewRadioButton_2 = new JRadioButton("Castellano");
		rdbtnNewRadioButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Locale.setDefault(new Locale("es"));
				paintAgain();
			}
		});
		buttonGroup.add(rdbtnNewRadioButton_2);

		panel = new JPanel();
		panel.add(rdbtnNewRadioButton_1);
		panel.add(rdbtnNewRadioButton_2);
		panel.add(rdbtnNewRadioButton);

		jButtonCreateQuery = new JButton();
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonCreateQuery.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new CreateSaleGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jButtonQueryQueries = new JButton();
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonQueryQueries.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new QuerySalesGUI(sellerMail);

				a.setVisible(true);
			}
		});

		jButtonLogOut = new JButton();
		jButtonLogOut.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.LogOut"));
		jButtonLogOut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				MainGUIErregistratua.this.setVisible(false);
				if (logout == null) {
					logout = new MainGUI(sellerMail);
					logout.setVisible(true);
				} else {
					logout.setVisible(true);
				}
			}
		});

		jButtonProfile = new JButton();
		jButtonProfile.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Profile"));
		jButtonProfile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a = new ProfileGUI(sellerMail);
				a.setVisible(true);
			}
		});

		jButtonOrders = new JButton();
		jButtonOrders.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Orders"));
		jButtonOrders.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JFrame a= new OrdersGUI(sellerMail);
				a.setVisible(true);;
			}
		});

		jButtonReviews = new JButton(); 
		jButtonReviews.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Reviews")); 
		jButtonReviews.addActionListener(new java.awt.event.ActionListener() { 
			public void actionPerformed(java.awt.event.ActionEvent e) { 
				JFrame a= new ReviewGUI(sellerMail); 
				a.setVisible(true);; 
			} 
		}); 
		
		jContentPane = new JPanel();
		jContentPane.setLayout(new GridLayout(8, 1, 10, 10));
		jContentPane.add(jLabelSelectOption);
		jContentPane.add(jButtonProfile);
		jContentPane.add(jButtonCreateQuery);
		jContentPane.add(jButtonQueryQueries);
		jContentPane.add(jButtonOrders);
		jContentPane.add(jButtonReviews);
		jContentPane.add(jButtonLogOut);
		jContentPane.add(panel);

		setContentPane(jContentPane);
		setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + sellerMail);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
	}

	private void paintAgain() {
		jLabelSelectOption.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.SelectOption"));
		jButtonQueryQueries.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.QuerySales"));
		jButtonCreateQuery.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.CreateSale"));
		jButtonLogOut.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.LogOut"));
		jButtonProfile.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUIErregistratua.Profile"));
		this.setTitle(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.MainTitle") + ": " + sellerMail);
		jButtonOrders.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Orders"));
		jButtonReviews.setText(ResourceBundle.getBundle("Etiquetas").getString("MainGUI.Reviews")); 
	}
} // @jve:decl-index=0:visual-constraint="0,0"