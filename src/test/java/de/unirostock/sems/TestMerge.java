/**
 * 
 */
package de.unirostock.sems;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.cellml.api.CellMLDiff;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.bives.merge.algorithm.ModelMerger;

import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.exception.BivesSBMLParseException;
import de.unirostock.sems.xmlutils.ds.TreeDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;


//import XPath packages
import java.io.IOException;




/**
 * @author Tom Gebhardt
 *
 */
@RunWith(JUnit4.class)
public class TestMerge
{

	/** The Constant SimpleV1. */
	public static final String SimpleV1 = "test/simpleModelV1.sbml";
	
	/** The Constant SimpleV2. */
	public static final String SimpleV2 = "test/simpleModelV2.sbml";
	
	/* Constants for local CellML model versions */
	public static final String CellMLExampleV1 = "test/bhalla_model_1999-version1-from-budhat";
	public static final String CellMLExampleV2 = "test/bhalla_model_1999-version2-from-budhat";
	/**
	 * Test functions of merge class.
	 * @throws BivesConnectionException
	 * @throws JDOMException 
	 * @throws IOException 
	 * @throws XmlDocumentParseException 
	 * @throws BivesDocumentConsistencyException 
	 * @throws BivesSBMLParseException 
	 */
	@Test
	public void testSBML() throws  IOException, JDOMException, BivesConnectionException {
		System.out.println("Testing SBML merge");
		
		try
		{
			//get Files
			File a = new File (SimpleV1);
			File b = new File (SimpleV2);

			SAXBuilder builder = new SAXBuilder();
			Document d1 = builder.build(a);
			Document d2 = builder.build(b);
			
			TreeDocument td1 = new TreeDocument (d1, null);
			TreeDocument td2 = new TreeDocument (d2, null);
			Diff diff = new SBMLDiff(td1, td2);
			
			diff.mapTrees(true, false, false);
			
			ModelMerger test = new ModelMerger (d1, d2, diff);

			test.getMerge();

		}
		catch (Exception e) {
			System.out.println("SBML Error: " + e);
		}
		
	}
	
	@Test
	public void testCellML() throws IOException, JDOMException, BivesConnectionException{
		System.out.println("Testing CellMl Merge");
		try{
		File a = new File (CellMLExampleV1);
		File b = new File (CellMLExampleV2);
		
		SAXBuilder builder = new SAXBuilder();
		Document d1 = builder.build(a);
		Document d2 = builder.build(b);
		
		TreeDocument td1 = new TreeDocument (d1, null);
		TreeDocument td2 = new TreeDocument (d2, null);
		Diff diff = new CellMLDiff(td1, td2);
		diff.mapTrees(true, false, false);
		
		ModelMerger test = new ModelMerger (d1, d2, diff);
	    

		test.getMerge();

		} catch (Exception e) {
			System.out.println("CellML Error: " + e);
		}
		
	}
	
	//@Test
	public void testSpeciesAddition() {
		System.out.println("adding species");
	}
	
	@Test
	public void testPedrosExample() {
		System.out.println("Testing Models from Pedro Mendes ");
		
		try
		{
			//get Files
			File a = new File ("test/curatedExamples/YeastGlycolysis.xml");
			File b = new File ("test/curatedExamples/YeastPPP.xml");

			SAXBuilder builder = new SAXBuilder();
			Document d1 = builder.build(a);
			Document d2 = builder.build(b);

			TreeDocument td1 = new TreeDocument (d1, null);
			TreeDocument td2 = new TreeDocument (d2, null);
			Diff diff = new SBMLDiff(td1, td2);

			diff.mapTrees(true, false, false);
			ModelMerger test = new ModelMerger (d1, d2, diff);

			test.getMerge();
	


		}
		catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
}
