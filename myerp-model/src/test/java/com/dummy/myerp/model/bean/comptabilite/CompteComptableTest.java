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
public class CompteComptableTest {
	
	private List<CompteComptable> comptesComptable;
	
	/*
	 * Création d'un compte comptable vCompte
	 * Création d'une liste de compte comptable comptesComptable
	 * Ajout dans cette liste de 3 comptes comptable
	 */
	@Before
	public void initCompteComptable() {
		
		comptesComptable = new ArrayList<CompteComptable>();
		
		for (int i = 0; i < 3; i++ ) {
			CompteComptable compteComptable = Mockito.mock(CompteComptable.class);
			Mockito.when(compteComptable.getNumero()).thenReturn(i);
			Mockito.when(compteComptable.getLibelle()).thenReturn("compte" + i);
			comptesComptable.add(compteComptable);
		}
		
	}
	
	/*
	 * Après chaque test effacement des données
	 */
	@After
	public void undefCompteComptable() {
		
		comptesComptable.clear();
	}
	
	/*
	 * Test si le compte est présent dans la liste
	 */
	@Test
	public void getByNumero_whenCompteComptableExist_Test() {
		assertThat(CompteComptable.getByNumero(comptesComptable, 2).getLibelle()).isEqualTo("compte2");	
	}
	
	/*
	 * Test si le compte n'est pas dans la liste
	 */
	@Test
	public void getByNumero_whenCompteComptableNotExist_Test() {
		assertThat(CompteComptable.getByNumero(comptesComptable, 3)).isEqualTo(null);
	}
}
