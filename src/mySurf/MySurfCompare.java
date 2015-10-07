package mySurf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import sun.java2d.loops.DrawRect;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.SurfCompare;

public class MySurfCompare extends SurfCompare{
	
	private List<PairInterestPoints> pairList;
	private Image imageBase;
	private int width_imageBase;
	private  int height_imageBase;
	
	private Image imageTarget;
	private int width_imageTarget;
	private int height_imageTarget;

	private MySurf mSurfA;
	private MySurf mSurfB;
	
	
	public MySurfCompare(BufferedImage image_base,BufferedImage image_target){
		super(image_base,image_target);
		//this.imageBase = image_base;
		//this.imageTarget = image_target;
		this.width_imageBase = image_base.getWidth();
		this.height_imageBase = image_base.getHeight();
		this.width_imageTarget = image_target.getWidth();
		this.height_imageTarget = image_target.getHeight();
		this.mSurfA  = new MySurf(image_base);
		this.mSurfB = new MySurf(image_target);
		this.pairList = mSurfA.getPairList(mSurfB, true);
	}
	

	public MySurfCompare(BufferedImage image_base,BufferedImage image_target,double threshold){
		super(image_base,image_target);
		//this.imageBase = image_base;
		//this.imageTarget = image_target;
		this.width_imageBase = image_base.getWidth();
		this.height_imageBase = image_base.getHeight();
		this.width_imageTarget = image_target.getWidth();
		this.height_imageTarget = image_target.getHeight();
		this.mSurfA  = new MySurf(image_base);
		this.mSurfB = new MySurf(image_target);
		this.pairList = mSurfA.getPairList(mSurfB, true,threshold);
	}
	

    public MySurf getMySurfA(){
    	return this.mSurfA;
    }
    
    public MySurf getMySurfB(){
    	return this.mSurfB;
    }
    
	
	
    
    /**
     * 対応付けられた特徴点のペアを返す
     * @return
     */
    public ArrayList<PairInterestPoints> getPairList(){
    	return (ArrayList<PairInterestPoints>) this.pairList;
    }
    
    
    
    
    /**
     * 
     */
    protected void paintComponent(Graphics g) {
    	Graphics2D g2 = (Graphics2D)g;
		// Center image in this component.
        g2.drawImage(imageBase,0,0,width_imageBase,height_imageBase,this);
		g2.drawImage(imageTarget,0,height_imageBase,width_imageTarget,height_imageTarget,Color.WHITE,this);

		//if there is a surf descriptor, go ahead and draw the points
        if(mSurfA != null && mSurfB!= null){
        	drawIPLine(g2,pairList);
        }
        
    }
    
    
    
    /**
     * 対応付けられた点を線で結ぶ
     * @param g
     * @param pairList
     */
    private void drawIPLine(Graphics g,java.util.List<PairInterestPoints> pairList){
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setColor(Color.RED);
    	for(PairInterestPoints pair : pairList){
    		SURFInterestPoint objectIPoint = pair.getPointBase();
    		SURFInterestPoint targetIPoint = pair.getPointTarget();
    		int xOIP = (int) (objectIPoint.getX());
    		int yOIP = (int) (objectIPoint.getY());
    		int xTIP = (int) (targetIPoint.getX());
    		int yTIP = (int) (targetIPoint.getY());
    		g2d.drawLine(xOIP, yOIP, xTIP, yTIP+height_imageBase);
    	}
    }
    
    
    /**
     * 指定の色で対応付けられた特徴点を線で結ぶ
     * @param g
     * @param pairList
     * @param color
     */
    private void drawIPLine(Graphics g,java.util.List<PairInterestPoints> pairList,Color color){
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setColor(color);
    	for(PairInterestPoints pair : pairList){
    		SURFInterestPoint objectIPoint = pair.getPointBase();
    		SURFInterestPoint targetIPoint = pair.getPointTarget();
    		int xOIP = (int) (objectIPoint.getX());
    		int yOIP = (int) (objectIPoint.getY());
    		int xTIP = (int) (targetIPoint.getX());
    		int yTIP = (int) (targetIPoint.getY());
    		g2d.drawLine(xOIP, yOIP, xTIP, yTIP+height_imageBase);
    	}
    }
    
    
    public void display(){
        JFrame f = new JFrame("MySurfCompare");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new JScrollPane(this));
        f.setSize(Math.max(width_imageBase, width_imageTarget),height_imageBase+height_imageTarget);
        f.setLocation(0,0);
        f.setVisible(true);
    }
    
    public static void main(String[] args) throws IOException {
    	BufferedImage imageA = ImageIO.read(new File(args[0]));
    	BufferedImage imageB = ImageIO.read(new File(args[1]));
    	MySurfCompare show = new MySurfCompare(imageA,imageB);
    	show.display();

    	//show.matchesInfo();
  }
    
}
