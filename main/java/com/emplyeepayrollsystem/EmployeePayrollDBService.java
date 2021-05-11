package com.emplyeepayrollsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;

    private EmployeePayrollDBService(){
    }
    public static EmployeePayrollDBService getInstance() {
        if(employeePayrollDBService == null)
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

    //update employee salary
    public int updateEmployeeData(String name, double salary) throws PayrollDatabaseException {
        return this.updateEmployeeDataUsingPreparedStatement(name, salary);
    }
    //update employee salary in database using jdbc prepared statement
    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws PayrollDatabaseException {
        String sql = " UPDATE employee_payroll SET salary = ? WHERE name = ? ";
        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1,salary);
            preparedStatement.setString(2,name);
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
    //get employee data for given name
    public List<EmployeePayrollData> getEmployeePayrollData(String name) throws PayrollDatabaseException {
        List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();
        String sql = " SELECT * FROM employee_payroll WHERE name= ? ";
        try (Connection connection = this.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,name);
            ResultSet resultSet = preparedStatement.executeQuery();
            employeePayrollDataList = this.getEmployeePayrollData(resultSet);
        }catch (SQLException e){
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
}