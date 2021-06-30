package com.chrisley.LM_calculadorpensiones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Estructuras.Pensionado;
import Estructuras.RecyclerItem;

public class ResultadoPensionExpress extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<RecyclerItem> elements;
    Pensionado pensionado;
    Button botonMod40;
    NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    Boolean fromHistory;
    ImageView shareButton;
    private final int REQUEST_PERMISSION_STORAGE = 1;
    RecyclerView recyclerViewPension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_pension_express);
        pensionado = (Pensionado) getIntent().getExtras().get("pensionado");
        fromHistory = getIntent().getBooleanExtra("historial", false);

        ImageButton backButton = findViewById(R.id.BackButtonCalculosResPen);
        backButton.setOnClickListener(v -> finish());

        ImageView logoLM = findViewById(R.id.LogoLM);
        logoLM.setOnClickListener(v -> RegresarMenuPrin());

        botonMod40 = findViewById(R.id.BotonCalcularMod40);

        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(v-> screenshot());

        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"Error al Leer el la tabla de cuantia",Toast.LENGTH_LONG).show();
        }

        botonMod40.setOnClickListener(v-> ChangeActivity());
    }

    void RegresarMenuPrin(){
        // Se redirige a la actividad de Menu principal
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Regresar al menu principal");
        builder.setMessage("¿Estas seguro que quieres regresar al menu principal?");
        // Boton para cerrar sesion
        builder.setPositiveButton("Regresar al menu principal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void init() throws IOException {

        String nombreCompleto = pensionado.getNombre() + " " + pensionado.getApellidos();


        // Se le da valor a cada atributo de la clase Pension
        pensionado.getPension().setSalarioPromedioUMA(pensionado.getSBC(),pensionado.getUMA_pensionado());
        pensionado.getPension().setSemanasExtra(pensionado.getSemanasCotizadas());
        pensionado.getPension().setCuantiaBasica(ResultadoPensionExpress.this, pensionado.getSBC());
        double penalizacion = 0.75;
        for (int i = 60; i < 66; i++) {
            if(pensionado.getEdadJubilacion() == i){
                pensionado.getPension().setPenalizacionEdad(penalizacion);
            }else{
                penalizacion+= 0.05;
            }
        }

        if(pensionado.isConyuge()){
            // Espos@ (+15%) y hijos si tiene (+10% c/u)
            double plusConyuge = (pensionado.getPension().getPensionConPenalizacion()) *0.15;
            double plusHijos = ((pensionado.getPension().getPensionConPenalizacion()) *0.10)*pensionado.getHijos();
            pensionado.getPension().setAsignacionesFamiliares(plusConyuge,plusHijos);
        }else{
            if (pensionado.getHijos() != 0){
                // Solo lo de los hijos (+10% c/u)
                double plusHijos = ((pensionado.getPension().getPensionConPenalizacion()) *0.10)*pensionado.getHijos();
                pensionado.getPension().setAsignacionesFamiliares(0.00,plusHijos);

            }else{
                // Ayuda existencial (+15%) + padres (+10% c/u)
                double plusConyuge = (pensionado.getPension().getPensionConPenalizacion()) *0.15;
                double pluspadres = ((pensionado.getPension().getPensionConPenalizacion()) *0.10)*pensionado.getPadres();
                pensionado.getPension().setAsignacionesFamiliares(plusConyuge,pluspadres);
            }
        }



        elements = new ArrayList<>();
        // Nombre de la persona a pensionar
        elements.add(new RecyclerItem(nombreCompleto,"Nombre del asegurado:",R.drawable.ic_assignment_ind_black_24dp));
        // Salario Promedio ingresado SBC round(pensionado.getSBC(),2)
        String text = formatoMoneda.format(pensionado.getSBC()) + " ó " +  round(pensionado.getPension().getSalarioPromedioUMA(),2) + " UMA";
        elements.add(new RecyclerItem(text,"S.B.C. promedio:",R.drawable.ic_money));
        // Semanas cotizadas ingresadas
        text = pensionado.getSemanasCotizadas() + " semanas";
        elements.add(new RecyclerItem(text,"Semanas cotizadas:",R.drawable.ic_date_range_black_24dp));
        // Semanas extra
        text = pensionado.getPension().getSemanasExtra() + " semanas ó " + pensionado.getPension().getAniosExtra() + " años";
        elements.add(new RecyclerItem(text,"Semanas extra:",R.drawable.ic_more_time_black_24dp));
        //Cuantía basica
        text = formatoMoneda.format(pensionado.getPension().getCuantiaBasica()) + " (" + pensionado.getPension().getPorcentajecuantiaBasica()*100 + "%)";
        elements.add(new RecyclerItem(text,"Cuantía basica:",R.drawable.ic_request_quote_black_24dp));
        // Incremento anual
        text =  formatoMoneda.format(pensionado.getPension().getIncrementoAnual()) + " (" + pensionado.getPension().getPorcentajeincrementoAnual() + "%)";
        elements.add(new RecyclerItem(text,"Incremento anual:",R.drawable.ic_price_change_black_24dp));
        // Monto total diario ( Sin contar extras etc.)
        text = formatoMoneda.format(pensionado.getPension().getMontTotaldiarioPension());
        elements.add(new RecyclerItem(text,"Monto total diario pension (sin % cesantía, vejez y extras):",R.drawable.ic_attach_money_black_24dp));
        // Monto mensual ( Sin contar extras etc.) SIN FACTOR FOX
        text = formatoMoneda.format(pensionado.getPension().getMontMensualPension());
        elements.add(new RecyclerItem(text,"Monto mensual pension (sin % cesantía, vejez y extras):",R.drawable.ic_payments_black_24dp));
        // Monto mensual ( Sin contar extras etc.) CON FACTOR FOX
        text = formatoMoneda.format(pensionado.getPension().getMontMensualTOTALPension());
        elements.add(new RecyclerItem(text,"Monto mensual pensión (Con incr. por decreto en 2001 FOX +11%):",R.drawable.ic_payments_black_24dp));
        // Porcentaje de penalizacion edad
        text = Math.floor(pensionado.getPension().getPenalizacionEdad()*100) + "% (" + pensionado.getEdadJubilacion() + " años)";
        elements.add(new RecyclerItem(text,"Porcentaje de cesantía o vejez:",R.drawable.ic_walking_stick));
        // Pension con penalizacion
        text = formatoMoneda.format(pensionado.getPension().getPensionConPenalizacion());
        elements.add(new RecyclerItem(text,"Monto mensual con % cesantía y vejez:",R.drawable.ic_payments_black_24dp));
        // Asignacion familiar
        if(pensionado.isConyuge()){
            // Espos@ (+15%) y hijos si tiene (+10% c/u)
            text = "+"+formatoMoneda.format(pensionado.getPension().getAyudaconyugue());
            elements.add(new RecyclerItem(text,"Asignacion conyugé (+15%)",R.drawable.ic_person_add_black_24dp));
            if (pensionado.getHijos() != 0){
                text = "+"+formatoMoneda.format(pensionado.getPension().getAyudahijos());
                elements.add(new RecyclerItem(text,"Asignacion "+ pensionado.getHijos() +" hijo(s) (+10% c/u)",R.drawable.ic_group_add_black_24dp));
            }

        }else{
            if (pensionado.getHijos() != 0){
                // Solo lo de los hijos (+10% c/u)
                text = "+"+formatoMoneda.format(pensionado.getPension().getAyudahijos());
                elements.add(new RecyclerItem(text,"Asignacion "+ pensionado.getHijos() +" hijo(s) (+10% c/u)",R.drawable.ic_group_add_black_24dp));

            }else{
                // Ayuda existencial (+15%) + padres (+10% c/u)
                text = "+"+formatoMoneda.format(pensionado.getPension().getAyudaconyugue());
                elements.add(new RecyclerItem(text,"Ayuda existencial (+15%)",R.drawable.ic_person_add_black_24dp));
                if (pensionado.getPadres() != 0){
                    text = "+"+formatoMoneda.format(pensionado.getPension().getAyudahijos());
                    elements.add(new RecyclerItem(text,pensionado.getPadres() + " ascendiente(s) (+10% c/u)",R.drawable.ic_group_add_black_24dp));
                }
            }
        }
        // pension final aproximada
        text = formatoMoneda.format(pensionado.getPension().getPENSION_FINAL());
        elements.add(new RecyclerItem(text,"Pension mensual total:",R.drawable.ic_paid_black_24dp));
        double ISR = (pensionado.getUMA_pensionado()*15)*30.4;
        if(pensionado.getPension().getPENSION_FINAL()>=ISR){
            // Si paga ISR
            text = "Sí PAGA ISR\n(El salario es mayor o igual a 15 UMA ("+formatoMoneda.format(ISR)+")";
        }else{
            // Excento
            text = "EXCENTO\n(El salario es menor a 15 UMA ("+formatoMoneda.format(ISR)+")";

        }
        elements.add(new RecyclerItem(text,"Retención de impuestos (ISR):",R.drawable.ic_account_balance_black_24dp));


        ListAdapterRes listAdapterRes = new ListAdapterRes(elements,this);
        recyclerViewPension = findViewById(R.id.recyclerViewResPensionexpress);
        recyclerViewPension.setHasFixedSize(true);
        recyclerViewPension.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPension.setAdapter(listAdapterRes);

        if(!fromHistory){
            savePensionadoOnDB();
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void ChangeActivity(){
        Intent intent = new Intent(ResultadoPensionExpress.this, ResultadoExpressMod40.class);
        intent.putExtra("pensionado",pensionado);
        startActivity(intent);
    }

    private void savePensionadoOnDB(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email_logged = prefs.getString("email",null);
        pensionado.setPENSION_FINAL(pensionado.getPension().getPENSION_FINAL());
        if(email_logged != null){
            Map<String, Object> PensionadoMap = new HashMap<>();

            PensionadoMap.put("nombre",pensionado.getNombre());
            PensionadoMap.put("apellidos",pensionado.getApellidos());
            PensionadoMap.put("SBC",pensionado.getSBC());
            PensionadoMap.put("UMA_pensionado",pensionado.getUMA_pensionado());
            PensionadoMap.put("edadJubilacion",pensionado.getEdadJubilacion());
            PensionadoMap.put("conyuge",pensionado.isConyuge());
            PensionadoMap.put("hijos",pensionado.getHijos());
            PensionadoMap.put("padres",pensionado.getPadres());
            PensionadoMap.put("semanasCotizadas",pensionado.getSemanasCotizadas());
            PensionadoMap.put("PENSION_FINAL",pensionado.getPENSION_FINAL());

            db.collection("Datos_Calculos_Pension_" + email_logged).document().set(PensionadoMap);
        }
    }

    private void screenshot(){
        // Se piden los permisos al usuario para poder escribir/leer alamcenamiento para guardar el screenshot
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);

        Bitmap screenShotBitmap = getScreenshotFromRecyclerView(recyclerViewPension);

        String filePathDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CalculosPensionLM" ;
        File dir = new File(filePathDir);
        dir.mkdirs();

        String filename = Calendar.getInstance().getTime().toString() + ".jpg";
        File outFile = new File(dir,filename);

        String bitmappath = MediaStore.Images.Media.insertImage(getContentResolver(),screenShotBitmap,"title","");

        Uri uri = Uri.parse(bitmappath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Este es el resultado de mi calculo de pensión!, con una pensión final de " + formatoMoneda.format(pensionado.getPENSION_FINAL()));
        startActivity(Intent.createChooser(intent, "Compartir"));

//         Save on Storage
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFile);
            screenShotBitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(),"Se guardo la imagen", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // Code of Prasham took from stackoverflow
    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }
//                holder.itemView.setDrawingCacheEnabled(false);
//                holder.itemView.destroyDrawingCache();
                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }

        }
        return bigBitmap;
    }

}