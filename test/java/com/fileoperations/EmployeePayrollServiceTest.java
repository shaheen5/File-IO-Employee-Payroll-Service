package com.fileoperations;

import com.emplyeepayrollsystem.EmployeePayrollData;
import com.emplyeepayrollsystem.EmployeePayrollService;
import com.emplyeepayrollsystem.PayrollDatabaseException;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.emplyeepayrollsystem.EmployeePayrollService.IOService.DB_IO;

public class EmployeePayrollServiceTest {
    @Test
    public void given3Employees_WhenWrittenToFile_ShouldMatchEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.count(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3, entries);
    }

    @Test
    public void given3Employees_WhenWrittenToFile_ShouldPrintEmployeeEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.count(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3, entries);
    }

    @Test
    public void givenEmployees_WhenWrittenToFile_ShouldReturnNumberOfFileEntries() {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0),
                new EmployeePayrollData(4, "Larry Page", 120000.0),
                new EmployeePayrollData(5, "Elon Musk", 500000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.count(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(5, entries);
    }

    @Test
    public void givenFile_OnReadingFromFile_ShouldMatchEmployeeCount() throws PayrollDatabaseException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(5, employeePayrollDataList.size());
    }

    @Test
    public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatch_EmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(DB_IO);
            Assert.assertEquals(3, employeePayrollDataList.size());
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldSyncWithDatabase() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(DB_IO);
            employeePayrollService.updateEmployeeSalary("Terisa", 300000.00);
            boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
            Assert.assertTrue(result);
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdatedUsing_PreparedStatement_ShouldSyncWithDatabase() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            List<EmployeePayrollData> employeePayrollDataList = employeePayrollService.readEmployeePayrollData(DB_IO);
            employeePayrollService.updateEmployeeSalary("Terisa", 100000.00);
            boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
            Assert.assertTrue(result);
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            LocalDate startDate = LocalDate.of(2018, 01, 01);
            LocalDate endDate = LocalDate.now();
            List<EmployeePayrollData> employeePayrollDataList =
                    employeePayrollService.readEmployeePayrollForDateRange(DB_IO, startDate, endDate);
            Assert.assertEquals(3, employeePayrollDataList.size());
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(DB_IO);
            Assert.assertTrue(averageSalaryByGender.get("M").equals(300000.00) &&
                    averageSalaryByGender.get("F").equals(400000.00));
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            employeePayrollService.addEmployeeToPayroll("Shaheen", 'F', 600000.00, LocalDate.now(),
                    6,"Accenture",new int[]{1,3});
            boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Shaheen");
            Assert.assertTrue(result);
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void givenEmployee_WhenRemoved_ShouldSyncWithDB() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        try {
            employeePayrollService.readEmployeePayrollData(DB_IO);
            int result = employeePayrollService.removeEmployeeFromPayroll(7);
            Assert.assertEquals(5,result);
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries(){
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Jeff Bezos", 'M', 100000.0, LocalDate.now(),
                        2,"Infosys",new int[]{1,4}),
                new EmployeePayrollData(0, "Bill Gates", 'M', 200000.0, LocalDate.now(),
                        3,"Infosys",new int[]{1,2}),
                new EmployeePayrollData(0, "Mark Zukeberg", 'M', 300000.0, LocalDate.now(),
                        4,"TCS",new int[]{3}),
                new EmployeePayrollData(0, "Sunder", 'M', 600000.0, LocalDate.now(),
                        5,"HeadStrait Technology",new int[]{4}),
                new EmployeePayrollData(0, "Mukesh", 'M', 100000.0, LocalDate.now(),
                        6,"Accenture",new int[]{1,4}),
                new EmployeePayrollData(0, "Anil", 'M', 200000.0, LocalDate.now(),
                        7,"Atos",new int[]{2,3})
        };
        try{
            EmployeePayrollService employeePayrollService = new EmployeePayrollService();
            employeePayrollService.readEmployeePayrollData(DB_IO);
            Instant start = Instant.now();
            employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
            Instant end = Instant.now();
            System.out.println("Duration without thread: "+ Duration.between(start, end));
            Assert.assertEquals(7, employeePayrollService.count(DB_IO));
        } catch (PayrollDatabaseException e) {
            e.printStackTrace();
        }

    }
}
