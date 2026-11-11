package businessLogic;

import java.util.List;
import java.util.NoSuchElementException;

import domain.Sale;

public class SalesIterator implements ExtendedIterator<Sale> {

	private List<Sale> sales;
	private int position = 0;

	public SalesIterator(List<Sale> sales) {
		this.sales = sales;
	}
	public boolean hasNext() {
		return position < sales.size();
	}

	public Sale next() {
		if (!hasNext()) {
			throw new NoSuchElementException("Ez daude produktu gehiago!");
		}
		Sale sale = sales.get(position);
		position++;
		return sale;
	}
	@Override
	public boolean hasPrevious() {
		return position > 0;
	}
	@Override
	public Sale previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException("Ez daude produktu gehiago!");
		}
		position--;
		return sales.get(position);
	}
	@Override
	public void goFirst() {
		position = 0;
	}
	
	@Override
	public void goLast() {
		position = sales.size();
	}
}
