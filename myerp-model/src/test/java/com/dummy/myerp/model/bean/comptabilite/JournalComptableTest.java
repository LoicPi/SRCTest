package com.dummy.myerp.model.bean.comptabilite;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JournalComptableTest {
	
	private List<JournalComptable> journauxComptable;
	
	/*
	 * Création d'un journal comptable vJournal
	 * Création d'une liste de journaux comptables journauxComptable
	 * Ajout dans cette liste de 3 journal comptable
	 */
	@Before
	public void initJournalComptable() {
			
		journauxComptable = new ArrayList<JournalComptable>();
		
		for(int i = 0; i < 3; i++) {
			JournalComptable journalComptable = Mockito.mock(JournalComptable.class);
			Mockito.when(journalComptable.getCode()).thenReturn("code"+i);
			Mockito.when(journalComptable.getLibelle()).thenReturn("journal"+i);
			journauxComptable.add(journalComptable);
		}
	}
	
	/*
	 * Après chaque test effacement des données
	 */
	@After
	public void undefJournalComptable() {
		
		journauxComptable.clear();
	}
	
	/*
	 * Test si le compte est présent dans la liste
	 */
	@Test
	public void getByCode_whenJournalComptableExist_Test() {		
		assertThat(JournalComptable.getByCode(journauxComptable, "code1").getLibelle()).isEqualTo("journal1");	
	}
	
	/*
	 * Test si le compte n'est pas dans la liste
	 */
	@Test
	public void getByCode_whenJournalComptableNotExist_Test() {	
		assertThat(JournalComptable.getByCode(journauxComptable, "dc")).isEqualTo(null);
	}
}
