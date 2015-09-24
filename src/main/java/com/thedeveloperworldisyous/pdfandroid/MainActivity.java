package com.thedeveloperworldisyous.pdfandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener{

    private static String sInPutfile = "Android/data/Example.pdf";
    private static String sOutPutFile = "Android/data/ExampleCopy.pdf";
    private File mPdfFile;
    private File mPdfFileOutPut;
    private ProgressDialog mProgressDialog;
    private Button mEdit;
    private Button mOpenOld;
    private Button mOpenNew;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            mProgressDialog = new ProgressDialog(this);
            // check if external storage is available so that we can dump our PDF
            // file there
            if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                Log.d("Main",
                        "External Storage not available or you don't have permission to write");

            } else {
                mPdfFile = new File(Environment.getExternalStorageDirectory(),
                        sInPutfile);
                mPdfFileOutPut = new File(Environment.getExternalStorageDirectory(),
                        sOutPutFile);

            }

            Button genterateButton = (Button) findViewById(R.id.button_generate_activity_main);
            mEdit = (Button) findViewById(R.id.button_edit_activity_main);
            mOpenOld = (Button) findViewById(R.id.button_open_old_activity_main);
            mOpenNew = (Button) findViewById(R.id.button_open_new_activity_main);

            genterateButton.setOnClickListener(this);
            mEdit.setOnClickListener(this);
            mOpenOld.setOnClickListener(this);
            mOpenNew.setOnClickListener(this);

        }

        @Override

        public void onClick(View v) {
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.show();

            switch (v.getId()) {

                case R.id.button_generate_activity_main:
                    generateAndwritePDF();
                    mEdit.setEnabled(true);
                    break;

                case R.id.button_edit_activity_main:

                    mOpenOld.setEnabled(true);
                    mOpenNew.setEnabled(true);
                    break;

                case R.id.button_open_old_activity_main:
                    openFile(mPdfFile);
                    break;

                case R.id.button_open_new_activity_main:
                    openFile(mPdfFileOutPut);
                    break;

                default:
                    break;

            }

        }

    public void openFile(File file) {

        if (file.exists()) {

            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {

                startActivity(intent);

            } catch (ActivityNotFoundException e) {

                Toast.makeText(MainActivity.this,
                        "No Application Available to View PDF",
                        Toast.LENGTH_SHORT).show();

            }

        }

        mProgressDialog.cancel();

    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }

        return false;
    }

    private static boolean isExternalStorageAvailable() {

        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;

        }

        return false;
    }

    private void stamperPDF(String string) {

        try {

            FileInputStream is = new FileInputStream(mPdfFile);
            PdfReader pdfReader = new PdfReader(is);
            PdfStamper pdfStamper = new PdfStamper(pdfReader,
                    new FileOutputStream(mPdfFileOutPut));

            for (int i = 1; i <= pdfReader.getNumberOfPages(); i++) {

                PdfContentByte content = pdfStamper.getUnderContent(i);

                // Text over the existing page
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA,
                        BaseFont.WINANSI, BaseFont.EMBEDDED);

                content.beginText();
                content.setFontAndSize(bf, 18);
                content.showTextAligned(PdfContentByte.ALIGN_LEFT, string, 430, 15, 0);
                content.endText();

            }

            pdfStamper.close();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (DocumentException e) {
            e.printStackTrace();

        }

        mProgressDialog.cancel();

    }

    private void generateAndwritePDF() {

// create a new document

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(mPdfFile));
            document.open();
            document.add(new Paragraph("hELLO word !!! tt adfafafda adfas"));
            document.close();

            mProgressDialog.cancel();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void editPdfWithAlert() {
        // custom dialog
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.setTitle("Title...");
        // set the custom dialog components - text, image and button
        final EditText text = (EditText) dialog.findViewById(R.id.edit_text_alert_dialog);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(

                MainActivity.this);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                stamperPDF(text.getText().toString());
            }

        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = alertDialogBuilder.show();
        dialog.show();

    }

}