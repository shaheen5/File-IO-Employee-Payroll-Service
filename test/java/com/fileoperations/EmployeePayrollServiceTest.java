package com.fileoperations;

import com.emplyeepayrollsystem.EmployeePayrollData;
import com.emplyeepayrollsystem.EmployeePayrollService;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

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
}
