package main.java.operation;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import main.java.data.Etudiant;
import main.java.data.Module;
import main.java.ihm.IHMAvisJury;
import meka.classifiers.multilabel.BCC;
import meka.classifiers.multilabel.BR;
import meka.classifiers.multilabel.Evaluation;
import meka.core.MLUtils;
import meka.core.Result;
import meka.filters.unsupervised.attribute.MekaClassAttributes;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.*;
import weka.filters.Filter;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSink;

public class Modele {
    private static Map<String,String> listeDecision=new LinkedHashMap<String, String>();
    private static Map<String,String> listeCommSemestre=new LinkedHashMap<String, String>();
    private static Map<String,String> listeCommComplementaire=new LinkedHashMap<>();
    private static ArrayList<String> keyLabel=new ArrayList<String>();

    public static Map<String, String> getListeCommComplementaire() {
        return listeCommComplementaire;
    }

    public static Map<String, String> getListeDecision() {
        return listeDecision;
    }
    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    public static final String UTF8_BOM = "\uFEFF";

    public Modele()
    {
        Modele.loadListeObservation(Modele.getListeDecision(),"src/main/java/files/decision.txt");
        Modele.loadListeObservation(Modele.getListeCommSemestre(),"src/main/java/files/commSemestre.txt");
        Modele.loadListeObservation(Modele.getListeCommComplementaire(),"src/main/java/files/commComplementaire.txt");
        for(Map.Entry decision:listeDecision.entrySet())
        {
            keyLabel.add((String)decision.getKey());
        }
        for(Map.Entry commSemestre:listeCommSemestre.entrySet())
        {
            keyLabel.add((String)commSemestre.getKey());
        }
        for(Map.Entry commComplementaire:listeCommComplementaire.entrySet())
        {
            keyLabel.add((String)commComplementaire.getKey());
        }
    }
    public static void setListeDecision(Map<String,String> listeDecision) {
        Modele.listeDecision = listeDecision;
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public static ArrayList<String> getKeyLabel() {
        return keyLabel;
    }

    public static void ecritureDataset(String nomFichierTexte, String nomFichierDataSet)
    {
        File fileDirectory=new File(nomFichierDataSet+"/");
        if(!fileDirectory.exists())
            fileDirectory.mkdir();
        File file = new File(nomFichierDataSet+"/"+fileDirectory.getName()+".arff");
        File fileNpml=new File(nomFichierDataSet+"/"+fileDirectory.getName()+"_NPML.arff");
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

        List<Etudiant> etudiants = GestionData.listeEtudiant(new File(nomFichierTexte),true);
        Iterator<Etudiant> it = etudiants.iterator();
        ArrayList<Attribute>     atts;
        ArrayList<String>      binaryVal;
        ArrayList<String> etapeCursus;
        Instances       data;
        Instances dataNpml;
        Map<String,Instances> subData;
        ArrayList<String> anglaisUTT;
        ArrayList<Attribute> attsNpml;
        double[]        vals;
        double[]        valsNPML;

        subData=new LinkedHashMap<>();
        // 1. set up attributes
        atts = new ArrayList<>();
        attsNpml=new ArrayList<>();
        binaryVal=new ArrayList<>();
        anglaisUTT=new ArrayList<>();
        anglaisUTT.add("LE00");anglaisUTT.add("LE01");anglaisUTT.add("LE02");anglaisUTT.add("LE03");anglaisUTT.add("LE04");anglaisUTT.add("LE08");anglaisUTT.add("LEXX");anglaisUTT.add("NoEnglish");
        etapeCursus=new ArrayList<>();
        etapeCursus.add("TC"); etapeCursus.add("BR");
        binaryVal.add("0");
        binaryVal.add("1");

        int nbLabel=keyLabel.size();
        for(int i=0;i<nbLabel-1;i++)
        {

            atts.add(new Attribute(keyLabel.get(i),binaryVal));
        }
        attsNpml.add(new Attribute(keyLabel.get(nbLabel-1),binaryVal));
        attsNpml.add(new Attribute("FIL",binaryVal));
        attsNpml.add(new Attribute("UEAnglais",anglaisUTT));
        attsNpml.add(new Attribute("etapeCursus",etapeCursus));
        // - numeric
        atts.add(new Attribute("scoreSemestre"));
        atts.add(new Attribute("nombreUe"));
        atts.add(new Attribute("nombreUeRatees"));
        atts.add(new Attribute("FIL",binaryVal));
        atts.add(new Attribute("etapeCursus",etapeCursus));
        //atts.add(new Attribute("semestreObservation",semestreUTT));

        // 2. create Instances object
        data = new Instances("AvisJury: -C "+(nbLabel-1), atts, 0);
        dataNpml=new Instances("AvisNpml: -C "+1,attsNpml,0);

        // 3. fill with data
        while(it.hasNext())
        {

            Etudiant etu=it.next();
            if(etu.getModules().size()>0)
            {
                int nbObservation=etu.getObservation().size();
                String uEAnglais = "";
                for(int i=0;i<nbObservation;i++)
                {

                    if(etu.getObservation().get(i).getDecision()!=null && etu.getObservation().get(i).getCommSemestre()!=null && etu.getObservation().get(i).getCommComplementaire().size()>0)
                    {
                        vals = new double[data.numAttributes()];
                        valsNPML=new double[dataNpml.numAttributes()];
                        int indexDecision = keyLabel.indexOf(etu.getObservation().get(i).getDecision());
                        // - numeric
                        if (indexDecision > -1)
                            vals[indexDecision] = 1;
                        int indexCommSemestre = keyLabel.indexOf(etu.getObservation().get(i).getCommSemestre());
                        if (indexCommSemestre > -1)
                            vals[indexCommSemestre] = 1;
                        int nbCC = etu.getObservation().get(i).getCommComplementaire().size();
                        for (int j = 0; j < nbCC; j++) {
                            int indexCommComplementaire = keyLabel.indexOf(etu.getObservation().get(i).getCommComplementaire().get(j));
                            if(indexCommComplementaire==nbLabel-1) {
                                valsNPML[0] = 1;
                            }
                            else
                                vals[indexCommComplementaire] = 1;
                        }
                        int nbModule = etu.getModules().size();
                        int nbUERatees = 0;
                        for (int j = 0; j < nbModule; j++) {
                            if (etu.getModules().get(j).getSemestre() == i) {
                                if (DecisionJury.estRatee(etu.getModules().get(j)))
                                    nbUERatees++;
                                if (RecherchePattern.rechercheUEAnglais(etu.getModules().get(j).getNom())) {
                                    uEAnglais = etu.getModules().get(j).getNom();
                                    if (Integer.parseInt(uEAnglais.substring(2)) > 8)
                                        uEAnglais = "LEXX";
                                }
                            }
                        }
                        vals[nbLabel-1] = DecisionJury.evalueSemestre(etu, i);
                        vals[nbLabel] = DecisionJury.nombreUeSemestre(etu, i);
                        vals[nbLabel+1] = nbUERatees;
                        int fil=etu.getObservation().get(i).isFiliere()?1:0;
                        vals[nbLabel+2]=fil;
                        vals[nbLabel+3]=etapeCursus.indexOf(etu.getObservation().get(i).getEtape());
                        valsNPML[1]=fil;
                         if (uEAnglais.equals(""))
                            uEAnglais = "NoEnglish";
                        valsNPML[2] = anglaisUTT.indexOf(uEAnglais);
                        valsNPML[3]=etapeCursus.indexOf(etu.getObservation().get(i).getEtape());
                        data.add(new DenseInstance(1.0, vals));
                        dataNpml.add(new DenseInstance(1.0, valsNPML));
                        String  semestreObservation=etu.getObservation().get(i).getSemestre();
                        if(!subData.containsKey(semestreObservation))
                        {
                            subData.put(semestreObservation, new Instances("AvisJury: -C " + (nbLabel - 1), atts, 0));
                            subData.put(semestreObservation+"_NPML",new Instances("AvisNpml: -C "+1,attsNpml,0));
                        }
                        else
                        {
                            subData.get(semestreObservation).add(new DenseInstance(1.0, vals));
                            subData.get(semestreObservation+"_NPML").add(new DenseInstance(1.0, valsNPML));
                        }
                    }
                }
            }
        }
        pw.print(data);
        pw.close();
        try {

            fw = new FileWriter(fileNpml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        pw.print(dataNpml);
        pw.close();

        for(Map.Entry sData:subData.entrySet())
        {
            try {

                fw = new FileWriter(nomFichierDataSet+"/"+fileDirectory.getName()+"_"+sData.getKey()+".arff");
            } catch (IOException e) {
                e.printStackTrace();
            }
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            pw.print(sData.getValue());
            pw.close();
        }
    }

    public static void loadListeObservation(Map liste,String nomFichier)
    {
        try {

            BufferedReader br = new BufferedReader(new FileReader(nomFichier));
            String line;
            boolean firstline=true;
            while ((line = br.readLine()) != null) {
                if(firstline) {
                    line = removeUTF8BOM(line);
                    firstline=false;
                }
                String observation[]=line.split(",");
                liste.put(observation[0],observation[1]);
            }
            br.close();
        }catch (IOException e) {

            e.printStackTrace();
        }

    }

    public static BCC entrainement(String fileDataset)
    {
        Instances data=null;
        try {
            data = DataSource.read(fileDataset);
            MLUtils.prepareData(data);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        double percentage = Double.parseDouble("100");
        int trainSize = (int) (data.numInstances() * percentage / 100.0);
        Instances train = new Instances(data, 0, trainSize);
        Instances test = new Instances(data, trainSize, data.numInstances() - trainSize);

        System.out.println("Build BR classifier on " + percentage + "%");
        BCC classifier = new BCC();
        String[] options=classifier.getOptions();
        for(int i=0;i<options.length;i++)
        {
            System.out.println(options[i]);
        }
        // further configuration of classifier
        try
        {
            classifier.buildClassifier(train);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Evaluate BR classifier on " + (100.0 - percentage) + "%");
        String top = "PCut1";
        String vop = "4";
        Result result=null;
        try
        {
            //result = Evaluation.evaluateModel(classifier, train, test, top, vop);
            //System.out.println(result);

        }catch (Exception e)
        {

        }
        return classifier;
    }
    public static Map<String, String> getListeCommSemestre() {
        return listeCommSemestre;
    }


}
