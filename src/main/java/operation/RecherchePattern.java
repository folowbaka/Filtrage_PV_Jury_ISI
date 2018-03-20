package main.java.operation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import main.java.data.Module;
import main.java.data.Note;
import main.java.io.LectureModules;

/**
 * RecherchePattern traite les pattern dans les fichiers texte pour recuperer des donnees senesible
 */
public abstract class RecherchePattern {

	/**
	 * modules contient la liste des modules existant dans le fichier modules pour filtrer les modules
	 */
	private static List<Module> modulesExistant = LectureModules.lireModules();

	/**
	 * regex pour savoir quand recuperer le nom et prenom
	 */
	private static String regexMrMme = "M.|Mme";

	/**
	 * regex pour recuperer le numero etudiant
	 */
	private static String regexNumEtudiant = "N°[0-9]{5}";

	/**
	 * regex pour recuperer le nom de l'etudiant
	 */
	private static String regexNom = "[A-ZÁÀ'ÇÉÈÊ]{1,}-*[A-ZÁÀÇÉÈÊ]{1,}";

	/**
	 * regex pour recuperer le prenom de l'etudiant
	 */
	private static String regexPrenom = "[A-Z][a-z]{1,}";

	/**
	 * regex pour l'annee de l'etudiant
	 */
	private static String regexAnnee = "[0-9]{2}/[0-9]{2}/20[0-9]{2}";

	/**
	 * regex pour les credits etudiants (par semestre il faut un total de 60 credits)
	 */
	private static String regexTotalCredit = "[0-9]{2,3}/(60|120|180|300|360)";

	/**
	 * regex pour le semestre de l'etudiant(ex: A10, P15)
	 */
	private static String regexSemestre = "(A|P)[0-9]{2}";

	/**
	 * regex pour le nom d'un module
	 */
	private static String regexNomModule = "([A-Z]{3,5})|([A-Z]{2,}[0-9]{2})";

	/**
	 * regex pour la note a un module
	 */
	private static String regexNoteModule = "[A-F]|FX|EQU|ABS|NULL";//juste une lettre

	/**
	 * regex pour le parcours du module
	 */
	private static String regexParcours = "ISI|TC|HC|SRT|MASTER";

	private static String regexDecision="Poursuite";
	private static String regexCommSemestre="Très|Mauvais|Semestre|Assez|Bon|Excellent";

	/**
	 * recupereNom recherche avec un systeme de regex le nom de l'etudiant dans le fichier
	 * @param dataEtudiant la liste des donnes dans laquelle chercher le nom
	 * @return le nom de l'etudiant
	 */
	public static String recupereNom(List<String> dataEtudiant){
		Iterator<String> it = dataEtudiant.iterator();
		String nom = "";
		List<String> noms = new ArrayList<String>();
		String contenu = "";
		while(it.hasNext()){
			contenu = it.next();
			if (Pattern.matches(regexMrMme, contenu)){ //on a trouve le M. de l'etudiant
				contenu = it.next(); // on passe le prenom
				while(it.hasNext()){
					contenu = it.next();
					if(Pattern.matches(regexNom, contenu)) // si le contenu ressemble a un nom
						noms.add(contenu);
					else if (Pattern.matches(regexNumEtudiant, contenu)) { // on arrete la recuperation du nom puisque on est au niveau du numero etudiant
						String[] nomTab = (String[]) noms.toArray(new String[0]);
						nom = String.join(" ", nomTab);
						return nom;
					}
				}
			}
		}
		return null;
	}

	/**
	 * recuperPrenom recherche avec un systeme de regex le prenom de l'etudiant dans le fichier
	 * @param dataEtudiant la liste des donnes dans laquelle chercher le prenom
	 * @return le prenom de l'etudiant
	 */
	public static String recuperePrenom(List<String> dataEtudiant){
		Iterator<String> it = dataEtudiant.iterator();
		String contenu = "";
		String prenom = "";
		while(it.hasNext()){
			contenu = it.next();
			if (Pattern.matches(regexMrMme, contenu)){ // on a trouve le M. de l'etudiant
				contenu = it.next(); 
				prenom += contenu + " "; // on recupere d'office la chaine qui suit Mr ou Mme car c'est le prenom
				while(it.hasNext()){
					contenu = it.next();
					if(Pattern.matches(regexPrenom, contenu)){ // si le contenu ressemble a un nom
						prenom += contenu + " ";
					}
					else if (Pattern.matches(regexNumEtudiant, contenu)) { // on arrete la recuperation du nom puisque on est au niveau du numero etudiant
						prenom = prenom.substring(0, prenom.length()-1); // on retire l'espace de fin de chaine
						return prenom;
					}
				}
			}
		}
		return null;
	}

	/**
	 * rechercheDebutEtudiant indique avec un systeme de regex quand commence les donnees d'un etudiant
	 * @param contenu la chaine a verifier 
	 * @return true si le conetnu indique que c'est un nouvelle etudiant
	 */
	public static boolean rechercheDebutEtudiant(String contenu) {
		if (Pattern.matches(regexAnnee, contenu))//si c'est un nouveau etudiant
			return true;
		return false;
	}

	/**
	 * rechercheFinEtudiant indique avec un systeme de regex quand fini les donnees d'un etudiant
	 * @param contenu la chaine a verifier 
	 * @return true si le contenu indique que c'est la fin d'un etudiant
	 */
	public static boolean rechercheFinEtudiant(String contenu) {
		if (Pattern.matches(regexTotalCredit, contenu))
			return true;
		return false;
	}

