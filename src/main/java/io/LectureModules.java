
package io;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import data.Module;


/**
 * LectureModule permet de connaitre les modules d'ISI avec leur credit, leur categorie et leur type
 */
public class LectureModules {

	/**
	 * file contient le chemin du fichier texte ou sont contenus les donnees sur les modules
	 */
	private static String file = "src/files/modules.txt";


	/**
	 * lireModules est la methode qui le le fichier ou sont contenus les modules et les instancie en objet java
	 * @return la liste des modules qui sont consideres comme des modules de type ISI
	 */
	public static List<Module> lireModules(){
		List<Module> modules = new ArrayList<Module>();
		BufferedReader lecteurAvecBuffer = null;
		String ligne;

		//Represente les mots d'une ligne 
		String listeMots[];

		String nomModule, parcoursModule, categorieModule;
		int creditModule;

		// Parcours toutes les lignes du fichier texte
		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(new File(file)));
			while ((ligne = lecteurAvecBuffer.readLine()) != null){
				listeMots=ligne.split(" ");
				
				if (listeMots[0].equals("//")){}
				else{
					if(listeMots.length==4){
					nomModule = listeMots[0];
					creditModule = Integer.valueOf(listeMots[1]);
					categorieModule = listeMots[2];
					parcoursModule = listeMots[3];
					modules.add(new Module(nomModule, creditModule, categorieModule, parcoursModule));
					}
				}
			}
			lecteurAvecBuffer.close();
		} catch (IOException e) {
			System.out.println("Erreur de fichier");
			e.printStackTrace();
		}	
		return modules;
	}
}


