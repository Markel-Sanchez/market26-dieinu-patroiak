package gui;

import java.util.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.awt.image.BufferedImage;

import businessLogic.BLFacade;
import domain.Registered;
import domain.Sale;

public class ShowSaleGUI extends JFrame {

	File targetFile;
	BufferedImage targetImg;
	public JPanel panel_1;
	private static final int baseSize = 160;
	private static final String basePath = "src/main/resources/images/";
	private static final long serialVersionUID = 1L;
	private JTextField fieldTitle = new JTextField();
	private JTextField fieldDescription = new JTextField();
	JLabel labelStatus = new JLabel();
	private JLabel jLabelTitle = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Title"));
	private JLabel jLabelDescription = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Description"));
	private JLabel jLabelProductStatus = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Status"));
	private JLabel jLabelPrice = new JLabel(ResourceBundle.getBundle("Etiquetas").getString("CreateSaleGUI.Price"));
	private JTextField fieldPrice = new JTextField();
	private File selectedFile;
	private String irudia;
	private JScrollPane scrollPaneEvents = new JScrollPane();
	DefaultComboBoxModel<String> statusOptions = new DefaultComboBoxModel<String>();
	private JButton jButtonClose = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Close"));
	private JLabel jLabelMsg = new JLabel();
	private JLabel jLabelError = new JLabel();
	private JLabel statusField = new JLabel();
	private JFrame thisFrame;
	private String email;
	private JButton btnFavorite = new JButton();
	private JButton btnBuy = new JButton();
	private JButton JButtonPutComplaint;
	private JButton JButtonCart; 
	private boolean isPurchased;
	private Registered user;

