package ihm;

import javax.swing.JFrame;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import io.SauvegardeRepertoire;
import operation.DecisionJury;
import operation.Statistiques;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.awt.Font;

/**
 * GestionStagesJuryIsi crée une fenétre graphique pour sélectionner le pv de jury ISI au format PDF
 * puis permet de construire le fichier TXT contenant le texte du PDF
 * puis permet d'avoir le résultat de l'étude faite par la Classe Filtrage dans un fichier CSV 
 * @author nigro
 * @version 1.0
 */
public class IHMAvisJury extends JFrame{

	private static final long serialVersionUID = 1L;

	private JTextField sourceTXT, cibleCSV, sourcePDF, cibleStat;
	private JLabel message;
	private File fileTXT, fileSourcePDF, fileDestPDF, fileDecisionJury, fileStats;
	private File dirAvisJury, dirStats, dirDatasTxt, dirAvisJuryCSV, dirAvisJuryPDF;
	private JButton exit, findPDF, conversionPdf_Txt, avisJury, statistique;

	/**
	 * Creation de l'application.
	 */
	public IHMAvisJury() {
		new JFrame();
		initialize();
		addListener();
	}

	/**
	 * conversion d'un fichier PDF en un fichier TXT
	 * @param sourcePDF nom du fichier PDF
	 * @param destinationTXT nom du fichier résultat TXT
	 * @throws IOException erreur d'ouverture ou d'écriture
	 */
	static void ConvertirPDF (String sourcePDF, String destinationTXT) throws IOException{
		BufferedWriter ecritureAvecBuffer= null;
		try {
			String text = new PDFTextStripper().getText(PDDocument.load(new File(sourcePDF)));
			ecritureAvecBuffer = new BufferedWriter(new FileWriter(destinationTXT));
			ecritureAvecBuffer.write(text);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			ecritureAvecBuffer.close();
		}
	}

	/**
	 * Initialisation du contenu de la fenêtre.
	 */
	private void initialize() {
		this.setVisible(true);
		this.setTitle("Gestion Stages Jury ISI");
		this.setBounds(100, 100, 800, 250);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);

		JLabel lblPDF = new JLabel("Source PDF");
		lblPDF.setBounds(10, 10, 77, 15);
		this.getContentPane().add(lblPDF);

		JLabel lblTxt = new JLabel("Cible TXT");
		lblTxt.setBounds(10, 65, 88, 15);
		this.getContentPane().add(lblTxt);

		JLabel lblCsv = new JLabel("Cible CSV");
		lblCsv.setBounds(10, 120, 88, 15);
		this.getContentPane().add(lblCsv);

		JLabel lblStat = new JLabel("Cible Stats");
		lblStat.setBounds(10, 175, 88, 15);
		this.getContentPane().add(lblStat);

		sourcePDF = new JTextField();
		sourcePDF.setBounds(108, 8, 627, 20);
		this.getContentPane().add(sourcePDF);

		sourceTXT = new JTextField();
		sourceTXT.setBounds(108, 59, 627, 20);
		this.getContentPane().add(sourceTXT);

		cibleCSV = new JTextField();
		cibleCSV.setBounds(108, 112, 627, 20);
		this.getContentPane().add(cibleCSV);

		cibleStat = new JTextField();
		cibleStat.setBounds(108, 170, 627, 20);
		this.getContentPane().add(cibleStat);

		// Bouton pour le chargement du fichier PDF 
		findPDF = new JButton("...");
		findPDF.setFont(new Font("Tahoma", Font.BOLD, 11));
		findPDF.setBounds(740, 10, 33, 20);
		this.getContentPane().add(findPDF);

		// Bouton de conversion PDF --> TXT 
		conversionPdf_Txt = new JButton("Conversion  PDF >- TXT");
		conversionPdf_Txt.setBounds(296, 32, 176, 20);
		this.getContentPane().add(conversionPdf_Txt);

		// Bouton pour la decisionJury 
		avisJury = new JButton("Générer avis jury");
		avisJury.setBounds(296, 85, 176, 20);
		this.getContentPane().add(avisJury);

		// Bouton pour les statistiques
		statistique = new JButton("Générer statistiques");
		statistique.setBounds(300, 140, 176, 20);
		this.getContentPane().add(statistique);

		message = new JLabel("");
		message.setBounds(10, 200, 1000, 20);
		this.getContentPane().add(message);

		// Bouton pour quitter l'application
		exit = new JButton("Quitter");
		exit.setBounds(670, 200, 110, 20);
		this.getContentPane().add(exit);

