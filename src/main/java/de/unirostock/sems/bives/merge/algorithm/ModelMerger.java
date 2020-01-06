package de.unirostock.sems.bives.merge.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
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
	
	public ModelMerger(File docA, File docB, boolean masterRight) throws IOException, JDOMException{
		if(masterRight){
			xmlDocB = XmlTools.readDocument (docA);
			xmlDocA = XmlTools.readDocument (docB);
			
			fileB = docA;
			fileA = docB;
		} else {
			xmlDocA = XmlTools.readDocument (docA);
			xmlDocB = XmlTools.readDocument (docB);
			
			fileA = docA;
			fileB = docB;
		}
	}
	
	public ModelMerger(File docA, File docB) throws IOException, JDOMException {
		xmlDocA = XmlTools.readDocument (docA);
		xmlDocB = XmlTools.readDocument (docB);
		
		fileA = docA;
		fileB = docB;
	}
	
	
	public void merge() throws BivesConnectionException, JDOMException  {
		//getDocuments
		
		SBMLValidator sbmlVal = new SBMLValidator();
		CellMLValidator cellmlVal = new CellMLValidator();
		if(sbmlVal.validate(fileA)){
			SBMLDocument d1 = sbmlVal.getDocument();
			if(sbmlVal.validate(fileB)){
				SBMLDocument d2 = sbmlVal.getDocument();
				SBMLDiff sbmlDiff = new SBMLDiff(d1, d2);
				sbmlDiff.mapTrees(Diff.ALLOW_DIFFERENT_IDS, Diff.CARE_ABOUT_NAMES, Diff.STRICTER_NAMES);
				diff = sbmlDiff;				
			} else {
				System.out.println("Second doc is no SBML, but the first one is.");
				return;
			}

		} else if(cellmlVal.validate(fileA)) {
			CellMLDocument d1 = cellmlVal.getDocument();
			if(cellmlVal.validate(fileB)){
				CellMLDocument d2 = cellmlVal.getDocument();
				CellMLDiff cellmlDiff = new CellMLDiff(d1, d2);
				cellmlDiff.mapTrees(Diff.ALLOW_DIFFERENT_IDS, Diff.CARE_ABOUT_NAMES, Diff.STRICTER_NAMES);
				diff = cellmlDiff;
			} else {
				System.out.println("Second doc is no CellML but the first one is!");
				return;
			}
		} else {
			System.out.println("The first doc is neither SBML nor CellML");
			return;
		}
		
		System.out.println(diff);
		
		SAXBuilder builder = new SAXBuilder();
		try{
			Document doc = builder.build(fileA);
			Document newDoc = builder.build(fileB);
		    XPathFactory xpathFactory = XPathFactory.instance();
		     	
			Element delete = (Element) diff.getPatch().getDeletes();	
			List <Element> deletes = delete.getChildren();
	
			for(Element del : deletes) {
				

				//skip changes that are included with the parents
				if(del.getAttribute("triggeredBy") == null){
					System.out.println("skip due to trigger");

					continue;
				}
				
				//get path add local-name function
				String oldPath = del.getAttributeValue("oldPath");
				String localPath = getLocalPath(oldPath);
				//System.out.println(oldPath);	
				//get path to parent
				String localParent = parentFromOldPath(localPath);
				
				//get Parent to add the new Element to
				XPathExpression<Element> exprAddTo = xpathFactory.compile(localParent, Filters.element());
				Element addTo = exprAddTo.evaluateFirst(newDoc);
		
				//get node to append to
				//XMLOutputter xout = new XMLOutputter();
				
				
				//xout.output(newDoc, System.out);
				if(oldPath.contains("text()")){
					System.out.println(localPath);
					XPathExpression<Text> expr = xpathFactory.compile(localPath, Filters.text());
					Text element = expr.evaluateFirst(doc);
					System.out.println("FOUND TEXT: " + element.getText());
					
					addTo.addContent(element.getText());
				} else {
					XPathExpression<Element> expr = xpathFactory.compile(localPath, Filters.element());
					Element element = expr.evaluateFirst(doc);
					
				    addTo.addContent(element.clone());
					//xout.output(newDoc, System.out);						
				}

			}
		} catch (IOException e) {
		    System.out.println("test");	

	        e.printStackTrace();
		} catch (JDOMException e) {
		    System.out.println("test");	

	        e.printStackTrace();
		}
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
	
	public String parentFromOldPath(String path){
		String parentPath;
		int lastOcc = path.lastIndexOf('/');
		parentPath = path.substring(0, lastOcc);
		return parentPath;
	}
	
	
}
