package main.java.operation;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import main.java.data.Etudiant;
import main.java.data.Module;
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

    public static void ecritureDataset(String nomFichierTexte, String nomFichierDataSet){
        File file = new File(nomFichierDataSet);
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
        ArrayList<String> semestreUTT;
        ArrayList<String> anglaisUTT;
        Instances       data;
        double[]        vals;

        // 1. set up attributes
        atts = new ArrayList<>();
        binaryVal=new ArrayList<>();
        semestreUTT=new ArrayList<>();
        semestreUTT.add("TC1");semestreUTT.add("TC2");semestreUTT.add("TC3");semestreUTT.add("TC4");semestreUTT.add("TC5");semestreUTT.add("TC6");semestreUTT.add("ISI1");semestreUTT.add("ISI2");semestreUTT.add("ISI3");semestreUTT.add("ISI4");semestreUTT.add("ISI5");semestreUTT.add("ISI6");semestreUTT.add("ISI7");semestreUTT.add("ISI8");semestreUTT.add("ISI9");semestreUTT.add("STIC3");semestreUTT.add("HC1");semestreUTT.add("IDR1");
        anglaisUTT=new ArrayList<>();
        anglaisUTT.add("LE00");anglaisUTT.add("LE01");anglaisUTT.add("LE02");anglaisUTT.add("LE03");anglaisUTT.add("LE08");anglaisUTT.add("LEXX");
        binaryVal.add("0");
        binaryVal.add("1");

        int nbLabel=keyLabel.size();
        for(int i=0;i<nbLabel;i++)
        {
            atts.add(new Attribute(keyLabel.get(i),binaryVal));
        }
        // - numeric
        atts.add(new Attribute("scoreSemestre"));
        atts.add(new Attribute("nombreUe"));
        atts.add(new Attribute("nombreUeRatees"));
        atts.add(new Attribute("semestreObservation",semestreUTT));
        atts.add(new Attribute("UEAnglais",anglaisUTT));
        // 2. create Instances object
        data = new Instances("AvisJury: -C "+nbLabel, atts, 0);

        // 3. fill with data
        int kebab=0;
        while(it.hasNext())
        {
            if(kebab==331)
                System.out.println("bite");
            kebab++;
            Etudiant etu=it.next();
                int semestre=etu.getModules().get(etu.getModules().size()-1).getSemestre();
                vals = new double[data.numAttributes()];
                int indexDecision = keyLabel.indexOf(etu.getObservation().getDecision());
                // - numeric
                if(indexDecision>-1)
                    vals[indexDecision] = 1;
                int indexCommSemestre = keyLabel.indexOf(etu.getObservation().getCommSemestre());
                if(indexCommSemestre>-1)
                    vals[indexCommSemestre] = 1;
                int nbCC=etu.getObservation().getCommComplementaire().size();
                for(int i=0;i<nbCC;i++)
                {
                    int indexCommComplementaire=keyLabel.indexOf(etu.getObservation().getCommComplementaire().get(i));
                    vals[indexCommComplementaire]=1;
                }
                int nbModule=etu.getModules().size();
                int nbUERatees=0;
                String uEAnglais="";
                for(int i=0;i<nbModule;i++)
                {
                    if(etu.getModules().get(i).getSemestre()==semestre)
                    {
                        if (DecisionJury.estRatee(etu.getModules().get(i)))
                            nbUERatees++;
                        if(RecherchePattern.rechercheUEAnglais(etu.getModules().get(i).getNom())) {
                            uEAnglais = etu.getModules().get(i).getNom();
                            if(Integer.parseInt(uEAnglais.substring(2))>8)
                                uEAnglais="LEXX";
                        }
                    }
                }
                vals[nbLabel]=DecisionJury.evalueSemestre(etu,semestre);
                vals[nbLabel+1]=DecisionJury.nombreUeSemestre(etu,semestre);
                vals[nbLabel+2]=nbUERatees;
                vals[nbLabel+3]=semestreUTT.indexOf(etu.getObservation().getSemestre());
                if(uEAnglais.equals(""))
                    uEAnglais="LEXX";
                vals[nbLabel+4]=anglaisUTT.indexOf(uEAnglais);
                data.add(new DenseInstance(1.0, vals));

        }
        pw.print(data);
        pw.close();
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

    public static BCC entrainement(File fileDataset)
    {
        System.out.println("Loading data: " + fileDataset.getName());
        Instances data=null;
        try {
            data = DataSource.read(fileDataset.getAbsolutePath());
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