		lockButton();
	}

	/**
	 * Methode qui ajoute les listeners aux boutons
	 */
	private void addListener(){
		this.setFocusable(true);
		this.addMouseListener(new MouseAdapter() {//on donne le focus a la fenetre
			@Override
			public void mouseClicked(MouseEvent me) {
				requestFocusInWindow();
			}
		});

		//Ajout d'un listener de touche de clavier
		this.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_R:
					if(findPDF.isEnabled())
						choixRepertoire();
					break;
				case KeyEvent.VK_C:
					if(conversionPdf_Txt.isEnabled())
						validerConversionPDF();
					break;
				case KeyEvent.VK_A:
					if(avisJury.isEnabled())
						creationDecisionJury();
					break;
				case KeyEvent.VK_S:
					if(statistique.isEnabled())
						genererStatistique();
					break;
				case KeyEvent.VK_Q:
					System.exit(0); 
					break;
				default:
					break;
				}
			}
			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		});

		// Listener qui detecte si le textfield pdf est non vide
		sourcePDF.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				detectionPDF();
				gestionFichier();//gere si le fichier est correcte

			}
		});

		findPDF.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(findPDF.isEnabled())
					choixRepertoire();
			}
		});

		conversionPdf_Txt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(conversionPdf_Txt.isEnabled())
					validerConversionPDF();
			}
		});

		avisJury.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(avisJury.isEnabled()){
					if(fileTXT.exists())
						creationDecisionJury();
					else
						message.setText("<html><span color='red'>la conversion du .pdf en .txt n'a pas été faite</span></html>");
				}
			}
		});

		statistique.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(statistique.isEnabled())
					if(fileTXT.exists())
						genererStatistique();
					else
						message.setText("<html><span color='red'>la conversion du .pdf en .txt n'a pas été faite</span></html>");
			}
		});

		exit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.exit(0);
			}
		});
	}

	/**
	 * Methode declenché lors de la decision de jury
	 */
	private void creationDecisionJury() {		
		if(fileDecisionJury.exists()){ //on regarde si le csv de decision existe
			int option = dialogEcrasmentFichier(fileDecisionJury); //on demande si on veut l'ecraser
			requestFocus();
			if(option == JOptionPane.OK_OPTION){
				if(fileDestPDF.exists()){//si le fichier PDF de decision existe
					option = dialogEcrasmentFichier(fileDestPDF);//on demande si on veut l'ecraser
					dialogEcritureFichier(fileDecisionJury);
					if(option == JOptionPane.OK_OPTION){
						DecisionJury.ecritureDecisionJury(sourceTXT.getText(), fileDestPDF.getAbsolutePath(), cibleCSV.getText());
						dialogEcritureFichier(fileDestPDF);
					}
				}
				else
					DecisionJury.ecritureDecisionJury(sourceTXT.getText(), fileDestPDF.getAbsolutePath(), cibleCSV.getText());
			}
		}
		else
			DecisionJury.ecritureDecisionJury(sourceTXT.getText(), fileDestPDF.getAbsolutePath(), cibleCSV.getText());
	}

	/**
	 * Methode qui detecte si le PDF est present est valable
	 */
	private void detectionPDF() {
		if (sourcePDF.getText().equals("")){
			lockButton();
			message.setText("<html><span color='red'>Pas de PDF</span></html>");
		}
		else{
			if(new File(sourcePDF.getText()).exists()){
				message.setText("<html><head><meta charset=\"UTF-8\"><span color='green'>Fichier PDF détecté</span></html>");
				unlockButton();
			}
			else
				message.setText("<html><span color='red'>le fichier "+ sourcePDF.getText() +" n'existe pas</span></html>");
		}
	}

	/**
	 * Methode declenché lors de la generation des statistiques
	 */
	private void genererStatistique() {
		if(fileStats.exists()){
			int option = dialogEcrasmentFichier(fileStats); //on demande si on veut l'ecraser
			requestFocus();
			if(option == JOptionPane.OK_OPTION){
				Statistiques.ecritureStatistiques(sourceTXT.getText(), fileStats.getAbsolutePath());
				dialogEcritureFichier(fileStats);
			}
		}
		else
			Statistiques.ecritureStatistiques(sourceTXT.getText(), fileStats.getAbsolutePath());
	}

	/**
	 * Méthode declenché lors de la conversion du PDF en TXT
	 */
	private void validerConversionPDF() {
		if (!(sourcePDF.getText()=="") && !(sourceTXT.getText()=="")){
			try {
				if(fileTXT.exists()){
					//on demande si on veut l'ecraser
					int option = dialogEcrasmentFichier(fileTXT);
					requestFocus();
					if(option == JOptionPane.OK_OPTION){
						ConvertirPDF(sourcePDF.getText(), sourceTXT.getText());
						dialogSuccesConversionFichier(fileSourcePDF, fileTXT);
					}
				}
				else
					ConvertirPDF(sourcePDF.getText(), sourceTXT.getText());
			} catch (FileNotFoundException e2) {
				dialogFichierInexistant(fileSourcePDF);
			} catch (IOException e1) {
				dialogEchecConversionFichier(fileSourcePDF, fileTXT);
			}
		}
	}

	/**
	 * choixRepertoire permet de definir le repertoire le plus adequat pour le JFileChooser
	 */
	private void choixRepertoire(){
		String path = ""; //Chemin a parcourir
		JFileChooser chooser = null;
		if(SauvegardeRepertoire.getPaths().isEmpty()){//Si la liste des repertoires est vide
			chooser = new JFileChooser();
		}
		else{//si elle n'est pas vide
			Iterator<String> it = SauvegardeRepertoire.getPaths().iterator();
			while (it.hasNext() && path.equals("")) {
				String str = (String) it.next();
				File file = new File(str);

				if (file.exists()) //on recupere le premier repertoire possible	
					path = file.getAbsolutePath();
			}

			if(path.equals(""))//si on a pas trouvé de chemin coherent
				chooser = new JFileChooser();
			else
				chooser = new JFileChooser(path);
		}

		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF","pdf");
		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			SauvegardeRepertoire.ajoutPath(chooser);//permet de sauvegarder les repertoires
			gestionFichier(chooser);
			unlockButton();
		}
	}

	/**
	 * Méthode qui gere les fichiers et les dossier en entrée/sortie
	 * @param chooser le choix du fichier PDF
	 */
	private void gestionFichier(JFileChooser chooser) {
		//Recuperation du PDF choisi dans le JFileChooser
		fileSourcePDF = new File(chooser.getSelectedFile().getAbsolutePath());
		sourcePDF.setText(fileSourcePDF.getAbsolutePath());

		//Creation du repertoire de decision jury
		dirDatasTxt = new File(fileSourcePDF.getParent()+"/DatasTXT");//creation du repertoire d'avis de jury
		if(!dirDatasTxt.exists())
			dirDatasTxt.mkdir();

		//Creation du repertoire de decision jury
		dirAvisJury = new File(fileSourcePDF.getParent()+"/AvisJury");//creation du repertoire d'avis de jury
		if(!dirAvisJury.exists())
			dirAvisJury.mkdir();

		//Creation du repertoire de decision jury CSV
		dirAvisJuryCSV = new File(dirAvisJury.getAbsolutePath()+"/csv");//creation du repertoire pour les CSV
		if(!dirAvisJuryCSV.exists())
			dirAvisJuryCSV.mkdir();

		//Creation du repertoire de decision jury PDF
		dirAvisJuryPDF = new File(dirAvisJury.getAbsolutePath()+"/pdf");//creation du repertoire pour les PDF
		if(!dirAvisJuryPDF.exists())
			dirAvisJuryPDF.mkdir();

		//Creation du repertoire de stat jury
		dirStats = new File(fileSourcePDF.getParent()+"/Stats");//creation du repertoire d'avis de jury
		if(!dirStats.exists())
			dirStats.mkdir();

		//creation du fichier txt pour les donnees des etudiants
		String nomFichierTXT = fileSourcePDF.getName().replace(".pdf", ".txt");
		fileTXT = new File(dirDatasTxt.getAbsolutePath()+"/"+nomFichierTXT);
		sourceTXT.setText(fileTXT.getAbsolutePath()); 	

		//creation du fichier de decision csv
		String nomDecisionJury = fileSourcePDF.getName().replace(".pdf", ".csv");
		fileDecisionJury = new File(dirAvisJuryCSV.getAbsolutePath()+"/"+nomDecisionJury);
		cibleCSV.setText(fileDecisionJury.getAbsolutePath()); 

		//creation du fichier de decision pdf
		String nomPDFDecisionJury = fileSourcePDF.getName();
		fileDestPDF = new File(dirAvisJuryPDF.getAbsolutePath()+"/"+nomPDFDecisionJury);

		//creation du fichier pour les stats
		String nomStats = fileSourcePDF.getName().replace(".pdf", ".csv");
		fileStats = new File(dirStats.getAbsolutePath()+"/"+nomStats);
		cibleStat.setText(dirStats.getAbsolutePath()+"/"+nomStats);
	}

	/**
	 * Méthode qui gere les saisie au clavier pour les fichiers
	 */
	private void gestionFichier() {
		fileSourcePDF = new File(sourcePDF.getText());
		detectionPDF();
		if(fileSourcePDF.exists()){
			//Creation du repertoire de decision jury
			dirDatasTxt = new File(fileSourcePDF.getParent()+"/DatasTXT");//creation du repertoire d'avis de jury
			if(!dirDatasTxt.exists())
				dirDatasTxt.mkdir();

			//Creation du repertoire de decision jury
			dirAvisJury = new File(fileSourcePDF.getParent()+"/AvisJury");//creation du repertoire d'avis de jury
			if(!dirAvisJury.exists())
				dirAvisJury.mkdir();

			//Creation du repertoire de decision jury CSV
			dirAvisJuryCSV = new File(dirAvisJury.getAbsolutePath()+"/csv");//creation du repertoire pour les CSV
			if(!dirAvisJuryCSV.exists())
				dirAvisJuryCSV.mkdir();

			//Creation du repertoire de decision jury PDF
			dirAvisJuryPDF = new File(dirAvisJury.getAbsolutePath()+"/pdf");//creation du repertoire pour les PDF
			if(!dirAvisJuryPDF.exists())
				dirAvisJuryPDF.mkdir();

			//Creation du repertoire de stat jury
			dirStats = new File(fileSourcePDF.getParent()+"/Stats");//creation du repertoire d'avis de jury
			if(!dirStats.exists())
				dirStats.mkdir();

			//creation du fichier txt pour les donnees des etudiants
			String nomFichierTXT = fileSourcePDF.getName().replace(".pdf", ".txt");
			fileTXT = new File(dirDatasTxt.getAbsolutePath()+"/"+nomFichierTXT);
			sourceTXT.setText(fileTXT.getAbsolutePath()); 	

			//creation du fichier de decision csv
			String nomDecisionJury = fileSourcePDF.getName().replace(".pdf", ".csv");
			fileDecisionJury = new File(dirAvisJuryCSV.getAbsolutePath()+"/"+nomDecisionJury);
			cibleCSV.setText(fileDecisionJury.getAbsolutePath()); 

			//creation du fichier de decision pdf
			String nomPDFDecisionJury = fileSourcePDF.getName();
			fileDestPDF = new File(dirAvisJuryPDF.getAbsolutePath()+"/"+nomPDFDecisionJury);

			//creation du fichier pour les stats
			String nomStats = fileSourcePDF.getName().replace(".pdf", ".csv");
			fileStats = new File(dirStats.getAbsolutePath()+"/"+nomStats);
			cibleStat.setText(dirStats.getAbsolutePath()+"/"+nomStats);
		}
		else{//si la source est pas bonne
			sourceTXT.setText("fichier inconnu"); 	
			cibleCSV.setText("fichier inconnu"); 
			cibleStat.setText("fichier inconnu");
		}
	}

	/**
	 * Bloquer bouton empeche l'actiavtion des boutons tant que le chemin vers le pdf n'est pas la
	 */
	private void lockButton(){
		conversionPdf_Txt.setEnabled(false);
		avisJury.setEnabled(false);
		statistique.setEnabled(false);
	}

	/**
	 * Debloquer bouton empeche l'actiavtion des boutons tant que le chemin vers le pdf n'est pas la
	 */
	private void unlockButton() {
		conversionPdf_Txt.setEnabled(true);
		avisJury.setEnabled(true);
		statistique.setEnabled(true);	
	}

	/**
	 * Methode affichant la boite de dialog pour demander l'ecrasement d'un fichier
	 * @param file le fichier qui doit etre ecraser
	 * @return ok ou non
	 */
	private int dialogEcrasmentFichier(File file) {
		String message = "Voulez-vous ecraser le fichier " + file.getName() +" ?";
		String message2 = "Confirmation de la suppression";
		int option = JOptionPane.showConfirmDialog(null, message, message2, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return option;
	}

	/**
	 * Methode affichant la boite de dialog pour afficher le succes de la conversion du fichier
	 * @param file le fichier source de la conversion
	 * @param file2 le fichier converti
	 */
	private void dialogSuccesConversionFichier(File file, File file2){
		String message = "le fichier " + file.getName() +"\na été converti en "+ file2.getName();
		JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Methode affichant la boite de dialog pour afficher un fichier inexistant
	 * @param file le fichier inexistant
	 */
	private void dialogFichierInexistant(File file){
		JOptionPane.showMessageDialog(null, "le fichier " + file.getName() +"\n'existe pas", "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Methode affichant la boite de dialog pour afficher l'echec de la conversion du fichier
	 * @param file le fichier source de la conversion
	 * @param file2 le fichier non converti
	 */
	private void dialogEchecConversionFichier(File file, File file2){
		JOptionPane.showMessageDialog(null, "le fichier " + file.getName() +"\nn'a pas été converti en "+ file2.getName(), "Erreur", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Methode affichant la boite de dialog pour demander l'ecrasement d'un fichier
	 * @param file le fichier qui doit etre ecraser
	 */
	private void dialogEcritureFichier(File file) {
		JOptionPane.showMessageDialog(null, "le fichier " + file.getName() +" a été écrit", "Info", JOptionPane.INFORMATION_MESSAGE);
	}
}
