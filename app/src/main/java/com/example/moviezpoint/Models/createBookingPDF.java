package com.example.moviezpoint.Models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.moviezpoint.Activities.PaymentActivity;
import com.example.moviezpoint.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class createBookingPDF {
    Context context;
    Canvas canvas;
    Paint myPaint,titlePaint,movieTitlePaint,textPaint;
    Bitmap bitmapElse, scaledBitmap, logoBitmap, logoScaledBitmap,backgroundBitmap, bgScaledBitmap,qRBitmap,scaledQRBitmap;
    File folder;
    File file;
    PdfDocument document;
    PdfDocument.PageInfo myPageInfo;
    PdfDocument.Page myPage;
    String movieName,movieTheatre,date,time,movieTransactionID,userName,userEmail,amount,totalAmount,imagePath,overview,genre="",seats="";
    ArrayList<String> selectedSeats;
    int genreCount;
    public createBookingPDF(Context context) {
        this.context = context;
    }

    public boolean createPDF(MovieDetailsModel movieDetailsModel, String theatre, String showDate, String showTime, String amt, ArrayList<String> seatList, String transactionID, StoreFirebaseUser firebaseUser) {

        movieName = movieDetailsModel.getTitle();
        imagePath = movieDetailsModel.getPosterPath();
        overview = movieDetailsModel.getOverview();
        genreCount = movieDetailsModel.getGenres().size();
        movieTheatre = theatre;
        date = showDate;
        selectedSeats = seatList;
        time = showTime;
        totalAmount = amt;
        amount = "100";
        movieTransactionID = transactionID;
        userName = firebaseUser.getUserName();
        userEmail = firebaseUser.getUserEmail();

        int i;
        for (i=0;i<genreCount;i++) {
            if (i==(genreCount-1)) {
                genre = genre+movieDetailsModel.getGenres().get(i).getName();
            }
            else {
                genre = genre+movieDetailsModel.getGenres().get(i).getName()+" | ";
            }
        }


        document = new PdfDocument();
        myPaint = new Paint();
        titlePaint = new Paint();
        movieTitlePaint = new Paint();
        textPaint = new Paint();

        myPageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
        myPage = document.startPage(myPageInfo);
        canvas = myPage.getCanvas();

        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background_about);
        bgScaledBitmap = Bitmap.createScaledBitmap(backgroundBitmap,1200,450,false);
        canvas.drawBitmap(bgScaledBitmap,0,0,myPaint);
        logoBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.app_logo);
        logoScaledBitmap = Bitmap.createScaledBitmap(logoBitmap,300,300,false);
        canvas.drawBitmap(logoScaledBitmap,40,75,myPaint);

        titlePaint.setTextSize(70);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setColor(Color.WHITE);
        canvas.drawText("Moviez Point",1200/2,450/2,titlePaint);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(movieTransactionID, BarcodeFormat.QR_CODE,300,300);
            qRBitmap = Bitmap.createBitmap(300,300,Bitmap.Config.RGB_565);

            for (int x = 0;x<300;x++) {
                for (int y = 0;y<300;y++) {
                    qRBitmap.setPixel(x,y,bitMatrix.get(x,y)?Color.BLACK:Color.WHITE);
                }
            }

            scaledQRBitmap = Bitmap.createScaledBitmap(qRBitmap,300,300,false);
            canvas.drawBitmap(scaledQRBitmap,860,75,myPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }


        final String imageURL = URLs.IMAGE_BASE_URL+imagePath;

        getMoviePoster poster = new getMoviePoster(imageURL);
        poster.execute();

        return true;
    }

    public class getMoviePoster extends AsyncTask<String,Void,Bitmap> {
        String imageUrl;
        public getMoviePoster(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream stream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                Log.e("IMAGE_PATH",imageUrl);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("IMAGE_EXCEPTION",e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            furtherPdf(bitmap);
        }
    }

    private void furtherPdf(Bitmap bitmap) {
        if (imagePath==null) {
            bitmapElse = BitmapFactory.decodeResource(context.getResources(),R.drawable.no_poster);
            scaledBitmap = Bitmap.createScaledBitmap(bitmapElse,320,450,false);
            canvas.drawBitmap(scaledBitmap,20,470,myPaint);
        }
        else {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,320,450,false);
            canvas.drawBitmap(scaledBitmap,20,470,myPaint);
        }

        movieTitlePaint.setColor(Color.BLACK);
        movieTitlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        movieTitlePaint.setTextSize(60);
        canvas.drawText(movieName,370,550,movieTitlePaint);

        textPaint.setTextSize(40);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        textPaint.setColor(Color.BLACK);
        canvas.drawText(genre,370,600,textPaint);
        textPaint.reset();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        canvas.drawText("Name :",20,1000,textPaint);
        textPaint.reset();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        canvas.drawText(userName,160,1000,textPaint);
        textPaint.reset();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        canvas.drawText("Email :",20,1060,textPaint);
        textPaint.reset();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        canvas.drawText(userEmail,160,1060,textPaint);
        textPaint.reset();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        canvas.drawText("Transaction ID :",20,1120,textPaint);
        textPaint.reset();

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        canvas.drawText(movieTransactionID,320,1120,textPaint);
        textPaint.reset();

        titlePaint.setTextSize(60);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);
        canvas.drawText("Booking Details",1200/2,1240,titlePaint);

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);
        canvas.drawRect(20,1280,1180,1360,myPaint);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        canvas.drawText("Date",40, 1330, textPaint);
        canvas.drawText("Time",225,1330,textPaint);
        canvas.drawText("Location",400,1330,textPaint);
        canvas.drawText("Seats",800,1330,textPaint);
        canvas.drawText("Amount",1000,1330,textPaint);

        myPaint.setStyle(Paint.Style.FILL);
        myPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine(205,1290,205,1350,myPaint);
        canvas.drawLine(380,1290,380,1350,myPaint);
        canvas.drawLine(780,1290,780,1350,myPaint);
        canvas.drawLine(980,1290,980,1350,myPaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(30);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
        canvas.drawText(date,30,1450,textPaint);
        canvas.drawText(time,215,1450,textPaint);
        canvas.drawText(movieTheatre+", Latur",390,1450,textPaint);
        canvas.drawText(amount+"/seat",990,1450,textPaint);

        int total = selectedSeats.size();
        int index;
        int y=1450;
        for (index=0;index<total;index++) {
            if (index == 0) {
                canvas.drawText(selectedSeats.get(index).toString(),790,y,textPaint);
            }
            if (index == 1) {
                canvas.drawText(", "+selectedSeats.get(index).toString(),830,y,textPaint);
            }
            if (index == 2) {
                canvas.drawText(", "+selectedSeats.get(index).toString(),880,y,textPaint);
                y = y+50;
            }
            if (index%3==0&&index!=0) {
                canvas.drawText(selectedSeats.get(index).toString(),790,y,textPaint);
            }
            if (index%3==1&&index!=1) {
                canvas.drawText(", "+selectedSeats.get(index).toString(),830,y,textPaint);
            }
            if (index%3==2&&index!=2) {
                canvas.drawText(", "+selectedSeats.get(index).toString(),880,y,textPaint);
                y = y+50;
            }
        }

        canvas.drawLine(780,1700,1180,1700,myPaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        textPaint.setTextSize(30);
        canvas.drawText("Total :",790,1750,textPaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD_ITALIC));
        textPaint.setTextSize(30);
        canvas.drawText(totalAmount,990,1750,textPaint);

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String bookingDate = dateFormat.format(date);

        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        canvas.drawText("Date : "+bookingDate,20,1810,textPaint);
        textPaint.reset();

        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String bookingTime = timeFormat.format(time);

        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        canvas.drawText("Time : "+bookingTime,20,1850,textPaint);
        textPaint.reset();

        Bitmap footerBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.background_about);
        Bitmap scaledFooterBitmap = Bitmap.createScaledBitmap(footerBitmap,1200,30,false);
        canvas.drawBitmap(scaledFooterBitmap,0,1980,myPaint);

        document.finishPage(myPage);
        folder = new File(Environment.getExternalStorageDirectory()+File.separator+"Moviez Point"+File.separator+"Movie Tickets");
        boolean success = true;
        Date saveDate = new Date();
        DateFormat saveDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        String dateSaved = saveDateFormat.format(saveDate);
        String fileName = movieName+" Tickets_"+dateSaved+".pdf";
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            file = new File(folder,"/"+fileName);
            try {
                document.writeTo(new FileOutputStream(file));
                String path = folder.getPath();
                Toast.makeText(context, "Your Ticket is stored to "+path, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri,"application/pdf");
                context.startActivity(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}