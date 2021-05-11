package com.emplyeepayrollsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;

    private EmployeePayrollDBService() {
    }

    public static EmployeePayrollDBService getInstance() {
        if (employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    //create connection to database and return connection object
    private Connection getConnection() throws SQLException {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
        String userName = "root";
        String passWord = "shaheen@5";
        Connection connection;
        System.out.println("Connecting to database " + jdbcURL);
        connection = DriverManager.getConnection(jdbcURL, userName, passWord);
        System.out.println("Connection is successful!! " + connection);
        return connection;
    }

    //read data from database and return list
    public List<EmployeePayrollData> readData() throws PayrollDatabaseException {
        String sql = "SELECT * FROM employee_payroll";
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    //update employee salary
    public int updateEmployeeData(String name, double salary) throws PayrollDatabaseException {
        return this.updateEmployeeDataUsingPreparedStatement(name, salary);
    }

    //update employee salary in database using jdbc prepared statement
    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws PayrollDatabaseException {
        String sql = " UPDATE employee_payroll SET salary = ? WHERE name = ? ";
        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, salary);
            preparedStatement.setString(2, name);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Error Occurred!");
        }
    }

    //update salary using statement object
    private int updateEmployeeDataUsingStatement(String name, double salary) throws PayrollDatabaseException {
        String sql = String.format("UPDATE employee_payroll SET salary = %.2f WHERE name = '%s' ;", salary, name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Error Occurred!");
        }
    }

    //execute sql query ,retrieve results ,store the results in list & return list
    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql) throws PayrollDatabaseException {
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Error");
        }
        return employeePayrollDataList;
    }

    //get employee data for given name
    public List<EmployeePayrollData> getEmployeePayrollData(String name) throws PayrollDatabaseException {
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        String sql = " SELECT * FROM employee_payroll WHERE name= ? ";
        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            employeePayrollDataList = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Error Occurred!");
        }
        return employeePayrollDataList;
    }

    // get employee data from given result set
    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws PayrollDatabaseException {
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollDataList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Error");
        }
        return employeePayrollDataList;
    }

    //get employee data who joined between startDate and endDate
    public List<EmployeePayrollData> getEmployeeForDateRange(LocalDate startDate,
                                                             LocalDate endDate) throws PayrollDatabaseException {
        String sql = String.format("SELECT * FROM employee_payroll WHERE start BETWEEN '%s' AND '%s' ;",
                Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    //get average salary group by gender from database
    public Map<String, Double> getAverageSalaryByGender() throws PayrollDatabaseException {
        String sql = "SELECT gender,AVG(salary) AS average_salary FROM employee_payroll  GROUP BY gender;";
        Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("average_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Error");
        }
        return genderToAverageSalaryMap;
    }

    // insert new employee to employee_payroll database after adding details to payroll_details DB
    public EmployeePayrollData addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
                                                     throws PayrollDatabaseException {
        int employeeId = -1;
        EmployeePayrollData employeePayrollData = null;
        Connection connection = null;
        try {
            connection = this.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Statement statement = connection.createStatement()) {
            String sql = String.format("INSERT INTO employee_payroll (name,gender,salary,start) " +
                    "VALUES ('%s','%s',%s,'%s')", name, gender, salary, Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if (rowAffected == 1) {
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) employeeId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection To Database Failed !");
        }
        try (Statement statement = connection.createStatement()) {
            double deductions = salary * 0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay * 0.1;
            double netPay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details " +
                    "(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES " +
                    "(%s,%s,%s,%s,%s,%s);", employeeId, salary, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if (rowAffected == 1) {
                employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
            }
        } catch (SQLException e) {
            throw new PayrollDatabaseException("Connection Failed!");
        }
        return employeePayrollData;
    }
}