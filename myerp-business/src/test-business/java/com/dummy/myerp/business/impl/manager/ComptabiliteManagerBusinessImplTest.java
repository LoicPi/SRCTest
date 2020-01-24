package com.dummy.myerp.business.impl.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.testbusiness.business.BusinessTestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/bootstrapContext.xml")
public class ComptabiliteManagerBusinessImplTest extends BusinessTestCase {

	private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

	private EcritureComptable vEcritureComptable;

	private LigneEcritureComptable ligneEcritureDebit;

	private LigneEcritureComptable ligneEcritureCredit;
	
	private SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable();
	
	@Before
	public void initComptabiliteManagerImpl() {
		
		vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2019,01,21);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setLibelle("Libelle");
        
        ligneEcritureDebit = new LigneEcritureComptable(new CompteComptable(401), null, new BigDecimal("123"), null);
        ligneEcritureCredit = new LigneEcritureComptable(new CompteComptable(411), null, null, new BigDecimal("123"));
        
        vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
        vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
        
        vSequenceEcritureComptable = new SequenceEcritureComptable("AC", 2019, 2);
	}
	
	@After
	public void undefComptabiliteManagerImpl() {
		manager.deleteEcritureComptable(vEcritureComptable.getId());
		manager.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}

	/*
	 * Vérifie que l'insertion d'une écriture comptable s'effectue bien en base de données
	 */
	@Test
	public void insertEcritureComptable_Test() throws FunctionalException {
		manager.insertEcritureComptable(vEcritureComptable);
		assertTrue(manager.getListEcritureComptable().contains(vEcritureComptable));
		manager.deleteEcritureComptable(vEcritureComptable.getId());
	}
	
	/*
	 * Vérifie que la maj d'une écriture comptable s'effectue bien en base de données
	 */
	@Test
	public void updateEcritureComptable_Test() throws FunctionalException {
		manager.insertEcritureComptable(vEcritureComptable);
		vEcritureComptable.setLibelle("pLibelle");
		manager.updateEcritureComptable(vEcritureComptable);
		manager.deleteEcritureComptable(vEcritureComptable.getId());
	}
	
	/*
	 * Vérifie que l'effacement d'une écriture comptable s'effectue bien en base de données
	 */
	@Test
	public void deleteEcritureComptable_Test() throws FunctionalException {
		manager.insertEcritureComptable(vEcritureComptable);
		manager.deleteEcritureComptable(vEcritureComptable.getId());
		assertFalse(manager.getListEcritureComptable().contains(vEcritureComptable));
	}
	
	/*
	 * Vérifie que l'insertion d'une séquence d'écriture comptable s'effectue bien en base de données
	 */
	@Test
	public void insertSequenceEcritureComptable_Test() throws FunctionalException {
		manager.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		assertTrue(manager.getListSequenceEcritureComptable(2019).contains(vSequenceEcritureComptable));
		manager.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}
	
	/*
	 * Vérifie que la maj d'une séquence d'écriture comptable s'effectue bien en base de données
	 */
	@Test
	public void updateSequenceEcritureComptable_Test() throws FunctionalException {
		manager.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		vSequenceEcritureComptable.setDerniereValeur(3);
		manager.updateSequenceEcritureComptable(vSequenceEcritureComptable);
		assertThat(manager.getListSequenceEcritureComptable(2019).get(0).getDerniereValeur()).isEqualTo(00003);
		manager.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}
	
	/*
	 * Vérifie que l'effacement d'une séquence d'écriture comptable s'effectue bien en base de données
	 */
	@Test
	public void deleteSequenceEcritureComptable_Test() throws FunctionalException {
		manager.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		manager.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
		assertFalse(manager.getListSequenceEcritureComptable(2019).contains(vSequenceEcritureComptable));
	}
}
