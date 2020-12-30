package de.pbma.moa.createroomdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.pbma.moa.createroomdemo.RoomParticipant.ParticipantItem;
import de.pbma.moa.createroomdemo.RoomRoom.RoomItem;

public class PdfClass {
    final static String TAG = PdfClass.class.getCanonicalName();
    public final static int A4_HEIGHT = 846;//11.75in * 72
    public final static int A4_WIDTH = 594; //8.25in * 72
    private Context context;

    public PdfClass(Context context) {
        this.context = context;
    }

    public File createPdfRoomInfos(RoomItem item, Bitmap qrCode) {
        final int leftborder = 50;
        final int infosleftborder = 150;
        final int y_spacing = 20;

        Log.v(TAG, "createPdfRoomInfos()");
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        Log.v(TAG, "createPdfRoomInfos() draw Text");
        paint.setColor(Color.BLACK);
        canvas.drawText("Titel", leftborder, 2 * y_spacing, paint);
        canvas.drawText("Start", leftborder, 3 * y_spacing, paint);
        canvas.drawText("Ende", leftborder, 4 * y_spacing, paint);
        canvas.drawText("Extras", leftborder, 5 * y_spacing, paint);
        canvas.drawText("Ort", leftborder, 6 * y_spacing, paint);
        canvas.drawText("Adresse", leftborder, 7 * y_spacing, paint);
        canvas.drawText("Gastgeber", leftborder, 8 * y_spacing, paint);
        canvas.drawText("E-Mail", leftborder, 9 * y_spacing, paint);
        canvas.drawText("Telephon", leftborder, 10 * y_spacing, paint);
        canvas.drawText("NFC-Uri", leftborder, 11 * y_spacing, paint);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        paint.setColor(Color.GRAY);
        canvas.drawText(item.roomName, infosleftborder, 2 * y_spacing, paint);
        canvas.drawText(df.format(item.startTime), infosleftborder, 3 * y_spacing, paint);
        canvas.drawText(df.format(item.endTime), infosleftborder, 4 * y_spacing, paint);
        canvas.drawText(item.extra, infosleftborder, 5 * y_spacing, paint);
        canvas.drawText(item.place, infosleftborder, 6 * y_spacing, paint);
        canvas.drawText(item.address, infosleftborder, 7 * y_spacing, paint);
        canvas.drawText(item.host, infosleftborder, 8 * y_spacing, paint);
        canvas.drawText(item.eMail, infosleftborder, 9 * y_spacing, paint);
        canvas.drawText(item.phone, infosleftborder, 10 * y_spacing, paint);
        canvas.drawText(item.getUri(), infosleftborder, 11 * y_spacing, paint);

        Log.v(TAG, "createPdfRoomInfos() draw Bitmap");
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(qrCode, (A4_WIDTH - qrCode.getWidth()) / 2, 15 * y_spacing, paint);
        document.finishPage(page);

        File file = savePDF(document, "Room_" + item.id + ".pdf");
        document.close();
        return file;
    }

    public File createPdfParticipantInfos(ArrayList<ParticipantItem> list, RoomItem item) {
        final int margin = 50;
        final int infosLeftBorder = 150;
        final int y_spacing = 20;
        int pageNumber = 1;
        float currentY = margin;


        Log.v(TAG, "createPdfParticipantInfos()");
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNumber++).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        Log.v(TAG, "createPdfParticipantInfos() draw Text");
        paint.setColor(Color.BLACK);
        canvas.drawText("Titel", margin, currentY, paint);
        canvas.drawText("Start", margin, currentY + y_spacing, paint);
        canvas.drawText("Ende", margin, currentY + 2 * y_spacing, paint);
        canvas.drawText("Extras", margin, currentY + 3 * y_spacing, paint);
        canvas.drawText("Ort", margin, currentY + 4 * y_spacing, paint);
        canvas.drawText("Adresse", margin, currentY + 5 * y_spacing, paint);
        canvas.drawText("Gastgeber", margin, currentY + 6 * y_spacing, paint);
        canvas.drawText("E-Mail", margin, currentY + 7 * y_spacing, paint);
        canvas.drawText("Telephon", margin, currentY + 8 * y_spacing, paint);

        paint.setColor(Color.GRAY);
        canvas.drawText(item.roomName, infosLeftBorder, currentY, paint);
        canvas.drawText(df.format(item.startTime), infosLeftBorder, currentY + y_spacing, paint);
        canvas.drawText(df.format(item.endTime), infosLeftBorder, currentY + 2 * y_spacing, paint);
        canvas.drawText(item.extra, infosLeftBorder, currentY + 3 * y_spacing, paint);
        canvas.drawText(item.place, infosLeftBorder, currentY + 4 * y_spacing, paint);
        canvas.drawText(item.address, infosLeftBorder, currentY + 5 * y_spacing, paint);
        canvas.drawText(item.host, infosLeftBorder, currentY + 6 * y_spacing, paint);
        canvas.drawText(item.eMail, infosLeftBorder, currentY + 7 * y_spacing, paint);
        canvas.drawText(item.phone, infosLeftBorder, currentY + 8 * y_spacing, paint);

        currentY += 10 * y_spacing;

        final float zeilenAbstand = (float) ((paint.descent() - paint.ascent()) * 1.25);
        final float widthTime = paint.measureText("dd.MM.yyyy HH:mm");
        final float x_exitTime = A4_WIDTH - margin - widthTime;
        final float x_enterTime = A4_WIDTH - margin - 2 * widthTime;
        final float x_kontaktDaten = (A4_WIDTH - 2 * margin - 2 * widthTime) / 2 + margin;

        for (ParticipantItem ele : list) {
            canvas.drawText(ele.name, margin, currentY, paint);
            canvas.drawText(ele.extra, margin, (currentY + zeilenAbstand), paint);

            canvas.drawText(ele.eMail, x_kontaktDaten, currentY, paint);
            canvas.drawText(ele.phone, x_kontaktDaten, (currentY + zeilenAbstand), paint);

            canvas.drawText(df.format(ele.enterTime), x_enterTime, currentY, paint);
            canvas.drawText(df.format(ele.exitTime), x_exitTime, currentY, paint);

            canvas.drawLine(margin, (float) (currentY + 1.25 * zeilenAbstand), A4_WIDTH - margin, (float) (currentY + 1.5 * zeilenAbstand), paint);
            currentY += 2 * zeilenAbstand;
            if (currentY >= A4_HEIGHT - margin - zeilenAbstand) {
                document.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, pageNumber++).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                currentY = margin;
            }
        }
        document.finishPage(page);
        File file = savePDF(document, "Participants_Room"+item.id + ".pdf");
        document.close();
        return file;
    }

    public boolean deletePDF(File file) {
        return file.delete();
    }

    private File savePDF(PdfDocument document, String filename) {
        Log.v(TAG, "savePDF(" + filename + ")");
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.v(TAG, "savePDF(" + filename + ") faild -> Environment.MEDIA_MOUNTED");
            return null;
        }
        File file = new File(((Activity) context).getExternalFilesDir(null), filename);
        FileOutputStream fos;
        try {
            file.createNewFile();
            fos = new FileOutputStream(file, false);
            document.writeTo(fos);
            //fos.flush();
            fos.close();
            Log.v(TAG, "savePDF(" + filename + ") sucessfully");
            return file;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }
}
