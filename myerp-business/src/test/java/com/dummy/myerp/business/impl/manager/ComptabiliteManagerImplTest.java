package com.dummy.myerp.business.impl.manager;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.*;

import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.business.impl.TransactionManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import com.dummy.myerp.model.bean.comptabilite.EcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import com.dummy.myerp.model.bean.comptabilite.SequenceEcritureComptable;
import com.dummy.myerp.technical.exception.FunctionalException;

@RunWith(MockitoJUnitRunner.class)
public class ComptabiliteManagerImplTest {

    private ComptabiliteManagerImpl manager = new ComptabiliteManagerImpl();

    private EcritureComptable vEcritureComptable;
    
    private LigneEcritureComptable ligneEcritureDebit;
    
    private LigneEcritureComptable ligneEcritureCredit;
    
    private SequenceEcritureComptable vSequence;
    
    @Mock
    private DaoProxy mockDaoProxy;
    
    @Mock
    private ComptabiliteDao mockComptabiliteDao;
    
    @Mock
    private TransactionManager mockTransactionManager;
    
    @Before
    public void initComptabilitéManagerImpl() {
    	
    	when(this.mockDaoProxy.getComptabiliteDao()).thenReturn(this.mockComptabiliteDao);
    	AbstractBusinessManager.configure(null, this.mockDaoProxy, this.mockTransactionManager);
    	
        vEcritureComptable = new EcritureComptable();
        vEcritureComptable.setId(1);
        vEcritureComptable.setJournal(new JournalComptable("AC", "Achat"));
        Calendar calendar = new GregorianCalendar(2020,0,21);
        vEcritureComptable.setDate(calendar.getTime());
        vEcritureComptable.setReference("AC-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        
        ligneEcritureDebit = new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("123"), null);
        ligneEcritureCredit = new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal("123"));
    }
    
    /*
     * Vérifie que aucune exception n'est levé avec des données correctes
     */
    @Test
    public void checkEcritureComptableUnit() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /*
     * Vérifie qu'une exception est levé avec une écriture comptable vide
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitViolation() throws Exception {
        EcritureComptable testEcritureComptable;
        testEcritureComptable = new EcritureComptable();
        manager.checkEcritureComptableUnit(testEcritureComptable);
    }

    /*
     * Vérifie qu'une exception est levé si la RG2 n'est pas respecté
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG2() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2),
                                                                                 null, null,
                                                                                 new BigDecimal("1234")));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }

    /*
     * Vérifie qu'une exception est levé si la RG3 n'est pas respectée avec moins de deux lignes d'écritures.
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3Moins2LigneEcriture() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG3 n'est pas respectée avec deux lignes d'écritures mais pas de ligne de crédit
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3PasDeLigneEcritureCredit() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("541"), null));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG3 n'est pas respectée avec deux lignes d'écritures mais pas de ligne de crédit
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableUnitRG3PasDeLigneEcritureDebit() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal("541")));
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG5 n'est pas respectée avec une année différente de celle de la référence
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableRG5AnneeDateDifferenteAnneeReference() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
        vEcritureComptable.setReference("AC-2019/00001");
        vEcritureComptable.setLibelle("Libelle");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG5 n'est pas respectée avec un code Journal différent de celle de la référence
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableRG5CodeJournalDifferenteCodeReference() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
        vEcritureComptable.setReference("CA-2020/00001");
        vEcritureComptable.setLibelle("Libelle");
        manager.checkEcritureComptableUnit(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'aucune exception n'est levé si la RG6 est respectée
     */
    @Test
    public void checkEcritureComptableContextRG6() throws Exception {
    	when(this.mockComptabiliteDao.getEcritureComptableByRef(anyString())).thenReturn(vEcritureComptable);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
    	manager.checkEcritureComptableContext(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG6 n'est pas respectée car les id des deux ecriture comptable sont différents 
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6IdDifferent() throws Exception {
    	when(this.mockComptabiliteDao.getEcritureComptableByRef(anyString())).thenReturn(vEcritureComptable);
    	
    	EcritureComptable ecritureComptable = Mockito.mock(EcritureComptable.class);
    	when(ecritureComptable.getId()).thenReturn(6);
    	when(ecritureComptable.getReference()).thenReturn("AC-2020/00001");
    	
    	manager.checkEcritureComptableContext(ecritureComptable);
    }
    
    
    /*
     * Vérifie qu'une exception est levé si la RG6 n'est pas respectée car l'id de l'écriture comptable testé est une nouvelle écriture
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableContextRG6IdNull() throws Exception {
    	when(this.mockComptabiliteDao.getEcritureComptableByRef(anyString())).thenReturn(vEcritureComptable);
    	
    	EcritureComptable ecritureComptable = Mockito.mock(EcritureComptable.class);
    	when(ecritureComptable.getId()).thenReturn(null);
    	when(ecritureComptable.getReference()).thenReturn("AC-2020/00001");
    	
    	manager.checkEcritureComptableContext(ecritureComptable);
    }
    
    /*
     * Vérifie qu'aucune exception est levé si la RG7 est respectée
     */
    @Test
    public void checkEcritureComptableDigitsDecimalPointRG7() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal("54.21")));
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("54.21"), null));
    	manager.checkEcritureComptableDigitsDecimalPoint(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG7 n'est pas respectée car la ligne d'écriture de Crédit contient plus de 2 chiffres après la virgule
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableDigitsDecimalPointRG7ThreeDigitsAfterDecimalPointCredit() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal("54.225")));
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("54.21"), null));
    	manager.checkEcritureComptableDigitsDecimalPoint(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'une exception est levé si la RG7 n'est pas respectée car la ligne d'écriture de Débit contient plus de 2 chiffres après la virgule
     */
    @Test(expected = FunctionalException.class)
    public void checkEcritureComptableDigitsDecimalPointRG7ThreeDigitsAfterDecimalPointDebit() throws Exception {
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, null, new BigDecimal("54.25")));
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("54.215"), null));
    	manager.checkEcritureComptableDigitsDecimalPoint(vEcritureComptable);
    }
    
    /*
     * Vérifie qu'aucune exception n'est levée si les fonctions checkEcritureComptableUnit checkEcritureComptableContext et checkEcritureComptableDigitsDecimalPoint sont lancés à la suite
     */
    @Test
    public void checkEcritureComptableRG4() throws Exception {
    	when(this.mockComptabiliteDao.getEcritureComptableByRef(anyString())).thenReturn(vEcritureComptable);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureDebit);
    	vEcritureComptable.getListLigneEcriture().add(ligneEcritureCredit);
    	manager.checkEcritureComptable(vEcritureComptable);
    }
    
    /*
     * Vérifie que l'aucune exception n'est levée si les lignes d'écritures sont négtives
     */
    @Test
    public void checkEcritureComptableRG4NegativeLigneEcriture() throws Exception {
    	when(this.mockComptabiliteDao.getEcritureComptableByRef(anyString())).thenReturn(vEcritureComptable);
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(1), null, new BigDecimal("-123"), null));
    	vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(2), null, null, new BigDecimal("-123")));
    	manager.checkEcritureComptable(vEcritureComptable);
    }
}
