package com.fileoperations;

import com.emplyeepayrollsystem.EmployeePayrollData;
import com.emplyeepayrollsystem.EmployeePayrollService;
import com.emplyeepayrollsystem.PayrollDatabaseException;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.emplyeepayrollsystem.EmployeePayrollService.IOService.DB_IO;

public class EmployeePayrollServiceTest {
    @Test
    public void given3Employees_WhenWrittenToFile_ShouldMatchEmployeeEntries(){
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1,"Jeff Bezos",100000.0),
                new EmployeePayrollData(2,"Bill Gates",200000.0),
                new EmployeePayrollData(3,"Mark Zuckerberg",300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.count(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3,entries);
    }
    @Test
    public void given3Employees_WhenWrittenToFile_ShouldPrintEmployeeEntries(){
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1,"Jeff Bezos",100000.0),
                new EmployeePayrollData(2,"Bill Gates",200000.0),
                new EmployeePayrollData(3,"Mark Zuckerberg",300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.count(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3,entries);
    }
    @Test
    public void givenEmployees_WhenWrittenToFile_ShouldReturnNumberOfFileEntries(){
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1,"Jeff Bezos",100000.0),
                new EmployeePayrollData(2,"Bill Gates",200000.0),
                new EmployeePayrollData(3,"Mark Zuckerberg",300000.0),
                new EmployeePayrollData(4,"Larry Page",120000.0),
                new EmployeePayrollData(5,"Elon Musk",500000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.count(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(5,entries);
    }
    @Test
    public void givenFile_OnReadingFromFile_ShouldMatchEmployeeCount() throws PayrollDatabaseException {
        EmployeePayrollService employeePayrollService  = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(5,employeePayrollDataList.size());
    }
    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatch_EmployeeCount(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(DB_IO);
            Assert.assertEquals(3, employeePayrollDataList.size());
        }catch (PayrollDatabaseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDatabase(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(DB_IO);
            employeePayrollService.updateEmployeeSalary("Terisa",300000.00);
            boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
            Assert.assertTrue(result);
        }catch (PayrollDatabaseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void givenNewSalaryForEmployee_WhenUpdatedUsing_PreparedStatement_ShouldSyncWithDatabase(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(DB_IO);
            employeePayrollService.updateEmployeeSalary("Terisa",400000.00);
            boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
            Assert.assertTrue(result);
        }catch (PayrollDatabaseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            LocalDate startDate = LocalDate.of(2018,01,01);
            LocalDate endDate = LocalDate.now();
            List<EmployeePayrollData> employeePayrollDataList =
                    employeePayrollService.readEmployeePayrollForDateRange(DB_IO,startDate,endDate);
            Assert.assertEquals(3,employeePayrollDataList.size());
        }catch (PayrollDatabaseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            Map<String,Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
            Assert.assertTrue(averageSalaryByGender.get("M").equals(300000.00) &&
                                       averageSalaryByGender.get("F").equals(400000.00));
        }catch (PayrollDatabaseException e){
            e.printStackTrace();
        }
    }
    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB(){
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            employeePayrollService.addEmployeeToPayroll("Mark","M",400000.00,LocalDate.now());
            boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        }catch (PayrollDatabaseException e){
            e.printStackTrace();
        }
    }
}
