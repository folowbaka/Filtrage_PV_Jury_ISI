package main.java.data;

import java.util.ArrayList;

public class Observation {
    private String decision;
    private String commSemestre;
    private ArrayList<String> commComplementaire;
    private boolean filiere=false;
    private String Semestre;
    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public String getCommSemestre() {
        return commSemestre;
    }

    public void setCommSemestre(String commSemestre) {
        this.commSemestre = commSemestre;
    }

    public ArrayList<String> getCommComplementaire() {
        return commComplementaire;
    }

    public void setCommComplementaire(ArrayList<String> commComplementaire) {
        this.commComplementaire = commComplementaire;
    }

    public Observation()
    {
        this.commComplementaire=new ArrayList<String>();
    }
    public String getSemestre() {
        return Semestre;
    }

    public void setSemestre(String semestre) {
        Semestre = semestre;
    }
    public String getEtape()
    {
        if(this.Semestre.matches("TC[0-9]{1}"))
            return "TC";
        else if(this.Semestre.matches("(ISI|TC|HC|SRT|MASTER|RT|STIC)[0-9]{1}")) {
            return "BR";
        }
        return null;
    }

    public boolean isFiliere() {
        return filiere;
    }

    public void setFiliere(boolean filiere) {
        this.filiere = filiere;
    }
}
