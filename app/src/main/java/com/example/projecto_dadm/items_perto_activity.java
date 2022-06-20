package com.example.projecto_dadm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

//TODO por radio button para selecionar raio
public class items_perto_activity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private RecyclerView rv;
    private LinearLayoutManager llm;
    private Geocoder geocoder;
    private List<Address> moradas;
    private List<String> dataset;
    private double currlat;
    private double currlong;
    private Spinner spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_perto);
        mDatabase = FirebaseDatabase.getInstance().getReference("Items");
        rv = (RecyclerView) findViewById(R.id.item_perto_recicle);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        geocoder = new Geocoder(this, Locale.getDefault());
        dataset = new ArrayList<String>();
        spin = findViewById(R.id.dropdown_item_perto);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.kms, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                getResult();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
        //getResult();
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void itemPertoMenu(View v) {
        updateUI();
    }

    private void updateUI() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    public void getResult() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<List<String>> dataset = new ArrayList<>();
                List<List<Double>> cordenadas = new ArrayList<>();
                MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(findViewById(R.id.item_perto_recicle).getContext(), dataset);
                rv.setLayoutManager(llm);
                rv.setAdapter(adapter);
                for (DataSnapshot nomeSnapshot : dataSnapshot.getChildren()) {
                    List<String> dados = new ArrayList<String>();
                    List<Double> cord = new ArrayList<Double>();
                    List<String> listaPerto = new ArrayList<String>();
                    getCurrLocation();
                    float[] result = new float[1];
                    Location.distanceBetween (currlat, currlong, nomeSnapshot.child("latitude").getValue(double.class), nomeSnapshot.child("longitude").getValue(double.class), result);
                    String temp = spin.getSelectedItem().toString();
                    String[] numeroKm = temp.split(" ");
                    if (nomeSnapshot.child("ativo").getValue(Boolean.class) == true && result[0] <= Integer.parseInt(numeroKm[0]) * 1000) {
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
                    }
                    adapter.notifyItemInserted(0);
                }
                adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        new AlertDialog.Builder(items_perto_activity.this)
                                .setTitle("Pretende abrir Mapas ?")
                                .setMessage("Ao premir Confirmar irá abrir o Maps com o destino:\n\n " + dataset.get(position).get(2) + ".")
                                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        getCurrLocation();
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
    }

    public void getCurrLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(items_perto_activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(items_perto_activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(items_perto_activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            getResult();
        }else {
            manager.requestLocationUpdates("gps", 10, 10, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                }
            });

            Location local = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (local != null) {
                currlat = local.getLatitude();
                currlong = local.getLongitude();
                if (currlat == 0 && currlong == 0) {
                    local = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    currlat = local.getLatitude();
                    currlong = local.getLongitude();
                }
            }
        }
    }
}
