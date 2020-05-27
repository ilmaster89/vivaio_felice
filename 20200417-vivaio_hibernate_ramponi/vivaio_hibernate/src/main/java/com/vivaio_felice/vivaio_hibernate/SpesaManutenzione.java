package com.vivaio_felice.vivaio_hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "spesa_manutenzione")
public class SpesaManutenzione {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Integer id;

	@OneToOne
	Auto auto;

	Double spesa;

	@Column(name = "data_spesa")
	Date dataSpesa;

	@NotNull(message = "campo obbligatorio")
	@Size(max = 45, message = "non può superare i 45 caratteri")
	String descrizione;

	@Size(max = 45, message = "non può superare i 45 caratteri")
	String dettaglio;

	public SpesaManutenzione() {
	}

	public SpesaManutenzione(Integer id, Auto auto, Double spesa, Date dataSpesa, @NotNull String descrizione,
			String dettaglio) {
		this.id = id;
		this.auto = auto;
		this.spesa = spesa;
		this.dataSpesa = dataSpesa;
		this.descrizione = descrizione;
		this.dettaglio = dettaglio;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Auto getAuto() {
		return auto;
	}

	public void setAuto(Auto auto) {
		this.auto = auto;
	}

	public Double getSpesa() {
		return spesa;
	}

	public void setSpesa(Double spesa) {
		this.spesa = spesa;
	}

	public Date getDataSpesa() {
		return dataSpesa;
	}

	public void setDataSpesa(Date dataSpesa) {
		this.dataSpesa = dataSpesa;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getDettaglio() {
		return dettaglio;
	}

	public void setDettaglio(String dettaglio) {
		this.dettaglio = dettaglio;
	}

	@Override
	public String toString() {
		return "SpesaManutenzione [id=" + id + ", auto=" + auto + ", spesa=" + spesa + ", dataSpesa=" + dataSpesa
				+ ", descrizione=" + descrizione + "]";
	}

}
