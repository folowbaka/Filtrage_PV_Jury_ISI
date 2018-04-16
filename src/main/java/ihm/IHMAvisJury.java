package main.java.ihm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import main.java.operation.Modele;
import meka.classifiers.multilabel.BCC;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import main.java.io.SauvegardeRepertoire;
import main.java.operation.DecisionJury;
import main.java.operation.Statistiques;
import weka.gui.treevisualizer.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

	private JTextField sourceTXT, cibleCSV, sourcePDF, cibleStat,cibleData,cibleTrainingData;
	private JPanel jPanelCenter,cardOne,cardTwo;
	private JLabel message;
	private ArrayList<JTabbedPane> panelGraphTraining;
	private ArrayList<JTabbedPane> panelGraphCompare;
	private File fileTXT, fileSourcePDF, fileDestPDF, fileDecisionJury, fileStats,fileDataSet;
	private File dirAvisJury, dirStats, dirDatasTxt, dirAvisJuryCSV, dirAvisJuryPDF,dirDataSet;
	private JButton exit, findPDF, conversionPdf_Txt, avisJury, statistique,bData,findDataSet,bDataTraining,bCompare;
	private JComboBox cbTrainingOne,cbTrainingTwo;
	private int screenWidth;
	private int screenHeight;
	private IHMAvisJury window=this;
	private Modele mo;
	public final static int PDFFile = 1;
	public final static int ARFFFile = 2;


	/**
	 * Creation de l'application.
	 */
	public IHMAvisJury() {
		new JFrame();
		initialize();
		addListener();
        this.mo=new Modele();
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
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenWidth=screenSize.width;
        this.screenHeight=screenSize.height;
		this.setBounds(this.screenWidth/2-(this.screenWidth/2)/2,this.screenHeight/2-500,800,950);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout());
		this.panelGraphCompare=new ArrayList<>();
		this.panelGraphTraining=new ArrayList<>();
		this.jPanelCenter=new JPanel();
		this.jPanelCenter.setLayout(new BoxLayout(this.jPanelCenter,BoxLayout.PAGE_AXIS));
		JPanel jPanelPDF=new JPanel();
        jPanelPDF.setLayout(new BoxLayout(jPanelPDF,BoxLayout.LINE_AXIS));

		JLabel lblPDF =this.createLabelCenter("Source PDF");
		jPanelPDF.add(lblPDF);
		jPanelPDF.add(Box.createRigidArea(new Dimension(20,0)));
        sourcePDF = this.createTextFieldCenter();
        jPanelPDF.add(sourcePDF);
		jPanelPDF.add(Box.createRigidArea(new Dimension(5,0)));
        // Bouton pour le chargement du fichier PDF
        findPDF = new JButton("...");
        findPDF.setFont(new Font("Tahoma", Font.BOLD, 11));
        findPDF.setMaximumSize(new Dimension(50,35));
        jPanelPDF.add(findPDF);
        this.jPanelCenter.add(jPanelPDF);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));
        JPanel jPanelTxt=new JPanel();
        jPanelTxt.setLayout(new BoxLayout(jPanelTxt,BoxLayout.LINE_AXIS));

		JLabel lblTxt = this.createLabelCenter("Cible TXT");
        jPanelTxt.add(lblTxt);
        jPanelTxt.add(Box.createRigidArea(new Dimension(20,0)));
        sourceTXT = this.createTextFieldCenter();
        jPanelTxt.add(sourceTXT);
        jPanelTxt.add(Box.createRigidArea(new Dimension(50,0)));
        this.jPanelCenter.add(jPanelTxt);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

        // Bouton de conversion PDF --> TXT
        JPanel jPanelBTxt=new JPanel();
        jPanelBTxt.setLayout(new BoxLayout(jPanelBTxt,BoxLayout.LINE_AXIS));
        conversionPdf_Txt = new JButton("Conversion  PDF >- TXT");
        jPanelBTxt.add(Box.createHorizontalGlue());
        jPanelBTxt.add(conversionPdf_Txt);
        jPanelBTxt.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBTxt);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));
        JPanel jPanelCsv=new JPanel();
        jPanelCsv.setLayout(new BoxLayout(jPanelCsv,BoxLayout.LINE_AXIS));

        JLabel lblCsv = this.createLabelCenter("Cible CSV");
        jPanelCsv.add(lblCsv);
        jPanelCsv.add(Box.createRigidArea(new Dimension(20,0)));
        cibleCSV =this.createTextFieldCenter();
        jPanelCsv.add(cibleCSV);
        jPanelCsv.add(Box.createRigidArea(new Dimension(50,0)));
        this.jPanelCenter.add(jPanelCsv);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

        // Bouton pour la decisionJury
        JPanel jPanelBCsv=new JPanel();
        jPanelBCsv.setLayout(new BoxLayout(jPanelBCsv,BoxLayout.LINE_AXIS));
        avisJury = new JButton("Générer avis jury");
        jPanelBCsv.add(Box.createHorizontalGlue());
        jPanelBCsv.add(avisJury);
        jPanelBCsv.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBCsv);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

        JPanel jPanelStat=new JPanel();
        jPanelStat.setLayout(new BoxLayout(jPanelStat,BoxLayout.LINE_AXIS));

		JLabel lblStat = this.createLabelCenter("Cible Stats");
		jPanelStat.add(lblStat);
		jPanelStat.add(Box.createRigidArea(new Dimension(20,0)));
        cibleStat = this.createTextFieldCenter();
        jPanelStat.add(cibleStat);
        jPanelStat.add(Box.createRigidArea(new Dimension(50,0)));
        this.jPanelCenter.add(jPanelStat);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

        // Bouton pour les statistiques
        JPanel jPanelBStat=new JPanel();
        jPanelBStat.setLayout(new BoxLayout(jPanelBStat,BoxLayout.LINE_AXIS));
		statistique = new JButton("Générer statistiques");
		jPanelBStat.add(Box.createHorizontalGlue());
		jPanelBStat.add(statistique);
        jPanelBStat.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBStat);
        this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

        JPanel jPanelData=new JPanel();
        jPanelData.setLayout(new BoxLayout(jPanelData,BoxLayout.LINE_AXIS));
        JLabel lblData=this.createLabelCenter("Cible Dataset");
        jPanelData.add(lblData);
        jPanelData.add(Box.createRigidArea(new Dimension(20,0)));
        cibleData = this.createTextFieldCenter();
        jPanelData.add(cibleData);
        jPanelData.add(Box.createRigidArea(new Dimension(50,0)));
        this.jPanelCenter.add(jPanelData);
        this.jPanelCenter.add(Box.createRigidArea(new Dimension(5,5)));

        JPanel jPanelBData=new JPanel();
        jPanelBData.setLayout(new BoxLayout(jPanelBData,BoxLayout.LINE_AXIS));
        bData = new JButton("Générer Dataset");
        jPanelBData.add(Box.createHorizontalGlue());
        jPanelBData.add(bData);
        jPanelBData.add(Box.createHorizontalGlue());
        this.jPanelCenter.add(jPanelBData);
        this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

		JPanel jPanelTrainingData=new JPanel();
		jPanelTrainingData.setLayout(new BoxLayout(jPanelTrainingData,BoxLayout.LINE_AXIS));
		JLabel lblTrainingData=this.createLabelCenter("Source Dataset");
		jPanelTrainingData.add(lblTrainingData);
		jPanelTrainingData.add(Box.createRigidArea(new Dimension(20,0)));
		cibleTrainingData = this.createTextFieldCenter();
		jPanelTrainingData.add(cibleTrainingData);
		jPanelTrainingData.add(Box.createRigidArea(new Dimension(5,0)));
		// Bouton pour le chargement du fichier PDF
		findDataSet = new JButton("...");
		findDataSet.setFont(new Font("Tahoma", Font.BOLD, 11));
		findDataSet.setMaximumSize(new Dimension(50,35));
		jPanelTrainingData.add(findDataSet);
		this.jPanelCenter.add(jPanelTrainingData);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));

		JPanel jPanelBDataTraining=new JPanel();
		jPanelBDataTraining.setLayout(new BoxLayout(jPanelBDataTraining,BoxLayout.LINE_AXIS));
		bDataTraining = new JButton("Entrainer le modèle");
		jPanelBDataTraining.add(bDataTraining);
		JPanel comboBoxPane = new JPanel(); //use FlowLayout
		String comboBoxItems[] = { "ALL"};
		this.cbTrainingOne = new JComboBox(comboBoxItems);
		this.cbTrainingOne.setEditable(false);
		comboBoxPane.add(cbTrainingOne);
		jPanelBDataTraining.add(comboBoxPane);
        bCompare = new JButton("Comparer");
		jPanelBDataTraining.add(Box.createHorizontalGlue());
		this.cbTrainingTwo = new JComboBox(comboBoxItems);
		this.cbTrainingTwo.setEditable(false);
		comboBoxPane = new JPanel();
		comboBoxPane.add(this.cbTrainingTwo);
		jPanelBDataTraining.add(comboBoxPane);
        jPanelBDataTraining.add(bCompare);
		this.jPanelCenter.add(jPanelBDataTraining);
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(0,5)));
		JPanel training=new JPanel();
        training.setLayout(new BoxLayout(training,BoxLayout.LINE_AXIS));
        cardOne=new JPanel(new CardLayout());
		cardTwo=new JPanel(new CardLayout());
		this.panelGraphTraining.add(new JTabbedPane());
		this.panelGraphCompare.add(new JTabbedPane());
		this.panelGraphTraining.get(0).setMinimumSize(new Dimension(800,600));
		this.panelGraphCompare.get(0).setMinimumSize(new Dimension(800,600));
        cardOne.add(this.panelGraphTraining.get(0),"ALL");
        cardTwo.add(this.panelGraphCompare.get(0),"ALL");
        cardTwo.setVisible(false);
        training.add(cardOne);
        training.add(cardTwo);
		this.jPanelCenter.add(training);
        JPanel jPanelFoot=new JPanel();
        jPanelFoot.setLayout(new BoxLayout(jPanelFoot,BoxLayout.LINE_AXIS));
        message = new JLabel("");
        jPanelFoot.add(message);
        jPanelFoot.add(Box.createHorizontalGlue());
        exit = new JButton("Quitter");
        jPanelFoot.add(exit);
		jPanelFoot.add(Box.createRigidArea(new Dimension(10,40)));
		this.jPanelCenter.add(Box.createRigidArea(new Dimension(5,(5))));
        // Bouton pour quitter l'application
		this.jPanelCenter.setBorder(new EmptyBorder(10,10,10,10));
        this.getContentPane().add(this.jPanelCenter,BorderLayout.CENTER);
        this.getContentPane().add(jPanelFoot,BorderLayout.SOUTH);
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
						choixRepertoire(PDFFile);
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
					choixRepertoire(PDFFile);
			}
		});
		findDataSet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (findDataSet.isEnabled())
					choixRepertoire(ARFFFile);
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
		bData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(bData.isEnabled())
					if(fileTXT.exists())
						genererDataSet();
					else
						message.setText("<html><span color='red'>la conversion du .pdf en .txt n'a pas été faite</span></html>");
			}
		});
		cbTrainingOne.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					CardLayout cl = (CardLayout)(cardOne.getLayout());
					int nbItem=cbTrainingOne.getItemCount();
					boolean foundPanel=false;
					int i=0;
					while (i<nbItem && !foundPanel)
					{
						if(((String)e.getItem()).equals(cbTrainingOne.getItemAt(i)))
							foundPanel=true;
						else
							i++;
					}
					if(panelGraphTraining.get(i).getComponents().length==0)
					{
						if(!e.getItem().equals("ALL")) {
							String pathDataset = fileDataSet.getAbsolutePath() + "/" + fileDataSet.getName();
							BCC cls = Modele.entrainement(pathDataset + "_" + e.getItem() + ".arff");
							BCC clsNpml = Modele.entrainement(pathDataset + "_" + e.getItem() + "_NPML.arff");
							try {
								drawGraph(i, cls, true);
								drawGraph(i, clsNpml, new String[]{"CC2"}, true);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					cl.show(cardOne, (String) e.getItem());
				}
			}
		});
		cbTrainingTwo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					CardLayout cl = (CardLayout)(cardTwo.getLayout());
					int nbItem=cbTrainingTwo.getItemCount();
					boolean foundPanel=false;
					int i=0;
					while (i<nbItem && !foundPanel)
					{
						if(((String)e.getItem()).equals(cbTrainingTwo.getItemAt(i)))
							foundPanel=true;
						else
							i++;
					}
					if(panelGraphCompare.get(i).getComponents().length==0)
					{
						if(!e.getItem().equals("ALL")) {
							String pathDataset = fileDataSet.getAbsolutePath() + "/" + fileDataSet.getName();
							BCC cls = Modele.entrainement(pathDataset + "_" + e.getItem() + ".arff");
							BCC clsNpml = Modele.entrainement(pathDataset + "_" + e.getItem() + "_NPML.arff");
							try {
								drawGraph(i, cls, false);
								drawGraph(i, clsNpml, new String[]{"CC2"}, false);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					cl.show(cardTwo, (String) e.getItem());
				}
			}
		});

		bCompare.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(bCompare.isEnabled()) {
                    if (!cardTwo.isVisible()) {
                        cardTwo.setVisible(true);
                        window.setBounds(screenWidth / 3 - (screenWidth / 2) / 2, screenHeight / 2 - 500, 1300, 950);
                    }
                    if (fileDataSet.exists()) {
                        cbTrainingTwo.removeAllItems();
                        cbTrainingTwo.insertItemAt("ALL", 0);
                        for (final File fileEntry : new File(cibleTrainingData.getText()).listFiles()) {
                            if (!fileEntry.isDirectory()) {
                                String[] fileName = fileEntry.getName().split("_");
                                fileName = fileName[fileName.length - 1].split("\\.");
                                if (fileName[0].matches("(ISI|TC|HC|SRT|MASTER|RT|STIC|TC)[0-9]{1}")) {
                                    JTabbedPane panel = new JTabbedPane();
                                    cbTrainingTwo.addItem(fileName[0]);
                                    cardTwo.add(panel, fileName[0]);
                                    panelGraphCompare.add(panel);
                                }
                            }
                        }
                        String pathDataset = fileDataSet.getAbsolutePath() + "/" + fileDataSet.getName();
                        BCC cls = Modele.entrainement(pathDataset + ".arff");
                        BCC clsNpml = Modele.entrainement(pathDataset + "_NPML.arff");
                        try {
                            drawGraph(0, cls, false);
                            drawGraph(0, clsNpml, new String[]{"CC2"}, false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            }
        });

		bDataTraining.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(bDataTraining.isEnabled())
					if(fileDataSet.exists())
					{
					    cbTrainingOne.removeAllItems();
                        cbTrainingOne.insertItemAt("ALL", 0);
                        for (final File fileEntry : new File(cibleTrainingData.getText()).listFiles()) {
                            if (!fileEntry.isDirectory()) {
                                String[] fileName = fileEntry.getName().split("_");
                                fileName = fileName[fileName.length - 1].split("\\.");
                                if (fileName[0].matches("(ISI|TC|HC|SRT|MASTER|RT|STIC|TC)[0-9]{1}")) {
                                    JTabbedPane panel = new JTabbedPane();
                                    cbTrainingOne.addItem(fileName[0]);
                                    cardOne.add(panel, fileName[0]);
                                    panelGraphTraining.add(panel);

                                }
                            }
                        }
						String pathDataset=fileDataSet.getAbsolutePath()+"/"+fileDataSet.getName();
						BCC cls = Modele.entrainement(pathDataset+".arff");
						BCC clsNpml=Modele.entrainement(pathDataset+"_NPML.arff");
						try {
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						try {
						    drawGraph(0,cls,true);
							drawGraph(0,clsNpml,new String[]{"CC2"},true);
							bCompare.setEnabled(true);
						}catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
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

	private void genererDataSet() {
		if(fileDataSet.exists()){
			int option = dialogEcrasmentFichier(fileDataSet); //on demande si on veut l'ecraser
			requestFocus();
			if(option == JOptionPane.OK_OPTION){
				Modele.ecritureDataset(sourceTXT.getText(), fileDataSet.getAbsolutePath());
				dialogEcritureFichier(fileDataSet);
			}
		}
		else
			Modele.ecritureDataset(sourceTXT.getText(), fileDataSet.getAbsolutePath());
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
	private void choixRepertoire(int format){
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
		FileNameExtensionFilter filter=null;
		switch(format)
		{
			case PDFFile:
				filter = new FileNameExtensionFilter("PDF","pdf");
				break;
			case ARFFFile:
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				break;
		}

		chooser.setFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
		int returnVal = chooser.showOpenDialog(null);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			SauvegardeRepertoire.ajoutPath(chooser);//permet de sauvegarder les repertoires
			if(filter!=null && filter.getExtensions()[0].equals("pdf")) {
				gestionFichier(chooser);
				unlockButton();
			}
			else
			{
				gestionFichierDataSet(chooser);
				bDataTraining.setEnabled(true);
			}

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

        dirDataSet = new File(fileSourcePDF.getParent()+"/DataSet");//creation du repertoire d'avis de jury
        if(!dirDataSet.exists())
            dirDataSet.mkdir();

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

        String nomDataSet= fileSourcePDF.getName().replace(".pdf", "/");
        fileDataSet= new File(dirDataSet.getAbsolutePath()+"/"+nomDataSet);
        cibleData.setText(dirDataSet.getAbsolutePath()+"/"+nomDataSet);
	}

	private void gestionFichierDataSet(JFileChooser chooser)
	{
		fileDataSet= new File(chooser.getSelectedFile().getAbsolutePath());
		cibleTrainingData.setText(fileDataSet.getAbsolutePath());
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

            dirDataSet = new File(fileSourcePDF.getParent()+"/DataSet");//creation du repertoire d'avis de jury
            if(!dirDataSet.exists())
                dirDataSet.mkdir();

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

            //creation du fichier contenant un set de données
            String nomDataSet= fileSourcePDF.getName().replace(".pdf", ".arff");
            fileDataSet = new File(dirDataSet.getAbsolutePath()+"/"+nomDataSet);
            cibleData.setText(dirDataSet.getAbsolutePath()+"/"+nomDataSet);
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
		bData.setEnabled(false);
		bDataTraining.setEnabled(false);
		bCompare.setEnabled(false);
	}

	/**
	 * Debloquer bouton empeche l'actiavtion des boutons tant que le chemin vers le pdf n'est pas la
	 */
	private void unlockButton() {
		conversionPdf_Txt.setEnabled(true);
		avisJury.setEnabled(true);
		statistique.setEnabled(true);
        bData.setEnabled(true);
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
	private JTextField createTextFieldCenter()
    {
        JTextField jt=new JTextField();
        jt.setMaximumSize(new Dimension(this.screenWidth,this.screenHeight/40));
        return  jt;
    }
    private JLabel createLabelCenter(String messageLabel)
    {
        JLabel jL=new JLabel(messageLabel);
        jL.setMaximumSize(new Dimension(150,this.screenHeight/40));
        jL.setPreferredSize(new Dimension(50,10));
        jL.setHorizontalAlignment(SwingConstants.RIGHT);
        return jL;
    }
    private void drawGraph(int numPanelGraph,BCC cls,boolean training) throws Exception
    {
    	ArrayList<JTabbedPane> panelGraph=null;
    	if(training)
    		panelGraph=panelGraphTraining;
    	else
    		panelGraph=panelGraphCompare;
        int nbTree=Modele.getKeyLabel().size()-1;
        panelGraph.get(numPanelGraph).removeAll();
        for(int i=0;i<nbTree;i++)
        {
            TreeVisualizer tv = new TreeVisualizer(null,
                    cls.graph().get(i),
                    new PlaceNode2());
			panelGraph.get(numPanelGraph).add(Modele.getKeyLabel().get(i),tv);
        }

    }
	private void drawGraph(int numPanelGraph,BCC cls,String[] Tab,boolean training) throws Exception
	{
		ArrayList<JTabbedPane> panelGraph=null;
		if(training)
			panelGraph=panelGraphTraining;
		else
			panelGraph=panelGraphCompare;
		int nbTree=Tab.length;
		for(int i=0;i<nbTree;i++)
		{
			TreeVisualizer tv = new TreeVisualizer(null,
					cls.graph().get(i),
					new PlaceNode2());
			panelGraph.get(numPanelGraph).add(Tab[i],tv);
		}

	}
}
