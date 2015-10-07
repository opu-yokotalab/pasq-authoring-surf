package mySurf;

import com.stromberglabs.jopensurf.SURFInterestPoint;

public class MySURFInterestPoint extends SURFInterestPoint{
	private int width_image,height_image;
	
	
	public MySURFInterestPoint(float x, float y, float scale, int laplacian,int width_image,int height_image) {
		super(x, y, scale, laplacian);
		this.width_image = width_image;
		this.height_image = height_image;
	}
	
	public MySURFInterestPoint(SURFInterestPoint surfip , int width_image,int height_image) {
		super(surfip.getX(),surfip.getY(),surfip.getScale(),surfip.getLaplacian());
		this.width_image = width_image;
		this.height_image = height_image;
	}
	
	
	public int getImageWidth(){
		return width_image;
	}
	
	public int getImageHeight(){
		return height_image;
	}
	
	public double getXRate(){
		return getX()/(double)width_image;
	}
	
	
}
