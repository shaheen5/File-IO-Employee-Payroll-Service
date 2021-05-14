package com.emplyeepayrollsystem;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {

    public enum IOService{CONSOLE_IO,FILE_IO,DB_IO,REST_IO}
    private List<EmployeePayrollData> employeePayrollDataList;
    private static EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService(){
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollDataList){
        this();
        this.employeePayrollDataList = employeePayrollDataList;
    }
    //method to read data
    public List readEmployeePayrollData(IOService ioService) throws PayrollDatabaseException {
        if(ioService.equals(IOService.CONSOLE_IO)) {
            Scanner consoleInputReader = new Scanner(System.in);
            System.out.println("Enter Employee Id:");
            int id=consoleInputReader.nextInt();
            System.out.println("Enter Employee name:");
            consoleInputReader.nextLine();
            String name=consoleInputReader.nextLine();
            System.out.println("Enter Employee salary:");
            double salary=consoleInputReader.nextInt();
            employeePayrollDataList.add(new EmployeePayrollData(id,name,salary));
        }
        List<String> employeeList = null;
        if(ioService.equals(IOService.FILE_IO)) {
            employeeList = new EmployeePayrollFileIOService().readData();
            return employeeList;
        }
        if(ioService.equals(IOService.DB_IO)) {
            this.employeePayrollDataList = employeePayrollDBService.readData();
            return employeePayrollDataList;
        }
        return null;
    }
    //method to write data on console
    public void writeEmployeePayrollData(IOService ioService) {
        if(ioService.equals(IOService.CONSOLE_IO))
            System.out.println("\nWriting Employee Payroll Roaster To console::\n"+employeePayrollDataList);
        else if (ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().writeData(employeePayrollDataList);
    }
    //method to count entries in a file
    public long count(IOService ioService) {
        if(ioService.equals(IOService.FILE_IO))
            return new EmployeePayrollFileIOService().countEntries();
        return 0;
    }

    //method to print entries from a file
    public void printData(IOService ioService){
        if(ioService.equals(IOService.FILE_IO))
            new EmployeePayrollFileIOService().printData();
    }
    //find employee data having given name
    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollDataList.stream()
                   .filter(employeePayrollDataItem ->employeePayrollDataItem.name.equals(name))
                   .findFirst()
                   .orElse(null);
    }

    //update employee salary in Database and program
    public void updateEmployeeSalary(String name, double salary) throws PayrollDatabaseException {
        int result = employeePayrollDBService.updateEmployeeData(name,salary);
        if(result == 0)
            throw new PayrollDatabaseException("Update To Database Failed!");
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData != null)
            employeePayrollData.salary = salary;
    }

    //check whether database is in sync with employee payroll data in program
    public boolean checkEmployeePayrollInSyncWithDB(String name) throws PayrollDatabaseException {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }
    //read Employee payroll from database having joining date within given range
    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService,
                                                                     LocalDate startDate, LocalDate endDate)
                                                                     throws PayrollDatabaseException {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeeForDateRange(startDate,endDate);
        return null;
    }
    //read the average salary of employee by gender from database
    public Map<String, Double> readAverageSalaryByGender(IOService ioService) throws PayrollDatabaseException {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
    }
    // add new employee to employee payroll
    public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate,int companyId,
                                     String companyName,int[] departmentId)
                                     throws PayrollDatabaseException {
        employeePayrollDataList.add(employeePayrollDBService.addEmployeeToPayroll( name, gender, salary, startDate,
                companyId,companyName,departmentId));
    }
}