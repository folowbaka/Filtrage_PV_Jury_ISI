package data;

/**
 * La classe module représente un module de l'UTT
 */
public class Module{
	
	/**Nom du module*/
	private String nom;

	/**CS ou TM*/
	private String categorie;

	/**ISI RT GI etc*/
	private String parcours;

	/**Semestre a laquel l'UV est fait par l'etudiant*/
	private int semestre;

	/**Note que l'etudiant a eu a cette UV*/
	private Note note;

	/**Credit de l'UV*/
	private int credit;

	/**
	 * Constructeur: pour les etudiants a traiter
	 * @param nom le nom du module
	 * @param note la note du module
	 * @param credit le credit du module
	 * @param semestre le semestre du module
	 * @param parcours le parcours du module
	 * @param categorie la categorie du module
	 */
	public Module(String nom, Note note, int credit, int semestre, String parcours, String categorie) {
		this.nom = nom;
		this.categorie = categorie;
		this.semestre = semestre; 
		this.note = note;
		this.parcours = parcours;
		this.credit = credit;
	}
	
	/**
	 * Constructeur: pour les modules existant	
	 * @param nom le nom du module
	 * @param credit le credit du module
	 * @param categorie la categorie du module
	 * @param parcours le parcours du module
	 */
	public Module(String nom, int credit, String categorie, String parcours){
		this.nom = nom;
		this.credit = credit;
		this.categorie = categorie;
		this.parcours = parcours;
	}
	
	@Override
	public String toString() {
		return "Module{ nom=" + nom + " parcours=" + parcours + " semestre: " + semestre + " Note=" + note + " credit=" + credit + " categorie=" + categorie + "}";
	}
	
	/**
	 * Getter: le nom du module
	 * @return le nom du module
	 */
	public String getNom() {
		return nom;
	}
	
	/**
	 * Setter: le nom du module
	 * @param nom le nom du module
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * Getter: la categorie du module
	 * @return la categorie du module
	 */
	public String getCategorie() {
		return categorie;
	}

	/**
	 * Setter: la categorie du module
	 * @param categorie la categorie du module
	 */
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	/**
	 * Getter: le parcours du module
	 * @return le parcours du module
	 */
	public String getParcours() {
		return parcours;
	}

	/**
	 * Setter: le parcours du module
	 * @param parcours le parcours du module
	 */
	public void setParcours(String parcours) {
		this.parcours = parcours;
	}

	/**
	 * Getter: le semestre du module
	 * @return le semestre du module
	 */
	public int getSemestre() {
		return semestre;
	}

	/**
	 * Setter: le semestre du module
	 * @param semestre le semestre 
	 */
	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	/**	
	 * Getter: la note du module
	 * @return la note du module
	 */
	public Note getNote() {
		return note;
	}

	/**
	 * Setter: la note du module
	 * @param note la note a modifié
	 */
	public void setNote(Note note) {
		this.note = note;
	}

	/**
	 * Getter: credit
	 * @return le credit du module
	 */
	public int getCredit() {
		return credit;
	}

	/**
	 * Setter: le credit du module
	 * @param credit le credit du module
	 */
	public void setCredit(int credit) {
		this.credit = credit;
	}
}
