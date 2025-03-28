package com.example.aula2teste;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class TelaIMC extends AppCompatActivity {
    private EditText Peso;

    private EditText Altura;

    private EditText Peso_Ideal;

    private EditText Imc;

    private EditText Interp;

    private SharedPreferences preferences;
    private static final String PREFS_NAME = "UltimoIMC";
    private static final String peso_salvo = "peso_salvo";
    private static final String imc_salvo = "imc_salvo";
    private static final String inter_salvo = "inter_salvo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //vincular variaveis aos id's especificos
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        setContentView(R.layout.telaimc);
        Peso = findViewById(R.id.peso);
        Altura = findViewById(R.id.altura);
        Button calc = findViewById(R.id.calcular);
        Button limpar = findViewById(R.id.limpar);
        Button saiba = findViewById(R.id.saiba_mais);
        Peso_Ideal = findViewById(R.id.peso_ideal);
        Imc = findViewById(R.id.imc);
        Interp = findViewById(R.id.interpretacao);

        //Recupera dados salvos
        float ultimoPeso = preferences.getFloat(peso_salvo,0);
        float ultimoImc = preferences.getFloat(imc_salvo,0);
        String ultimainterp = preferences.getString(inter_salvo,"");

        //apresenta os dados salvos (se tiver)
        if(ultimoPeso!=0) Peso_Ideal.setHint(String.valueOf(ultimoPeso));
        if(ultimoImc!=0) Imc.setHint(String.valueOf(ultimoImc));
        if(!ultimainterp.isEmpty()) Interp.setHint(ultimainterp);

        //Função ao apertar o botão de calcular
        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String PesoTexto = Peso.getText().toString();
                String AlturaTexto = Altura.getText().toString();
                double PesoDoublee;
                double AlturaDoublee;

                if (PesoTexto.isEmpty() || AlturaTexto.isEmpty()) {
                    mensagemErro("Campos Altura e/ou peso são obrigatórios");
                    return;
                }
                if(isnotvalid(PesoTexto)){
                    System.out.println("Peso inválido: " + PesoTexto);
                    mensagemErro("Valores de peso inválidos");
                    return;
                }
                if (isnotvalid(AlturaTexto)) {
                    System.out.println("ALtura inválidos: " + AlturaTexto);
                    mensagemErro("Valor de altura inválido");
                    return;
                }
                PesoDoublee = Double.parseDouble(PesoTexto);
                AlturaDoublee = Double.parseDouble(AlturaTexto);


                calcula(AlturaDoublee, PesoDoublee); //Chama a função que calcula as informações e as mostra
            }
        });

        //zera as informações ao apertar em Limpar
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                zeraValores();
            }
        });
        //chama a nova tela ao apertar em saiba mais
        saiba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelaIMC.this, saibamais.class);
                startActivity(intent);
            }
        });

    }


    private void mensagemErro(String mensagem){
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaIMC.this);
        builder.setMessage(mensagem);
        builder.setTitle("informações");
        builder.setPositiveButton("entendi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void calcula(Double AlturaDouble, Double PesoDouble){
        double Alturacm = AlturaDouble * 100;
        Double Peso_Ideal_novo = (Alturacm - 100) - ((Alturacm - PesoDouble) / 4) * (5.0/ 100);
        double imc = PesoDouble /(AlturaDouble * AlturaDouble);
        String Inter;

        if(imc < 20){
            Inter = "Peso Baixo"; //Baixo Peso
        }else if(imc <25){
            Inter = "Normal"; //Normal
        }else if(imc <=30){
            Inter = "Acima do peso"; //Acima do peso
        }else{
            Inter = "Obeso"; //Obeso
        }
        String imcFormatado = String.format("%.0f", imc);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(peso_salvo, Peso_Ideal_novo.floatValue());
        editor.putFloat(imc_salvo, (float) imc);
        editor.putString(inter_salvo, Inter);
        editor.apply();

        Imc.setHint(imcFormatado);
        Peso_Ideal.setHint(Peso_Ideal_novo.toString());
        Interp.setHint(Inter);


    }
    private void zeraValores(){
        //Limpa os dados antigos
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        //volta os textos ao original
        Imc.setHint("IMC");
        Peso_Ideal.setHint("Peso ideal");
        Interp.setHint("Interpretação");

        //zera os valores que foram colocados
        Peso.setText("");
        Altura.setText("");
    }
    private boolean isnotvalid(String valor){
        try {
            Double.parseDouble(valor);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
