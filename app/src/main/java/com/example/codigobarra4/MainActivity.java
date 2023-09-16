package com.example.codigobarra4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 111;
    private Bitmap Imagenselecionada;
    private ImageView Imagen;
    private TextView Resultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
    }

    private void initializeViews() {
        Resultado = findViewById(R.id.Tx_Resul);
        Imagen = findViewById(R.id.Imag);
    }

    public void onCameraButtonClick(View view) {
        launchCamera();
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            handleImageCapture(requestCode, data);
        }
    }

    private void handleImageCapture(int requestCode, Intent data) {
        try {
            Imagenselecionada = requestCode == REQUEST_CAMERA ? (Bitmap) data.getExtras().get("data")
                    : MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            displayCapturedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayCapturedImage() {
        Imagen.setImageBitmap(Imagenselecionada);
    }

    public void onScanQRButtonClick(View v) {
        if (Imagenselecionada != null) {
            scanImageForQR();
        } else {
            Resultado.setText("No hay imagen");
        }
    }

    private void scanImageForQR() {
        InputImage image = InputImage.fromBitmap(Imagenselecionada, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient();
        processImage(scanner, image);
    }

    private void processImage(BarcodeScanner scanner, InputImage image) {
        scanner.process(image)
                .addOnSuccessListener(this::displayBarcodes)
                .addOnFailureListener(e -> Resultado.setText("Error al procesar imagen"));
    }

    private void displayBarcodes(List<Barcode> barcodes) {
        for (Barcode barcode : barcodes) {
            String value = barcode.getDisplayValue();
            Resultado.setText(value);
        }
    }
}