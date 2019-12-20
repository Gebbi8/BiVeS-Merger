package de.unirostock.sems.bives.merge.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;


import de.unirostock.sems.bives.api.Diff;
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
	
	public ModelMerger (Document docA, Document docB, Diff diffed) {
		xmlDocA = docA;
		xmlDocB = docB;
		diff = diffed;
		
	}
	
	public ModelMerger(File docA, File docB) throws IOException, JDOMException, BivesConnectionException {
		//parse XML from files
		FileInputStream fileIS = new FileInputStream(xmlDocA);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(false); // never forget this!
        DocumentBuilder db = dbf.newDocumentBuilder();
        xmlDocA = (Document) db.parse(docA);
        
		xmlDocA = XmlTools.readDocument (docA);
		xmlDocB = XmlTools.readDocument (docB);
		
		//getDocuments
		SBMLValidator val = new SBMLValidator ();
		val.validate(docA);
		SBMLDocument d1 = val.getDocument();
		val.validate(docB);
		SBMLDocument d2 = val.getDocument();
		
		SBMLDiff sbmlDiff = new SBMLDiff(d1, d2);
		sbmlDiff.mapTrees(Diff.ALLOW_DIFFERENT_IDS, Diff.CARE_ABOUT_NAMES, Diff.STRICTER_NAMES);
		diff = sbmlDiff;
		
	}
	
	
	public void getSlaveElements() throws XPathExpressionException, ParserConfigurationException {

		Element delete = (Element) diff.getPatch().getDeletes();
		System.out.println("test");

		List <Element> deletes = delete.getChildren();

		for(Element del : deletes) {
			if(del.getAttribute("triggeredBy") == null) continue; 
			String oldPath = del.getAttributeValue("oldPath");
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile(oldPath);
			

			
			System.out.println(oldPath);
			System.out.println(xmlDocA == null);
			NodeList oldNode =  (NodeList) expr.evaluate(xmlDocA, XPathConstants.NODESET);
			if(oldNode != null) System.out.println("success");
		}	
	}
	
}
