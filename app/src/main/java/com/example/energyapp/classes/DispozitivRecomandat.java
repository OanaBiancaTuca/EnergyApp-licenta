package com.example.energyapp.classes;

public class DispozitivRecomandat {
    private String tipDispozitiv;
    private String clasaDispozitiv;
    private Float putereDispozitiv;
    private Float pretMinim;
    private Float pretMaxim;
    private String link;

    public DispozitivRecomandat(String tipDispozitiv, String clasaDispozitiv, Float putereDispozitiv, Float pretMinim, Float pretMaxim) {
        this.tipDispozitiv = tipDispozitiv;
        this.clasaDispozitiv = clasaDispozitiv;
        this.putereDispozitiv = putereDispozitiv;
        this.pretMinim = pretMinim;
        this.pretMaxim = pretMaxim;
    }

    public DispozitivRecomandat(String tipDispozitiv, String clasaDispozitiv, Float putereDispozitiv, Float pretMinim, Float pretMaxim, String link) {
        this.tipDispozitiv = tipDispozitiv;
        this.clasaDispozitiv = clasaDispozitiv;
        this.putereDispozitiv = putereDispozitiv;
        this.pretMinim = pretMinim;
        this.pretMaxim = pretMaxim;
        this.link = link;
    }

    public String getTipDispozitiv() {
        return tipDispozitiv;
    }

    public String getClasaDispozitiv() {
        return clasaDispozitiv;
    }

    public Float getPutereDispozitiv() {
        return putereDispozitiv;
    }

    public Float getPretMinim() {
        return pretMinim;
    }

    public Float getPretMaxim() {
        return pretMaxim;
    }

    public String getLink() {
        return link;
    }
}
