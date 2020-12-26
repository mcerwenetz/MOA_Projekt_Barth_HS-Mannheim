package de.pbma.moa.createroomdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import de.pbma.moa.createroomdemo.room.RoomItem;

public class PdfClass {
    final static String TAG = PdfClass.class.getCanonicalName();
    final static int A4_HEIGHT = 846;//11.75in * 72
    final static int A4_WIDTH = 594; //8.25in * 72
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
        canvas.drawText("NFC-Tag", leftborder, 11 * y_spacing, paint);
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
        canvas.drawText(item.eMail, infosleftborder, 10 * y_spacing, paint);
        canvas.drawText(item.roomName + "/" + item.eMail + "/" + item.id, infosleftborder, 11 * y_spacing, paint);  //TODO durch function ersetzen wie in CreateNEwRoom

        Log.v(TAG, "createPdfRoomInfos() draw Bitmap");
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(qrCode, (A4_WIDTH - qrCode.getWidth()) / 2, 15 * y_spacing, paint);
        document.finishPage(page);

        File file = savePDF(document, "Room_" + item.id + ".pdf");
        document.close();
        return file;
    }


    //TODO vielleicht umschreiben (savepdfauch) sodas des file garned mehr local gespeichert werden muss
    //TODO funktionirt nicht immer geht nur wenn pdf viewer oder etc installiert ist ansonsten error  -> exeption
    public void showPDF(File file) {
        Log.v(TAG, "showPDF(" + file.getName() + ")");
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        if (file.exists()) {
            intentShareFile.setType("application/pdf");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
//            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...");
//            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");
            ((Activity) context).startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
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
