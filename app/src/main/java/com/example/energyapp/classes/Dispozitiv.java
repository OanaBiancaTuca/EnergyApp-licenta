package com.example.energyapp.classes;


//clasa dispozitiv-->asa este salvat dispozitivul in bd
public class Dispozitiv {
    String dispozitivId;
    String tipDispozitiv;
    String clasaDispozitiv;
    String consumDispozitiv;
    int minuteFunctionareZilnic;
    String delaora;
    String panalaora;
    int numarDispozitive;
    String modfunctionare;
    String incapereId;
    String uid;

    public Dispozitiv() {
    }

    public Dispozitiv(String dispozitivId, String tipDispozitiv, String clasaDispozitiv, String consumDispozitiv,
                      int minuteFunctionareZilnic, String delaora, String panalaora, int numarDispozitive, String modfunctionare,String incapereId,
                      String uid) {
        this.dispozitivId = dispozitivId;
        this.tipDispozitiv = tipDispozitiv;
        this.clasaDispozitiv = clasaDispozitiv;
        this.consumDispozitiv = consumDispozitiv;
        this.minuteFunctionareZilnic = minuteFunctionareZilnic;
        this.delaora = delaora;
        this.panalaora = panalaora;
        this.numarDispozitive = numarDispozitive;
        this.modfunctionare = modfunctionare;
        this.incapereId=incapereId;
        this.uid=uid;
    }

    public String getDispozitivId() {
        return dispozitivId;
    }

    public String getTipDispozitiv() {
        return tipDispozitiv;
    }

    public String getClasaDispozitiv() {
        return clasaDispozitiv;
    }

    public String getConsumDispozitiv() {
        return consumDispozitiv;
    }

    public int getMinuteFunctionareZilnic() {
        return minuteFunctionareZilnic;
    }

    public String getDelaora() {
        return delaora;
    }

    public String getPanalaora() {
        return panalaora;
    }

    public int getNumarDispozitive() {
        return numarDispozitive;
    }

    public String getModfunctionare() {
        return modfunctionare;
    }

    public void setDispozitivId(String dispozitivId) {
        this.dispozitivId = dispozitivId;
    }

    public void setTipDispozitiv(String tipDispozitiv) {
        this.tipDispozitiv = tipDispozitiv;
    }

    public void setClasaDispozitiv(String clasaDispozitiv) {
        this.clasaDispozitiv = clasaDispozitiv;
    }

    public void setConsumDispozitiv(String consumDispozitiv) {
        this.consumDispozitiv = consumDispozitiv;
    }

    public void setMinuteFunctionareZilnic(int minuteFunctionareZilnic) {
        this.minuteFunctionareZilnic = minuteFunctionareZilnic;
    }

    public void setDelaora(String delaora) {
        this.delaora = delaora;
    }

    public void setPanalaora(String panalaora) {
        this.panalaora = panalaora;
    }

    public void setNumarDispozitive(int numarDispozitive) {
        this.numarDispozitive = numarDispozitive;
    }

    public void setModfunctionare(String modfunctionare) {
        this.modfunctionare = modfunctionare;
    }

    public String getIncapereId() {
        return incapereId;
    }

    public void setIncapereId(String incapereId) {
        this.incapereId = incapereId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return  tipDispozitiv + ", clasa " + clasaDispozitiv ;
    }
}
