package operation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.PrintWriter;

import data.Etudiant;
import data.Module;
import data.Note;
import io.LectureModules;

/**
 * Classe qui gere les decisions de jury des etudiants 
 */
public abstract class DecisionJury{

	/*
	 * fichierTexte represente le fichier qui contient les donnees des etudiants
	 */
	private static String fichierTexte;

	/*
	 * fichierCsv represente le fichier qui contiendra les decisionsJury en csv
	 */
	private static String fichierCsv;

	/*
	 * fichierPDF represente le fichier qui contiendra les decisionsJury en pdf
	 */
	private static String fichierPdf;
	/**
	 * Liste des etudiants qui sont entrain d'etre traités
	 */
	List<Etudiant> etudiants = new ArrayList<Etudiant>();

	/**
	 * Méthode qui ecrit les decisions de jury dans le fichier PDF et CSV a partir des donnes du fichier Texte
	 * @param nomFichierTexte le fichier texte à analyser contenant les donnees des etudiants
	 * @param nomFichierPDF  le fichier PDF de sortie (résultat)
	 * @param nomFichierCSV le fichier CSV de sortie (résultat)
	 */
	public static void ecritureDecisionJury (String nomFichierTexte, String nomFichierPDF, String nomFichierCSV){
		fichierPdf=nomFichierPDF;
		fichierTexte=nomFichierTexte;
		fichierCsv=nomFichierCSV;
		ecritureDecisionJuryCSV();//ecriture des decisions dans les CSV
		ecritureDecisionJuryPDF();//ecriture des decisions dans les PDF
	}

	/**
	 * Methode qui écrit les decisions de jury dans un fichier CSV
	 */
	public static void ecritureDecisionJuryCSV(){
		File file = new File(fichierCsv);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BufferedWriter bw;
		PrintWriter pw;
		bw = new BufferedWriter(fw);
		pw = new PrintWriter(bw);

		List<Etudiant> etudiants = GestionData.listeEtudiant(new File(fichierTexte),false);
		Iterator<Etudiant> it = etudiants.iterator();

		String entete="";
		entete+="Nom;Prenom;Stage;";
		int j =1, semSize= DecisionJury.maxSemestre(etudiants);
		while (j<=semSize){
			entete+="Avis Semestre "+(j)+";";
			j++;
		}
		entete+="\n";
		pw.print(entete);

		while (it.hasNext()) {
			Etudiant etudiant = it.next();
			String out="";
			out=out+etudiant.getNom()+";"+etudiant.getPrenom()+";";
			out+=DecisionJury.dernierStage(etudiant)+";";
			int i=1;
			List<String> avisSem= DecisionJury.avisJury(etudiant);
			while(i<avisSem.size()){
				out+= avisSem.get(i)+";";
				i++;
			}
			pw.println(out);
		}
		//pw.println(etudiants);

		pw.close();	
	}

