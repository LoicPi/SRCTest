package com.dummy.myerp.consumer.impl.db.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.NotFoundException;
import com.dummy.myerp.testconsumer.ConsumerTestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/bootstrapContext.xml")
public class ComptabiliteDaoImplTest extends ConsumerTestCase {

	private ComptabiliteDao dao = getDaoProxy().getComptabiliteDao();
	
	private EcritureComptable vEcritureComptable = new EcritureComptable();
	
	private LigneEcritureComptable ligneEcritureDebit;
    
    private LigneEcritureComptable ligneEcritureCredit;
	
	private SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable();
	
	private EcritureComptable testEcritureComptable;
	
	@Before
	public void initComptabiliteDao() {
    	
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2020,01,21);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        
        ligneEcritureDebit = new LigneEcritureComptable(new CompteComptable(401), null, new BigDecimal("123"), null);
        ligneEcritureCredit = new LigneEcritureComptable(new CompteComptable(411), null, null, new BigDecimal("123"));
    
        vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
        
        vSequenceEcritureComptable = new SequenceEcritureComptable("AC", 2020, 23);
	}
	
	@After
	public void undefComptabiliteManagerImpl() {
		testEcritureComptable = null;
		dao.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}
	
	@Test
	public void getListCompteComptable_Test() {
		List<CompteComptable> listCompteComptable = dao.getListCompteComptable();
		assertThat(listCompteComptable.isEmpty()).isFalse();
	}
	
	@Test
	public void getListJournalComptable_Test() {
		List<JournalComptable> listJournalComptable = dao.getListJournalComptable();
		assertThat(listJournalComptable.isEmpty()).isFalse();
	}
	
	@Test
	public void getListEcritureComptable_Test() {
		List<EcritureComptable> listEcritureComptable = dao.getListEcritureComptable();
		assertThat(listEcritureComptable.isEmpty()).isFalse();
	}
	
	@Test
	public void getListSequenceEcritureComptable_Test() throws Exception {
		dao.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		List<SequenceEcritureComptable> listSequenceEcritureComptable = dao.getListSequenceEcritureComptable(2020);
		assertThat(listSequenceEcritureComptable.isEmpty()).isFalse();
		dao.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}
	
	@Test
	public void getEcritureComptable_ExistanceDeLEcritureComptable_Test() throws Exception {
		testEcritureComptable = dao.getEcritureComptable(-1);
		assertThat(testEcritureComptable).isNotNull();
	}
	
	@Test(expected = NotFoundException.class)
	public void getEcritureComptable_NonExistanceDeLEcritureComptable_Test() throws Exception {
		testEcritureComptable = dao.getEcritureComptable(0);
		assertThat(testEcritureComptable).isNull();
	}
	
	@Test
	public void getEcritureComptableByRef_ExistanceDeLaReference_Test() throws Exception {
		dao.insertEcritureComptable(vEcritureComptable);
		testEcritureComptable = dao.getEcritureComptableByRef("AC-2020/00001");
		assertThat(testEcritureComptable).isEqualTo(vEcritureComptable);
		dao.deleteEcritureComptable(testEcritureComptable.getId());
	}
	
	@Test(expected = NotFoundException.class)
	public void getEcritureComptableByRef_NonExistanceDeLaReference_Test() throws Exception {
		testEcritureComptable = dao.getEcritureComptableByRef("AC-2020/00001");
		assertThat(testEcritureComptable).isNull();
	}
	
	@Test
	public void insertEcritureComptable_Test() throws Exception {
		List<EcritureComptable> testListEcritureComptable = dao.getListEcritureComptable();
		dao.insertEcritureComptable(vEcritureComptable);
		List<EcritureComptable> secondListEcritureComptable = dao.getListEcritureComptable();
		assertThat(secondListEcritureComptable.size()).isEqualTo(testListEcritureComptable.size() + 1);
		testEcritureComptable = dao.getEcritureComptableByRef("AC-2020/00001");
		dao.deleteEcritureComptable(testEcritureComptable.getId());
	}
	
	@Test
	public void updateEcritureComptable_Test() throws Exception {
		dao.insertEcritureComptable(vEcritureComptable);
		vEcritureComptable.setLibelle("testLibelle");
		dao.updateEcritureComptable(vEcritureComptable);
		testEcritureComptable = dao.getEcritureComptableByRef("AC-2020/00001");
		assertThat(testEcritureComptable.getLibelle()).isEqualTo("testLibelle");
		dao.deleteEcritureComptable(testEcritureComptable.getId());
	}
	
	@Test(expected = NotFoundException.class)
	public void deleteEcritureComptable_Test() throws Exception {
		dao.insertEcritureComptable(vEcritureComptable);
		testEcritureComptable = dao.getEcritureComptableByRef("AC-2020/00001");
		dao.deleteEcritureComptable(testEcritureComptable.getId());
		EcritureComptable ecritureComptableTest = dao.getEcritureComptable(testEcritureComptable.getId());
		assertThat(ecritureComptableTest).isNull();
	}
	
	@Test
	public void insertSequenceEcritureComptable_Test() throws Exception {
		List<SequenceEcritureComptable> testSequenceEcritureComptable = dao.getListSequenceEcritureComptable(2020);
		dao.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		List<SequenceEcritureComptable> secondSequenceEcritureComptable = dao.getListSequenceEcritureComptable(2020);
		assertThat(secondSequenceEcritureComptable.size()).isEqualTo(testSequenceEcritureComptable.size() + 1);
		dao.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}
	
	@Test
	public void updateSequenceEcritureComptable_Test() throws Exception {
		dao.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		vSequenceEcritureComptable.setDerniereValeur(32);
		dao.updateSequenceEcritureComptable(vSequenceEcritureComptable);
		SequenceEcritureComptable testSequenceEcritureComptable = new SequenceEcritureComptable();
		List<SequenceEcritureComptable> testListSequenceEcritureComptable = dao.getListSequenceEcritureComptable(2020);
		for (SequenceEcritureComptable sequenceEcritureComptable : testListSequenceEcritureComptable) {
			testSequenceEcritureComptable = sequenceEcritureComptable;
		}
		assertThat(testSequenceEcritureComptable.getDerniereValeur()).isEqualTo(32);
		dao.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
	}
	
	@Test
	public void deleteSequenceEcritureComptable_Test() throws Exception {
		dao.insertSequenceEcritureComptable(vSequenceEcritureComptable);
		dao.deleteSequenceEcritureComptable(vSequenceEcritureComptable);
		assertThat(dao.getListSequenceEcritureComptable(2020).size()).isEqualTo(0);
	}
}