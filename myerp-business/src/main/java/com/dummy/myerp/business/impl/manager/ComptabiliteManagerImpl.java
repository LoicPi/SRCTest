package com.dummy.myerp.business.impl.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;
import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;

/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

	// ==================== Attributs ====================

	// ==================== Constructeurs ====================
	/**
	 * Instantiates a new Comptabilite manager.
	 */
	public ComptabiliteManagerImpl() {
	}

	// ==================== Getters/Setters ====================
	@Override
	public List<CompteComptable> getListCompteComptable() {
		return getDaoProxy().getComptabiliteDao().getListCompteComptable();
	}

	@Override
	public List<JournalComptable> getListJournalComptable() {
		return getDaoProxy().getComptabiliteDao().getListJournalComptable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EcritureComptable> getListEcritureComptable() {
		return getDaoProxy().getComptabiliteDao().getListEcritureComptable();
	}

	@Override
	public List<SequenceEcritureComptable> getListSequenceEcritureComptable(Integer pAnnee) {
		return getDaoProxy().getComptabiliteDao().getListSequenceEcritureComptable(pAnnee);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addReference(EcritureComptable pEcritureComptable) {
		// Bien se réferer à la JavaDoc de cette méthode !
		/*
		 * Le principe : 1. Remonter depuis la persitance la dernière valeur de la
		 * séquence du journal pour l'année de l'écriture (table
		 * sequence_ecriture_comptable)
		 */
		// Récupération du code du Journal
		try {
			String codeJournal = pEcritureComptable.getJournal().getCode();

			// Récupération de l'année de la date de l'écriture comptable
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(pEcritureComptable.getDate());

			Integer anneeDate = Integer.valueOf(calendar.get(Calendar.YEAR));

			String anneeDateEC = String.valueOf(calendar.get(Calendar.YEAR));

			// Récupération de la liste des séquences des écritures comptable avec l'année
			// récupérer auparavant
			List<SequenceEcritureComptable> listSequenceEcritureComptable = this
					.getListSequenceEcritureComptable(anneeDate);

			// Création de la séquence de la référence
			String seqRefEC = "";

			int seqRef = 0;

			DecimalFormat decimalFormat = new DecimalFormat("00000");

			/*
			 * 2. * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
			 * 1. Utiliser le numéro 1. Sinon : 1. Utiliser la dernière valeur + 1
			 */
			if (listSequenceEcritureComptable.size() == 0) {
				seqRef = 1;
			} else {
				for (SequenceEcritureComptable sequenceEcritureComptable : listSequenceEcritureComptable) {
					if (sequenceEcritureComptable.getCodeJournal().equals(codeJournal)) {
						seqRef = sequenceEcritureComptable.getDerniereValeur() + 1;
					}
				}
			}

			// Formattage de la séquence de la requête avec 5 chiffres
			seqRefEC = decimalFormat.format(seqRef);

			/*
			 * 3. Mettre à jour la référence de l'écriture avec la référence calculée
			 * (RG_Compta_5)
			 */
			// Création de la nouvelle référence
			StringBuilder reference = new StringBuilder();

			reference.append(codeJournal).append("-").append(anneeDateEC).append("/").append(seqRefEC);

			// Maj du paramètre reference dans l'écriture comptable
			pEcritureComptable.setReference(reference.toString());

			// Maj dans la BDD de l'écriture comptable

			this.updateEcritureComptable(pEcritureComptable);

			/*
			 * 4. Enregistrer (insert/update) la valeur de la séquence en persitance (table
			 * sequence_ecriture_comptable)
			 */
			// Maj de la sequence dans la bdd
			SequenceEcritureComptable pSequenceEcritureComptable = new SequenceEcritureComptable();

			pSequenceEcritureComptable.setDerniereValeur(seqRef);

			if (seqRef == 1) {
				pSequenceEcritureComptable.setCodeJournal(codeJournal);
				pSequenceEcritureComptable.setAnnee(anneeDate);
				this.insertSequenceEcritureComptable(pSequenceEcritureComptable);

			} else {
				this.updateSequenceEcritureComptable(pSequenceEcritureComptable);
			}
		} catch (FunctionalException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptableUnit(pEcritureComptable);
		this.checkEcritureComptableContext(pEcritureComptable);
		this.checkEcritureComptableDigitsDecimalPoint(pEcritureComptable);
	}

	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
	 * c'est à dire indépendemment du contexte (unicité de la référence, exercie
	 * comptable non cloturé...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 *                             règles de gestion
	 */
	protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== Vérification des contraintes unitaires sur les attributs de l'écriture
		Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
		if (!vViolations.isEmpty()) {
			throw new FunctionalException("L'écriture comptable ne respecte pas les règles de gestion.",
					new ConstraintViolationException(
							"L'écriture comptable ne respecte pas les contraintes de validation", vViolations));
		}

		// ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit
		// être équilibrée
		if (!pEcritureComptable.isEquilibree()) {
			throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
		}

		// ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes
		// d'écriture (1 au débit, 1 au crédit)
		int vNbrCredit = 0;
		int vNbrDebit = 0;
		for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
			if (BigDecimal.ZERO
					.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(), BigDecimal.ZERO)) != 0) {
				vNbrCredit++;
			}
			if (BigDecimal.ZERO
					.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(), BigDecimal.ZERO)) != 0) {
				vNbrDebit++;
			}
		}
		// On test le nombre de lignes car si l'écriture à une seule ligne
		// avec un montant au débit et un montant au crédit ce n'est pas valable
		if (pEcritureComptable.getListLigneEcriture().size() < 2 || vNbrCredit < 1 || vNbrDebit < 1) {
			throw new FunctionalException(
					"L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
		}

		// RG_Compta_5 : Format et contenu de la référence
		// vérifier que l'année dans la référence correspond bien à la date de
		// l'écriture, idem pour le code journal...
		if (pEcritureComptable.getReference() != null) {
			String ref = pEcritureComptable.getReference();
			String codeJournal = pEcritureComptable.getJournal().getCode();

			String codeRef = ref.substring(0, ref.indexOf("-"));

			String anneeRef = ref.substring(ref.indexOf("-") + 1, ref.indexOf("/"));

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(pEcritureComptable.getDate());

			String anneeDate = String.valueOf(calendar.get(Calendar.YEAR));

			if (!codeRef.equals(codeJournal)) {
				throw new FunctionalException(
						"La référence de l'écriture comptable contient un code de journal différent du code du journal de l'écriture comptable.");
			}

			if (!anneeDate.equals(anneeRef)) {
				throw new FunctionalException(
						"L'année de la date de l'écriture comptable est différente de l'année du journal.");
			}
		}
	}

	/**
	 * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au
	 * contexte (unicité de la référence, année comptable non cloturé...)
	 *
	 * @param pEcritureComptable -
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 *                             règles de gestion
	 */
	protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
		// ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
		if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
			try {
				// Recherche d'une écriture ayant la même référence
				EcritureComptable vECRef = getDaoProxy().getComptabiliteDao()
						.getEcritureComptableByRef(pEcritureComptable.getReference());

				// Si l'écriture à vérifier est une nouvelle écriture (id == null),
				// ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
				// c'est qu'il y a déjà une autre écriture avec la même référence
				if (pEcritureComptable.getId() == null || !pEcritureComptable.getId().equals(vECRef.getId())) {
					throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
				}
			} catch (NotFoundException vEx) {
				// Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la
				// même référence.
			}
		}
	}

	/*
	 * Vérifie que l'Ecriture Comptable respecte les règles de gestion liées aux
	 * nombres de chiffres après la virgule
	 *
	 * @param pEcritureComptable
	 * 
	 * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les
	 * règles de gestion
	 */
	protected void checkEcritureComptableDigitsDecimalPoint(EcritureComptable pEcritureComptable)
			throws FunctionalException {
		// RG_Compta 7 : Les montants des lignes d'écritures peuvent comporter 2
		// chiffres maximum après la virgule
		// On vérifie que seul 2 chiffres apparaît après la virgule pour chacune des
		// lignes d'écritures
		for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
			if (BigDecimal.ZERO
					.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(), BigDecimal.ZERO)) != 0) {
				if (vLigneEcritureComptable.getCredit().scale() > 2) {
					throw new FunctionalException(
							"L'écriture comptable contient une ligne d'écriture de crédit contenant plus de 2 chiffres après la virgule.");
				}
			} else {
				if (vLigneEcritureComptable.getDebit().scale() > 2) {
					throw new FunctionalException(
							"L'écriture comptable contient une ligne d'écriture de débit contenant plus de 2 chiffres après la virgule.");
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		this.checkEcritureComptable(pEcritureComptable);
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().insertEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().updateEcritureComptable(pEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteEcritureComptable(Integer pId) {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().deleteEcritureComptable(pId);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable)
			throws FunctionalException {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().insertSequenceEcritureComptable(pSequenceEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable)
			throws FunctionalException {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().updateSequenceEcritureComptable(pSequenceEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteSequenceEcritureComptable(SequenceEcritureComptable pSequenceEcritureComptable) {
		TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
		try {
			getDaoProxy().getComptabiliteDao().deleteSequenceEcritureComptable(pSequenceEcritureComptable);
			getTransactionManager().commitMyERP(vTS);
			vTS = null;
		} finally {
			getTransactionManager().rollbackMyERP(vTS);
		}
	}

}