package tests;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// YOUR CODE FOR PART3 SHOULD GO HERE.

public class QEPTest extends TestDriver {
	
	private static final String TEST_NAME = "Part III QEP tests";
	
	private static Schema s_employee;
	private static Schema s_department;
	
	private static Tuple tuple_department;
	private static Tuple tuple_employee;
	private static HeapFile file_department;
	private static HeapFile file_employee;
//	private static HashIndex index;
	
	
	public static void main(String argv[]) throws java.io.FileNotFoundException{
		
//		System.out.println("argv[]="+argv[0]);
		QEPTest qept = new QEPTest();
		qept.create_minibase();
		
		s_employee = new Schema(5);
		s_employee.initField(0, AttrType.INTEGER, 6, "EmpId");
		s_employee.initField(1, AttrType.STRING, 24, "Name");
		s_employee.initField(2, AttrType.INTEGER, 6, "Age");
		s_employee.initField(3, AttrType.INTEGER, 6, "Salary");
		s_employee.initField(4, AttrType.INTEGER, 6, "DeptID");
		
		s_department = new Schema(4);
		s_department.initField(0, AttrType.INTEGER, 6, "DeptId");
		s_department.initField(1, AttrType.STRING, 24, "Name");
		s_department.initField(2, AttrType.INTEGER, 6, "MinSalary");
		s_department.initField(3, AttrType.INTEGER, 6, "MaxSalary");
    
 //   System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		tuple_department = new Tuple(s_department);
		tuple_employee = new Tuple(s_employee);
		
		file_department = new HeapFile(null);
		file_employee = new HeapFile(null);
//		HashIndex index = new HashIndex(null);

		FileReader f_department;
		FileReader f_employee;
		
		// read date to Department and Employee
		try {
			f_department = new FileReader(argv[0] + "Department.txt");
			f_employee = new FileReader(argv[0] + "Employee.txt");
		} catch (java.io.FileNotFoundException exc) {
			exc.printStackTrace();
			return;
		}
	
		try {
			String[] ar = new String[5];
		
			BufferedReader in_department = new BufferedReader(f_department);
			String str_department;
			str_department = in_department.readLine();
			while((str_department = in_department.readLine()) != null){
				ar = str_department.split(",");
				
				tuple_department.setIntFld(0, Integer.parseInt(ar[0].trim()));
				tuple_department.setStringFld(1, ar[1]);
				tuple_department.setIntFld(2, Integer.parseInt(ar[2].trim()));
				tuple_department.setIntFld(3, Integer.parseInt(ar[3].trim()));
				
				RID rid = file_department.insertRecord(tuple_department.getData());
//				index.insertEntry(new SearchKey(
//				tuple_department.print();
			}
			System.out.println();
			
			BufferedReader in_employee = new BufferedReader(f_employee);	
			String str_employee;		
			str_employee = in_employee.readLine();
			while((str_employee = in_employee.readLine()) != null){
				ar = str_employee.split(",");
			
				tuple_employee.setIntFld(0, Integer.parseInt(ar[0].trim()));	// empID
				tuple_employee.setStringFld(1, ar[1]);
				tuple_employee.setIntFld(2, Integer.parseInt(ar[2].trim()));	// age
				tuple_employee.setIntFld(3, Integer.parseInt(ar[3].trim()));	// salary
				tuple_employee.setIntFld(4, Integer.parseInt(ar[4].trim()));	// deptID
				
				RID rid = file_employee.insertRecord(tuple_employee.getData());
//				index.insertEntry(new SearchKey(
//				tuple_employee.print();
			}
			
			in_department.close();
			in_employee.close();
		} catch(IOException e){
			return;
		}
		
		
		System.out.println("\n"+"Running " + TEST_NAME + "...");
		boolean status = PASS;
		status &= qept.test1();
		status &= qept.test3();
		status &= qept.test5();
		status &= qept.test7();
		
		System.out.println();
		if(status != PASS){
			System.out.println("Error(s) encountered during " + TEST_NAME + ".");
		} else {
			System.out.println("All " + TEST_NAME + " completed; verify output for correctness.");
		}
	}// public static void main (String argv[])
	
	protected boolean test1(){
		try {
			System.out.println("\nTest 1: Display for each employee his Name and Salary");
			initCounts();
			saveCounts(null);
			
			FileScan scan = new FileScan(s_employee, file_employee);
			Projection pro = new Projection(scan, 1, 3);
			pro.execute();
			saveCounts("test1");
			
			System.out.print("\n\nTest 1 completed without exception.");
			return PASS;
			
		} catch (Exception exc){
			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 1 terminated because of exception.");
			return FAIL;
		} finally {
			printSummary(6);
			System.out.println();
		}
	} // protected boolean test1()
	
		protected boolean test3(){
		try {
			System.out.println("\nTest 3: Display the Name for the departments with MinSalary = MaxSalary");
			initCounts();
			saveCounts(null);
			
			FileScan scan = new FileScan(s_department, file_department);
			Predicate[] preds = new Predicate[] { new Predicate(AttrOperator.EQ, AttrType.FIELDNO, 2, AttrType.FIELDNO, 3) };
			Selection sel = new Selection(scan, preds);
			Projection pro = new Projection(sel, 1);
			pro.execute();
			saveCounts("test3");
			
			System.out.print("\n\nTest 3 completed without exception.");
			return PASS;
			
		} catch (Exception exc){
			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 3 terminated because of exception.");
			return FAIL;
		} finally {
			printSummary(6);
			System.out.println();
		}
	} // protected boolean test1()
	
		protected boolean test5(){
		try {
			System.out.println("\nTest 5: For each employee, display his Salary and the Name of his department");
			initCounts();
			saveCounts(null);
			
			HashJoin join = new HashJoin(new FileScan(s_employee, file_employee), new FileScan(s_department, file_department), 4, 0);
			Projection pro = new Projection(join, 1, 3, 6);
			pro.execute();
			saveCounts("test5");
			
			System.out.print("\n\nTest 5 completed without exception.");
			return PASS;
			
		} catch (Exception exc){
			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 5 terminated because of exception.");
			return FAIL;
		} finally {
			printSummary(6);
			System.out.println();
		}
	} // protected boolean test1()
	
		protected boolean test7(){
		try {
			System.out.println("\nTest 7: Display the Salary for each employee who works in a department that has MaxSalary > 100000");
			initCounts();
			saveCounts(null);
			
			Predicate[] preds = new Predicate[] { new Predicate(AttrOperator.GT, AttrType.FIELDNO, 8, AttrType.INTEGER, 100000) };
			
			HashJoin join = new HashJoin(new FileScan(s_employee, file_employee), new FileScan(s_department, file_department), 4, 0);
//			join.execute();
			Selection sel = new Selection(join, preds);
//			sel.execute();
			Projection pro = new Projection(sel, 1, 3);
			pro.execute();
			saveCounts("test7");
			
			System.out.print("\n\nTest 7 completed without exception.");
			return PASS;
			
		} catch (Exception exc){
			exc.printStackTrace(System.out);
			System.out.print("\n\nTest 7 terminated because of exception.");
			return FAIL;
		} finally {
			printSummary(6);
			System.out.println();
		}
	} // protected boolean test1()
}
