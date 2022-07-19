package com.example.energyapp.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> televizoare = new ArrayList<String>();
        televizoare.add("Televizor 48cm clasa A 19 W");
        televizoare.add("Televizor 81cm clasa A 31 W");
        televizoare.add("Televior 125cm clasa A 100 W");


        List<String> becuri = new ArrayList<String>();
        becuri.add("Bec LED clasa A 4 W ");
        becuri.add("Bec Economic clasa A 8 W");
        becuri.add("Bec Industrial clasa E 60 W");
        becuri.add("Bec Halogen clasa C 70 W");

        List<String> frigidere = new ArrayList<String>();
        frigidere.add("Frigider 368L clasa A 397 kWh/anual");
        frigidere.add("Frigider 250L clasa C 367 kWh/anual");
        frigidere.add("Frigider 330L clasa A 66 kWh/anual");
        frigidere.add("Frigider 162L clasa A 65 kwh/anual");

        List<String> masiniSpalatRufe = new ArrayList<String>();
        masiniSpalatRufe.add("MașinăSpălatRufe 9kg clasa A 2200 W");
        masiniSpalatRufe.add("MașinăSpălatRufe 8kg clasa C 2300 W");
        masiniSpalatRufe.add("MașinăSpălatRufe 8.5kg clasa A 250 kWh/anual");

        List<String> laptop = new ArrayList<String>();
        laptop.add("Laptop ultraportabil clasa A 50 W");
        laptop.add("Laptop gaming clasa B 500 W");
        laptop.add("Calculator desktop clasa A 500 W ");

        expandableListDetail.put("Televizor", televizoare);
        expandableListDetail.put("Bec", becuri);
        expandableListDetail.put("Frigider", frigidere);
        expandableListDetail.put("Mașină de spălat", masiniSpalatRufe);
        expandableListDetail.put("Laptop", laptop);

        return expandableListDetail;
    }
}