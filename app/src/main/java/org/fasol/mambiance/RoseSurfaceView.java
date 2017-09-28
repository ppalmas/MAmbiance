package org.fasol.mambiance;

/**
 * Created by fasol on 27/11/16.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

public class RoseSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private DrawingThread mThread;

    private Paint mPaint;

    private SeekBar o,t,v,a;

    private Resources res;
    private float paddX,paddY;

    /**
     * Constructeur utilisé pour inflater avec un style
     */
    public RoseSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Constructeur utilisé pour inflater sans style
     */
    public RoseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructeur utilisé pour construire dans le code
     */
    public RoseSurfaceView(Context context, SeekBar o, SeekBar t, SeekBar v, SeekBar a) {
        super(context);

        res = context.getResources();

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mThread = new DrawingThread();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);

        this.o=o;
        this.a=a;
        this.t=t;
        this.v=v;

        paddX=res.getDimension(R.dimen.activity_horizontal_margin);
        paddY=res.getDimension(R.dimen.activity_vertical_margin);
    }


    @Override
    protected void onDraw(Canvas pCanvas) {

        super.onDraw(pCanvas);

        pCanvas.drawColor(Color.WHITE);
        mPaint.setColor(Color.BLACK);

        // écrit textes axes
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(2*paddY/3);
        pCanvas.drawText("Olfactory",this.getX()+this.getWidth()/2-paddX,this.getY()-paddY/3,mPaint);
        pCanvas.save();
        pCanvas.rotate(90f,this.getX()+this.getWidth()-2*paddX + paddY/3, this.getY()+this.getHeight()/2-paddY);
        pCanvas.drawText("Thermal",this.getX()+this.getWidth()-2*paddX + paddY/3, this.getY()+this.getHeight()/2-paddY, mPaint);
        pCanvas.restore();
        pCanvas.drawText("Visual",this.getX()+this.getWidth()/2-paddX, this.getY()+this.getHeight()-4*paddY/3,mPaint);
        pCanvas.save();
        pCanvas.rotate(-90f, this.getX()-paddY/3, this.getY()+this.getHeight()/2-paddY);
        pCanvas.drawText("Acoustical", this.getX()-paddY/3, this.getY()+this.getHeight()/2-paddY, mPaint);
        pCanvas.restore();

        // calcul position rose
        float oThumbPosX = (this.getWidth()/2) + this.getX() - paddX;
        float oThumbPosY = (this.getHeight()/2) + this.getY() -paddY - (this.getHeight()/2-paddY) * o.getProgress() / o.getMax();
        float tThumbPosX = (this.getWidth()/2) + this.getX() - paddX + (this.getWidth()/2-paddX) * t.getProgress() / t.getMax();
        float tThumbPosY = (this.getHeight()/2) + this.getY() - paddY;
        float vThumbPosX = (this.getWidth()/2) + this.getX() - paddX;
        float vThumbPosY = (this.getHeight()/2) + this.getY() - paddY + (this.getHeight()/2-paddY) * v.getProgress() / v.getMax();
        float aThumbPosX = (this.getWidth()/2) + this.getX() - paddX - (this.getWidth()/2-paddX) * a.getProgress() / a.getMax();
        float aThumbPosY = (this.getHeight()/2) + this.getY() - paddY;

        Path p = new Path();
        p.moveTo(oThumbPosX,oThumbPosY);
        p.lineTo(tThumbPosX,tThumbPosY);
        p.lineTo(vThumbPosX,vThumbPosY);
        p.lineTo(aThumbPosX,aThumbPosY);
        p.lineTo(oThumbPosX,oThumbPosY);

        // rempli rose
        mPaint.setColor(Color.LTGRAY);
        pCanvas.drawPath(p,mPaint);

        //écrit échelle axe
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setColor(Color.BLACK);
        float echelle=1.0f;
        for(int i=0;i<5;i++){
            pCanvas.drawText(String.valueOf(echelle), this.getX()+this.getWidth()/2-paddY, this.getY()+i*this.getHeight()/9+2*paddY/3, mPaint);
            echelle-=0.5;
        }

        // trace rose
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.BLACK);
        pCanvas.drawLine(oThumbPosX,oThumbPosY,tThumbPosX,tThumbPosY,mPaint);
        pCanvas.drawLine(tThumbPosX,tThumbPosY,vThumbPosX,vThumbPosY,mPaint);
        pCanvas.drawLine(vThumbPosX,vThumbPosY,aThumbPosX,aThumbPosY,mPaint);
        pCanvas.drawLine(aThumbPosX,aThumbPosY,oThumbPosX,oThumbPosY,mPaint);

        // trace axe
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.BLACK);
        pCanvas.drawLine(this.getX()+this.getWidth()/2-paddX, this.getY(),
                this.getX()+this.getWidth()/2-paddX, this.getY()+this.getHeight()-2*paddY, mPaint);
        pCanvas.drawLine(this.getX(), this.getY()+this.getHeight()/2-paddY,
                this.getX()+this.getWidth()-2*paddX, this.getY()+this.getHeight()/2-paddY, mPaint);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread = new DrawingThread();
        mThread.keepDrawing = true;
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mThread.keepDrawing = false;

        boolean joined = false;
        while (!joined) {
            try {
                mThread.join();
                joined = true;
            } catch (InterruptedException e) {}
        }
    }

    private class DrawingThread extends Thread {
        // Utilisé pour arrêter le dessin quand il le faut
        boolean keepDrawing = true;

        @Override
        public void run() {

            while (keepDrawing) {
                Canvas canvas = null;

                try {
                    // On récupère le canvas pour dessiner dessus
                    canvas = mSurfaceHolder.lockCanvas();
                    // On s'assure qu'aucun autre thread n'accède au holder
                    synchronized (mSurfaceHolder) {
                        // Et on dessine
                        if (canvas != null) onDraw(canvas);
                    }
                } finally {
                    // Notre dessin fini, on relâche le Canvas pour que le dessin s'affiche
                    if (canvas != null)
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                }

                // Pour dessiner à 50 fps
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {}
            }
        }
    }
}

