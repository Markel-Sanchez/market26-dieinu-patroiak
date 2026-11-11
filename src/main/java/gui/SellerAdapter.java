package gui;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import domain.Sale;
import domain.User;
import domain.Registered;

public class SellerAdapter extends AbstractTableModel {
    private User user;
    private List<Sale> sales;
    private String[] columnNames = new String[] { "title", "description", "price" };

    public SellerAdapter(User user) {
        this.user = user;
        
        if (this.user != null && this.user instanceof Registered) {
            this.sales = ((Registered) this.user).getSales(); 
        }
    }

    @Override
    public int getRowCount() {
        if (sales == null) return 0;
        return sales.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (sales == null || row >= sales.size()) return null;
        
        Sale sale = sales.get(row);
        switch (col) {
            case 0:
                return sale.getTitle();
            case 1:
                return sale.getDescription();
            case 2:
                return sale.getPrice();
            default:
                return null;
        }
    }
}
