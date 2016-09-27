package com.example.note.cesar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;


/**
 * Created by NOTE on 15.09.2016.
 */
public class ChartDialog extends AppCompatActivity {
    String[] eng = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o",
            "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    final int engSize = 25;
    String[] ukr = {"а", "б", "в", "г", "д", "е", "є", "ж", "з", "и", "і", "ї", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т"
            , "у", "ф", "х", "ц", "ч", "ш", "щ", "ь", "ю", "я"};
    final int ukrSize = 31;
    private String[] currentAlp;
    private int currentSize;
    ColumnChartView chart;
    private TextView vDecodeText;
    private Button vButton;
    private HashMap engFreq = new HashMap();
    private HashMap ukrFreq = new HashMap();
    private static HashMap<Character, Double> currentFreq = new HashMap();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_layout);
        Intent intent = getIntent();
        currentFreq  = (HashMap<Character, Double>) intent.getSerializableExtra("freq");
        int i = 0;
        vDecodeText = (TextView) findViewById(R.id.decode_text);
        vButton = (Button) findViewById(R.id.next);
        chart = (ColumnChartView) findViewById(R.id.chart);
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        List<AxisValue> x = new ArrayList<>();
        List<AxisValue> y = new ArrayList<>();
        if (intent != null) {
            if (intent.getStringExtra("lang").equals("eng")) {
                currentAlp = eng;
                currentSize = engSize;
            } else {
                currentAlp = ukr;
                currentSize = ukrSize;
            }
            //columns.add(new Column());
            for (Character c : currentFreq.keySet()) {
                for (String letter : currentAlp) {
                    if (c.charValue() ==letter.toCharArray()[0]) {
                        values = new ArrayList<SubcolumnValue>();
                        SubcolumnValue sV = new SubcolumnValue(Float.parseFloat(currentFreq.get(c)+""));
                        sV.setLabel(String.format("%(.2f",currentFreq.get(c)));
                        values.add(sV);
                        columns.add(new Column(values).setHasLabels(true));
                        x.add(new AxisValue(i).setLabel(""+c));
                        y.add(new AxisValue(i).setLabel(String.valueOf(i)));
                    }
                }
                i++;
            }
        }
        Axis xAxis = new Axis(x).setHasLines(true);
        Axis yAxis = new Axis(y);
        ColumnChartData d = new ColumnChartData(columns);
        final Viewport port = new Viewport(chart.getMaximumViewport());
        port.bottom = 0;
        port.top = 100;
        chart.setMaximumViewport(port);
        chart.setCurrentViewport(port);
        chart.setColumnChartData(d);
        chart.getChartData().setAxisYLeft(yAxis);
        chart.getChartData().setAxisXBottom(xAxis);
        /*initFreq();
        frequencyDecoding(0);*/
        dec();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
    public String codeText(char[] text, int shifting){
        String codeText = "";
        char[] copy = text;
        for (int i = 0; i < copy.length; i++) {
            for (int j = 0; j < currentAlp.length; j++) {
                if (currentAlp[j].toCharArray()[0] == (copy[i])) {
                    while (shifting < 0 && j < Math.abs(shifting)) {
                        j += currentSize+1;
                    }
                    int shift = j + shifting;
                    while (shift > currentSize) {
                        shift -= currentSize;
                    }
                    codeText += currentAlp[shift];
                }
            }
        }
        codeText += '\n' + "{Entropy = " + getEntropy(codeText) +" }";
        return  codeText+'\n';
    }
    public void dec(){
        char[] text = getIntent().getCharArrayExtra("codeText");
        for (int i =0; i<currentSize;i++){
            vDecodeText.append(codeText(text,i));
        }
    }
    public void frequencyDecoding(int v) {
        char[] text = getIntent().getCharArrayExtra("codeText");
        Iterator<Character> iterator = currentFreq.keySet().iterator();
        Set ignore = new HashSet();
        while (iterator.hasNext()) {
            char c = iterator.next().charValue();
            for (int j = 0; j < text.length;j++) {
                if (text[j] == c) {
                    text[j] = Character.toUpperCase((Character) engFreq.keySet().toArray()[v]);
                    ignore.add(text[j]);
                }
            }
            v++;
        }
        vDecodeText.setText(String.valueOf(text).toLowerCase());
    }

    public void initFreq() {
        //  in %                      in 1.000
        engFreq.put('e', 12.7); ukrFreq.put('а', 0.072);
        engFreq.put('t', 9.06); ukrFreq.put('б', 0.017);
        engFreq.put('a', 8.17); ukrFreq.put('в', 0.052);
        engFreq.put('o', 7.51); ukrFreq.put('г', 0.016);
        engFreq.put('n', 6.75); ukrFreq.put('д', 0.035);
        engFreq.put('s', 6.33); ukrFreq.put('е', 0.017);
        engFreq.put('h', 6.09); ukrFreq.put('є', 0.008);
        engFreq.put('r', 5.99); ukrFreq.put('ж', 0.009);
        engFreq.put('d', 4.25); ukrFreq.put('з', 0.023);
        engFreq.put('l', 4.03); ukrFreq.put('и', 0.061);
        engFreq.put('c', 2.78); ukrFreq.put('і', 0.057);
        engFreq.put('u', 2.76); ukrFreq.put('ї', 0.006);
        engFreq.put('m', 2.41); ukrFreq.put('й', 0.008);
        engFreq.put('w', 2.36); ukrFreq.put('к', 0.035);
        engFreq.put('f', 2.23); ukrFreq.put('л', 0.036);
        engFreq.put('g', 2.02); ukrFreq.put('м', 0.031);
        engFreq.put('y', 1.97); ukrFreq.put('н', 0.065);
        engFreq.put('p', 1.93); ukrFreq.put('о', 0.094);
        engFreq.put('b', 1.49); ukrFreq.put('п', 0.029);
        engFreq.put('v', 0.98); ukrFreq.put('р', 0.047);
        engFreq.put('k', 0.77); ukrFreq.put('с', 0.041);
        engFreq.put('x', 0.15); ukrFreq.put('т', 0.055);
        engFreq.put('j', 0.15); ukrFreq.put('у', 0.04);
        engFreq.put('q', 0.1);  ukrFreq.put('ф', 0.001);
        engFreq.put('z', 0.05); ukrFreq.put('х', 0.012);
                                ukrFreq.put('ц', 0.006);
                                ukrFreq.put('ч', 0.018);
                                ukrFreq.put('ш', 0.012);
                                ukrFreq.put('щ', 0.001);
                                ukrFreq.put('ю', 0.004);
                                ukrFreq.put('я', 0.029);
                                ukrFreq.put(',', 0.017);
                                ukrFreq.put(',', 0.17);
        engFreq = (HashMap) sortByValue(engFreq);
        ukrFreq = (HashMap) sortByValue(ukrFreq);
        currentFreq = (HashMap) sortByValue(currentFreq);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
    public double getEntropy(String str) {
        double[] freq = {0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228,
                0.02015, 0.06094, 0.06966, 0.00153, 0.00772, 0.04025,
                0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
                0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150,
                0.01974,0.00074};

        double res = 0;
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if ('a' <= ch && ch <= 'z')
                res += -Math.log(freq[ch - 'a']);
            else if ('A' <= ch && ch <= 'Z')
                res += -Math.log(freq[ch - 'A']);
            // We don't need to do anything for other characters
        }
        return res;
    }
}
