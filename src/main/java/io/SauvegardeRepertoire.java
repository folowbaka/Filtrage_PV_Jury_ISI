package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * SauvegardeRepertoire sauvegarde les repertoires parcouru dans le JFileChooser
 */
public class SauvegardeRepertoire {

	/**
	 * Ensemble des chemins deja parcouru
	 */
	private static List<String> paths = new ArrayList<String>();

	/**
	 * chemin vers le fichier contenant les paths
	 */
	private static File pathFile = new File("src/files/paths.txt");

	/**
	 * getPaths recupere tous les chemins se trouvant dans le fichier paths.txt
	 * @return la liste des chemins
	 */
	public static List<String> getPaths() {
		String ligne;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pathFile));
			while ((ligne = br.readLine()) != null){
				if (!paths.contains(ligne)) {
					paths.add(ligne);//on recupere les anciens repertoires si il n'existe pas deja		
				}
			}
			br.close();
		} catch (IOException e) {
			System.out.println ("Erreur lors de la lecture : " + e.getMessage());
		}
		return paths;
	}

	/**
	 * ajoutPath ajoute le chemin du JfileChooser dans le fichier paths.txt
	 * @param chooser le choix du repertoire
	 */
	public static void ajoutPath(JFileChooser chooser) {
		if (!paths.contains(chooser.getSelectedFile().getParent())) {
			paths.add(chooser.getSelectedFile().getParent());//on recupere le rep selectionne	
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(pathFile)));
			for (String string : paths) {
				pw.println(string);//on reecrit la nouvelle liste
			}
			pw.close();
		} catch (IOException e) {
			System.out.println ("Erreur lors de la lecture : " + e.getMessage());
		}
	}

	/** 
	 * setter pour modifier les chemins de fichiers
	 * @param paths les chemins
	 */
	public static void setPaths(List<String> paths) {
		SauvegardeRepertoire.paths = paths;
	}

	/**
	 * Getter pour recuperer le fichier ou sont contenu les chemins
	 * @return les chemins
	 */
	public static File getPathFile() {
		return pathFile;
	}
}

