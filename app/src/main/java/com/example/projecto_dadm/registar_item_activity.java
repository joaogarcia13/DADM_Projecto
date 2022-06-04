package com.example.projecto_dadm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class registar_item_activity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String FotoB64 = "";
    private String thumbnail = "";
    private Uri fileUri;
    private String nome;
    private String descricao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar_item);
        nome = findViewById(R.id.reg_item_nome_input).toString();

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
                try{
                    InputStream imageStream = getContentResolver().openInputStream(fileUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    FotoB64 = encodeImage(selectedImage);
                    Log.d("imagem:", FotoB64);
                }catch (Exception e) {
                    DynamicToast.makeError(registar_item_activity.this, "Ocorreu um erro ao obter a imagem. Por favor tente novamente.").show();
                    e.printStackTrace();
                }
            }
    }

    public void Tirar_Foto_regItem(View v){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        i.putExtra( MediaStore.EXTRA_OUTPUT, fileUri );
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }

    public void regItem_Confirmar(View v){
        Log.d("d3",nome);
    }

    public void regItem_Reset(View v){

    }

    public void regItem_menu(View v){

    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    //vai buscar o Uri da foto criado em getOutputMediaFile()
    private Uri getOutputMediaFileUri(){
        return FileProvider.getUriForFile(registar_item_activity.this, BuildConfig.APPLICATION_ID + ".provider",getOutputMediaFile());
    }

    //cria um ficheiro para guardar imagem
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("Error: ", "Nao foi possivel criar o directorio.");
                return null;
            }
        }
        // Cria um nome para a imagem
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        return mediaFile;
    }
}
