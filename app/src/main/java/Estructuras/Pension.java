package Estructuras;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Pension implements Serializable {
    final double FACTOR_FOX = 1.11;

    double salarioPromedioUMA;
    int semanasExtra;
    double aniosExtra;
    double cuantiaBasica;
    double incrementoAnual;
    double porcentajecuantiaBasica;
    double porcentajeincrementoAnual;
    double montTotaldiarioPension;
    double montMensualTOTALPension;
    double penalizacionEdad;
    double pensionConPenalizacion;
    double PENSION_FINAL;
    double montMensualPension;
    double ayudaconyugue;
    double ayudahijos;

    public double getMontMensualPension() {
        return montMensualPension;
    }

    public double getPensionConPenalizacion() {
        return pensionConPenalizacion;
    }

    public double getPENSION_FINAL() {
        return PENSION_FINAL;
    }

    public void setAsignacionesFamiliares(double ayudaconyugue, double ayudahijos ){
        this.ayudaconyugue = ayudaconyugue;
        this.ayudahijos = ayudahijos;
        this.PENSION_FINAL = pensionConPenalizacion + ayudaconyugue + ayudahijos;
    }

    public double getPenalizacionEdad() {
        return penalizacionEdad;
    }

    public void setPenalizacionEdad(double penalizacionEdad) {
        this.penalizacionEdad = penalizacionEdad;
        this.pensionConPenalizacion = montMensualTOTALPension * penalizacionEdad;
    }

    public double getMontTotaldiarioPension() {
        return montTotaldiarioPension;
    }

    public double getSalarioPromedioUMA() {
        return salarioPromedioUMA;
    }

    public void setSalarioPromedioUMA(double salarioPromedio, double UMA) {
        this.salarioPromedioUMA = salarioPromedio/UMA;
    }

    public int getSemanasExtra() {
        return semanasExtra;
    }

    public void setSemanasExtra(int semanas) {
        this.semanasExtra = semanas-500;
        double aniosExtrasinCot = (double) semanasExtra/52;
        double parteEntera = Math.floor(aniosExtrasinCot);
        double parteDecimal = aniosExtrasinCot - parteEntera;
        if (parteDecimal <= 0.25){
            parteDecimal = 0;
        }else{
            if (parteDecimal <= 0.5){
                parteDecimal = 0.5;
            }else{
                parteDecimal = 1;
            }
        }
        this.aniosExtra = parteEntera + parteDecimal;
    }

    public double getAniosExtra() {
        return aniosExtra;
    }

    public double getIncrementoAnual() {
        return incrementoAnual;
    }

    public double getCuantiaBasica() {
        return cuantiaBasica;
    }

    public void setCuantiaBasica(Context context, double SalarioPromedio) throws IOException {
        HashMap<Double, ArrayList<Double>> TablaCuantia = new HashMap<>();
        ArrayList<Double> llaves = new ArrayList<>();
        // Se lee el archivo con la tabla csv
        InputStream lector = context.getAssets().open("Tabla_cuantia_basica.csv");
        int size = lector.available();
        byte[] buffer = new byte[size];
        lector.read(buffer);
        lector.close();

        String contenido = new String(buffer);
        // Separa el contenido en saltos de linea
        StringTokenizer lineas = new StringTokenizer(contenido, "\n");
        while(lineas.hasMoreTokens()) {
            String line = lineas.nextToken();
            if(!line.isEmpty()){
                StringTokenizer columnas = new StringTokenizer(line, ",");
                double grupoSalarial = Double.parseDouble(columnas.nextToken());
                double cuantia = Double.parseDouble(columnas.nextToken());
                double incrementoanual = Double.parseDouble(columnas.nextToken());

                TablaCuantia.put(grupoSalarial,new ArrayList<Double>());
                TablaCuantia.get(grupoSalarial).add(cuantia);
                TablaCuantia.get(grupoSalarial).add(incrementoanual);
                llaves.add(grupoSalarial);
            }
        }
        double grupoSalarial = 0;

        for (double i: llaves) {
            if(this.salarioPromedioUMA <= i){
                grupoSalarial = i;
                break;
            }
        }
        
        this.porcentajecuantiaBasica = TablaCuantia.get(grupoSalarial).get(0)/100;
        this.porcentajeincrementoAnual = TablaCuantia.get(grupoSalarial).get(1);

        this.cuantiaBasica = SalarioPromedio * this.porcentajecuantiaBasica;
        this.incrementoAnual = ((this.aniosExtra * this.porcentajeincrementoAnual)/100)*SalarioPromedio;

        this.montTotaldiarioPension = this.cuantiaBasica + this.incrementoAnual;
        this.montMensualPension = ((montTotaldiarioPension*365)/12);
        this.montMensualTOTALPension = ((montTotaldiarioPension*365)/12)*FACTOR_FOX;
    }


    public double getPorcentajecuantiaBasica() {
        return porcentajecuantiaBasica;
    }

    public double getPorcentajeincrementoAnual() {
        return porcentajeincrementoAnual;
    }

    public double getMontMensualTOTALPension() {
        return montMensualTOTALPension;
    }

    public double getAyudaconyugue() {
        return ayudaconyugue;
    }

    public double getAyudahijos() {
        return ayudahijos;
    }
}
