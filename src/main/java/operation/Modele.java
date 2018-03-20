package main.java.operation;
import java.io.*;
import java.util.*;

import main.java.data.Etudiant;
import main.java.data.Module;
import meka.filters.unsupervised.attribute.MekaClassAttributes;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.*;
import weka.filters.Filter;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSink;

public abstract class Modele {
    private static Map<String,String> listeDecision=new LinkedHashMap<String, String>();
    private static Map<String,String> listeCommSemestre=new LinkedHashMap<String, String>();
    private static ArrayList<String> keyLabel=new ArrayList<String>();
    public static Map<String, String> getListeDecision() {
        return listeDecision;
    }
    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    public static final String UTF8_BOM = "\uFEFF";

    public static void setListeDecision(Map<String,String> listeDecision) {
        Modele.listeDecision = listeDecision;
    }

    private static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
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
        Instances       data;
        double[]        vals;

        // 1. set up attributes
        atts = new ArrayList<Attribute>();
        binaryVal=new ArrayList<String>();
        binaryVal.add("0");
        binaryVal.add("1");

        int nbLabel=listeDecision.size()+listeCommSemestre.size();
        for(Map.Entry decision:listeDecision.entrySet())
        {
            atts.add(new Attribute((String)decision.getKey(),binaryVal));
            keyLabel.add((String)decision.getKey());
        }
        for(Map.Entry commSemestre:listeCommSemestre.entrySet())
        {
            atts.add(new Attribute((String)commSemestre.getKey(),binaryVal));
            keyLabel.add((String)commSemestre.getKey());
        }
        // - numeric
        atts.add(new Attribute("scoreSemestre"));
        atts.add(new Attribute("nombreUe"));
        atts.add(new Attribute("nombreUeRatees"));
        // 2. create Instances object
        data = new Instances("AvisJury: -C "+nbLabel, atts, 0);

        // 3. fill with data
        while(it.hasNext())
        {
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
                int nbModule=etu.getModules().size();
                int nbUERatees=0;
                for(int i=0;i<nbModule;i++)
                {
                    if(etu.getModules().get(i).getSemestre()==semestre)
                    {
                        if (DecisionJury.estRatee(etu.getModules().get(i)))
                            nbUERatees++;
                    }
                }
                vals[nbLabel]=DecisionJury.evalueSemestre(etu,semestre);
                vals[nbLabel+1]=DecisionJury.nombreUeSemestre(etu,semestre);
                vals[nbLabel+2]=nbUERatees;
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

    public static Map<String, String> getListeCommSemestre() {
        return listeCommSemestre;
    }

}
