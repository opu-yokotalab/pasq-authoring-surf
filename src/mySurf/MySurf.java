package mySurf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;

public class MySurf extends Surf{
	
	public MySurf(BufferedImage image){
		super(image);
	}
	
	
	
	/**
	 * 2つの画像で対応づけられる点のリストを返す
	 * getMatchingPointsを変更したもの
	 * @param descriptor
	 * @param upright
	 * @return List<PairInterestPoints>
	 */
	public List<PairInterestPoints> getPairList(Surf descriptor , boolean upright){
		return getPairList(descriptor,upright,0.80d);
	}
	
	/**
	 * 2つの画像で対応付けられる特徴点のリストを返す
	 * @param descriptor
	 * @param upright
	 * @param threshold
	 * @return
	 */
	public List<PairInterestPoints> getPairList(Surf descriptor , boolean upright, double threshold){

		List<PairInterestPoints> pairPointList = new ArrayList<PairInterestPoints>();
		List<SURFInterestPoint> points = upright ? getUprightInterestPoints() : getFreeOrientedInterestPoints();
		
		
		for ( SURFInterestPoint a : points ){
			double smallestDistance = Float.MAX_VALUE;
			double nextSmallestDistance = Float.MAX_VALUE;
			SURFInterestPoint possibleMatch = null;
			
			for ( SURFInterestPoint b : upright ? descriptor.getUprightInterestPoints() : descriptor.getFreeOrientedInterestPoints() ){
				double distance = a.getDistance(b);
				//System.out.println("Distance = " + distance);
				if ( distance < smallestDistance ){
					nextSmallestDistance = smallestDistance;
					smallestDistance = distance;
					possibleMatch = b;
				} else if ( distance < nextSmallestDistance ){
					nextSmallestDistance = distance;
				}
			}
			
		    // If match has a d1:d2 ratio < 0.65 ipoints are a match
			if ( smallestDistance/nextSmallestDistance < threshold ){
				//not storing change in position
				pairPointList.add(new PairInterestPoints(a,possibleMatch,smallestDistance/nextSmallestDistance));
			}
		}
		return pairPointList;
	}
	
	
	
	/**
	 * 	特徴点のデータをファイルから読み込む
	 * @param filePath
	 * @return
	 */
	public List<SURFInterestPoint> getSurfPoints(String filePath){
		//ファイル読込
		Document xmlDoc = readXML(filePath);
		////エラー処理
		if(xmlDoc==null){
			System.out.println("xmlファイルの読み込みに失敗");
		}
		
		//DocumentからList<SURFInterestPoint>に変換
		List<SURFInterestPoint> pointList = xmlToInterestPoints(xmlDoc);
		return pointList;
		
	}
	
	
	
	private Document readXML(String filePath){
		try {
			// ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			// ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			// パースを実行してDocumentオブジェクトを取得
			Document xmlDoc = builder.parse(new File(filePath));
			return xmlDoc;
		}catch (Exception e) {
			return null;
		}
	}
	
	
	
	private List<SURFInterestPoint> xmlToInterestPoints(Document xmlDoc){
		List<SURFInterestPoint> interestPointList = new ArrayList<SURFInterestPoint>();
		
		//ルート要素(InterestPoints)の取得
		Element root = xmlDoc.getDocumentElement();
		
		//Point要素の取得
		NodeList pointElementList = root.getElementsByTagName("point");
		for(int i=0;i<pointElementList.getLength();i++){
			Element pointElement = (Element) pointElementList.item(i);
			
			//x
			Element tempElement = (Element) pointElement.getElementsByTagName("x").item(0);
			Text tempText = (Text) tempElement.getFirstChild();
			float x = Float.valueOf(tempText.getNodeValue());
			
			//y
			tempElement = (Element) pointElement.getElementsByTagName("y").item(0);
			tempText = (Text) tempElement.getFirstChild();
			float y = Float.valueOf(tempText.getNodeValue());
			
			
			
			//scale
			tempElement = (Element) pointElement.getElementsByTagName("scale").item(0);
			tempText = (Text) tempElement.getFirstChild();
			float scale = Float.valueOf(tempText.getNodeValue());
			
			//orientation
			tempElement = (Element) pointElement.getElementsByTagName("orientation").item(0);
			tempText = (Text) tempElement.getFirstChild();
			float orientation = Float.valueOf(tempText.getNodeValue());
			
			//laplacian
			tempElement = (Element) pointElement.getElementsByTagName("laplacian").item(0);
			tempText = (Text) tempElement.getFirstChild();
			int laplacian = Integer.valueOf(tempText.getNodeValue());
			
			//descriptor
			tempElement = (Element) pointElement.getElementsByTagName("descriptor").item(0);
			NodeList tempList = tempElement.getElementsByTagName("value");
			double[] descriptor = new double[tempList.getLength()];
			for(int j=0;j<tempList.getLength();j++){
				Element valueElement = (Element) tempList.item(j);
				//基本的には、順番通りのはずなので、noとjは一致するはず
				if( Integer.valueOf(valueElement.getAttribute("no")) == j ){
					descriptor[j] = Double.valueOf( valueElement.getFirstChild().getNodeValue() );
				}else{
					for(int k=0;k<tempList.getLength();k++){
						valueElement = (Element) tempList.item(k);
						if( Integer.valueOf(valueElement.getAttribute("no")) == j ){
							descriptor[j] = Double.valueOf( valueElement.getFirstChild().getNodeValue() );
						}
					}
				}
			}
			
			
			//dx
			tempElement = (Element) pointElement.getElementsByTagName("dx").item(0);
			tempText = (Text) tempElement.getFirstChild();
			float dx = Float.valueOf(tempText.getNodeValue());
			
			//dy
			tempElement = (Element) pointElement.getElementsByTagName("dy").item(0);
			tempText = (Text) tempElement.getFirstChild();
			float dy = Float.valueOf(tempText.getNodeValue());
			
			//ClusterIndex
			tempElement = (Element) pointElement.getElementsByTagName("clusterIndex").item(0);
			tempText = (Text) tempElement.getFirstChild();
			int clusterIndex = Integer.valueOf(tempText.getNodeValue());
			
			
			//SURFInterestPointオブジェクトの作成
			SURFInterestPoint point = new SURFInterestPoint(x,y,scale,laplacian);
			point.setClusterIndex(clusterIndex);
			point.setDescriptor(descriptor);
			point.setDx(dx);
			point.setDy(dy);
			point.setOrientation(orientation);
			
			interestPointList.add(point);
		}
		
		return interestPointList;
		
	}
	
	
	
	
	
