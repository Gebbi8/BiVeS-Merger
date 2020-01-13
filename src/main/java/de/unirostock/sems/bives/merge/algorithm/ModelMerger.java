package de.unirostock.sems.bives.merge.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.SAXException;

import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.cellml.algorithm.CellMLValidator;
import de.unirostock.sems.bives.cellml.api.CellMLDiff;
import de.unirostock.sems.bives.cellml.parser.CellMLDocument;
import de.unirostock.sems.bives.ds.graph.GraphTranslator;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.markup.Typesetting;
import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;



public class ModelMerger {
	
	protected Diff diff;
	
	protected Document xmlDocA;
	protected Document xmlDocB;
	
	protected File fileA;
	protected File fileB;
	
	protected Map<String, String> moveMap;
	
	public ModelMerger(Document docA, Document docB, Diff diffed, boolean masterRight) throws IOException, JDOMException{
		if(masterRight){
			xmlDocB = docA;
			xmlDocA = docB;
			
//			fileB = docA;
//			fileA = docB;
		} else {
			xmlDocA = docA;
			xmlDocB = docB;
			
//			fileA = docA;
//			fileB = docB;
		}
		diff = diffed;
	}
	
	public ModelMerger(Document docA, Document docB, Diff diffed) throws IOException, JDOMException {
		xmlDocA = docA;
		xmlDocB = docB;
		
//		fileA = docA;
//		fileB = docB;
		diff = diffed;
	}
	
	public void mapMoves() throws IOException{
		moveMap = new HashMap<String, String>();
		List<Element> moves = diff.getPatch().getMoves().getChildren();
		
		XMLOutputter xout = new XMLOutputter();
		Format f = Format.getPrettyFormat(); 
		xout.setFormat(f);
		xout.output(moves, System.out);

		for(Element move : moves){
			String oldPath = move.getAttributeValue("oldPath");
			String newPath = move.getAttributeValue("newPath");
			moveMap.put(oldPath, newPath);
		}
	}
	
	
	public String getMerge() throws BivesConnectionException, JDOMException, IOException  {

		String mergedDoc = null;
	
		try{
			mapMoves();
			Document doc = xmlDocA;
			Document newDoc = xmlDocB;
		    XPathFactory xpathFactory = XPathFactory.instance();
		     	
			List <Element> deletes = diff.getPatch().getDeletes().getChildren();
			if(deletes == null) {
				System.out.println("no nodes that exist only in the first model");
				return null;
			}

			for(Element del : deletes) {
				System.out.println("-------------------------");
				//skip changes that are included with the parents
				if(del.getAttribute("triggeredBy") != null){
					System.out.println("skip due to trigger");
					continue;
				}

				//get path add local-name function
				String oldPath = del.getAttributeValue("oldPath");
				
				String localPath = getLocalPath(oldPath);
				//System.out.println(oldPath);	
				
				//get path to parent
				String parent = parentFrom(oldPath);
				System.out.println("--> " + parent);
				//check if an ancestor was moved
				String localParent = getLocalPath(parentFrom(movedAncestor(oldPath)));

				System.out.println(localParent);
				
				//get Parent to add the new Element to
				XPathExpression<Element> exprAddTo = xpathFactory.compile(localParent, Filters.element());
				Element addTo = exprAddTo.evaluateFirst(newDoc);
		

				
				//get node to append to				
				if(oldPath.contains("text()")){
					System.out.println("text");

					XPathExpression<Text> expr = xpathFactory.compile(localPath, Filters.text());
					Text element = expr.evaluateFirst(doc);

					System.out.println("FOUND TEXT: " + element.getText());
					System.out.println("adding to: " + localParent);
					addTo.addContent(element.getText());
					System.out.println("check");
				} else {
					System.out.println("no text");
System.out.println(localPath);
					XPathExpression<Element> expr = xpathFactory.compile(localPath, Filters.element());
					System.out.println("TESTESTESTE");
					Element element = expr.evaluateFirst(doc);
				System.out.println("CHEEEECK " + element.toString());	
				    addTo.addContent(element.clone());
					//xout.output(newDoc, System.out);						
				}

			}
			
			XMLOutputter xout = new XMLOutputter();
			Format f = Format.getPrettyFormat(); 
			xout.setFormat(f);

			mergedDoc = xout.outputString(newDoc);
			//xout.output(newDoc, System.out);
						
		} catch (Error e) {
	        e.printStackTrace();
		}
		return mergedDoc;
	}
	
	public String getLocalPath(String path){
		String locPath = "";
		path = path.substring(1);
		for(String s : path.split("/")){
			String[] k = s.split("\\[");
			if(!k[0].contains("text()")) locPath = locPath + "/*[local-name() = '" + k[0] +"'][" + k[1];
			else locPath += "/text()";
		}
		return locPath;
	}
	
	public String parentFrom(String path){
		String parentPath;
		int lastOcc = path.lastIndexOf('/');
		parentPath = path.substring(0, lastOcc);
		return parentPath;
	}
	
	public String movedAncestor(String path){
		System.out.println(path);
		String end = path;
		while(path.contains("/")){
			int cutoff = path.lastIndexOf('/');
			path = path.substring(0, cutoff);
			if(moveMap.containsKey(path)){
				end = end.substring(cutoff);
				String movedOldPath = moveMap.get(path) + end;
				System.out.println("Moved Ancestor");
				return movedOldPath;
			}

		}
		return end;
	}
}