	/**
	 * Methode qui ecrit les decisions de jury dans un fichier PDF
	 */
	public static void ecritureDecisionJuryPDF(){
		PDDocument doc;
		try {
			doc = new PDDocument();
			doc.save(fichierPdf);//sauvegarde du pdf dans le dossier qu'on a choisit
			doc.addPage(new PDPage());//on creer la premiere page

			PDFont font = PDType1Font.HELVETICA_BOLD;
			float fontSize = 7.0f;
			int marginLeft = 10;
			int marginBottom = 650;
			PDPageContentStream contentStream = null;

			int nbEtudiantPage = 31;
			int numPage = 0;
			List<Etudiant> etudiants = GestionData.listeEtudiant(new File(fichierTexte),false);
			Iterator<Etudiant> it = etudiants.iterator();

			//Ajout du titre
			String Titre = "Avis Etudiant ";
			float fontSizeTitle = 22.0f;
			PDPage page = doc.getPage(numPage);
			contentStream = new PDPageContentStream(doc, page);
			contentStream.beginText();// on commence l'ecriture des decision
			contentStream.newLineAtOffset(200, 700);
			contentStream.setFont(font, fontSizeTitle);
			contentStream.showText(Titre);//Ajout du titre
			contentStream.endText();

			//on prepare le text de decision
			contentStream.beginText();// on commence l'ecriture des decision
			contentStream.newLineAtOffset(marginLeft, marginBottom);
			contentStream.setFont(font, fontSize);
			contentStream.setLeading(20);

			int cptEtudiant = 0, cptTotal = 0;
			while(it.hasNext()){				
				Etudiant etudiant = it.next();
				cptEtudiant++;
				cptTotal++;
				if(cptEtudiant >= nbEtudiantPage){//on on ajoute les etudiants sur la page courante
					contentStream.endText();//on ferme le flux sur l'ancienne page 
					contentStream.close();
					doc.addPage(new PDPage());//on ajoute une nouvelle page
					cptEtudiant=0;//le compteur sur cette page est donc remis a zero
					page = doc.getPage(++numPage);// on change de page

					contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);//on ajoute du contenu
					contentStream.beginText();// on commence l'ecriture des decision
					marginBottom = 750;
					contentStream.newLineAtOffset(marginLeft, marginBottom);
					contentStream.setFont(font, fontSize);
					contentStream.setLeading(20);
				}
				System.out.println(etudiant.getNom());
				List<String> avisSem = DecisionJury.avisJury(etudiant);
				String lastAvis = avisSem.get(avisSem.size()-1);//on recupere le dernier avis de l'etudiant
				contentStream.showText(cptTotal+" : "+etudiant.getNom() + " " + etudiant.getPrenom() + " : " + lastAvis);	
				contentStream.newLine();
			}
			contentStream.endText();
			contentStream.close();
			doc.save(fichierPdf);//sauvegarde du pdf dans le dossier qu'on a choisit
			doc.close();//fermeture di document
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le nombre de note obtenues pour un étudiant et semestre donné.
	 * @param etu l'etudiant a traiter
	 * @param note la note que l'on veut compter
	 * @param semestre le semestre
	 * @return nbNote
	 */
	public static int compteNote(Etudiant etu, Note note, int semestre){
		int nbNote=0, i=0;
		while(i<etu.getModules().size()){
			if (etu.getModules().get(i).getNote()==note && etu.getModules().get(i).getSemestre()==semestre){
				nbNote++;
			}
			i++;
		}
		return nbNote;
	}
	
	/**
	 * Retourne le nombre de crédit obtenues pour un étudiant et semestre donné.
	 * @param etu l'etudiant a traiter
	 * @param note la note que l'on veut compter
	 * @param semestre le semestre
	 * @return evaluation
	 * @author Nigro
	 * exemple : un etu qui a un A en CS + un A en HT obtient 10 crédits. 
	 */
	public static float evalueSemestre(Etudiant etu, int semestre){
		int evaluation=0, i=0, cpt=0;
		int valeur;
		while(i<etu.getModules().size()){
			
			// détermine la valeur d'une UE
			if (etu.getModules().get(i).getParcours().equals("ISI"))
				valeur=6;
			else if (etu.getModules().get(i).getCategorie().equals("CS")||etu.getModules().get(i).getCategorie().equals("TM")||etu.getModules().get(i).getCategorie().equals("EC"))
				    valeur=3;
			     else
			    	valeur=1; 
			
//			if (etu.getNom().equals("BARBE"))
//				System.out.println("trouvé");
			
			// Calcule l'évaluation en fonction des lettre obtenue
			if (etu.getModules().get(i).getSemestre()==semestre){
				cpt++;
				switch (etu.getModules().get(i).getNote()) {
				case A : evaluation=evaluation+11*valeur;
				         break;
				case B : evaluation=evaluation+7*valeur;
					     break;
				case C : evaluation=evaluation+4*valeur;
					     break;
				case D : evaluation=evaluation+2*valeur;
					     break;
				case E : evaluation=evaluation+1*valeur;
					     break;
				case F :   evaluation=evaluation-7*valeur;
					       break;
				case FX :  evaluation=evaluation-7*valeur;
					       break;
				case ABS : evaluation=evaluation-7*valeur;
					       break;
				default : cpt--;	      
				}
			}
			i++;
		}
		if (cpt==0) return 0;
		else return (float)evaluation/cpt;
	}
	
	/**
	 * Retourne le total de crédit de la catégorie donnée en entrée ex: CS, TM...
	 * La fonction retourne uniquement le total des semestres précédent au semestre donné en entrée.
	 * @param etu l'etudiant etudié
	 * @param semestre le semestre actuel
	 * @param categorie ex : CS
	 * @return Le total de crédit pour la categorie donnée.
	 */
	public static int totalCategorie(Etudiant etu, int semestre, String categorie){
		int totalCS=0;
		Iterator<Module> it = etu.getModules().iterator();
		while(it.hasNext()){
			Module mod = it.next();
			if(mod.getSemestre()<semestre && mod.getCategorie().equals(categorie)){
				totalCS+=mod.getCredit();
			}
		}
		return totalCS;
	}
	
	/**
	 * retourne le nombre de semestre de l'étudiant qui en a fait le plus 
	 * @param etu l'etudiant a traiter
	 * @return le nombre de semestres effectués
	 */
	public static int maxSemestre(List<Etudiant> etudiants){
		int maxSemestre = 0, semLastModule = 0;
		Iterator<Etudiant> it = etudiants.iterator();
		while (it.hasNext()) {
			Etudiant etudiant = (Etudiant) it.next();
			 semLastModule = etudiant.getModules().get(etudiant.getModules().size()-1).getSemestre();//on recupere le semestre du dernier module
			 System.out.println(etudiant.getNom() + " " + semLastModule);
			 if(maxSemestre < semLastModule)//si c'est plus grand que pour les qutres etudiants on change
				 maxSemestre = semLastModule;
		}
		System.out.println(maxSemestre);
		return maxSemestre;
		
	}

	/**
	 * retourne le nombre de semestres effectués pour un étudiant
	 * @param etu l'etudiant a traiter
	 * @return le nombre de semestres effectués
	 */
	public static int nbSemestre(Etudiant etu){
		int maxSem=0, i=0;
		while(i<etu.getModules().size()){
			if(etu.getModules().get(i).getSemestre()>maxSem){
				maxSem=etu.getModules().get(i).getSemestre();
			}
			i++;
		}		
		return maxSem;
	}

	/**
	 * indique si une UE est ratée
	 * @param mod le module qui est tester
	 * @return true si c'est le cas, false sinon
	 */
	public static boolean estRatee(Module mod){
		boolean out=false;
		if (mod.getNote()==Note.F || mod.getNote()==Note.FX || mod.getNote()==Note.ABS){
			out=true;
		}
		return out;
	}

	/**
	 * retourne le nombre d'ue pour un étudiant et semestre donné
	 * @param etu l'etudiant traité
	 * @param sem le semestre visée
	 * @return le nombre d'UE de l'etudiant ce semestre
	 */
	public static int nombreUeSemestre(Etudiant etu, int sem){
		int nbUe=0, i=0;
		while(i<etu.getModules().size()){
			if(etu.getModules().get(i).getSemestre()==sem){
				nbUe++;
			}
			i++;
		}
		return nbUe;
	}

	/**
	 * renvois le nom du dernier stage effectué par l'étudiant
	 * @param etu l'étudiant en question
	 * @return "TN30" si tn30, etc... puis "pas de stage" si aucun stage n'a été trouvé
	 */
	public static String dernierStage(Etudiant etu){
		int i=0;
		boolean tn09=false, tn10=false, tn30=false;
		while(i<etu.getModules().size()){
			if (etu.getModules().get(i).getNom().equals("TN09")){
				tn09=true;
			}
			if (etu.getModules().get(i).getNom().equals("TN10")){
				tn10=true;
			}
			if (etu.getModules().get(i).getNom().equals("TN30")){
				tn30=true;
			}
			i++;
		}
		if(tn30==true){
			return "TN30";
		}
		else if(tn10==true){
			return "TN10";
		}
		else if(tn09==true){
			return "TN09";
		}
		else {
			return "Pas de stage";
		}
	}

	/**
	 * Methode qui compte les credits des modules ISI de l'etudiant
	 * @return le nombre de credit pour les modules ISI
	 */
	public int compteCreditsISI (){
		int credits = 0;
		List<Module> modules = LectureModules.lireModules();
		Iterator<Module> it = modules.iterator();
		while(it.hasNext()){
			Module mod =it.next();
			if(mod.getParcours().equals("TC") || mod.getParcours().equals("ISI")){//si l'UV est ISI ou TC on ajoute ces credits
				credits+=mod.getCredit();
			}
		}
		return credits;
	}	

	/**
	 * Retourne si oui ou non il faut afficher un avertissement pour le NPML
	 * @param etu l'etudiant choisis
	 * @param sem le semestre 
	 * @return un booleen 
	 */
	public static boolean avertissementNPML(Etudiant etu, int sem){
		boolean avertissementNPML=false;
		int i=0;
		if (sem==0 || sem==1){
			while(i<etu.getModules().size()){
				if (etu.getModules().get(i).getSemestre()==0 && etu.getModules().get(i).getParcours().equals("ISI") && (etu.getModules().get(i).getNom().equals("LE01"))){
					avertissementNPML=true;
				}/* LE01(validé ou non) en ISI 1  */
				if (etu.getModules().get(i).getSemestre()==1 && etu.getModules().get(i).getParcours().equals("ISI") && (etu.getModules().get(i).getNom().equals("LE01") || etu.getModules().get(i).getNom().equals("LE02"))){
					avertissementNPML=true;
				}/* LE01 ou LE02 (validé ou non) en ISI 2  */
				i++;
			}
		}
		if (sem==2){
			boolean LE03=false;
			i=0;
			while(i<etu.getModules().size()){
				if (etu.getModules().get(i).getSemestre()==2 && etu.getModules().get(i).getParcours().equals("ISI") && etu.getModules().get(i).getNom().equals("LE03")){
					LE03=true;
				}
				if (etu.getModules().get(i).getSemestre()==2 && !etu.getModules().get(i).getParcours().equals("ISI")){
					LE03=true;
				}
				i++;
			}
			if (LE03==false){
				avertissementNPML=true;
			}
		}
		return avertissementNPML;
	}

	/**
	 * Scan les modules d'un étudiant et affiche en réponse un string contenant l'avis jury.
	 * @param etu l'etudiant choisis.
	 * @return l'avis jury en string.
	 */
	public static List<String> avisJury(Etudiant etu){
		ArrayList<String> out= new ArrayList<String>();
		int maxSem=nbSemestre(etu)+1;
		
		//int sem=1; // Modifié par nigro le 09/02/2018 initialement "int sem=0;"
		int sem=nbSemestre(etu); // pour avoir uniquement le dernier semestre
		boolean buleadm=false;
		while(sem<maxSem){
			String str="";
			
			int nbA=compteNote(etu, Note.A, sem);
			int nbB=compteNote(etu, Note.B, sem);
			int nbC=compteNote(etu, Note.C, sem);
			int nbD=compteNote(etu, Note.D, sem);
			int nbE=compteNote(etu, Note.E, sem);
			int nbUe=nombreUeSemestre(etu, sem);
			int nbUeRatees=0, i=0, nbUeRateesCSTM=0, nbUeRateesCS=0, nbUeRateesTM=0, nbUeRateesMECT=0;
			boolean ISI1=false;
			boolean avertissementEtranger = true;
			boolean avertissementNPML=avertissementNPML(etu, sem);
			boolean LE03valide=false;
			while(i<etu.getModules().size()){
				if (estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==sem && !(etu.getModules().get(i).getCategorie().equals("CS") || etu.getModules().get(i).getCategorie().equals("TM"))){
					nbUeRatees++;
				}
				if (estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==sem && (etu.getModules().get(i).getCategorie().equals("CS") || etu.getModules().get(i).getCategorie().equals("TM"))){
					nbUeRateesCSTM++;
				}
				if (estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==sem && etu.getModules().get(i).getCategorie().equals("CS")){
					nbUeRateesCS++;
				}
				if (estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==sem && etu.getModules().get(i).getCategorie().equals("TM")){
					nbUeRateesTM++;
				}
				if (estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==sem && (etu.getModules().get(i).getCategorie().equals("ME") || etu.getModules().get(i).getCategorie().equals("CT") || etu.getModules().get(i).getCategorie().equals("HT"))){
					nbUeRateesMECT++;
				}
				
				
				if (!estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==sem && etu.getModules().get(i).getNom().equals("LE08")){
					buleadm=true;
				}
				if(!estRatee(etu.getModules().get(i)) && etu.getModules().get(i).getSemestre()==(sem-1) && etu.getModules().get(i).getNom().equals("LE03")){
					LE03valide=true;
				}// LE03 a été validé au semestre précédent
				if(etu.getModules().get(i).getParcours().equals("ISI") && sem==1){
					ISI1=true;
				}
				if(etu.getModules().get(i).getSemestre()<sem && etu.getModules().get(i).getNom().startsWith("UX")){
					avertissementEtranger = false;
				}
				i++;
			}
			int nbUeRateesTotal=nbUeRatees+nbUeRateesCSTM;
			if (nbUeRateesTotal==0){
				str+="Poursuite Normale";
				if (((float)nbA+nbB)/nbUe>0.7){
					str+=", Excellent Semestre.";
				}
				else if (((float)nbA+nbB)/nbUe>0.6){
					str+=", Tres Bon Semestre.";
				}
				else if (((float)nbA+nbB)/nbUe>0.5){
					str+=", Bon Semestre.";
				}
				else if (((float)nbE+nbD)/nbUe<0.5){
					str+=", Assez Bon Semestre.";
				}
				else{
					str+=", Semestre Moyen.";
				}
			}
			if (nbUeRateesTotal==1){
				str+="Poursuite avec Conseil";
				if (((float)nbE+nbD)/nbUe<0.5){
					str+=", Semestre Moyen.";
				}
				else{
					str+=", Semestre Mediocre.";
				}
			}
			if (nbUeRateesTotal>=2){
				str+="Poursuite avec Reserve";
				if (nbUeRateesCSTM<=1){
					str+=", Mauvais Semestre.";
				}
				else{
					str+=", Tres Mauvais Semestre.";
				}
			}
			if (buleadm==false){
				if(avertissementNPML){
					str+=" Vos resultats en langues sont insuffisants pour obtenir le NPML en temps voulu, reagissez.";
				}else if (LE03valide){
					str+=" Attention, vous n'avez toujours pas valide votre NPML, indispensable pour etre diplome(e).";
				}
			}
			if (ISI1 && avertissementEtranger){
				str+=" N'oubliez pas que pour etre diplome(e), vous devez passer un semestre a l'etranger.";
			}
			if (totalCategorie(etu, sem, "CS")<30 && nbUeRateesCS>0){
				str+=" Attention aux UE CS.";
			}
			if (totalCategorie(etu, sem, "TM")<30 && nbUeRateesTM>0){
				str+=" Attention aux UE TM.";
			}
			if ((totalCategorie(etu, sem, "ME")+totalCategorie(etu, sem, "CT")+totalCategorie(etu, sem, "HT"))<8 && nbUeRateesMECT>0){
				str+=" Attention aux UE ME/CT."; 
			}
			
			// Test Nigro 09/02/2018
			float evalSemestre=evalueSemestre(etu, sem);
			System.out.println(etu.getNom()+" : "+String.valueOf(evalSemestre));
			
			sem++;
			out.add(str);
		}
		
		return out;
	}
}

