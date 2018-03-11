package data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe representant un etudiant
 */
public class Etudiant {

	/**
	 * nom de l'etudiant
	 */
	private String nom;

	/**
	 * prenom de l'etudiant
	 */
	private String prenom;

	/**
	 * nombre de credit obtenu par l'etudiant
	 */
	private int creditTotal;

	/**
	 * nombre de semestre effectué par l'etudiant
	 */
	private int nbSemestres;

	/**liste des modules fait par l'etudiant*/
	private List<Module> modules = new ArrayList<Module>();

	/**
	 * @param nom le nom de l'etudiant
	 * @param prenom le prenom de l'etudiant
	 * @param modules les modules de l'etudaint
	 * @param creditTotal le total des credit de l'etudiant
	 * @param semestres le nombre de semestres
	 */
	public Etudiant(String nom, String prenom, List<Module> modules, int creditTotal, int semestres) {
		setNom(nom);
		setPrenom(prenom);
		setModules(modules);
		setCreditTotal(creditTotal);
		setNbSemestres(semestres);
	}


	/**
	 * Methode pour savoir si l'etudiant est en stage dans son dernier semestre
	 * @return si l'etudiant est en stage
	 */
	public boolean enStage(){
		Iterator<Module> it = modules.iterator();
		while(it.hasNext()){
			Module mod = it.next();
			if(mod.getNom().equals("ST09") && mod.getSemestre()==nbSemestres) //si durant sont dernier semestre il a fait un ST09
				return true;
			if(mod.getNom().equals("ST10") && mod.getSemestre()==nbSemestres) //si durant sont dernier semestre il a fait un ST10
				return true;
			if(mod.getNom().equals("ST30") && mod.getSemestre()==nbSemestres) //si durant sont dernier semestre il a fait un ST30
				return true;
		}
		return false;
	}

	/**
	 * Getter nom etudiant
	 * @return le nom de l'etudiant
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Setter nom de l' etudiant
	 * @param nom le nom de l'etudiant
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * Getter prenom etudiant
	 * @return le prenom de l'etudiant
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * Setter prenom de l' etudiant
	 * @param prenom le prenom de l'etudiant
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * Getter modules etudiant
	 * @return les modules de l'etudiant
	 */
	public List<Module> getModules() {
		return modules;
	}

	/**
	 * Setter liste des modules de l' etudiant
	 * @param modules les modules de l'etudiant
	 */
	public void setModules(List<Module> modules) {
		this.modules = modules;
	}

	/**
	 * Getter creditTotal de l'etudiant
	 * @return le nombre de credit de l'etudiant
	 */
	public int getCreditTotal() {
		return creditTotal;
	}

	/**
	 * Setter creditTotal de l' etudiant
	 * @param creditTotal le nombre de semestre de l'etudiant
	 */
	public void setCreditTotal(int creditTotal) {
		this.creditTotal = creditTotal;
	}

	/**
	 * Getter nombre de semestre de l' etudiant
	 * @return le nombre de semestre de l'etudiant
	 */
	public int getNbSemestres() {
		return nbSemestres;
	}

	/**
	 * Setter nombre de semestre de l' etudiant
	 * @param nbSemestres le nombre de semestre de l'etudiant
	 */
	public void setNbSemestres(int nbSemestres) {
		this.nbSemestres = nbSemestres;
	}

	@Override
	public boolean equals(Object obj) {
		Etudiant etu = (Etudiant) obj;
		if(etu == this)
			return true;
		if( this.getNom().equals(etu.getNom()) && this.getPrenom().equals(etu.getPrenom()))
			return true;
		else
			return false;
	}

	@Override
	public String toString(){

		String chaine = nom + " "+ prenom + " total credit:" + creditTotal + " Semestre total:" + nbSemestres + "\n";
		if (modules!=null){
			Iterator<Module> it = modules.iterator();
			while (it.hasNext()) {
				Module module = (Module) it.next();
				chaine+= module + "\n";
			}
		}
		return chaine;
	}
}
