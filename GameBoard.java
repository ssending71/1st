package com.example.gallaggame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameBoard extends SurfaceView implements Runnable {

    private Context context;
    public SpaceShip spaceShip;
    public Missile missile;
    public ArrayList<Ufo> ufos;
    private Thread thread;
    static final int ARRAYWSIZE=5;
    static final int ARRAYHSIZE=3;
    private boolean running;


    public int wBoard;
    public int hBoard;

    private Canvas canvas;
    private Paint paint=new Paint();
    private SurfaceHolder holder;

    public GameBoard(Context context,int w, int h) {
        super(context);
        this.context=context;
        this.wBoard=w;
        this.hBoard=h;

        holder=getHolder();

        init(context);
    }

    public void init(Context context){
        spaceShip=new SpaceShip(context,R.drawable.space211,wBoard/2, hBoard-400,this);

        ufos=new ArrayList<Ufo>();

        for(int i=0;i<ARRAYWSIZE;++i){
            for(int j=0;j<ARRAYHSIZE;++j){
                Ufo ufo=new Ufo(context, R.drawable.ufo211,wBoard/10*(1+i),hBoard/20*(1+j),this);
                ufos.add(ufo);
            }
        }
    }

    public void resume(){
        running=true;
        thread=new Thread(this);
        thread.start();
    }

    public void pause(){
        running=false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void endGame(){
        System.exit(0);

    }
    public void removeSprite(Sprite sprite){
        ufos.remove(sprite);

    }

    @Override
    public void run() {

        while (running) {

            for (Ufo u : ufos) {
                u.move();
            }
            spaceShip.move();

            if(missile!=null){
                missile.move();
                if(missile.getY()<0){
                    missile=null;
                }
            }

            for(int i=0;i<ufos.size();++i) {
                if (missile != null && ufos.get(i).collisionCheck(missile)) {
                    removeSprite(ufos.get(i));
                    missile = null;
                }
            }
            for(int i=0;i<ufos.size();++i) {
                if (ufos.get(i).collisionCheck(spaceShip)){
                    endGame();
                }
            }

            if(holder.getSurface().isValid()){
                canvas = holder.lockCanvas();

                canvas.drawColor(Color.BLACK);

                spaceShip.draw(canvas, paint);
                if(missile!=null){
                    missile.draw(canvas,paint);
                }
                for (Ufo u : ufos) {
                    u.draw(canvas, paint);
                }
                holder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void fire(){
        missile=new Missile(context, R.drawable.missile211 , spaceShip.getX()+(spaceShip.getWidth()/2),spaceShip.getY()-10,this);
    }

    public boolean onTouchEvent(MotionEvent e){

        switch(e.getAction()){
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
                if(e.getY()<hBoard*4/5){
                    fire();
                }
                else{
                    if(e.getX()>wBoard/2){
                        spaceShip.setDx(20);
                    }
                    else{
                        spaceShip.setDx(-20);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                spaceShip.setDx(0);;
                break;
        }
        return true;
    }
}
