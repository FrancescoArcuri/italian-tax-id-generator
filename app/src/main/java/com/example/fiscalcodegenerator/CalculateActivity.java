package com.example.fiscalcodegenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;

public class CalculateActivity extends AppCompatActivity {

    private int sex = -1;
    private boolean countyOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);

        //dichiaro tutti gli altri elementi grafici
        EditText name = findViewById(R.id.editTextName);
        EditText surname = findViewById(R.id.editTextSurname);
        EditText date = findViewById(R.id.editTextDate);
        EditText birthplace = findViewById(R.id.editTextBirthPlace);
        EditText county = findViewById(R.id.editTextCounty);
        TextView display = findViewById(R.id.textViewCF);


        display.setVisibility(View.INVISIBLE);


        //definisco l'evento a runtime e assegno il listener al pulsante (lo metto nell'oncreate che è il metodo dell'attività)
        Button buttonCalculate = findViewById(R.id.buttonCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() { //definisco un oggetto di tipo View.OnClickListener e lo assegno tramite la funzione set al bottone
            @Override
            public void onClick(View v) {

                final String surnameStr = surname.getText().toString();
                final String nameStr = name.getText().toString();
                final String birthplaceStr = birthplace.getText().toString();
                final String countyStr = county.getText().toString();
                final String dateStr = date.getText().toString();

                String cadastralCode = findCadastralCode(birthplaceStr,countyStr);

                if (controlStringNominative(nameStr) && controlStringNominative(surnameStr) && controlDate(dateStr) && cadastralCode != null &&
                        countyOk && sex != -1) {

                    String CF = calculateCF(nameStr, surnameStr, dateStr, cadastralCode);

                    display.setText(CF);
                    display.setVisibility(View.VISIBLE);

                } else {
                    Context context = getApplicationContext(); //restituisce il contesto del processo corrente
                    CharSequence text = "DATI MANCANTI O FORMATO NON CORRETTO";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration); //mettendo "this" al posto di "context" ottenevo errore, quindi ho visto la documentazione
                    toast.show();
                }
            }
        });


    }


    private void btclick(View view) {
        boolean checked = ((RadioButton) view).isChecked(); //controlla se uno dei due è stato premuto

        //controlla quale dei due è stato premuto
        switch(view.getId()) {
            case R.id.radioButtonF:
                if (checked)
                    sex = 0;
                break;
            case R.id.radioButtonM:
                if (checked)
                    sex = 1;
                break;
        }
    }



    //FUNZIONE DI CONTROLLO DELLA DATA: si assicura che siano stati inseriti numeri interi.
    public boolean controlDate(String dateStr) {

        if(dateStr.equals("")){
            return false;
        }

        String[] dateStrVector = dateStr.split("/"); //divide la stinga in vettori di stringhe
        int d = Integer.parseInt(dateStrVector[0]); //dateStrVector contiene una stringa di decimali e la funzione mi fa ottenere il numero intero (solo in decimale)
        int m = Integer.parseInt(dateStrVector[1]);
        int y = Integer.parseInt(dateStrVector[2]);
        Calendar calendar = Calendar.getInstance(); //creo istanza vuota di calendario

        if(d <= 31 && m <= 12 && y <= calendar.get(Calendar.YEAR)) { //metto la maiuscola perché chiamo un valore statico della classe Calendar
            return true;
        } else {
            return false;
        }
    }

    //FUNZIONE DI CONTROLLO NOMINATIVI: si assicura che siano stati inseriti solo caratteri;
    public boolean controlStringNominative(String nominative) {

        if(nominative.equals("")){ //alternativa a compareTo
            return false;
        }

        int error = 0;
        for (int i = 0; i < nominative.length(); i++) {

            if ((nominative.charAt(i) == '0') || (nominative.charAt(i) == '1') || (nominative.charAt(i) == '2') || (nominative.charAt(i) == '3')
                    || (nominative.charAt(i) == '4') || (nominative.charAt(i) == '5') || (nominative.charAt(i) == '6')
                    || (nominative.charAt(i) == '7') || (nominative.charAt(i) == '8') || (nominative.charAt(i) == '9')) {

                error++;

            }
        }

        if (error == 0) {
            return true;
        } else {
            return false;
        }

    }

    //FUNZIONE DI CREAZIONE CODICE COGNOME
    private String codedSurname(String nominative){

        String codeSurname = new String();
        nominative = nominative.toLowerCase(); //riporta tutto in minuscolo
        nominative = nominative.replaceAll(" ", ""); //elimina gli spazi
        int numCons = 0;

        if (nominative.length() < 3){
            if (nominative.length() == 2){
                nominative=nominative + "x";
            }
            else
                nominative=nominative + "xx";

            return nominative.toUpperCase();
        }

        for (int i = 0; i<nominative.length(); i++){
            char letter = nominative.charAt(i); //estrae il carattere nella posizione i
            if(letter != 'a' && letter != 'e' && letter != 'i' && letter != 'o' && letter !='u') {

                codeSurname = codeSurname + letter;
                numCons++;

            }

            if(numCons == 3){
                break;
            }
        }

        if(numCons < 3) {

            for (int j = 0; j < nominative.length(); j++) {

                char letter = nominative.charAt(j);
                if(letter == 'a' || letter == 'e' || letter =='i' || letter =='o' || letter =='u') {
                    codeSurname = codeSurname + letter;
                    numCons++;
                }

                if (numCons == 3) {
                    break;
                }

            }
        }

        return codeSurname.toUpperCase();
    }


    //FUNZIONE DI CREAZIONE CODICE NOME
    private String codedName(String nominative) {

        String codeName = new String();
        nominative = nominative.toLowerCase();
        nominative = nominative.replaceAll(" ", ""); //elimina gli spazi
        int numCons = 0;

        if (nominative.length() < 3){
            if (nominative.length() == 2){
                nominative=nominative + "x";
            }
            else
                nominative=nominative+"xx";

            return nominative.toUpperCase();
        }

        for (int i = 0; i < nominative.length(); i++) {
            char letter = nominative.charAt(i);
            if (letter != 'a' && letter != 'e' && letter != 'i' && letter != 'o' && letter != 'u') {
                numCons++;
            }
        }

        if(numCons > 3) {
            numCons = 0;
            for (int i = 0; i < nominative.length(); i++) {
                char letter = nominative.charAt(i);
                if (letter != 'a' && letter != 'e' && letter != 'i' && letter != 'o' && letter != 'u') {
                    numCons++;
                    if (numCons != 2) {
                        codeName = codeName + letter;
                    } else {
                        continue;
                    }
                }

                if (numCons == 4) {
                    break;
                }
            }
        }

        if(numCons == 3){
            for (int i = 0; i < nominative.length(); i++) {
                char letter = nominative.charAt(i);
                if (letter != 'a' && letter != 'e' && letter != 'i' && letter != 'o' && letter != 'u') {
                    codeName = codeName + letter;
                }
            }
        }

        if (numCons < 3) {
            numCons = 0;
            for (int i = 0; i < nominative.length(); i++) {
                char letter = nominative.charAt(i);
                if (letter != 'a' && letter != 'e' && letter != 'i' && letter != 'o' && letter != 'u') {
                    codeName = codeName + letter;
                    numCons++;
                }
            }
            for (int i = 0; i < nominative.length(); i++) {
                char letter = nominative.charAt(i);
                if (letter == 'a' || letter == 'e' || letter == 'i' || letter == 'o' || letter == 'u') {
                    codeName = codeName + letter;
                    numCons++;
                }

                if (numCons == 3) {
                    break;
                }

            }
        }

        return codeName.toUpperCase();
    }


    //FUNZIONE DI CALCOLO CODICE DATA: calcola codice della data di nascita
    private String codedDate(String dateStr){

        String[] dateStrVector = dateStr.split("/"); //divide la stringa in vettori di stringhe
        String dayStr = dateStrVector[0];
        int month = Integer.parseInt(dateStrVector[1]); //dateStrVector contiene una stringa di decimali e la funzione mi fa ottenere il numero intero (solo in decimale)
        String yearStr = dateStrVector[2];

        String m=null; //perché assegno indirizzo di memoria


        if  (sex== 0) { //dichiaro qui sex perché dovrebbe essere già stato selezionato
            int day = Integer.parseInt(dayStr); //dayStr contiene una stringa di decimali e la funzione mi fa ottenere il numero intero (solo in decimale)
            day +=40;
            dayStr = String.valueOf(day); //questa funzione converte differenti tipi di valori in stringa
        }


        switch (month){
            case 1:
                m="A";
                break;
            case 2:
                m="B";
                break;
            case 3:
                m="C";
                break;
            case 4:
                m="D";
                break;
            case 5:
                m="E";
                break;
            case 6:
                m="H";
                break;
            case 7:
                m="L";
                break;
            case 8:
                m="M";
                break;
            case 9:
                m="P";
                break;
            case 10:
                m="R";
                break;
            case 11:
                m="S";
                break;
            case 12:
                m="T";
                break;

            default:
        }

        String Result1=yearStr.charAt(3)+m+dayStr;
        String Result2=yearStr.charAt(2)+Result1;

        return Result2;

    }


    //FUNZIONE DI CALCOLO CODICE CATASTALE: prende in input birthplace e provincia, andando a leggere da file restituisce codice catastale
    public String findCadastralCode(String birthplace, String county) {

        if(birthplace.equals("") || county.equals("")){
            return null;
        }

        birthplace = birthplace.toLowerCase();
        county = county.toUpperCase();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("listacomuni.txt"))); //apro il file con inputstreamreader e lo incapsulo in buffered imponendo di utilizzare il buffer per renderlo più efficiente
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {

                String[] line=nextLine.split(";");
                String place=line[1];
                String acronym=line[2];
                String code=line[6];

                if (birthplace.compareTo(place.toLowerCase()) == 0) {

                    if(county.compareTo(acronym.toUpperCase()) == 0){

                        countyOk = true; //se non entro mai qua countyOk rimane a false e non entro nell'if iniziale
                    }

                    return code;
                }
            }
            return null;

        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }

    }



    //FUNZIONE DI CALCOLO CARATTERE DI CONTROLLO: calcola ultima cifra del CF che dipende dalle 15 precedenti
    private char controlCode(String tempCodeCF) {

        int count = 0;

        for (int i = 0; i < tempCodeCF.length(); i++) {

            char tempChar = tempCodeCF.charAt(i);

            if ((i + 1) % 2 == 0) {
                if (Character.isLetter(tempChar)) { //controlla se il carattere selezionato è una lettera o numero
                    count += (tempChar - 65); //sottraggo 65 perché è il valore della lettera A nella tabella ASCII
                } else {
                    count += Character.getNumericValue(tempChar); //restituisce il valore numerico intero del carattere selezionato
                }
            }
            if ((i + 1) % 2 == 1) {
                switch (tempChar) {
                    case '0':
                    case 'A':
                        count += 1;
                        break;
                    case '1':
                    case 'B':
                        count += 0;
                        break;
                    case '2':
                    case 'C':
                        count += 5;
                        break;
                    case '3':
                    case 'D':
                        count += 7;
                        break;
                    case '4':
                    case 'E':
                        count += 9;
                        break;
                    case '5':
                    case 'F':
                        count += 13;
                        break;
                    case '6':
                    case 'G':
                        count += 15;
                        break;
                    case '7':
                    case 'H':
                        count += 17;
                        break;
                    case '8':
                    case 'I':
                        count += 19;
                        break;
                    case '9':
                    case 'J':
                        count += 21;
                        break;
                    case 'K':
                        count += 2;
                        break;
                    case 'L':
                        count += 4;
                        break;
                    case 'M':
                        count += 18;
                        break;
                    case 'N':
                        count += 20;
                        break;
                    case 'O':
                        count += 11;
                        break;
                    case 'P':
                        count += 3;
                        break;
                    case 'Q':
                        count += 6;
                        break;
                    case 'R':
                        count += 8;
                        break;
                    case 'S':
                        count += 12;
                        break;
                    case 'T':
                        count += 14;
                        break;
                    case 'U':
                        count += 16;
                        break;
                    case 'V':
                        count += 10;
                        break;
                    case 'W':
                        count += 22;
                        break;
                    case 'X':
                        count += 25;
                        break;
                    case 'Y':
                        count += 24;
                        break;
                    case 'Z':
                        count += 23;
                        break;
                }
            }

        }

        int carry = count % 26;

        char controlChar = (char) (carry + 65); //sommo 65 per tenere conto dei valori numerici della tabella ASCII e lo forzo come carattere

        return controlChar;
    }



    //FUNZIONE FINALE: costruisce stringa del codice fiscale richiamando al suo interno tutte le funzioni sviluppate
    private String calculateCF(String nameStr, String surnameStr, String dateStr, String cadastralCode){

        String surnamecode = codedSurname(surnameStr);
        String namecode = codedName(nameStr);
        String datecode = codedDate(dateStr);

        String tempFC = surnamecode + namecode + datecode + cadastralCode;

        char controlChar = controlCode(tempFC);

        //CODICE FISCALE FINALE DA RESTITUIRE ALL'ACTIVITY CALCULATE
        String CFcode = tempFC + controlChar;

        return CFcode;
    }



}