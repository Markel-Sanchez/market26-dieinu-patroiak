package dataAccess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import configuration.ConfigXML;
import configuration.UtilDate;
import domain.Registered;
import domain.Review;
import domain.Admin;
import domain.Complaint;
import domain.Movement;
import domain.Order;
import domain.Sale;
import domain.User;
import exceptions.FileNotUploadedException;
import exceptions.MustBeLaterThanTodayException;
import exceptions.SaleAlreadyExistException;

public class DataAccess {
    private EntityManager db;
    private EntityManagerFactory emf;
    private static final int BASE_SIZE = 160;
    private static final String basePath = "src/main/resources/images/";
    private boolean initializeDB = false;
    ConfigXML c = ConfigXML.getInstance();
    
    /**
     * It implements the data access to the objectDb database
     */
    public DataAccess() {
        if (initializeDB) {
            String fileName = c.getDbFilename();
            File fileToDelete = new File(fileName);
            if (fileToDelete.delete()) {
                File fileToDeleteTemp = new File(fileName + "$");
                fileToDeleteTemp.delete();
            }
        }
        open(); 
        if (initializeDB) initializeDB();
        System.out.println("DataAccess created and OPEN");
    }
    
    
    /**
	 * This method  initializes the database with some products and sellers.
	 * This method is invoked by the business logic (constructor of BLFacadeImplementation) when the option "initialize" is declared in the tag dataBaseOpenMode of resources/config.xml file
	 */	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try { 
	       
		    //Create sellers 
			Registered seller1=new Registered("seller1@gmail.com","Aitor Fernandez","aurrera", 0);
			Registered seller2=new Registered("seller2@gmail.com","Ane Gaztañaga","aurrera", 0);
			Registered seller3=new Registered("seller3@gmail.com","Test Seller","aurrera", 0);
			Admin admin= new Admin("admin@ehu.eus", "Imanol Egaña","aurrera");

			
			//Create products
			Date today = UtilDate.trim(new Date());
			

			seller1.addSale("futbol baloia", "oso polita, gutxi erabilita", 2, 10,  today, null, false);
			seller1.addSale("salomon mendiko botak", "44 zenbakia, 3 ateraldi",2,  20,  today, null, false);
			seller1.addSale("samsung 42\" telebista", "berria, erabili gabe", 1, 175,  today, null, false);


			seller2.addSale("imac 27", "7 urte, dena ondo dabil", 1, 200,today, null, false);
			seller2.addSale("iphone 17", "oso gutxi erabilita", 2, 400, today, null, false);
			seller2.addSale("orbea mendiko bizikleta", "29\" 10 urte, mantenua behar du", 3,225, today, null, false);
			seller2.addSale("polar kilor erlojua", "Vantage M, ondo dago", 3, 30, today, null, false);

			seller3.addSale("sukaldeko mahaia", "1.8*0.8, 4 aulkiekin. Prezio finkoa", 3,45, today, null, false);

			
			db.persist(seller1);
			db.persist(seller2);
			db.persist(seller3);
			db.persist(admin);

	
			db.getTransaction().commit();
			System.out.println("Db initialized");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	/**
     * Datu-basearekiko konexioa irekitzen du.
     * EntityManagerFactory-a konfiguratzen du datu-basea lokala edo urrunekoa den arabera.
     */
    public void open() {
        String fileName = c.getDbFilename();
        if (c.isDatabaseLocal()) {
            emf = Persistence.createEntityManagerFactory("objectdb:" + fileName);
            db = emf.createEntityManager();
        } else {
            Map<String, String> properties = new HashMap<String, String>();
            properties.put("javax.persistence.jdbc.user", c.getUser());
            properties.put("javax.persistence.jdbc.password", c.getPassword());
            emf = Persistence.createEntityManagerFactory("objectdb://" + c.getDatabaseNode() + ":" + c.getDatabasePort() + "/" + fileName, properties);
            db = emf.createEntityManager();
        }
    }

    
    /**
	 * This method creates/adds a product to a seller
	 * 
	 * @param title of the product
	 * @param description of the product
	 * @param status 
	 * @param selling price
	 * @param category of a product
	 * @param publicationDate
	 * @return Product
 	 * @throws SaleAlreadyExistException if the same product already exists for the seller
	 */
    public Sale createSale(String title, String description, int status, float price, Date pubDate, String sellerEmail, File file) 
            throws FileNotUploadedException, MustBeLaterThanTodayException, SaleAlreadyExistException {
        
        if (pubDate.before(UtilDate.trim(new Date()))) {
            throw new MustBeLaterThanTodayException(ResourceBundle.getBundle("Etiquetas").getString("DataAccess.ErrorSaleMustBeLaterThanToday"));
        }
        if (file == null) throw new FileNotUploadedException("No file");

        db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered seller = db.find(Registered.class, sellerEmail);
            if (seller == null) {
                db.getTransaction().rollback();
                return null;
            }
            if (seller.getSales() != null) seller.getSales().size();

            if (seller.doesSaleExist(title)) {
                db.getTransaction().rollback();
                throw new SaleAlreadyExistException("Sale already exists");
            }

            Sale sale = seller.addSale(title, description, status, price, pubDate, file, false);
            db.persist(sale);
            db.getTransaction().commit();
            return sale;
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            throw e;
        }
    }
    
    
    /**
	 * This method retrieves all the products that contain a desc text in a title
	 * 
	 * @param desc the text to search
	 * @return collection of products that contain desc in a title
	 */
    public List<Sale> getSales(String desc) { //Aldatuta hainbat errore ematen zizkigutelako
        TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.sold = false", Sale.class);
        query.setParameter(1, "%" + desc + "%");
        return query.getResultList();
    }
    
    
    /**
     * Argitaratutako salmentak berreskuratzen ditu, testu bidez iragazita eta 
     * data jakin bat baino lehenagokoak direnak.
     * * @param desc Bilatu nahi den testua.
     * @param pubDate Gehienezko argitaratze data.
     * @return Argitaratutako salmenten zerrenda.
     */
    public List<Sale> getPublishedSales(String desc, Date pubDate) { //Aldatuta hainbat errore ematen zizkigutelako
        TypedQuery<Sale> query = db.createQuery("SELECT s FROM Sale s WHERE s.title LIKE ?1 AND s.pubDate <= ?2 AND s.sold = false", Sale.class);
        query.setParameter(1, "%" + desc + "%");
        query.setParameter(2, pubDate);
        return query.getResultList();
    }
    
