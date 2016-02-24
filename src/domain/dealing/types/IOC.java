package domain.dealing.types;

import java.io.PrintWriter;
import java.util.List;

import domain.dealing.BuyingOffer;
import domain.dealing.SellingOffer;
import server.StockMarket;

public class IOC implements ITypeExecutor {

	@Override
	public UpdatedOfferingLists sellingExecute(PrintWriter out, SellingOffer offer, List<SellingOffer> sellingOffers, List<BuyingOffer> buyingOffers,String symbol) {
		if(buyingOffers.isEmpty()){
			out.println("Order is declined");
			return null;
		}
		Long count = offer.getQuantity();
		for (int i = 0; i < buyingOffers.size(); i++) {
			if(buyingOffers.get(i).getPrice()<offer.getPrice())
				break;
			count -= buyingOffers.get(i).getQuantity();
		}
		if(count > 0){
			out.println("Order is declined");
			return null;
		}
		else{
			while(true){
				Long buyPrice = buyingOffers.get(0).getPrice();
				Long buyQuantity = 0L ;
				if(buyingOffers.get(0).getQuantity() <= offer.getQuantity()){
					buyQuantity = offer.getQuantity() - buyingOffers.get(0).getQuantity();
					StockMarket.changeCustomerProperty(offer, buyingOffers.get(0), buyPrice, buyQuantity, symbol);
					out.println(offer.getID()+" sold "+buyQuantity+" shares of "+symbol+" @"+buyPrice+" to "+buyingOffers.get(0).getID());
					buyingOffers.remove(0);
					offer.setQuantity("delete", buyQuantity);
					//sellingOffers.set(0, offer);
				}
				else{
					buyQuantity = buyingOffers.get(0).getQuantity() - offer.getQuantity();
					buyingOffers.get(0).setQuantity("delete", buyQuantity);
					//buyingOffers.set(0, buyingOffers.get(0));
					StockMarket.changeCustomerProperty(offer, buyingOffers.get(0), buyPrice, buyQuantity, symbol);
					out.println(offer.getID()+" sold "+buyQuantity+" shares of "+symbol+" @"+buyPrice+" to "+buyingOffers.get(0).getID());
					break;
				}


			}
		}
		return new UpdatedOfferingLists(sellingOffers,buyingOffers);
	}

	@Override
	public UpdatedOfferingLists buyingExecute(PrintWriter out, BuyingOffer offer, List<SellingOffer> sellingOffers, List<BuyingOffer> buyingOffers,String symbol) {
		if(sellingOffers.isEmpty()){
			out.println("Order is declined");
			return null;
		}
		Long count = offer.getQuantity();
		for (int i = 0; i < sellingOffers.size(); i++) {
			if(sellingOffers.get(i).getPrice()>offer.getPrice())
				break;
			count -= sellingOffers.get(i).getQuantity();
		}
		if(count > 0){
			out.println("Order is declined");
			return null;
		}
		else{
			//count = offer.getQuantity();
			while(true){
				if(offer.getPrice() > sellingOffers.get(0).getPrice()){
					Long buyPrice = offer.getPrice();
					Long buyQuantity = 0L ;
					if(offer.getQuantity() < sellingOffers.get(0).getQuantity()){
						buyQuantity = sellingOffers.get(0).getQuantity() - offer.getQuantity();
						sellingOffers.get(0).setQuantity("delete", buyQuantity);
						//sellingOffers.set(0, sellingOffers.get(0));
						StockMarket.changeCustomerProperty(sellingOffers.get(0), offer, buyPrice, buyQuantity, symbol);
						out.println(sellingOffers.get(0).getID()+" sold "+buyQuantity+" shares of "+symbol+" @"+buyPrice+" to "+offer.getID());
						break;
					}
					else{
						buyQuantity = offer.getQuantity() - sellingOffers.get(0).getQuantity();
						StockMarket.changeCustomerProperty(sellingOffers.get(0), offer, buyPrice, buyQuantity, symbol);
						out.println(sellingOffers.get(0).getID()+" sold "+buyQuantity+" shares of "+symbol+" @"+buyPrice+" to "+offer.getID());
						sellingOffers.remove(0);
						offer.setQuantity("delete", buyQuantity);
						//buyingOffers.set(0, offer);

					}

				}
			}
		}
		return new UpdatedOfferingLists(sellingOffers,buyingOffers);
	}
}
