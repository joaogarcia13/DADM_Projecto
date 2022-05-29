package com.example.projecto_dadm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class registar_item_activity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap iconeImg;
    private String currentPhotoPath;
    private Image foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registar_item);

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
            Bundle extras = data.getExtras();
            iconeImg = (Bitmap) extras.get("data");
            //TODO ja tens o icon falta a imagem toda para ir para o parametro foto
        }
    }

    public void Tirar_Foto_regItem(View v){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            //startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("fds","caralho!");
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
        }catch (Exception e){
            DynamicToast.makeError(registar_item_activity.this, "Ocorreu um erro ao abrir a câmara, tente novamente.").show();
            Log.e("error:", String.valueOf(e));
        }
    }

    public void regItem_Confirmar(View v){

    }

    public void redItem_Reset(View v){

    }

    public void redItem_menu(View v){

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
