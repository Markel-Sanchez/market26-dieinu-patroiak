package gui;

import businessLogic.BLFacade;
import businessLogic.BLFactory;
import domain.User;

public class AdapterLauncher {

    public static void main(String[] args) {
        try {
            boolean isLocal = true;
            BLFacade blFacade = new BLFactory().getBusinessLogicFactory(isLocal);

            User u = blFacade.isLogged("seller1@gmail.com", "aurrera");
            
            if (u != null) {
                SellerTable st = new SellerTable(u);
                st.setVisible(true);
            } else {
                System.out.println("Error: Ez da aurkitu user-a edo pasahitz okerra.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
