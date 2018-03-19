package main.java.operation;

import java.io.*;
import java.util.*;


public abstract class Modele {
    private  static Map<String,String> listeDecision;

    public static Map<String, String> getListeDecision() {
        return listeDecision;
    }

    public static void setListeDecision(Map<String,String> listeDecision) {
        Modele.listeDecision = listeDecision;
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

        List<data.Etudiant> etudiants = operation.GestionData.listeEtudiant(new File(nomFichierTexte),true);
        Iterator<data.Etudiant> it = etudiants.iterator();
        FastVector      atts;
        FastVector      attsRel;
        FastVector      attVals;
        FastVector      attValsRel;
        Instances       data;
        Instances       dataRel;
        double[]        vals;
        double[]        valsRel;
        int             i;

        // 1. set up attributes
        atts = new FastVector();
        // - numeric
        atts.addElement(new Attribute("att1"));
        // - nominal
        attVals = new FastVector();
        for (i = 0; i < 5; i++)
            attVals.addElement("val" + (i+1));
        atts.addElement(new Attribute("att2", attVals));
        // - string
        atts.addElement(new Attribute("att3", (FastVector) null));
        // - date
        atts.addElement(new Attribute("att4", "yyyy-MM-dd"));
        // - relational
        attsRel = new FastVector();
        // -- numeric
        attsRel.addElement(new Attribute("att5.1"));
        // -- nominal
        attValsRel = new FastVector();
        for (i = 0; i < 5; i++)
            attValsRel.addElement("val5." + (i+1));
        attsRel.addElement(new Attribute("att5.2", attValsRel));
        dataRel = new Instances("att5", attsRel, 0);
        atts.addElement(new Attribute("att5", dataRel, 0));

        // 2. create Instances object
        data = new Instances("MyRelation", atts, 0);

        // 3. fill with data
        // first instance
        vals = new double[data.numAttributes()];
        // - numeric
        vals[0] = Math.PI;
        // - nominal
        vals[1] = attVals.indexOf("val3");
        // - string
        vals[2] = data.attribute(2).addStringValue("This is a string!");
        // - date
        vals[3] = data.attribute(3).parseDate("2001-11-09");
        // - relational
        dataRel = new Instances(data.attribute(4).relation(), 0);
        // -- first instance
        valsRel = new double[2];
        valsRel[0] = Math.PI + 1;
        valsRel[1] = attValsRel.indexOf("val5.3");
        dataRel.add(new Instance(1.0, valsRel));
        // -- second instance
        valsRel = new double[2];
        valsRel[0] = Math.PI + 2;
        valsRel[1] = attValsRel.indexOf("val5.2");
        dataRel.add(new Instance(1.0, valsRel));
        vals[4] = data.attribute(4).addRelation(dataRel);
        // add
        data.add(new Instance(1.0, vals));

        // second instance
        vals = new double[data.numAttributes()];  // important: needs NEW array!
        // - numeric
        vals[0] = Math.E;
        // - nominal
        vals[1] = attVals.indexOf("val1");
        // - string
        vals[2] = data.attribute(2).addStringValue("And another one!");
        // - date
        vals[3] = data.attribute(3).parseDate("2000-12-01");
        // - relational
        dataRel = new Instances(data.attribute(4).relation(), 0);
        // -- first instance
        valsRel = new double[2];
        valsRel[0] = Math.E + 1;
        valsRel[1] = attValsRel.indexOf("val5.4");
        dataRel.add(new Instance(1.0, valsRel));
        // -- second instance
        valsRel = new double[2];
        valsRel[0] = Math.E + 2;
        valsRel[1] = attValsRel.indexOf("val5.1");
        dataRel.add(new Instance(1.0, valsRel));
        vals[4] = data.attribute(4).addRelation(dataRel);
        // add
        data.add(new Instance(1.0, vals));

        // 4. output data
        System.out.println(data);
        pw.close();
    }

    public static void loadListeDecision()
    {
        try {

            BufferedReader br = new BufferedReader(new FileReader("decision.txt"));
            String line;
            listeDecision=new HashMap();
            while ((line = br.readLine()) != null) {
            String decision[]=line.split(",");
            listeDecision.put(decision[0],decision[1]);
            }
            br.close();
        }catch (IOException e) {

            e.printStackTrace();
        }

    }
}