	public ShowSaleGUI(Sale sale, String email, boolean isPurchased) {
		this.email = email;
		this.isPurchased = isPurchased;
		thisFrame = this;
		this.getContentPane().setLayout(null);
		this.setSize(new Dimension(700, 500));
		this.setLocationRelativeTo(null);
		this.revalidate();
		this.repaint();
		this.setVisible(true);

		fieldTitle.setText(sale.getTitle());
		fieldDescription.setText(sale.getDescription());

		fieldPrice.setText(Float.toString(sale.getPrice()));

		labelStatus.setText(new SimpleDateFormat("dd-MM-yyyy").format(sale.getPublicationDate()));

		jLabelTitle.setBounds(new Rectangle(6, 56, 92, 20));

		jLabelPrice.setBounds(new Rectangle(6, 166, 101, 20));
		fieldPrice.setEditable(false);
		fieldPrice.setBounds(new Rectangle(137, 166, 60, 20));

		scrollPaneEvents.setBounds(new Rectangle(25, 44, 346, 116));
		jButtonClose.setBounds(new Rectangle(16, 350, 100, 30));
		jButtonClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BLFacade facade = MainGUI.getBusinessLogic();
				if (facade.isAdmin(email)) {
					JFrame adminMenu = new MainAdminGUI(email);
					adminMenu.setVisible(true);
					thisFrame.setVisible(false);
				} else {
					thisFrame.setVisible(false);
				}
			}
		});

		jLabelMsg.setBounds(new Rectangle(275, 214, 305, 20));
		jLabelMsg.setForeground(Color.red);

		jLabelError.setBounds(new Rectangle(6, 231, 320, 20));
		jLabelError.setForeground(Color.red);

		this.getContentPane().add(jLabelMsg, null);
		this.getContentPane().add(jLabelError, null);

		this.getContentPane().add(jButtonClose, null);
		this.getContentPane().add(jLabelTitle, null);

		this.getContentPane().add(jLabelPrice, null);
		this.getContentPane().add(fieldPrice, null);

		jLabelProductStatus.setBounds(new Rectangle(40, 15, 140, 25));
		jLabelProductStatus.setBounds(6, 187, 140, 25);
		getContentPane().add(jLabelProductStatus);

		jLabelDescription.setBounds(6, 81, 109, 16);
		getContentPane().add(jLabelDescription);
		fieldTitle.setEditable(false);

		fieldTitle.setBounds(128, 53, 370, 26);
		getContentPane().add(fieldTitle);
		fieldTitle.setColumns(10);
		fieldDescription.setEditable(false);

		fieldDescription.setBounds(127, 81, 371, 73);
		getContentPane().add(fieldDescription);
		fieldDescription.setColumns(10);

		panel_1 = new JPanel();
		panel_1.setBounds(420, 166, 180, 160);
		getContentPane().add(panel_1);

		labelStatus.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		labelStatus.setBounds(37, 231, 289, 16);
		getContentPane().add(labelStatus);

		if (email != null && !email.equals("") && !isPurchased) {
			BLFacade facade = MainGUI.getBusinessLogic();
			user=facade.getUserByEmail(email);

			boolean isonFavs = facade.isFavorite(email, sale);
			updateFavoriteButton(isonFavs);

			btnFavorite.setBounds(new Rectangle(125, 350, 280, 30));
			this.getContentPane().add(btnFavorite);

			// Faboritoen botoia
			btnFavorite.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (facade.isFavorite(email, sale)) {
							facade.removeFavorite(email, sale);
							updateFavoriteButton(false);
						} else {
							facade.addFavorite(email, sale);
							updateFavoriteButton(true);
						}
						btnFavorite.revalidate();
						btnFavorite.repaint();

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			
		

			JButtonCart = new JButton(ResourceBundle.getBundle("Etiquetas").getString("Profile.Cart"));
			JButtonCart.setBounds(new Rectangle(555, 350, 120, 30));
			this.getContentPane().add(JButtonCart);

			// Cart botoia
			JButtonCart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						if (user.getEmail().equals(sale.getUser().getEmail())) {
			                JOptionPane.showMessageDialog(null, "You cannot add your own products to the cart!/¡No puedes añadir tus propios productos!/Ezin dituzu zure produktuak orgara sartu!");
			            } else if (user.isOnCart(sale)) {
			                facade.removeFromCart(email, sale);
			                JOptionPane.showMessageDialog(null, "Removed from cart!¡Eliminado del carrito!/Orgatik kenduta!");
			            } else if (user.isEmptyCart() || user.isSameSeller(sale)) {
			                facade.addToCart(email, sale);
			                JOptionPane.showMessageDialog(null, "Added to cart!/¡Añadaido al carrito!/Orgara sartuta!");
			            } else {
			                JOptionPane.showMessageDialog(null, "Can't enter different seller's sales to cart!!!/¡¡¡No puede añadir productos de otros vendedores!!!/Ezin dtuzu beste saltzaile baten produktuak sartu!!!");
			            }
			            
			            user = facade.getUserByEmail(email);
						
						JButtonCart.revalidate();
						JButtonCart.repaint();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});
			
			
			
			JButton btnBuy = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Buy"));

			btnBuy.setBounds(new Rectangle(415, 350, 130, 30));
			btnBuy.setBackground(Color.ORANGE);
			this.getContentPane().add(btnBuy);

			// Erosteko botoia
			btnBuy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String result = facade.buySale(sale, email);

					if (result == null) {
						JOptionPane.showMessageDialog(null,"Erosketa ondo burutu da! / Compra realizada! / Purchase done!");
						thisFrame.dispose();
					} else {
						JOptionPane.showMessageDialog(null, result, "Advise", JOptionPane.WARNING_MESSAGE);
					}
				}
			});
		} // if (sellerMail != null && !sellerMail.equals("") && !isPurchased)

		JButton jButtonSendComplaintAdmin = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.SendComplaintAdmin"));
		jButtonSendComplaintAdmin.setBounds(471, 355, 207, 20);
		getContentPane().add(jButtonSendComplaintAdmin);
		jButtonSendComplaintAdmin.setVisible(false);

		JButton jButtonPutComplaint = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.PutComplaint"));
		jButtonPutComplaint.setBounds(329, 355, 130, 20);
		getContentPane().add(jButtonPutComplaint);
		jButtonPutComplaint.setVisible(false);

		JButton jButtonDeclineComp = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Decline"));
		jButtonDeclineComp.setBounds(353, 416, 89, 23);
		getContentPane().add(jButtonDeclineComp);
		jButtonDeclineComp.setVisible(false);

		JButton jButtonAcceptComp = new JButton(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.Accept"));
		jButtonAcceptComp.setBounds(529, 416, 89, 23);
		getContentPane().add(jButtonAcceptComp);
		jButtonAcceptComp.setVisible(false);
		
		JButton jButtonPutReview = new JButton(ResourceBundle.getBundle("Etiquetas").getString("CreateReview"));
		jButtonPutReview.setBounds(190, 379, 130, 30);
		getContentPane().add(jButtonPutReview);
		jButtonPutReview.setVisible(false);

		if (email != null && !email.isEmpty() && isPurchased) {
			BLFacade facade = MainGUI.getBusinessLogic();
			
			//Review jartzeko
			if(sale.getReview() == null) {
				jButtonPutReview.setVisible(true);
			}else {
				jButtonPutReview.setVisible(false);
			}
			
			jButtonPutReview.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame a = new PutReviewGUI(email, sale);
					a.setVisible(true);
					a.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							jButtonPutReview.setVisible(false);
							getContentPane().repaint();
						}
					});
				}
			});
			
			
			// Admin-eko botoiarentzat
			jButtonSendComplaintAdmin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						facade.sendComplaintToAdmin(sale);
						JOptionPane.showMessageDialog(null,"Enviada a administrador / Administratzaileari bidalia / Sent to the administrator");

						if (sale.getComplaint() != null) {
							sale.getComplaint().setEgoera(true);
						}
						jButtonSendComplaintAdmin.setVisible(false);
						getContentPane().repaint();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			// Erreklamazioa jartzeko botoiarentzat
			jButtonPutComplaint.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFrame a = new SendComplaintGUI(sale, email);
					a.setVisible(true);
					a.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosed(java.awt.event.WindowEvent windowEvent) {
							if (sale.isAlreadyComplaint()) {
								jButtonPutComplaint.setVisible(false);
								jButtonSendComplaintAdmin.setVisible(true);
								getContentPane().repaint();
							}
						}
					});
				}
			});

			// Erreklamazioa ezeztatu (Admin)
			jButtonDeclineComp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						BLFacade facade = MainGUI.getBusinessLogic();

						facade.declineComplaint(sale);

						// Objektu lokala eguneratu
						sale.setAlreadyComplaint(false);

						JOptionPane.showMessageDialog(null,"Reclamación rechazada / Erreklamazioa atzera botata / The complaint has been dismissed");

						// Botoiak okultatu
						jButtonAcceptComp.setVisible(false);
						jButtonDeclineComp.setVisible(false);
						thisFrame.setVisible(false);
						getContentPane().repaint();

					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			// Erreklamazioa onartu (Admin edo Erregistratu normala)
			jButtonAcceptComp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					BLFacade facade = MainGUI.getBusinessLogic();

					facade.acceptComplaint(sale);
					JOptionPane.showMessageDialog(null,"Reclamación aceptada / Erreklamazioa onartuta / Complaint accepted");
					// Botoiak okultatu
					jButtonAcceptComp.setVisible(false);
					jButtonDeclineComp.setVisible(false);
					jButtonSendComplaintAdmin.setVisible(false);
					getContentPane().repaint();
				}
			});

			jButtonAcceptComp.setVisible(false);
			jButtonDeclineComp.setVisible(false);
			jButtonSendComplaintAdmin.setVisible(false);
			jButtonPutComplaint.setVisible(false);

			if (facade.isAdmin(email)) { // dataAccess-en metodoa jakiteko ea admin-a den.
				// Admin
				if (sale.isAlreadyComplaint() && sale.getComplaint() != null) {
					if (sale.getComplaint().isEgoera()) {
						jButtonAcceptComp.setVisible(true);
						jButtonDeclineComp.setVisible(true);
					}
				}
			} else {
				// Erabiltzaile arrunta
				if (isPurchased) {
					if (!sale.isAlreadyComplaint()) {
						jButtonPutComplaint.setVisible(true);
					} else if (sale.getComplaint() != null && !sale.getComplaint().isEgoera()) {
						jButtonSendComplaintAdmin.setVisible(true);
						if(email.equals(sale.getComplaint().getReceiver().getEmail())) {
							jButtonAcceptComp.setVisible(true);
						}
					}
				}
			} // if(facade.isAdmin(email))

		} // if (sellerMail != null && !sellerMail.isEmpty() && isPurchased)

		BLFacade facade = MainGUI.getBusinessLogic();
		String file = sale.getFile();
		if (file != null) {
			Image img = facade.downloadImage(file);
			targetImg = rescale((BufferedImage) img);
			panel_1.setLayout(new BorderLayout(0, 0));
			panel_1.add(new JLabel(new ImageIcon(targetImg)));
		}
		System.out.println("status: " + sale.getStatus());
		statusField = new JLabel(Utils.getStatus(sale.getStatus()));
		statusField.setBounds(137, 191, 92, 16);
		getContentPane().add(statusField);

	}

	private void updateFavoriteButton(boolean isFavorite) {
		if (isFavorite) {
			btnFavorite.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.RemoveFavorite"));
			btnFavorite.setBackground(Color.RED);
		} else {
			btnFavorite.setText(ResourceBundle.getBundle("Etiquetas").getString("ShowSaleGUI.AddFavorite"));
			btnFavorite.setBackground(Color.GREEN);
		}
	}

	public BufferedImage rescale(BufferedImage originalImage) {
		BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
		g.dispose();
		return resizedImage;
	}
}