	/**
	 * rechercheDebutSemestre indique avec un systeme de regex quand debute les donnees d'un semestre d'un etudiant
	 * @param contenu la chaine a verifier
	 * @return true si le contenu indique que c'est le debut d'un semestre
	 */
	public static boolean rechercheDebutSemestre(String contenu) {
		if (Pattern.matches(regexSemestre, contenu))
			return true;
		return false;
	}

	/**
	 * rechercheFinSemestre indique avec un systeme de regex quand fini les donnees d'un semestre d'un etudiant
	 * @param contenu la chaine a verifier
	 * @return true si le contenu indique que c'est le debut d'un semestre
	 */
	public static boolean rechercheFinSemestre(String contenu) {
		String regex = "Total";
		if (Pattern.matches(regex, contenu))
			return true;
		return false;
	}

	public static boolean rechercheDecision(String contenu) {
		if (Pattern.matches(regexDecision, contenu))
			return true;
		return false;
	}

	public static boolean rechercheCommSemestre(String contenu) {
		if (Pattern.matches(regexCommSemestre, contenu))
			return true;
		return false;
	}

	/**
	 * recupereNomModule recherche avec un systeme de regex le nom du module
	 * @param contenu la chaine a verifier
	 * @return le nom du module
	 */
	public static String recupereNomModule(String contenu) {
		if (Pattern.matches(regexNomModule, contenu))
			return contenu;
		return null;
	}
	// Version JMN
	public static String recupereNomModule2(String contenu) {
		boolean trouve=false;
		int i=0;
		while ((!trouve)&&(i<modulesExistant.size())){
			if (modulesExistant.get(i).getNom().length()!=2){
			   trouve=modulesExistant.get(i).getNom().equals(contenu);
			} else {
//				System.out.println(modulesExistant.get(i).getNom()+" ---"+contenu+"--");
			   trouve=modulesExistant.get(i).getNom().equals(contenu.substring(0,2));
			}
			i++;
		}
		  
		if (trouve)
			return contenu;
		return null;
	}

	/**
	 * recupereNote recherche avec un systeme de regex la note du module
	 * @param contenu la chaine a verifier
	 * @return la note du module
	 */
	public static Note recupereNote(String contenu) {
		if (Pattern.matches(regexNoteModule, contenu))
			return Note.getNote(contenu);
		return null;
	}

	/**
	 * recupereCredit recherche avec un systeme de regex le credit du module
	 * @param nomModule le nom du module
	 * @return le credit du module
	 */
	public static int recupereCredit(String nomModule) {
		Iterator<Module> it = modulesExistant.iterator();
		while (it.hasNext()) {
			Module mod = (Module) it.next();
			if(mod.getNom().equals(nomModule)){//si on a trouve un module correspondant
				return mod.getCredit();//on retourne son credit
			}
		}
		it = modulesExistant.iterator();
		while (it.hasNext()) {
			Module mod = (Module) it.next();
			if (nomModule.startsWith(mod.getNom())){ //si on trouve un nom de module approchant
				return mod.getCredit();//on retourne son credit
			}
		}
		return 0;
	}

	/**
	 * recupereSemestreModule recherche avec un systeme de regex le semestre du module
	 * @param modulesData la liste des donnees du semestre
	 * @return le semestre du module
	 */
	public static int recupereSemestre(List<String> modulesData) {
		String semestre = null;
		int pos = 0;
		try{
			String recupereParcours = recupereParcours(modulesData);// on recupere le parcours qui se trouve juste avant le semestre
			pos = modulesData.indexOf(recupereParcours);//on recupere la position
			semestre = modulesData.get(pos+1);//on recupere le semestre
			return Integer.valueOf(semestre);
		}catch(NumberFormatException e){}
		return 0;
	}

	/**
	 * recupereParcours recherche avec un systeme de regex le parcours du module
	 * @param modulesData la liste des donnees du semestre
	 * @return le parcours du module
	 */
	public static String recupereParcours(List<String> modulesData) {
		Iterator<String> it = modulesData.iterator();
		String contenu;
		while(it.hasNext()){
			contenu = it.next();
			if (Pattern.matches(regexParcours, contenu))//si c'est un nouveau etudiant
				return contenu;
		}
		return null;
	}

	/**
	 * la categorie du module a partir de son nom et en utilisant modulesExistant
	 * @param nomModule le nom du module traiter
	 * @return la categorie du module
	 */
	public static String recupereCategorie(String nomModule){
		Iterator<Module> it = modulesExistant.iterator();
		while (it.hasNext()) {
			Module mod = (Module) it.next();
			if(mod.getNom().equals(nomModule)){//si on a trouve un module correspondant
				return mod.getCategorie();//on retourne sa categorie
			}
		}
		it = modulesExistant.iterator();
		while (it.hasNext()) {
			Module mod = (Module) it.next();
			if (nomModule.startsWith(mod.getNom())){ //si on trouve un nom de module approchant
				return mod.getCategorie();//on retourne sa categorie
			}
		}
		return "Inconnue";
	}

	/**
	 * totalCredit recupere le nombre de credit a partir d'un module
	 * @param data les datas a traiter
	 * @return le nombre de credit
	 */
	public static int recupereTotalCredit(List<String> data){
		Iterator<String> it = data.iterator();
		while (it.hasNext()) {
			String contenu = it.next();
			if (Pattern.matches(regexTotalCredit, contenu)){
				String tab[] = contenu.split("/");
				return Integer.valueOf(tab[0]);
			}
		}
		return 0;
	}
}