package de.unirostock.sems.bives.merge.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import org.xml.sax.SAXException;

import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;

public class SBMLMerger extends SBMLDiff {
	/** The original document. */
	protected SBMLDocument doc1;
	protected Document xmlDoc1;
	
	/** The modified document. */
	protected SBMLDocument doc2;
	protected Document xmlDoc2;
	
	protected SBMLDiff diff;

	public SBMLMerger(SBMLDocument a, SBMLDocument b) throws BivesSBMLParseException, BivesDocumentConsistencyException,
			XmlDocumentParseException, IOException, JDOMException, BivesConnectionException {
		super(a, b);

		doc1 = new SBMLDocument (treeA);
		doc2 = new SBMLDocument (treeB);
		System.out.println("WANT DOCs");
		xmlDoc1 = documentA;
		xmlDoc2 = documentB;
		System.out.println(xmlDoc1 != null);
//		xmlDoc1 = documentBuilder.parse(doc1.);
//		xmlDoc2 = documentBuilder.parse(doc2);
		
				
		diff = new SBMLDiff (doc1, doc2);
		diff.mapTrees(Diff.ALLOW_DIFFERENT_IDS, Diff.CARE_ABOUT_NAMES, Diff.STRICTER_NAMES);
		
		// TODO Auto-generated constructor stub
	}
	
	public SBMLMerger(String a, String b) throws BivesSBMLParseException, BivesDocumentConsistencyException,
			XmlDocumentParseException, IOException, JDOMException, BivesConnectionException, ParserConfigurationException, SAXException {
		super(a, b);
		
		doc1 = new SBMLDocument (treeA);
		doc2 = new SBMLDocument (treeB);
		
		//get xml files
//		File xml1 = new File(a);
//		File xml2 = new File(b);
		
//		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//		xmlDoc1 = documentBuilder.parse(xml1);
//		xmlDoc2 = documentBuilder.parse(xml2);
		
		System.out.println("!!: xmlDocs are done");
		
		diff = new SBMLDiff (doc1, doc2);
		diff.mapTrees(Diff.ALLOW_DIFFERENT_IDS, Diff.CARE_ABOUT_NAMES, Diff.STRICTER_NAMES);
		// TODO Auto-generated constructor stub
	}

	public void printDiff() {
		System.out.println(diff.getDiff());
	}
	
	public void printSlaveElements() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException  {
		
		Element delete = (Element) diff.getPatch().getDeletes();
		List <Element> deletes = delete.getChildren();
		
		
		
		for(Element del : deletes) {
			if(del.getAttribute("triggeredBy") == null) continue; 
			String oldPath = del.getAttributeValue("oldPath");
			
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			XPathExpression expr = xpath.compile(oldPath);
			
			System.out.println(oldPath);
			System.out.println(xmlDoc1 == null);
			//Node oldNode = (Node) expr.evaluate(xmlDoc1, XPathConstants.NODE);
			//if(oldNode != null) System.out.println("success");
		}	
		
//		XPathFactory xpathFactory = XPathFactory.newInstance();
//		XPath xpath = xpathFactory.newXPath();
//		XPathExpression expr = xpath.compile("/bives/delete");
//		Node insert = (Node) expr.evaluate(convertStringToXMLDocument(xmlDiff), XPathConstants.NODE);
//		NodeList inserts = insert.getChildNodes();
//		System.out.println(xmlDiff + inserts.getLength());
//		for(Element del : deletes) {
//			if(del.getAttribute("triggeredBy") != null)
//			System.out.println("del.getAttributeValue("oldPath"));
			//if(node.getNodeType() != Node.ELEMENT_NODE) continue;
//			if(attr.getNamedItem("triggeredBy") != null) {
//				System.out.println("skipped element due to triggerdby");
//				continue;
//			}
			// example to get attribute value
			//System.out.println(attr.getNamedItem("oldPath"));
			
			
	}
}
