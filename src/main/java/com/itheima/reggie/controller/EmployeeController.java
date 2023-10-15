package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.pojo.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee one = employeeService.getOne(employeeLambdaQueryWrapper);//getone获取符合条件的一条数据对象
        //3如果没有查询到返回错误结果
        if(one==null){
            return R.error("登录失败");
        }
        //4密码比对不一致返回错误结果
        if (!one.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //5判断该账号是否禁用,禁用返回错误结果
        if (one.getStatus()!=1){
            return R.error("登录失败");
        }
        //6如果成功,就返回session对象将员工id传入
        request.getSession().setAttribute("employee",one.getId());
        return R.success(one);
    }
    //前端发送一个退出请求,我们只要将session清除一下 ,返回退出成功即可
@PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.removeAttribute("employee");
        return R.success("退出成功");
    }
}
