package code.Exercise4;

public class Variable {

    private String nom;
    private String classe;
    private String paquetage;

    public Variable(String nom, String classe, String paquetage) {
        this.nom = nom;
        this.classe = classe;
        this.paquetage = paquetage;
    }

    public String getNom() {
        return nom;
    }

    public String getClasse() {
        return classe;
    }

    public String getPaquetage() {
        return paquetage;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public void setPaquetage(String paquetage) {
        this.paquetage = paquetage;
    }

    public String toString() {
        return nom + ";" + classe + ";" + paquetage;
    }
}
