package com.emplyeepayrollsystem;

import java.time.LocalDate;

public class EmployeePayrollData {
    public int id;
    public String name;
    char gender;
    public double salary;
    LocalDate startDate;
    public int companyId;
    public String companyName;
    int [] departmentId;

    public EmployeePayrollData(int id,String name,double salary){
        this.id=id;
        this.name=name;
        this.salary=salary;
    }
    public EmployeePayrollData(int id,String name,double salary,LocalDate startDate){
       this(id, name, salary);
       this.startDate = startDate;
    }
    public EmployeePayrollData(int id,String name,char gender,double salary,LocalDate startDate,
                               int companyId,String companyName,int[] departmentId){
        this(id, name, salary,startDate);
        this.gender = gender;
        this.companyId = companyId;
        this.companyName = companyName;
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "EmployeePayrollData{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", salary=" + salary +
                ", startDate=" + startDate +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) obj;
        return id   ==  that.id &&
                Double.compare(salary,that.salary)==0 &&
                name.equals(that.name) &&
                gender == that.gender &&
                startDate.equals(that.startDate);
    }
}
