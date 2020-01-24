package com.dummy.myerp.model.bean.comptabilite;


/**
 * Bean représentant une séquence pour les références d'écriture comptable
 */
public class SequenceEcritureComptable {

    // ==================== Attributs ====================
	/** Le code du journal **/
	private String codeJournal;
    /** L'année */
    private Integer annee;
    /** La dernière valeur utilisée */
    private Integer derniereValeur;

    // ==================== Constructeurs ====================
    /**
     * Constructeur
     */
    public SequenceEcritureComptable() {
    }

    /**
     * Constructeur
     *
     * @param pAnnee -
     * @param pDerniereValeur -
     */
    public SequenceEcritureComptable(String pCodeJournal, Integer pAnnee, Integer pDerniereValeur) {
        codeJournal = pCodeJournal;
    	annee = pAnnee;
        derniereValeur = pDerniereValeur;
    }


    // ==================== Getters/Setters ====================
    
    public String getCodeJournal() {
		return codeJournal;
	}
    public Integer getAnnee() {
        return annee;
    }
    public Integer getDerniereValeur() {
        return derniereValeur;
    }
	public void setCodeJournal(String codeJournal) {
		this.codeJournal = codeJournal;
	}
	public void setAnnee(Integer pAnnee) {
        annee = pAnnee;
    }
	public void setDerniereValeur(Integer pDerniereValeur) {
        derniereValeur = pDerniereValeur;
    } 

	// ==================== HashCode/Equals ====================
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codeJournal == null) ? 0 : codeJournal.hashCode());
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
		SequenceEcritureComptable other = (SequenceEcritureComptable) obj;
		if (codeJournal == null) {
			if (other.codeJournal != null)
				return false;
		} else if (!codeJournal.equals(other.codeJournal))
			return false;
		return true;
	}

	// ==================== Méthodes ====================
    @Override
    public String toString() {
        final StringBuilder vStB = new StringBuilder(this.getClass().getSimpleName());
        final String vSEP = ", ";
        vStB.append("{")
        	.append("codeJournal=").append(codeJournal)
            .append(vSEP).append("annee=").append(annee)
            .append(vSEP).append("derniereValeur=").append(derniereValeur)
            .append("}");
        return vStB.toString();
    }
}
