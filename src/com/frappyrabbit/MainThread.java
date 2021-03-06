package com.frappyrabbit;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
	private boolean running;
	private SurfaceHolder surfaceHolder;
	private MainGamePanel gamePanel;
	private final static int MAX_FPS = 50;
	private final static int MAX_FRAME_SKIPS = 5;
	private final static int FRAME_PERIOD = 1000/MAX_FPS;
	
	public void setRunning(boolean running){
		this.running = running;
		
	}
	
	public MainThread(SurfaceHolder surfaceHolder,MainGamePanel gamePanel){
		super();
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}
	
	@SuppressLint("WrongCall")
	@Override
	public void run(){
		Canvas canvas;
		long beginTime;
		long timeDiff;
		int sleepTime;
		int framesSkipped;
		sleepTime = 0;
		while(running){
			canvas = null;
			try{
			canvas = this.surfaceHolder.lockCanvas();
			synchronized (surfaceHolder){
				beginTime = System.currentTimeMillis();
				framesSkipped = 0;
				this.gamePanel.update();
				this.gamePanel.onDraw(canvas);
				timeDiff = System.currentTimeMillis() - beginTime;
				sleepTime = (int)(FRAME_PERIOD - timeDiff);
				if(sleepTime>0){
					try{
						Thread.sleep(sleepTime);
					}
					catch(InterruptedException e){};
				}
				while (sleepTime<0&&framesSkipped<MAX_FRAME_SKIPS){
					this.gamePanel.update();
					sleepTime += FRAME_PERIOD;
					framesSkipped++;
				}
			}
			}
			finally{
				if(canvas!=null){
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
