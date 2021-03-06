package myServiceHandlers;

import java.io.IOException;
import java.io.PrintWriter;

import server.MyServiceHandler;
import server.StockMarket;
import exception.DataIllegalException;
import domain.dealing.SellingOffer;

public class SellOrder extends MyServiceHandler {

	@Override
	public int executeByStatus(PrintWriter out) throws IOException {
		String id = params.get("id");
        String instrument = params.get("instrument");
        String type = params.get("type");
        SellingOffer sellingOffer=null;
        try {
            Long price = Long.parseLong(params.get("price"));
            Long quantity = Long.parseLong(params.get("quantity"));
            sellingOffer = new SellingOffer(price,quantity,type,id);
            if(instrument==null || instrument.isEmpty())
                throw new DataIllegalException("Mismatched Parameters");
            sellingOffer.validateVariables();
        } catch (DataIllegalException e) {
            out.println(e.getMessage());
            return 404;
        }catch (Exception e){
            out.println("Mismatched Parameters");
            return 404;
        }
        
        StockMarket.getInstance().executeSellingOffer(out,sellingOffer,instrument);
        return 200;
    }

}
