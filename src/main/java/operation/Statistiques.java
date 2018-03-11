package operation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import data.Etudiant;
import data.Module;
import data.Note;


/**
 * Classe qui réunit toutes les methodes pour les statistiques.
 */
public abstract class Statistiques {	

	/**
	 * Ecrit dans un csv les statistiques sous forme de table.
	 * @param nomFichierTexte le fichier contenant les données des etudiants
	 * @param nomFichierStat le fichier de sortie
	 */
	public static void ecritureStatistiques(String nomFichierTexte, String nomFichierStat){
		File fileStat = new File(nomFichierStat);// on recupere le fichier de stat
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileStat);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedWriter bw;
		PrintWriter pw;
		bw = new BufferedWriter(fw);
		pw = new PrintWriter(bw);
		List<Etudiant> etudiants = GestionData.listeEtudiant(new File(nomFichierTexte));
		Iterator<Etudiant> it = etudiants.iterator();

		pw.println("Nom Ue;Total;Reussie;Ratee;%Reussie;A;B;C;D;E;F/FX;Autres");
		ArrayList<Module> modules = new ArrayList<Module>();			
		while(it.hasNext()){
			Etudiant etu =it.next();
			Iterator<Module> it2 = etu.getModules().iterator();
			while(it2.hasNext()){
				boolean existe= false;
				Module mod = it2.next();
				Iterator<Module> it3 = modules.iterator();
				while (it3.hasNext()){//cette boucle vérifie qu'il n'y a pas de doublons
					Module mod2=it3.next();
					if (mod2.getNom().equals(mod.getNom())){
						existe=true;
					}
				}
				if (!existe){
					modules.add(mod);
				}
			}
		}

		Iterator<Module> it3 = modules.iterator();
		while(it3.hasNext()){
			Module mod = it3.next();
			ArrayList<Integer> stats= new ArrayList<Integer>();
			stats= pourcentUe(etudiants, mod);
			if (stats.get(0)!=0){
				float pourcentReussite=(float)stats.get(1)/stats.get(0);
				pw.println(mod.getNom()+ ";" + stats.get(0) + ";" + stats.get(1) + ";" + stats.get(2) + ";" + pourcentReussite*100 + ";" + stats.get(3) + ";" + stats.get(4) + ";" + stats.get(5) + ";" + stats.get(6) + ";" + stats.get(7) + ";" + stats.get(8) + ";" + stats.get(9));
			}
		}
		pw.close();
	}

	/**
	 * Calcule le nombre total d'une note entrée en paramètre parmis tout les etudiants.
	 * @param etudiants la liste des etudiants
	 * @param note la note dont on veut compter le total
	 * @return le nombre total de cette note.
	 */
	public static int totalNote(List<Etudiant> etudiants, Note note){
		int totalNote=0;
		Iterator<Etudiant> it = etudiants.iterator();
		while (it.hasNext()) {
			Etudiant etu = it.next();
			int i=0;
			while(i<etu.getModules().size()){
				if (etu.getModules().get(i).getNote()==note){
					totalNote++;
				}
				i++;
			}
		}
		return totalNote;
	}

	/**
	 * Renvois un tableau contenant des statistiques.
	 * @param etudiants les etudiants traités
	 * @param mod le module que l'on veut tester
	 * @return 	un tableau : le nombre total de fois qu'elle a été effectuée, 
	 * le nombre de fois qu'elle a été réussie, le nb de fois qu'elle a été ratée,
	 * le nombre de chaque note de A à F. nbElse signifie tout autres notes (ex: ABS).
	 */
	public static ArrayList<Integer> pourcentUe(List<Etudiant> etudiants, Module mod){
		String nomUe = mod.getNom();
		int nbReussie=0, nbRate=0, nbA=0, nbB=0, nbC=0, nbD=0, nbE=0, nbF=0, nbElse=0;
		Iterator<Etudiant> it = etudiants.iterator();
		while (it.hasNext()) {
			Etudiant etu = it.next();
			int i=0;
			while(i<etu.getModules().size()){
				if (etu.getModules().get(i).getNom().equals(nomUe)){
					if (DecisionJury.estRatee(etu.getModules().get(i))){
						nbRate++;
					}
					else{
						nbReussie++;
					}
					switch(etu.getModules().get(i).getNote()){
					case A:
						nbA++;
						break;
					case B:
						nbB++;
						break;
					case C:
						nbC++;
						break;
					case D:
						nbD++;
						break;
					case E:
						nbE++;
						break;
					case F:
					case FX:
						nbF++;
						break;
					default:
						nbElse++;
						break;
					}
				}

				i++;
			}
		}
		int nbTotal = nbRate+nbReussie;
		ArrayList<Integer> out = new ArrayList<Integer>();
		out.addAll(Arrays.asList(nbTotal, nbReussie, nbRate, nbA, nbB, nbC, nbD, nbE, nbF, nbElse));
		return out;
	}
}