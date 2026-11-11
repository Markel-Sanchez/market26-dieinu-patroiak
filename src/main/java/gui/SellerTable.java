package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import domain.User;

public class SellerTable extends JFrame {
    private User user;
    private JTable tabla;

    public SellerTable(User user) {
        super(user.getEmail() + "'s sales "); 
        this.setBounds(100, 100, 700, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.user = user;

        SellerAdapter adapt = new SellerAdapter(user);
        tabla = new JTable(adapt);
        tabla.setPreferredScrollableViewportSize(new Dimension(500, 70));

        JScrollPane scrollPane = new JScrollPane(tabla);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
}
