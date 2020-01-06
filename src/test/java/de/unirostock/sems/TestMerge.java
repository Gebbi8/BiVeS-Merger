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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.unirostock.sems.bives.api.Diff;
import de.unirostock.sems.bives.exception.BivesConnectionException;
import de.unirostock.sems.bives.exception.BivesDocumentConsistencyException;
import de.unirostock.sems.bives.merge.algorithm.ModelMerger;

import de.unirostock.sems.bives.sbml.algorithm.SBMLValidator;
import de.unirostock.sems.bives.sbml.api.SBMLDiff;
import de.unirostock.sems.bives.sbml.exception.BivesSBMLParseException;
import de.unirostock.sems.bives.sbml.parser.SBMLDocument;
import de.unirostock.sems.xmlutils.exception.XmlDocumentParseException;
import de.unirostock.sems.xmlutils.tools.XmlTools;


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
		
		
		try
		{
			//get Files
			File a = new File (SimpleV1);
			File b = new File (SimpleV2);

			ModelMerger test = new ModelMerger (a, b);

			test.merge();

		}
		catch (Exception e) {
			System.out.println("SBML Error: " + e);
		}
		
	}
	
	@Test
	public void testCellML() throws IOException, JDOMException, BivesConnectionException{
		try{
		File a = new File (CellMLExampleV1);
		File b = new File (CellMLExampleV2);
		
		ModelMerger test = new ModelMerger (a,b);
	    

		test.merge();

		} catch (Exception e) {
			System.out.println("CellML Error: " + e);
		}
		
	}
}
