package com.example.note.cesar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements TextWatcher, NumberPicker.OnValueChangeListener {
    Spinner vSpinner;
    Spinner vLang;
    EditText vSlide;
    NumberPicker vNormalPicker;
    NumberPicker vNumberPicker;
    NumberPicker vCodePicker;
    Button vSave;
    TextView vNormalText;
    TextView vCodeText;
    String[] eng = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    final int engSize = 25;
    String[] ukr = {"а", "б", "в", "г", "д", "е", "є", "ж", "з", "и", "і", "ї", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т"
            , "у", "ф", "х", "ц", "ч", "ш", "щ", "ь", "ю", "я"};
    final int ukrSize = 31;
    int currentLangSize = engSize;
    String[] currentAlp = eng;
    int slide = 0;
    String[] lang = {"eng", "ukr"};
    String[] action = {"code","encode","decode"};
    private final String eng_text = "eng_text.txt";
    private final String ukr_text = "ukr_text.txt";
    private final String eng_code_text = "eng_code_text.txt";
    private final String ukr_code_text = "ukr_code_text.txt";
    private String readFilename = eng_text;
    private String writeFilename = eng_code_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vSpinner = (Spinner) findViewById(R.id.action_spinner);
        vLang = (Spinner) findViewById((R.id.lang_spinner));
        vLang.setAdapter(createAdapter(lang));
        vSpinner.setAdapter(createAdapter(action));
        vLang.setSelection(0);
        vSpinner.setSelection(0);
        vLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentLangSize = engSize;
                        currentAlp = eng;
                        readFilename = eng_text;
                        writeFilename = eng_code_text;
                        break;
                    case 1:
                        currentLangSize = ukrSize;
                        currentAlp = ukr;
                        readFilename = ukr_text;
                        writeFilename = ukr_code_text;
                        break;
                }
                pickerInit();
                codeLang();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        vSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        readFilename = vLang.getSelectedItem().toString()+"_text.txt";
                        writeFilename=vLang.getSelectedItem().toString()+"_code_text.txt";

                        break;
                    case 1:
                        readFilename = vLang.getSelectedItem().toString()+"_code_text.txt";
                        writeFilename=vLang.getSelectedItem().toString()+"_text.txt";
                        break;
                    case 2:
                        readFilename = vLang.getSelectedItem().toString()+"_code_text.txt";
                        decode();
                        break;
                }
                codeLang();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        vSlide = (EditText) findViewById(R.id.slide);
        vNormalPicker = (NumberPicker) findViewById(R.id.normal_picker);
        vNumberPicker = (NumberPicker) findViewById(R.id.number_picker);
        vCodePicker = (NumberPicker) findViewById(R.id.code_picker);
        vSave = (Button) findViewById(R.id.file_chooser);
        vSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToFile(readFilename,vNormalText.getText().toString());
                writeToFile(writeFilename,vCodeText.getText().toString());
            }
        });
        vNormalText = (TextView) findViewById(R.id.normal_text);
        vCodeText = (TextView) findViewById(R.id.code_text);
        pickerInit();
        vNumberPicker.setEnabled(false);
        vCodePicker.setEnabled(false);
        vSlide.setVisibility(View.VISIBLE);
        vSlide.addTextChangedListener(this);
        vNormalPicker.setOnValueChangedListener(this);
        codeLang();
    }

    public void pickerInit() {
        vNormalPicker.setMinValue(0);
        vNumberPicker.setMinValue(0);
        vCodePicker.setMinValue(0);
        vNormalPicker.setDisplayedValues(null);
        vNumberPicker.setDisplayedValues(null);
        vCodePicker.setDisplayedValues(null);
        vNormalPicker.setMaxValue(currentLangSize);
        vNumberPicker.setMaxValue(currentLangSize);
        vCodePicker.setMaxValue(currentLangSize);
        vNormalPicker.setDisplayedValues(currentAlp);
        vCodePicker.setDisplayedValues(currentAlp);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            slide = Integer.parseInt(s.toString());
            changeSlide();
        } catch (NumberFormatException ex) {
            slide = 0;
        }
        codeLang();
    }

    public void changeSlide() {
        if (slide <= currentLangSize && slide >= 0) {
            vNumberPicker.setValue(slide);
            vCodePicker.setValue(slide);
        } else if (slide <= currentLangSize && slide < 0) {
            vNumberPicker.setValue(currentLangSize + 1 + slide);
            vCodePicker.setValue(currentLangSize + 1 + slide);
        }
        codeLang();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (slide <= currentLangSize) {
            vNumberPicker.setValue(newVal + slide);
            vCodePicker.setValue(newVal + slide);
        }
    }

    public void codeLang() {
        String normalText = null;
        normalText = getStringFromFile();
        vNormalText.setText(normalText);
        char[] text = normalText.toLowerCase().toCharArray();
        vCodeText.setText(codeText(text, slide));
    }
    public String codeText(char[] text, int shifting){
        String codeText = "";
        for (int i = 0; i < text.length; i++) {
            for (int j = 0; j < currentAlp.length; j++) {
                if (currentAlp[j].toCharArray()[0] == (text[i])) {
                    while (shifting < 0 && j < Math.abs(shifting)) {
                        j += currentLangSize;
                    }
                    int shift = j + shifting;
                    while (shift > currentLangSize) {
                        shift -= currentLangSize;
                    }
                    codeText += currentAlp[shift];
                }
            }
        }
        return  codeText;
    }
    public String getStringFromFile() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"/Notes/" + readFilename);
        if (!file.exists()) {
            switch (readFilename){
                case eng_text:
                    writeToFile(readFilename, getResources().getString(R.string.eng_text));
                    break;
                case eng_code_text:
                    writeToFile(readFilename, getResources().getString(R.string.eng_code_text));
                    break;
                case ukr_text:
                    writeToFile(readFilename, getResources().getString(R.string.ukr_text));
                    break;
                case ukr_code_text:
                    writeToFile(readFilename, getResources().getString(R.string.ukr_code_text));
                    break;
            }
        }
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    public void writeToFile(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved to "+ sFileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ArrayAdapter<String> createAdapter(String[] names){
        ArrayAdapter<String> actionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return actionAdapter;
    }
    public void decode() {
        char[] codeText = getStringFromFile().toCharArray();
        HashMap<Character, Double> frequency = new HashMap<Character, Double>();
        for (char c : codeText) {
            if (frequency.containsKey(c)) {
                frequency.put(c, frequency.get(c) + 1);
            } else {
                frequency.put(c, 1.0);
            }
        }
        for (char c : frequency.keySet()) {
            frequency.put(c, frequency.get(c) * 100 / codeText.length);
        }

        Intent intent = new Intent(this, ChartDialog.class);
        intent.putExtra("lang", vLang.getSelectedItem().toString());
        intent.putExtra("codeText", codeText);
        intent.putExtra("freq", frequency);
        startActivity(intent);
    }
}
