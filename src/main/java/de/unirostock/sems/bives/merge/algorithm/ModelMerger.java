package de.unirostock.sems.bives.merge.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.SAXException;

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
	
	protected File fileA;
	
	protected File fileB;
			
	public ModelMerger (Document docA, Document docB, Diff diffed) {
		xmlDocA = docA;
		xmlDocB = docB;
		diff = diffed;
		
	}
	
	public ModelMerger(File docA, File docB) throws IOException, JDOMException, BivesConnectionException, ParserConfigurationException, SAXException {

		xmlDocA = XmlTools.readDocument (docA);
		xmlDocB = XmlTools.readDocument (docB);
		
		fileA = docA;
		fileB = docB;
		
		
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
	
	
	public void getSlaveElements() throws ParserConfigurationException, SAXException, IOException, JDOMException {

		SAXBuilder builder = new SAXBuilder();
try{
		Document doc = builder.build(fileA);
		Document newDoc = builder.build(fileB);
		//  URL url = new URL("https://raw.github.com/hunterhacker/jdom/master/build.xml");
	     XPathFactory xpathFactory = XPathFactory.instance();
	     	
		Element delete = (Element) diff.getPatch().getDeletes();
		System.out.println("test!!!!!!!");

		List <Element> deletes = delete.getChildren();

		for(Element del : deletes) {
			if(del.getAttribute("triggeredBy") == null) continue; 
			String oldPath = del.getAttributeValue("oldPath");
			
			System.out.println(oldPath);
			
			String localPath = "";
			oldPath = oldPath.substring(1);
			for(String s : oldPath.split("/")){
				System.out.println(s);
				String[] k = s.split("\\[");
				localPath = localPath + "/*[local-name() = '" + k[0] +"'][" + k[1];
				System.out.println(localPath);
			}
			
			XPathExpression<Element> expr = xpathFactory.compile(localPath, Filters.element());
			
//			 XMLOutputter xout = new XMLOutputter();
//			  xout.output(doc, System.out);
		      
			 List<Element> elements = expr.evaluate(doc);
		     for( Element el : elements){
		    	 System.out.println(el.toString());
		     };


		      if(true) System.out.println("success");		      

		}
		

      } catch (IOException e) {
        e.printStackTrace();
      }
	}
	
}
