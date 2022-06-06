package com.example.projecto_dadm;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class registar_item_activity extends AppCompatActivity implements LocationListener{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_PERMISSION_CODE = 2;

    private String FotoB64 = "";
    private Uri fileUri;
    private EditText nome;
    private EditText descricao;
    private Location location;
    private double latitude;
    private double longitude;
    private LocationListener listener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Bitmap selectedImage;
    private Geocoder geocoder;
    private List<Address> moradas;
    private String morada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar_item);
        nome = findViewById(R.id.reg_item_nome_input);
        descricao = findViewById(R.id.reg_item_descricao_input);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    @Override
    public void onBackPressed() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //converte a imagem que esta no caminho getOutputMediaFile() num bitmap e converte para Base64
            try {
                InputStream imageStream = getContentResolver().openInputStream(fileUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                FotoB64 = encodeImage(selectedImage);
            } catch (Exception e) {
                DynamicToast.makeError(registar_item_activity.this, "Ocorreu um erro ao obter a imagem. Por favor tente novamente.").show();
                e.printStackTrace();
            }
        }
    }

    public void Tirar_Foto_regItem(View v) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    public void regItem_Confirmar(View v) {
        //nao funciona
        //getLocation();
        Log.d("dqwdqw", String.valueOf(latitude));
        Log.d("dqwdqw", String.valueOf(longitude));

        if (nome.getText().toString().equals("") || descricao.getText().toString().equals("") || FotoB64.equals("")){
            DynamicToast.makeError(registar_item_activity.this, "Por favor preencha todos os campos").show();
        }else if (latitude == 0 && longitude == 0){
            DynamicToast.makeError(registar_item_activity.this, "Por favor ligue o gps").show();
        }else {
            try {
                moradas = geocoder.getFromLocation(latitude, longitude, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (moradas != null && moradas.size() > 0) {
                morada = moradas.get(0).getAddressLine(0) + ", " + moradas.get(0).getLocality() + ", " + moradas.get(0).getCountryName();
            } else {
                morada = "Localizaçao indisponivél";
            }
            ImageView img = new ImageView(this);
            img.setImageBitmap(resizeBitmap(selectedImage, 500));
            img.setPadding(0, 0, 0, 10);
            new AlertDialog.Builder(this)
                    .setTitle("Confirme os dados por favor")
                    .setMessage("\nNome:\n " + nome.getText().toString() + "\n\nLocalização:\n " + morada + "\n\nDescricao:\n " + descricao.getText().toString() + "\n\n")
                    .setView(img)
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("Items").push().setValue(new Item(nome.getText().toString(), descricao.getText().toString(), latitude, longitude, FotoB64, user.getUid()));
                            DynamicToast.makeSuccess(registar_item_activity.this, "Item registado com sucesso.").show();
                            updateUI();
                        }
                    })
                    .setNegativeButton("Cancelar", null).show();
        }
    }

    public void regItem_Reset(View v) {
        FotoB64 = "";
        nome.setText("");
        descricao.setText("");
    }

    public void regItem_menu(View v) {
        updateUI();
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    //vai buscar o Uri da foto criado em getOutputMediaFile()
    private Uri getOutputMediaFileUri() {
        return FileProvider.getUriForFile(registar_item_activity.this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile());
    }

    //cria um ficheiro para guardar imagem
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Error: ", "Nao foi possivel criar o directorio.");
                return null;
            }
        }
        // Cria um nome para a imagem
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    private void getLocation(){
        //ve se o gps esta ligado
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPSEnabled && !isNetworkEnabled) {
            DynamicToast.makeError(registar_item_activity.this, "Por favor ative o GPS ou a ligação à internet antes de continuar.").show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                DynamicToast.makeError(registar_item_activity.this, "Esta aplicação necessita de aceder à sua Localização para continuar. " +
                        "Por favor ative o acesso da aplicação á sua localização nas definições do seu dispositivo ", 100000).show();
                ActivityCompat.requestPermissions(registar_item_activity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, new LocationListener(){
                    // @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                    // @Override
                    public void onLocationChanged(Location location) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });
                Log.d("lat:", String.valueOf(location.getLatitude()));
                Log.d("long:", String.valueOf(location.getLongitude()));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderEnabled(String provider) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                return result;
            } else {
                int targetWidth = maxLength;

                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                return result;

            }
        }
        catch (Exception e)
        {
            return source;
        }
    }
    private void updateUI() {
        Intent switchActivityIntent = new Intent(this, menu_activity.class);
        startActivity(switchActivityIntent);
        finish();
    }

}
