package mySurf;

import java.awt.Point;
import java.util.List;

import com.stromberglabs.jopensurf.SURFInterestPoint;

public class PairInterestPoints {

	
	private SURFInterestPoint point_base;
	private SURFInterestPoint point_target;
	private double similarValue;
	
	public PairInterestPoints(SURFInterestPoint pointA,SURFInterestPoint pointB,double distance){
		this.point_base = pointA;
		this.point_target = pointB;
		this.similarValue = distance;
	}
	
	public SURFInterestPoint getPointBase(){
		return this.point_base;
	}
	
	public SURFInterestPoint getPointTarget(){
		return this.point_target;
	}
	
	public double getValue(){
		return similarValue;
	}
	
	/**
	 * ”äŠrŒ³‚ÌxÀ•W‚Æ”äŠr‘ÎÛ‚ÌxÀ•W‚Ì·•ª
	 * @return target.x - base.x
	 */
	public double getDx(){
		return point_target.getX() - point_base.getX();
	}
	
	/**
	 * ”äŠrŒ³‚ÌyÀ•W‚Æ”äŠr‘ÎÛ‚ÌyÀ•W‚Ì·•ª
	 * @return target.y - target.y
	 */
	public double getDy(){
		return point_target.getY() - point_base.getY();
	}
	
	
}
