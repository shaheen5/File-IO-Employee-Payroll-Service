package com.emplyeepayrollsystem;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
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
        return this.updateEmployeeDataUsingStatement(name, salary);
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
        String sql = String.format("SELECT * FROM employee_payroll WHERE name= '%s' ;",name);
        try (Connection connection = this.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
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
