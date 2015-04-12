package tests;

// YOUR CODE FOR PART3 SHOULD GO HERE.
import java.io.*;

import global.AttrOperator;
import global.AttrType;
import global.RID;
import global.SearchKey;
import heap.HeapFile;
import index.HashIndex;
import relop.FileScan;
import relop.HashJoin;
import relop.IndexScan;
import relop.KeyScan;
import relop.Predicate;
import relop.Projection;
import relop.Schema;
import relop.Selection;
import relop.SimpleJoin;
import relop.Tuple;

public class QEPTest extends TestDriver {
	/** The display name of the test suite. */
	private static final String TEST_NAME = "Query Evaluation Pipelines tests";

	/** Size of tables in test3. */
	private static final int SUPER_SIZE = 2000;

	/** employee table schema. */
	private static Schema s_employee;
    private static HeapFile employee;
    
	/** department table schema. */
	private static Schema s_department;
    private static HeapFile department;
    
	// --------------------------------------------------------------------------

	/**
	 * Test application entry point; runs all tests.
	 */
	public static void main(String argv[]) {

		// create a clean Minibase instance
		QEPTest qept = new QEPTest();
		qept.create_minibase();

		String data_path = argv[0]; // the first argument is the data path
		System.out.println("Data path = "+data_path);

		// initialize schema for the "Employee" table
		s_employee = new Schema(5);
		s_employee.initField(0, AttrType.INTEGER, 4, "EmpId");
		s_employee.initField(1, AttrType.STRING, 20, "Name");
		s_employee.initField(2, AttrType.FLOAT, 4, "Age");
		s_employee.initField(3, AttrType.FLOAT, 4, "Salary");
		s_employee.initField(4, AttrType.INTEGER, 4, "DeptID");

		// populate the table
		String thisFilePath = data_path + "/Employee.txt";
        BufferedReader br = null;
        String line = "";
        Tuple tuple = new Tuple(s_employee);
        employee = new HeapFile(null);
        
		try {
			br = new BufferedReader(new FileReader(thisFilePath));
            line = br.readLine(); // skip first line
            
			while ((line = br.readLine()) != null) {
				String[] thisLine = line.split(",");
				
				tuple.setAllFields(Integer.parseInt(thisLine[0].trim()), thisLine[1].trim(), Float.parseFloat(thisLine[2].trim()), Float.parseFloat(thisLine[3].trim()), Integer.parseInt(thisLine[4].trim()));
				tuple.insertIntoFile(employee);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// initialize schema for the "department" table
		s_department = new Schema(4);
		s_department.initField(0, AttrType.INTEGER, 4, "DeptId");
		s_department.initField(1, AttrType.STRING, 20, "Name");
		s_department.initField(2, AttrType.FLOAT, 4, "MinSalary");
		s_department.initField(3, AttrType.FLOAT, 4, "MaxSalary");

		// populate the table
		thisFilePath = data_path + "/Department.txt";
        br = null;
        line = "";
        tuple = new Tuple(s_department);
        department = new HeapFile(null);
        
		try {
			br = new BufferedReader(new FileReader(thisFilePath));
            line = br.readLine(); // skip first line
            
			while ((line = br.readLine()) != null) {
				String[] thisLine = line.split(",");
				
				tuple.setAllFields(Integer.parseInt(thisLine[0].trim()), thisLine[1].trim(), Float.parseFloat(thisLine[2].trim()), Float.parseFloat(thisLine[3].trim()));
				tuple.insertIntoFile(department);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// run all the test cases
		System.out.println("\n" + "Running " + TEST_NAME + "...");
		boolean status = PASS;
		status &= qept.test1();
		status &= qept.test2();
		status &= qept.test3();
		status &= qept.test4();
		status &= qept.test5();
		status &= qept.test6();
		status &= qept.test7();
		status &= qept.test8();

		// display the final results
		System.out.println();
		if (status != PASS) {
			System.out.println("Error(s) encountered during " + TEST_NAME + ".");
		} else {
			System.out.println("All " + TEST_NAME
					+ " completed; verify output for correctness.");
		}

	} // public static void main (String argv[])

	/**
	 * 
	 */
	protected boolean test1() {
		try {
			// End of test 1
			System.out.print("\n\nTest 1 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 1 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test1()

	/**
	 * Display the Name for the departments with MinSalary = 1000
	 */
	protected boolean test2() {
		try {
			Selection sel = new Selection(new FileScan(s_department, department), new Predicate(AttrOperator.EQ,
			    AttrType.FIELDNO, 2, AttrType.FLOAT, 1000F));
		    Projection pro = new Projection(sel, 1);	
			pro.execute();
			
			// End of test 2
			System.out.print("\n\nTest 2 completed without exception.");
			return PASS;

		} catch (Exception exc) {
			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 2 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test2()

	/**
	 * 
	 */
	protected boolean test3() {
		try {
			// End of test 3
			System.out.print("\n\nTest 3 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 3 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test3()

	/**
	 * Display the name for employees whose Age > 30 and Salary < 1000
	 */
	protected boolean test4() {
		try {
			Selection sel = new Selection(new Selection(new FileScan(s_employee, employee), new Predicate(AttrOperator.GT,
			    AttrType.FIELDNO, 2, AttrType.FLOAT, 30F)), new Predicate(AttrOperator.LT,
			    AttrType.FIELDNO, 3, AttrType.FLOAT, 1000F));
		    Projection pro = new Projection(sel, 1);	
			pro.execute();
					
			// End of test 4
			System.out.print("\n\nTest 4 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 4 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test4()

	/**
	 * 
	 */
	protected boolean test5() {
		try {
			// End of test 5
			System.out.print("\n\nTest 5 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 5 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test5()

	/**
	 * Display the Name and Salary for employees who work in the department that has DeptId = 3
	 */
	protected boolean test6() {
		try {
		    Selection sel = new Selection(new FileScan(s_employee, employee), new Predicate(AttrOperator.EQ,
			    AttrType.FIELDNO, 4, AttrType.INTEGER, 3));
		    Projection pro = new Projection(sel, 1, 3);	
			pro.execute();
			
			// End of test 6
			System.out.print("\n\nTest 6 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 6 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test6()

	/**
	 * 
	 */
	protected boolean test7() {
		try {
			// End of test 7
			System.out.print("\n\nTest 7 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 7 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test7()

	/**
	 * Display the Name for each employee whose Salary is less than the MinSalary of his department
	 */
	protected boolean test8() {
		try {
		    HashJoin join1 = new HashJoin(new FileScan(s_employee, employee),
		        new FileScan(s_department, department), 4, 0);
		    Selection sel = new Selection(join1, new Predicate(AttrOperator.LT,
			    AttrType.FIELDNO, 3, AttrType.FIELDNO, 7));
		    Projection pro = new Projection(sel, 1);	
			pro.execute();
			
			// End of test 8
			System.out.print("\n\nTest 8 completed without exception.");
			return PASS;

		} catch (Exception exc) {

			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 8 terminated because of exception.");
			return FAIL;

		} finally {
			//printSummary(6);
			System.out.println();
		}
	} // protected boolean test8()
}