	/**
	 * 特徴点のデータをファイルに書き出す
	 * @param filePath
	 * @param upright
	 */
	public List<SURFInterestPoint> writeSurfPoints(String filePath,boolean upright){
		//特徴点の取得
		List<SURFInterestPoint> points = upright ? getUprightInterestPoints() : getFreeOrientedInterestPoints();
		
		//書き出す内容の決定
		Document xmlDoc = interestPointsToXML(points);
		if(xmlDoc==null){
			System.out.println("xmlの生成に失敗");
			return points;
		}
		
		//ファイルに書き出す
		writeXMLFile(filePath, xmlDoc);
		return points;
	}
	
	
	
	private Document interestPointsToXML(List<SURFInterestPoint> points){
		try{
			////Documentの生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuiler;
			dbuiler = dbfactory.newDocumentBuilder();
			Document doc = dbuiler.newDocument();  
			
			//ルート要素の作成
			Element interestPoints = doc.createElement("InterestPoints");
			doc.appendChild(interestPoints);
			
			//point要素(各特徴点)の作成
			for(SURFInterestPoint point : points){
				Element pointElement = doc.createElement("point");
				interestPoints.appendChild(pointElement);
				
				//x
				Element xElement = doc.createElement("x");
				Text xNode = doc.createTextNode(Float.toString(point.getX()));
				xElement.appendChild(xNode);
				pointElement.appendChild(xElement);
				
				//y
				Element yElement = doc.createElement("y");
				Text yNode = doc.createTextNode(Float.toString(point.getY()));
				yElement.appendChild(yNode);
				pointElement.appendChild(yElement);
				
				//scale
				Element scaleElenent = doc.createElement("scale");
				Text scaleNode = doc.createTextNode(Float.toString(point.getScale()));
				scaleElenent.appendChild(scaleNode);
				pointElement.appendChild(scaleElenent);
				
				//orientation
				Element oriElenent = doc.createElement("orientation");
				Text oriNode = doc.createTextNode(Float.toString(point.getOrientation()));
				oriElenent.appendChild(oriNode);
				pointElement.appendChild(oriElenent);
				
				
				//laplacian
				Element lapElenent = doc.createElement("laplacian");
				Text lapNode = doc.createTextNode(Integer.toString(point.getLaplacian()));
				lapElenent.appendChild(lapNode);
				pointElement.appendChild(lapElenent);
				
				//descriptor
				Element descriptorElement = doc.createElement("descriptor");
				double[] descriptor = point.getDescriptor();
				for(int i=0;i<descriptor.length;i++){
					Element valueElement = doc.createElement("value");
					valueElement.setAttribute("no", Integer.toString(i));
					Text valueNode = doc.createTextNode(Double.toString(descriptor[i]));
					valueElement.appendChild(valueNode);
					descriptorElement.appendChild(valueElement);
				}
				pointElement.appendChild(descriptorElement);
				
				
				//dx
				Element dxElenent = doc.createElement("dx");
				Text dxNode = doc.createTextNode(Float.toString(point.getDx()));
				dxElenent.appendChild(dxNode);
				pointElement.appendChild(dxElenent);
				
				
				//dy
				Element dyElenent = doc.createElement("dy");
				Text dyNode = doc.createTextNode(Float.toString(point.getDy()));
				dyElenent.appendChild(dyNode);
				pointElement.appendChild(dyElenent);
				
				//ClusterIndex
				Element clusterElenent = doc.createElement("clusterIndex");
				Text clusterNode = doc.createTextNode(Integer.toString(point.getClusterIndex()));
				clusterElenent.appendChild(clusterNode);
				pointElement.appendChild(clusterElenent);
			}
			
			return doc;
			
		}catch (Exception e) {
			return null;
		}
	}
	
	
	
	/**
	 * 指定のDocumentを指定ファイルに書き出す
	 * @param writeFileName
	 * @param doc
	 */
	private void writeXMLFile(String writeFileName , Document doc){
		try {
			TransformerFactory  tfactory = TransformerFactory.newInstance();
			Transformer tf = tfactory.newTransformer();
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
			File outputFile = new File(writeFileName);
			tf.transform(new DOMSource(doc), new StreamResult(outputFile));
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args){
		try {
			BufferedImage imageA = ImageIO.read(new File(args[0]));
			MySurf ms = new MySurf(imageA);
			ms.writeSurfPoints(args[1], true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
