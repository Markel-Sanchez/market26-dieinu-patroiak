package businessLogic;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import configuration.ConfigXML;

public class BLFactory {
    
    public BLFacade getBusinessLogicFactory(boolean isLocal) throws Exception {
        ConfigXML c = ConfigXML.getInstance();
        
        if (isLocal) {
            return new BLFacadeImplementation();
        } else {
            String serviceName = "http://" + c.getBusinessLogicNode() + ":" 
                                + c.getBusinessLogicPort() + "/ws/" 
                                + c.getBusinessLogicName() + "?wsdl";
            URL url = new URL(serviceName);
            QName qname = new QName("http://businessLogic/", "BLFacadeImplementationService");
            Service service = Service.create(url, qname);
            return service.getPort(BLFacade.class);
        }
    }
}