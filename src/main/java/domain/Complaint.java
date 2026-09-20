package domain;

import java.util.Date;

import javax.persistence.Entity;

@Entity
public class Complaint {
	private Sale sale;
	private Date data;
	private String zergatia;
	private boolean egoera; //true bada admin-ak ikusi behar du
	private Registered sender; //Complaint-a bidaltzen duena
	private Registered receiver; //Complaint-a jasotzen duena

	public Complaint(Date data, String zergatia, Sale s, Registered sender, Registered receiver) {
		this.data = data;
		this.zergatia = zergatia;
		this.sale = s;
		this.sender = sender;
		this.receiver = receiver;
	}

	public Registered getSender() {
		return sender;
	}

	public void setSender(Registered sender) {
		this.sender = sender;
	}

	public Registered getReceiver() {
		return receiver;
	}

	public void setReceiver(Registered receiver) {
		this.receiver = receiver;
	}

	public Complaint() {
		super();
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public boolean isEgoera() {
		return egoera;
	}

	public void setEgoera(boolean egoera) {
		this.egoera = egoera;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getZergatia() {
		return zergatia;
	}

	public void setZergatia(String zergatia) {
		this.zergatia = zergatia;
	}
}