    /**
     * Salmenta bat gehitzen du erabiltzailearen gogokoen zerrendara.
     * * @param email Erabiltzailearen posta elektronikoa.
     * @param s Gehitu nahi den Sale objektua.
     */
    public void addFavorite(String email, Sale s) {
        db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            Sale saleDB = db.find(Sale.class, s.getSaleNumber());
            if (u != null && saleDB != null && !u.getFavorites().contains(saleDB)) {
                u.getFavorites().add(saleDB);
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback(); //Arazoak sahiesteko aurkitu genuen trikimailua
        }
    }
    
    
    /**
     * Salmenta bat kentzen du erabiltzailearen gogokoen zerrendatik.
     * * @param email Erabiltzailearen posta elektronikoa.
     * @param s Kendu nahi den Sale objektua.
     */
    public void removeFavorite(String email, Sale s) {
        db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            Sale saleDB = db.find(Sale.class, s.getSaleNumber());
            if (u != null && saleDB != null) u.getFavorites().remove(saleDB);
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback(); //Arazoak sahiesteko aurkitu genuen trikimailua
        }
    }
    
    
    /**
     * Salmenta jakin bat erabiltzailearen gogokoetan dagoen ala ez egiaztatzen du.
     * * @param email Erabiltzailearen posta elektronikoa.
     * @param s Egiaztatu nahi den salmenta.
     * @return true gogokoa bada, false bestela.
     */
    public boolean isFavorite(String email, Sale s) {
        // DB badaezpada ireki
        if (db == null || !db.isOpen()) {
            open();
        }
        try { //Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            if (u == null || s == null) {
            	return false;
            }
            for (Sale fav : u.getFavorites()) {
                // Erreferentzia arazoak sahiesteko
                if (fav.getSaleNumber().equals(s.getSaleNumber())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    

    /**
     * Erosketa prozesua burutzen du: salmenta erosleari esleitu, gogokoetatik kendu 
     * eta salmenta "erosita" bezala markatzen du (seller = null jarriz).
     * * @param sale Erosi nahi den salmenta.
     * @param buyerEmail Eroslearen posta elektronikoa.
     * @throws Exception 
     */
    public String buySale(Sale sale, String buyerEmail)  {
        if (!db.getTransaction().isActive()) {
        	db.getTransaction().begin();
        }
        try { //Erroreak sahiesteko
            Registered buyer = db.find(Registered.class, buyerEmail);
            Sale s = db.find(Sale.class, sale.getSaleNumber());//bestela errorea ematen du, DB-an bilatu
            Registered seller = s.getUser();
            
            
            if (buyer != null && seller != null) {
            	
            	float price = s.getPrice();
                
            	if (buyer.getEmail().equals(seller.getEmail())) {
                    return "Can't buy own sale";
                }
            	
                if (buyer.getMoneyKop() < price) {
                    return "Not enough money";
                }
                buyer.setMoneyKop(buyer.getMoneyKop() - price);  
                seller.setMoneyKop(seller.getMoneyKop() + price);
                
                buyer.addPurchasedSale(s);
                s.setSold(true);

                if (buyer.getFavorites() != null) {
                    buyer.getFavorites().remove(s);
                }
                
                //(HAY QUE CAMBIAR!!)
                addMovement(buyerEmail, ResourceBundle.getBundle("Etiquetas").getString("Buy") + ": " + s.getTitle(), -s.getPrice()); //Erosi duenari mugimendua gehitu 
                addMovement(seller.getEmail(), ResourceBundle.getBundle("Etiquetas").getString("Sell") + ": " + s.getTitle(), s.getPrice()); //Saldu duenari mugimendua gehitu
            }
            db.getTransaction().commit();
            return null;
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Erabiltzaile baten kredentzialak zuzenak diren egiaztatzen du.
     * * @param mail Posta elektronikoa.
     * @param pass Pasahitza.
     * @return Seller objektua zuzena bada, null bestela.
     */
    public User isLogged(String mail, String pass) {
        TypedQuery<User> query = db.createQuery("SELECT s FROM User s WHERE s.email=?1 AND s.pass=?2", User.class);
        query.setParameter(1, mail);
        query.setParameter(2, pass);
        List<User> list = query.getResultList();
        return list.isEmpty() ? null : list.get(0); //if isEmpty return null, else return lista
    }
    
    
    /**
     * Saltzaile berri bat erregistratzen du sisteman.
     * * @param mail Posta elektroniko bakarra.
     * @param log Erabiltzaile izena (login).
     * @param pass Pasahitza.
     * @return Sortutako Seller objektua.
     */
    public Registered register(String mail, String log, String pass) {
        db.getTransaction().begin();
        Registered seller = new Registered(mail, log, pass, 0);
        try {//Erroreak sahiesteko
            db.persist(seller);
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
        }
        return seller;
    }
    
    /**
     * Erabiltzaile baten kontuan dirua gehitzeko metodoa.
     * Transakzioa kudeatzen du eta mugimendu bat sortzen du historian.
     * * @param email Erabiltzailearen helbide elektronikoa.
     * @param amount Gehitu nahi den diru kopurua.
     */
    public void addMoney(String email, float amount) {
    	db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            if (u != null) {
            	float kop = u.getMoneyKop();
                u.setMoneyKop(kop + amount);
                String deskripzioa = ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.jButtonAdd.text");
                addMovement(email, deskripzioa, amount);
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
        }
    }
    
    /**
     * Erabiltzaile baten kontutik dirua kentzeko metodoa.
     * Transakzioa kudeatzen du eta diru-kentze mugimendua erregistratzen du.
     * * @param email Erabiltzailearen helbide elektronikoa.
     * @param amount Kendu nahi den diru kopurua.
     */
    public void quitMoney(String email, float amount) { 
        db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            if (u != null) {
            	float kop = u.getMoneyKop();
                u.setMoneyKop(kop - amount);
                String deskripzioa = ResourceBundle.getBundle("Etiquetas").getString("MoneyGUI.jButtonQuit.text");
                addMovement(email, deskripzioa, -amount);
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
        }
    }
    
    /**
     * Diru-mugimendu berri bat sortu eta erabiltzaileari esleitzen dio.
     * Oharra: Metodo honek ez du transakzio propiorik irekitzen.
     * * @param email Erabiltzailearen helbide elektronikoa.
     * @param deskripzioa Mugimenduaren azalpena.
     * @param kop Diru kopurua (positiboa edo negatiboa).
     */
    public void addMovement(String email, String deskripzioa, float kop) { //cambiar este metodo de dataccess a user/registered
        try { //Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            if (u != null) {
                Movement m = new Movement(deskripzioa, kop, this.getUserByEmail(email));
                u.getMovementList().add(m); //Ez da transakziorik irekitzen beste metodoetatik deitzen delako.
            }
        } catch (Exception e) {
            System.err.println("Error in addMovement: " + e.getMessage());
            throw e; 
        }
    }
    
    /**
     * Salmenta bati buruzko erreklamazio berri bat sortu eta datu-basean gordetzen du.
     * Eroslearen eta saltzailearen zerrendak eguneratzen ditu.
     * * @param email Erreklamazioa jartzen duen eroslearen helbidea.
     * @param s Lotutako salmenta.
     * @param deskripzioa Erreklamazioaren xehetasunak.
     */
    public void putComplaint(String email, Sale s, String deskripzioa) {
    	db.getTransaction().begin();
    	try { //Erroreak sahiesteko
        	Registered buyer=db.find(Registered.class, email);
        	Sale sale=db.find(Sale.class,s.getSaleNumber());
        	Registered seller = sale.getUser();
        	if(buyer!=null) {
        		Complaint comp= new Complaint(new Date(),deskripzioa, sale, buyer, seller);
        		db.persist(comp);
        		buyer.getSentComplaintList().add(comp);
        		seller.getReceivedComplaintList().add(comp);
        		sale.setComplaint(comp);
        		sale.setAlreadyComplaint(true);
        	}
        	db.getTransaction().commit();
        } catch (Exception e) {
        	if (db.getTransaction().isActive()) db.getTransaction().rollback();
        }
    }
    
    /**
     * Email baten bidez erregistratutako erabiltzailearen objektua lortzen du.
     * * @param email Erabiltzailearen helbidea.
     * @return Registered objektua.
     */
    public Registered getUserByEmail(String email) {
        return db.find(Registered.class, email);
    }
    
    /**
     * Administratzaileak kudeatu behar dituen erreklamazioen zerrenda lortzen du.
     * (Egoera 'true' duten erreklamazioak).
     * * @return Administratzailearentzako erreklamazio zerrenda.
     */
    public List<Complaint> getAdminComplaints() {
        List<Complaint> adminComplaints = new ArrayList<>();
        try { //Erroreak sahiesteko
            TypedQuery<Complaint> query = db.createQuery("SELECT c FROM Complaint c WHERE c.egoera = true", Complaint.class);    
            adminComplaints = query.getResultList();
            for (Complaint complaint : adminComplaints) {
                if (complaint.getSale() != null) {
                    complaint.getSale().getTitle(); 
                    if (complaint.getSale().getUser() != null) {
                        complaint.getSale().getUser().getEmail(); 
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener quejas para el admin: " + e.getMessage());
        }
        return adminComplaints;
    }
    
    /**
     * Salmenta bati lotutako erreklamazioa administratzaileari bidaltzen dio kudeatzeko.
     * * @param s Erreklamazioa duen salmenta.
     */
    public void sendComplaintToAdmin(Sale s) {
        db.getTransaction().begin();
        try { //Erroreak sahiesteko
            Sale sale = db.find(Sale.class, s.getSaleNumber());
            TypedQuery<Complaint> q = db.createQuery("SELECT c FROM Complaint c WHERE c.sale = :s", Complaint.class);
            q.setParameter("s", sale);
            Complaint c = q.getSingleResult();
            c.setEgoera(true);
            db.getTransaction().commit();
        } catch (Exception e) {
            db.getTransaction().rollback();
        }
    }
    
    /**
     * Emandako posta elektronikoa administratzaile batena den ala ez egiaztatzen du.
     * * @param email Egiaztatu nahi den helbidea.
     * @return True administratzailea bada, false bestela.
     */
    public boolean isAdmin(String email) { //Admin bada jakiteko
        User user = db.find(User.class, email);  
        if (user == null) {
        	return false;
        }else {
            return user instanceof Admin;
        }        
    }
    
    /**
     * Erreklamazio bat ezesten du. Erreklamazioa ezabatu egiten da 
     * eta salmenta egoera berrezartzen da.
     * * @param s Lotutako salmenta.
     */
    public void declineComplaint(Sale s) {
        db.getTransaction().begin();
        try { //Erroreak sahiesteko
            Sale sale = db.find(Sale.class, s.getSaleNumber());
            if (sale != null && sale.getComplaint() != null) {
                Complaint comp = sale.getComplaint();
                Registered sender = comp.getSender();
                Registered receiver = comp.getReceiver();
                
                if (sender != null) {
                    sender.getSentComplaintList().remove(comp);
                }
                if (receiver != null) {
                    receiver.getReceivedComplaintList().remove(comp);
                }
                
                sale.setComplaint(null);
                sale.setAlreadyComplaint(false);
                db.remove(comp); 
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            db.getTransaction().rollback();
        }
    }
    
    /**
     * Erreklamazioa onartzen du. Dirua erosleari itzultzen dio, 
     * saltzaileari kendu eta salmenta "ez saldua" bezala markatzen du.
     * * @param s Lotutako salmenta.
     */
    public void acceptComplaint(Sale s) {
        db.getTransaction().begin();
        try { //Erroreak sahiesteko
            Sale sale = db.find(Sale.class, s.getSaleNumber());
            
            if (sale != null && sale.getComplaint() != null) {
                Complaint comp = sale.getComplaint();
                
                Registered buyer = comp.getSender();   
                Registered seller = comp.getReceiver();           

                float price = sale.getPrice();
                if (buyer != null) {
                	buyer.getSentComplaintList().remove(comp);
                	buyer.getPurchasedSales().remove(sale);
                    buyer.setMoneyKop(buyer.getMoneyKop() + price);
                    addMovement(buyer.getEmail(), ResourceBundle.getBundle("Etiquetas").getString("Devolucion") + ": " + s.getTitle() , price);
                }
                if (seller != null) {
                	seller.getReceivedComplaintList().remove(comp);
                    seller.setMoneyKop(seller.getMoneyKop() - price);
                    addMovement(seller.getEmail(),ResourceBundle.getBundle("Etiquetas").getString("Devolucion") + ": " + s.getTitle() , -price);
                }   
                sale.setComplaint(null);
                sale.setAlreadyComplaint(false); 
                sale.setSold(false);             
                db.remove(comp);
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback();
            e.printStackTrace();
        }
    }
    
    /**
     * Produktu bat erabiltzailearen erosketa-saskira gehitzen du.
     * @param email Erabiltzailearen identifikazio elektronikoa.
     * @param s     Gehitu nahi den salmenta (Sale).
     */
	public void addToCart(String email, Sale s){
		db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            Sale saleDB = db.find(Sale.class, s.getSaleNumber());
            if (u != null && saleDB != null) {
                if (!u.isOnCart(saleDB)) {
                	u.addToCart(saleDB);
                }
            }
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback(); //Arazoak sahiesteko aurkitu genuen trikimailua
        }
	}
	
	/**
	 * Produktu bat erabiltzailearen erosketa-saskitik kentzen du.
	 * @param email Erabiltzailearen identifikazio elektronikoa.
	 * @param s     Kendu nahi den salmenta (Sale).
	 */
	public void removeFromCart(String email, Sale s){
		db.getTransaction().begin();
        try {//Erroreak sahiesteko
            Registered u = db.find(Registered.class, email);
            Sale saleDB = db.find(Sale.class, s.getSaleNumber());
            if (u != null && saleDB != null){
            	u.getCart().remove(saleDB);
            } 
            db.getTransaction().commit();
        } catch (Exception e) {
            if (db.getTransaction().isActive()) db.getTransaction().rollback(); //Arazoak sahiesteko aurkitu genuen trikimailua
        }
	}
	
	/**
	 * Saskiko produktu guztien erosketa-prozesua burutzen du. Saldoa egiaztatzen du,
	 * diru-transferentzia egiten du erosle eta saltzaileen artean, eta produktuak 
	 * saldu gisa markatzen ditu.
	 * @param buyerEmail Eroslearen posta elektronikoa.
	 * @param cart       Erosi nahi diren produktuen zerrenda.
	 * @return true erosketa ondo burutu bada, false arazoak egon badira (adib. saldo nahikorik ez).
	 */
	public boolean buyCart(String buyerEmail, List<Sale> cart) {
	    db.getTransaction().begin();
	    try { //Erroreak sahiesteko
	        Registered buyer = db.find(Registered.class, buyerEmail);
	        if (buyer == null) return false;

	        float total = 0;
	        for (Sale s : cart) total += s.getPrice();
	        
	        if (buyer.getMoneyKop() < total) {
	            db.getTransaction().rollback();
	            return false;
	        }

	        for (Sale saleFromGui : cart) {
	            Sale s = db.find(Sale.class, saleFromGui.getSaleNumber());
	            if (s == null || s.isSold()) {
	                db.getTransaction().rollback();
	                return false; 
	            }

	            Registered seller = s.getUser();
	            float price = s.getPrice();

	            buyer.setMoneyKop(buyer.getMoneyKop() - price);
	            seller.setMoneyKop(seller.getMoneyKop() + price);

	            buyer.addPurchasedSale(s);
	            s.setSold(true);

	            if (buyer.getFavorites() != null) {
	                buyer.getFavorites().remove(s);
	            }

	            addMovement(buyerEmail, ResourceBundle.getBundle("Etiquetas").getString("Buy") + ": " + s.getTitle(), -price);
	            addMovement(seller.getEmail(), ResourceBundle.getBundle("Etiquetas").getString("Sell") + ": " + s.getTitle(), price);
	        }

	        buyer.getCart().clear();

	        db.getTransaction().commit();
	        return true;

	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) db.getTransaction().rollback(); //Arazoak sahiesteko aurkitu genuen trikimailua
	        e.printStackTrace();
	        return false;
	    }
	}
	
	/**
	 * Datubasean eskaera berri bat sortu eta erabiltzaileari esleitzen dio.
	 * @param description Eskaeraren azalpen xehea.
	 * @param title       Eskaeraren izenburua.
	 * @param priceMin    Gutxieneko prezioa.
	 * @param priceMax    Gehienezko prezioa.
	 * @param email       Eskaera egiten duen erabiltzailearen posta elektronikoa.
	 */
	public void createOrder(String description, String title, float priceMin, float priceMax, String email) {
		if (!db.isOpen()) {
	        System.out.println("¡Error! La base de datos estaba cerrada. Reabriendo...");
	    }
		
		db.getTransaction().begin();
	    try {
	        Registered user = db.find(Registered.class, email);
	        Order newOrder = new Order(description, title, priceMin, priceMax, user);
	        db.persist(newOrder);
	        user.getOrders().add(newOrder);
	        db.getTransaction().commit();
	        System.out.println("Orden creada con éxito en la DB");
	    } catch (Exception e) {
	        db.getTransaction().rollback();
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sistemako eskaera guztiak lortzen ditu.
	 * @return Eskaeren (Order) zerrenda osoa.
	 */
	public List<Order> getAllOrders() {
		db.clear();
	    TypedQuery<Order> query = db.createQuery("SELECT o FROM Order o", Order.class);
	    return query.getResultList();
	}
	
	/**
	 * Eskaera bat sistematik ezabatzen du, erabiltzailearekin duen lotura kenduz.
	 * @param order Ezabatu nahi den eskaera.
	 */
	public void removeOrder(Order order) {
	    db.getTransaction().begin();
	    try {
	    	System.out.println("Buscando orden con título: '" + order.getTitle() + "'");
	        Order o = db.find(Order.class, order.getTitle());
	        
	        if (o != null) {
	            Registered reg = o.getRegistered();

	            reg.getOrders().remove(o);
	            
	            o.setRegistered(null); 
	            
	            db.remove(o);
	            
	            db.getTransaction().commit();
	            System.out.println("Transacción completada para: " + o.getTitle());
	        } else {
	            db.getTransaction().rollback();
	            System.out.println("No se encontró la orden en la DB");
	        }
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) db.getTransaction().rollback();
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Sistemako balorazio eta iruzkin guztiak lortzen ditu.
	 * @return Balorazioen (Review) zerrenda osoa.
	 */
	public List<Review> getAllReviews() { 
		 TypedQuery<Review> query = db.createQuery("SELECT r FROM Review r", Review.class);
		 List<Review> results = query.getResultList(); 
		 return results; 
	} 
	
	/**
	 * Erosketa baten inguruko balorazio berria sortzen du. Balorazioa erosleari, 
	 * saltzaileari eta salmentari lotzen zaio.
	 * @param sale        Baloratzen den salmenta.
	 * @param sellerEmail Saltzailearen posta elektronikoa.
	 * @param buyerEmail  Eroslearen (baloratzailearen) posta elektronikoa.
	 * @param stars       Emaniko izar kopurua (balorazio numerikoa).
	 * @param comment     Idatzitako iruzkina edo azalpena.
	 */
	public void createReview(Sale sale, String sellerEmail, String buyerEmail, int stars, String comment) {
	    db.getTransaction().begin();
	    try {
	        // 1. Recuperamos los objetos persistentes
	        Sale s = db.find(Sale.class, sale.getSaleNumber());
	        Registered buyer = db.find(Registered.class, buyerEmail);
	        Registered seller = db.find(Registered.class, sellerEmail);

	        if (s != null && buyer != null && seller != null) {
	            Review review = new Review(buyer, seller, s, stars, comment);
	            
	            db.persist(review);
	            
	            buyer.addWrittenReview(review);
	            seller.addReceivedReview(review);
	            s.setReview(review);
	            
	            db.getTransaction().commit();
	            System.out.println(">> Review guardada: De " + buyerEmail + " para " + sellerEmail);
	        } else {
	            System.err.println("Error: No se pudo crear la review. Datos incompletos.");
	            db.getTransaction().rollback();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        if (db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	    }
	}
	
    /**
     * Datu-basearen konexioa eta factory-a ixten ditu.
     */
    public void close() { //Aldatuta arazoak sahiesteko
        if (db != null && db.isOpen()) {
            db.close();
            if (emf != null) {
            	emf.close();
            }
        }
        System.out.println("DataAcess closed");
    }
  
    
    /**
     * Irudi fitxategi bat irakurtzen du diskotik eta tamaina aldatuta (reeskalatua) itzultzen du.
     * * @param fileName Irudi fitxategiaren izena.
     * @return BufferedImage reeskalatua, edo null erroren bat egonez gero.
     */
    public BufferedImage getFile(String fileName) {
        File file = new File(basePath + fileName);
        try { //Erroreak sahiesteko
        	return rescale(ImageIO.read(file)); 
        } catch (Exception ex) { //Arazoak sahiesteko aldatua
        	return null;
        }
    }
    
    
    /**
     * Irudi baten tamaina aldatzen du baseSize parametroan definitutako neurrira (160x160).
     * * @param originalImage Jatorrizko irudia.
     * @return Tamaina aldatutako irudia.
     */
    public BufferedImage rescale(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(BASE_SIZE, BASE_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, BASE_SIZE, BASE_SIZE, null);
        g.dispose();
        return resizedImage;
    }
}