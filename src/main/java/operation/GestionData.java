package main.java.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import main.java.data.Etudiant;
import main.java.data.Module;
import main.java.data.Note;
import main.java.data.Observation;
import main.java.operation.Modele;

/**
 * GestionData gere les donnees des fichiers des etudiants
 */
public class GestionData {

	/**
	 * etudiants est la liste des objets de type Etudiant que nous sommes en train de traiter dans le fichier
	 */
	private List<Etudiant> etudiants = new ArrayList<Etudiant>();

	/*
	 * nbSemestre indique le nombre de semestre qu'a fait l'etudiant
	 */
	private static int nbSemestres = 0; 

	/*
	 * nbEtudiant indique le nombre d'etudiant que nous avons traité
	 */
	private static int nbEtudiant = 0;

	/**
	 * Reset des données pour un nouvel etudiant
	 */
	public static void reset(){
		setNbSemestres(0);
	}

	/**
	 * listeEtudiant traite le fichier txt des etudiants et permet d;instncier les etudaints et les modules pour pouvoir les manipuler en java
	 * @param file le fichier qui traite les donnees
	 * @return la liste des etudiants
	 */
	public static List<Etudiant> listeEtudiant(File file,boolean modele){
		BufferedReader lecteurAvecBuffer;
		List<String> datas = new ArrayList<String>();//donnes du fichier
		reset();// Initialisation
		try {
			lecteurAvecBuffer = new BufferedReader(new FileReader(file));
			String contenu;
			String listeMots[];
			while ((contenu = lecteurAvecBuffer.readLine()) != null){
				listeMots = contenu.split(" ");

				//Ajout des donnees du fichier dans une liste
				for (String string : listeMots) {
					datas.add(string);
				}
			}
		}
		catch(FileNotFoundException exc) {
			System.out.println("Erreur d'ouverture");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		//On creer les etudiants
		return creationListeEtudiants(datas,modele);
	}

	/**
	 * methode qui instancie la liste des etudiants a partir du fichier texte
	 * @param datas les donnees dans le fichier
	 * @return la liste des etudiants
	 */
	private static List<Etudiant> creationListeEtudiants(List<String> datas,boolean modele){
		List<String> dataEtudiant = new ArrayList<String>();
		List<Etudiant> etudiants = new ArrayList<Etudiant>();
		Iterator<String> it = datas.iterator();
		while (it.hasNext()) {//on parcourt les donnees
			String data = it.next();
			if(RecherchePattern.rechercheDebutEtudiant(data)){	//si on change d'etudiant on reset les donnees concernant l'ancien etudiant
				reset();
				dataEtudiant = new ArrayList<String>();//on reset les donnees
				setNbEtudiant(getNbEtudiant() + 1);
			}
			else{//si on a pas changer d'etudiant on stocke ces donnees
				if(RecherchePattern.rechercheFinEtudiant(data)){//si on est a la fin des donnees on creer l'etudiant
					dataEtudiant.add(data); //on ajoute le dernier element au donnee
					if(!modele)
						etudiants.add(ajoutEtudiant(dataEtudiant));
					else
						etudiants.add(ajoutEtudiantModele(dataEtudiant));
					reset();
				}
				else{
					dataEtudiant.add(data);//on est pas a la fin on attend
				}
			}
		}
		etudiants = suppressionDoublons(etudiants); //on supprime les
		return etudiants;
	}

	/**
	 * ajoutEtudiant permet de créer l'etudiant en objet Java et de l'ajouter à la liste des etudiants deja instanciées 
	 * @param dataEtudiant les donnees de l'etudiant
	 * @return l'etudiant
	 */
	private static Etudiant ajoutEtudiantModele(List<String> dataEtudiant){
		Etudiant etu=ajoutEtudiant(dataEtudiant);
		ArrayList<Observation> observations=ajoutObservationEtudiant(dataEtudiant);
		etu.setObservations(observations);
		return etu;
	}
	private static ArrayList<Observation> ajoutObservationEtudiant(List<String> dataEtudiant)
	{
		Iterator<String> it = dataEtudiant.iterator();
		boolean enSemestre = false;
		ArrayList<Observation> observations=new ArrayList<>();
		Observation observation=null;
		while (it.hasNext()) {//on parcourt les donnees
			String data = it.next();
			if(RecherchePattern.rechercheDebutSemestre(data) &&  !enSemestre){	//pour le premier semestre
				observation=new Observation();
				data=it.next();
				String semestreObservation=data;
				data=it.next();
				semestreObservation+=data;
				observation.setSemestre(semestreObservation);
				enSemestre = true;
				data=it.next();
				if(RecherchePattern.rechercheFiliere(data))
					observation.setFiliere(true);
			}
			if(enSemestre){//si on est dans la zone de semestres
				if(RecherchePattern.rechercheFinSemestre(data)){//si on est a la fin du semestre
					enSemestre=false;
					observations.add(observation);
				}
				else if(RecherchePattern.rechercheDecision(data)){//si on est dans la zone semestre
					if(data.equals("Poursuite"))
					{
						data=it.next();
						if(data.equals("normale"))
							observation.setDecision("PN");
						else
						{
							data=it.next();
							if(data.equals("conseil"))
								observation.setDecision("PC");
							else
								observation.setDecision("PR");
						}
					}
				}
				else if(RecherchePattern.rechercheCommSemestre(data))
				{
					switch(data)
					{
						case "Très":
							data = it.next();
							if (data.equals("bon"))
								observation.setCommSemestre("COMS7");
							else
								observation.setCommSemestre("COMS1");
							break;
						case "Mauvais":
							observation.setCommSemestre("COMS2");
							break;
						case "Semestre":
							data = it.next();
							if (data.equals("moyen"))
								observation.setCommSemestre("COMS4");
							else
								observation.setCommSemestre("COMS3");
							break;
						case "Assez":
							observation.setCommSemestre("COMS5");
							break;
						case "Bon" :
							observation.setCommSemestre("COMS6");
							break;
						case "Excellent":
							observation.setCommSemestre("COMS8");
							break;
					}
				}
				else if(RecherchePattern.rechercheCommComplementaire(data))
				{
					switch(data)
					{
						case "Attention,":
							data = it.next();
							if (data.equals("vous"))
							{
								for(int i=0;i<6;i++)
									data=it.next();
								if(data.equals("NPML"))
								{
									data=it.next();
									if(data.equals("Anglais,")) {
										observation.getCommComplementaire().add("CC2");
									}
								}
							}
							break;
						case "Votre":
							data=it.next();
							if(data.equals("maintien"))
								observation.getCommComplementaire().add("CC1");
							break;
					}
				}

			}
		}
		return observations;
	}
	private static Etudiant ajoutEtudiant(List<String> dataEtudiant){
		String nom = RecherchePattern.recupereNom(dataEtudiant);//on recupere le nom
		String prenom = RecherchePattern.recuperePrenom(dataEtudiant);// on recupere le prenom
		// JM pour test
//		if (nom.equals("TROIS"))
//		  System.out.println(nom);// à enlever

		List<Module> modulesEtudiant = ajoutModulesEtudiant(dataEtudiant);//on recupere les UE
		int credit = RecherchePattern.recupereTotalCredit(dataEtudiant);
		nbEtudiant++;
		return new Etudiant(nom, prenom, modulesEtudiant, credit, nbSemestres);
	}

	/**
	 * ajoutModulesEtudiant methode qui recupere tous les modules qu'a fait l'etudiant
	 * @param dataEtudiant  les donnees de l'etudiant
	 * @return la liste des modules
	 */
	private static List<Module> ajoutModulesEtudiant(List<String> dataEtudiant){
		List<Module> modules = new ArrayList<Module>();
		List<String> dataSemestreEtudiant = new ArrayList<String>(); //donnees d'un semestre de l'etudiant
		boolean enSemestre = false;
		Iterator<String> it = dataEtudiant.iterator();
		while (it.hasNext()) {//on parcourt les donnees
			String data = it.next();
			if(RecherchePattern.rechercheDebutSemestre(data)){	//pour le premier semestre
				enSemestre = true;
			}
			if(enSemestre){//si on est dans la zone de semestres
				if(RecherchePattern.rechercheFinSemestre(data)){//si on est a la fin du semestre
					nbSemestres++; //on ajute un semestre a l'etudiant
					modules.addAll(creationModulesSemestre(dataSemestreEtudiant));
					dataSemestreEtudiant = new ArrayList<String>();// on reset les donnees
					enSemestre=false;
				}
				else{//si on est dans la zone semestre
					dataSemestreEtudiant.add(data);
				}
			}
		}
		return modules;
	}

	/**
	 * creationModulesSemestre creer les modules de l'etudiant d'un semestre
	 * @param modulesData les donnees sur les UEs d'un semestre
	 * @return la liste des modules du semestre
	 */
	private static List<Module> creationModulesSemestre(List<String> modulesData) {
		List<Module> mods = new ArrayList<Module>();
		String parcours = RecherchePattern.recupereParcours(modulesData);
		//int semestre = RecherchePattern.recupereSemestre(modulesData);
		String nomModule = null;
		Note note = null;
		String categorie = null;
		int credit = 0;

		Iterator<String> it = modulesData.iterator();
		while (it.hasNext()) {
			String str = it.next();
			
			if (str.equals("A17"))
				System.out.println("printemps2017");

			if ((str.length()>1)&&(RecherchePattern.recupereNomModule2(str) != null)){//des qu'on a le premier module 
				nomModule=RecherchePattern.recupereNomModule2(str);//on recupere le nom du module
				categorie=RecherchePattern.recupereCategorie(nomModule);// la categorie
				credit=RecherchePattern.recupereCredit(str); //le nombre de credit
			}

			if(RecherchePattern.recupereNote(str) != null){
				note=RecherchePattern.recupereNote(str);	
			}

			//si toutes les valeurs sont ok alors on creer le module
			if (nomModule != null && note != null && credit != 0 && parcours != null) {
				Module module = new Module(nomModule, note, credit, nbSemestres, parcours, categorie);
				mods.add(module);
				
				//on reset les donnees pour le prochain module
				nomModule = null;
				note = null;
				credit = 0;
				categorie = null;
			}	



		}
		return mods;
	}

	//TODO Redecrire cette methode A VOIR SI CET METHODE PEUT ETRE UTILE POUR LA GESTION DES ETUDIANTS CHINOIS
	/**
	 * Indique si le mot clé "Jiaotong" se trouve dans le tableau
	 * Ces étudiants chinois ne font qu'un ST09 et pas de ST10
	 * @param tabMots tableau de mots composant une ligne de texte à analyser
	 * @return true si le tableau tabMots contient une université Chinoise (Jiaotong), sinon false
	 */
	public boolean trouveUniversiteChinoise (String tabMots[]){
		int i;
		i=0;
		while ((i<tabMots.length) && (! (tabMots[i].equals("Jiaotong"))) ) {
			i=i+1;
		}
		return (i<tabMots.length);	
	}


	/**
	 * Getter la liste des etudiants
	 * @return les etudiants
	 */
	public List<Etudiant> getEtudiants() {
		return etudiants;
	}

	/**
	 * Setter etudiants
	 * @param etudiants les etudiants
	 */
	public void setEtudiants(List<Etudiant> etudiants) {
		this.etudiants = etudiants;
	}

	/**
	 * Getter nombre etudiants
	 * @return le nombre d'etudiant
	 */
	public static int getNbEtudiant() {
		return nbEtudiant;
	}

	/**
	 * Setter nombre d'etudiant
	 * @param nbEtudiant le nombre d'etudiant
	 */
	public static void setNbEtudiant(int nbEtudiant) {
		GestionData.nbEtudiant = nbEtudiant;
	}

	/**
	 * Getter nombre de semestre
	 * @return le nombre de semestre
	 */
	public static int getNbSemestres() {
		return nbSemestres;
	}
	
	/**
	 * Setter nombre de semestre
	 * @param nbSemestreEtudiant le nobre de semestre de l'etudiant
	 */
	public static void setNbSemestres(int nbSemestreEtudiant) {
		GestionData.nbSemestres = nbSemestreEtudiant;
	}

	/**
	 * Methode qui supprime les doublons des etudiants
	 * @param etudiants la liste des etudiants
	 * @return la liste sans les doublons
	 */
	private static List<Etudiant> suppressionDoublons(List<Etudiant> etudiants) {
		List<Etudiant> listEtu = new ArrayList<Etudiant>();
		for (int i=0; i<etudiants.size(); i++) {
			Etudiant etudiant = etudiants.get(i);
			if(etudiant.getNom()!=null) {
				if (!listEtu.contains(etudiant))
					listEtu.add(etudiant);
			}
		}
		return listEtu;
	}
}
