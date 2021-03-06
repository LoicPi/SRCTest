package com.dummy.myerp.model.bean.comptabilite;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;


/**
 * Bean représentant une Écriture Comptable
 */
public class EcritureComptable {

    // ==================== Attributs ====================
    /** The Id. */
    private Integer id;
    /** Journal comptable */
    @NotNull private JournalComptable journal;
    /** The Reference. */
    //Correction sur la regex car prenait en premier instance des chiffres au lieu de lettres
    @Pattern(regexp = "^[A-Za-z]{1,5}-\\d{4}\\/\\d{5}")
    private String reference;
    /** The Date. */
    @NotNull 
    private Date date;

    /** The Libelle. */
    @NotNull
    @Size(min = 1, max = 200)
    private String libelle;

    /** La liste des lignes d'écriture comptable. */
    @Valid
    @Size(min = 2)
    private final List<LigneEcritureComptable> listLigneEcriture = new ArrayList<>();


    // ==================== Getters/Setters ====================
    public Integer getId() {
        return id;
    }
    public void setId(Integer pId) {
        id = pId;
    }
    public JournalComptable getJournal() {
        return journal;
    }
    public void setJournal(JournalComptable pJournal) {
        journal = pJournal;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String pReference) {
        reference = pReference;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date pDate) {
        date = pDate;
    }
    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String pLibelle) {
        libelle = pLibelle;
    }
    public List<LigneEcritureComptable> getListLigneEcriture() {
        return listLigneEcriture;
    }
    
    // ==================== HashCode/Equals ====================
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
    
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EcritureComptable other = (EcritureComptable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

    /**
     * Calcul et renvoie le total des montants au débit des lignes d'écriture
     *
     * @return {@link BigDecimal}, {@link BigDecimal#ZERO} si aucun montant au débit
     */
    public BigDecimal getTotalDebit() {
        BigDecimal vRetour = BigDecimal.ZERO;
        for (LigneEcritureComptable vLigneEcritureComptable : listLigneEcriture) {
            if (vLigneEcritureComptable.getDebit() != null) {
                vRetour = vRetour.add(vLigneEcritureComptable.getDebit());
            }
        }
        return vRetour;
    }

    /**
     * Calcul et renvoie le total des montants au crédit des lignes d'écriture
     * 
     * @return {@link BigDecimal}, {@link BigDecimal#ZERO} si aucun montant au crédit
     */
    public BigDecimal getTotalCredit() {
        BigDecimal vRetour = BigDecimal.ZERO;
        for (LigneEcritureComptable vLigneEcritureComptable : listLigneEcriture) {
        	//Modification sur le paramètre utilisé car utilisation du débit au lieu du crédit
            if (vLigneEcritureComptable.getCredit() != null) {
                vRetour = vRetour.add(vLigneEcritureComptable.getCredit());
            }
        }
        return vRetour;
    }

    /**
     * Renvoie si l'écriture est équilibrée (TotalDebit = TotalCrédit)
     * @return boolean
     */
    public boolean isEquilibree() {
    	//Modification de la fonction de comparaison de 2 BigDecimal en utilisant compareTo
        boolean vRetour = (this.getTotalDebit().compareTo(getTotalCredit())==0);
        return vRetour;
    }
	
	// ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
            .append("id=").append(id)
            .append(vSEP).append("journal=").append(journal)
            .append(vSEP).append("reference='").append(reference).append('\'')
            .append(vSEP).append("date=").append(date)
            .append(vSEP).append("libelle='").append(libelle).append('\'')
            .append(vSEP).append("totalDebit=").append(this.getTotalDebit().toPlainString())
            .append(vSEP).append("totalCredit=").append(this.getTotalCredit().toPlainString())
            .append(vSEP).append("listLigneEcriture=[\n")
            .append(StringUtils.join(listLigneEcriture, "\n")).append("\n]")
            .append("}");
        return vStB.toString();
    }
}
