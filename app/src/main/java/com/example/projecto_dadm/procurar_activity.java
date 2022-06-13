package com.example.projecto_dadm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class procurar_activity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private SearchView search;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private Geocoder geocoder;
    private List<Address> moradas;
    private List<String> dataset;
    private double currlat;
    private double currlong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procurar);
        mDatabase = FirebaseDatabase.getInstance().getReference("Items");
        search = findViewById(R.id.procurar_pesquisar);
        rv = (RecyclerView) findViewById(R.id.procurar_result);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        geocoder = new Geocoder(this, Locale.getDefault());
        dataset = new ArrayList<String>();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<List<String>> dataset = new ArrayList<>();
                        List<List<Double>> cordenadas = new ArrayList<>();
                        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(findViewById(R.id.procurar_result).getContext(), dataset);
                        rv.setLayoutManager(llm);
                        rv.setAdapter(adapter);
                        for (DataSnapshot nomeSnapshot : dataSnapshot.getChildren()) {
                            List<String> dados = new ArrayList<String>();
                            List<Double> cord = new ArrayList<Double>();
                            String temp = nomeSnapshot.child("nome").getValue(String.class);
                            String[] palavras = temp.split("\\W");
                            int i = 0;
                            while (i < palavras.length) {
                                if (findSimilarity(palavras[i], newText) > 0.4 && nomeSnapshot.child("ativo").getValue(Boolean.class) == true) {
                                    dados.add(nomeSnapshot.child("nome").getValue(String.class));
                                    dados.add(nomeSnapshot.child("descricao").getValue(String.class));
                                    try {
                                        moradas = geocoder.getFromLocation(nomeSnapshot.child("latitude").getValue(double.class), nomeSnapshot.child("longitude").getValue(double.class), 5);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (moradas != null && moradas.size() > 0) {
                                        dados.add(moradas.get(0).getAddressLine(0));
                                    } else {
                                        dados.add("Localizaçao indisponivél");
                                    }
                                    dados.add(nomeSnapshot.child("foto").getValue(String.class));
                                    dataset.add(dados);
                                    cord.add(nomeSnapshot.child("latitude").getValue(double.class));
                                    cord.add(nomeSnapshot.child("longitude").getValue(double.class));
                                    cordenadas.add(cord);
                                    break;
                                }
                                i++;
                            }
                        }
                        adapter.notifyItemInserted(0);
                        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                new AlertDialog.Builder(procurar_activity.this)
                                        .setTitle("Pretende abrir Mapas ?")
                                        .setMessage("Ao premir Confirmar irá abrir o Maps com o destino:\n\n " + dataset.get(position).get(2) + ".")
                                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                                if (ActivityCompat.checkSelfPermission(procurar_activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(procurar_activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                    DynamicToast.makeError(procurar_activity.this, "Esta aplicação necessita de aceder à sua Localização para continuar. " +
                                                            "Por favor ative o acesso da aplicação á sua localização nas definições do seu dispositivo ", 100000).show();
                                                    ActivityCompat.requestPermissions(procurar_activity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
                                                }
                                                manager.requestLocationUpdates("gps", 10, 10, new LocationListener() {
                                                    @Override
                                                    public void onLocationChanged(Location location) {

                                                        double lat = location.getLatitude();
                                                        double lon = location.getLongitude();
                                                    }
                                                });
                                                Location local = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                                if (local != null){
                                                    currlat = local.getLatitude();
                                                    currlong = local.getLongitude();
                                                    if(currlat == 0 && currlong == 0) {
                                                        local = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                                        currlat = local.getLatitude();
                                                        currlong = local.getLongitude();
                                                    }
                                                }
                                                System.out.println(cordenadas.get(position));
                                                String uri = "http://maps.google.com/maps?saddr=" + currlat + "," + currlong + "&daddr=" + cordenadas.get(position).get(0) + "," + cordenadas.get(position).get(1);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                intent.setPackage("com.google.android.apps.maps");
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("Cancelar", null).show();
                                //TODO adicionar o item selecionado à pagina do utilizador para ser mais facil marcar como recebido. Em alternativa os items podem ser removidos todos os dias a uma hora certa tipo 4h
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void procurarMenuBtn(View v) {
        updateUI();
    }

    private void updateUI() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    //calcula o numero minimo de operacoes necessarias para tornar uma string noutra
    //ver https://en.wikipedia.org/wiki/Levenshtein_distance e https://www.techiedelight.com/calculate-string-similarity-java/
    //---------------------------------------------------------------------------------------
    public static int getLevenshteinDistance(String X, String Y)
    {
        int m = X.length();
        int n = Y.length();

        int[][] T = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            T[i][0] = i;
        }
        for (int j = 1; j <= n; j++) {
            T[0][j] = j;
        }

        int cost;
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                cost = X.charAt(i - 1) == Y.charAt(j - 1) ? 0: 1;
                T[i][j] = Integer.min(Integer.min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                        T[i - 1][j - 1] + cost);
            }
        }

        return T[m][n];
    }

    public static double findSimilarity(String x, String y) {
        if (x == null || y == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        double maxLength = Double.max(x.length(), y.length());
        if (maxLength > 0) {
            // optionally ignore case if needed
            return (maxLength - getLevenshteinDistance(x, y)) / maxLength;
        }
        return 1.0;
    }
    //----------------------------------------------------------------------------------------------
}