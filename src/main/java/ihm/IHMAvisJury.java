package ihm;

import javax.swing.*;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import io.SauvegardeRepertoire;
import operation.DecisionJury;
import operation.Statistiques;

import java.awt.*;
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
	private JPanel jPanelCenter;
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
		this.getContentPane().setLayout(new BorderLayout());
		this.jPanelCenter=new JPanel();
		this.jPanelCenter.setLayout(new BoxLayout(this.jPanelCenter,BoxLayout.PAGE_AXIS));
		JPanel jPanelPDF=new JPanel();
        jPanelPDF.setLayout(new BoxLayout(jPanelPDF,BoxLayout.LINE_AXIS));

		JLabel lblPDF = new JLabel("Source PDF");
		jPanelPDF.add(lblPDF);
        sourcePDF = new JTextField();
        jPanelPDF.add(sourcePDF);
        // Bouton pour le chargement du fichier PDF
        findPDF = new JButton("...");
        findPDF.setFont(new Font("Tahoma", Font.BOLD, 11));
        jPanelPDF.add(findPDF);
        this.jPanelCenter.add(jPanelPDF);

        JPanel jPanelTxt=new JPanel();
        jPanelTxt.setLayout(new BoxLayout(jPanelTxt,BoxLayout.LINE_AXIS));

		JLabel lblTxt = new JLabel("Cible TXT");
        jPanelTxt.add(lblTxt);
        sourceTXT = new JTextField();
        jPanelTxt.add(sourceTXT);
        this.jPanelCenter.add(jPanelTxt);


        // Bouton de conversion PDF --> TXT
        JPanel jPanelBTxt=new JPanel();
        jPanelBTxt.setLayout(new BoxLayout(jPanelBTxt,BoxLayout.LINE_AXIS));
        conversionPdf_Txt = new JButton("Conversion  PDF >- TXT");
        jPanelBTxt.add(Box.createHorizontalGlue());
        jPanelBTxt.add(conversionPdf_Txt);
        jPanelBTxt.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBTxt);

        JPanel jPanelCsv=new JPanel();
        jPanelCsv.setLayout(new BoxLayout(jPanelCsv,BoxLayout.LINE_AXIS));

        JLabel lblCsv = new JLabel("Cible CSV");
        jPanelCsv.add(lblCsv);
        cibleCSV = new JTextField();
        jPanelCsv.add(cibleCSV);
        this.jPanelCenter.add(jPanelCsv);

        // Bouton pour la decisionJury
        JPanel jPanelBCsv=new JPanel();
        jPanelBCsv.setLayout(new BoxLayout(jPanelBCsv,BoxLayout.LINE_AXIS));
        avisJury = new JButton("Générer avis jury");
        jPanelBCsv.add(Box.createHorizontalGlue());
        jPanelBCsv.add(avisJury);
        jPanelBCsv.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBCsv);

        JPanel jPanelStat=new JPanel();
        jPanelStat.setLayout(new BoxLayout(jPanelStat,BoxLayout.LINE_AXIS));

		JLabel lblStat = new JLabel("Cible Stats");
		jPanelStat.add(lblStat);
        cibleStat = new JTextField();
        jPanelStat.add(cibleStat);
        this.jPanelCenter.add(jPanelStat);

        // Bouton pour les statistiques
        JPanel jPanelBStat=new JPanel();
        jPanelBStat.setLayout(new BoxLayout(jPanelBStat,BoxLayout.LINE_AXIS));
		statistique = new JButton("Générer statistiques");
		jPanelBStat.add(Box.createHorizontalGlue());
		jPanelBStat.add(statistique);
        jPanelBStat.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBStat);

        JPanel jPanelFootCenter=new JPanel();
        jPanelFootCenter.setLayout(new BoxLayout(jPanelFootCenter,BoxLayout.LINE_AXIS));
        message = new JLabel("");
        jPanelFootCenter.add(message);
        jPanelFootCenter.add(Box.createHorizontalGlue());
        exit = new JButton("Quitter");
        jPanelFootCenter.add(exit);
        this.jPanelCenter.add(jPanelFootCenter);
        // Bouton pour quitter l'application
        this.getContentPane().add(this.jPanelCenter,BorderLayout.CENTER);
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
