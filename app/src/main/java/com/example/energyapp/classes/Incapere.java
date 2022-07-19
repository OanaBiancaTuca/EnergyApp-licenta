package com.example.energyapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

//clasa Incapere-->asa cum este salvata o incapere in baza de date
public class Incapere implements Parcelable {
    String incapereId;
    String denumire;
    String tipIncapere;
    String consumTotal;
    int numardispozitive;
    String uid;

    public Incapere() {
    }

    public Incapere(String incapereId, String denumire, String tipIncapere,String uid) {
        this.incapereId = incapereId;
        this.denumire = denumire;
        this.tipIncapere = tipIncapere;
        this.consumTotal = "";
        this.numardispozitive = 0;
        this.uid=uid;
    }

    public Incapere(String incapereId, String denumire, String tipIncapere, String consumTotal, int numardispozitive, String uid) {
        this.incapereId = incapereId;
        this.denumire = denumire;
        this.tipIncapere = tipIncapere;
        this.consumTotal = consumTotal;
        this.numardispozitive = numardispozitive;
        this.uid = uid;
    }

    protected Incapere(Parcel in) {
        incapereId = in.readString();
        denumire = in.readString();
        tipIncapere = in.readString();
        consumTotal = in.readString();
        numardispozitive = in.readInt();
        uid=in.readString();
    }

    public static final Creator<Incapere> CREATOR = new Creator<Incapere>() {
        @Override
        public Incapere createFromParcel(Parcel in) {
            return new Incapere(in);
        }

        @Override
        public Incapere[] newArray(int size) {
            return new Incapere[size];
        }
    };

    public String getIncapereId() {
        return incapereId;
    }

    public void setIncapereId(String incapereId) {
        this.incapereId = incapereId;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public String getTipIncapere() {
        return tipIncapere;
    }

    public void setTipIncapere(String tipIncapere) {
        this.tipIncapere = tipIncapere;
    }

    public String getConsumTotal() {
        return consumTotal;
    }

    public void setConsumTotal(String consumTotal) {
        this.consumTotal = consumTotal;
    }

    public int getNumardispozitive() {
        return numardispozitive;
    }

    public void setNumardispozitive(int numardispozitive) {
        this.numardispozitive = numardispozitive;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.incapereId);
        dest.writeString(this.denumire);
        dest.writeString(this.tipIncapere);
        dest.writeString(this.consumTotal);
        dest.writeInt(this.numardispozitive);
        dest.writeString(this.uid);

    }

    @Override
    public String toString() {
        return "Incapere{" +
                "incapereId='" + incapereId + '\'' +
                ", denumire='" + denumire + '\'' +
                ", tipIncapere='" + tipIncapere + '\'' +
                ", consumTotal='" + consumTotal + '\'' +
                ", numardispozitive=" + numardispozitive +
                ", uid='" + uid + '\'' +
                '}';
    }
}
