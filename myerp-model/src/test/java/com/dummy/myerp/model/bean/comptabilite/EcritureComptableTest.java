package com.dummy.myerp.model.bean.comptabilite;

import static org.assertj.core.api.Assertions.assertThat;


import java.math.BigDecimal;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EcritureComptableTest {


    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                                     .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        LigneEcritureComptable vRetour = new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                                                                    vLibelle,
                                                                    vDebit, vCredit);
        return vRetour;
    }
    
    private EcritureComptable vEcriture;
    
    private EcritureComptable ecritureComptable;
    
    @Before
    public void initEcritureComptable() {
    	
        vEcriture = new EcritureComptable();
    	
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
    }
    
    @Test
    public void getTotalDebit_Test() {
    	
        BigDecimal totalDebit = vEcriture.getTotalDebit();
        
        assertThat(totalDebit.intValueExact()).isEqualTo(341);
    }
    
    @Test
    public void getTotalCredit_Test() {
    	
        BigDecimal totalCredit = vEcriture.getTotalCredit();
        
        assertThat(totalCredit.intValueExact()).isEqualTo(341);
    }

    @Test
    public void isEquilibree() {        
    	assertThat(vEcriture.isEquilibree()).isEqualTo(true);
    }
    
    @Test
    public void isNotEquilibree() {

    	vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));   	
    	
    	assertThat(vEcriture.isEquilibree()).isEqualTo(false);
    }  
}
