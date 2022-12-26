package com.zhou.servlet;

import javax.naming.Context;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HelloServlet extends HttpServlet   {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        this.getInitParameter();   //初始化参数
//
//        this.getServletConfig();        //servlet配置
        ServletContext context = this.getServletContext();   //servlet上下文

        String username = "zhoujie";
        context.setAttribute("username",username);
        System.out.println("Hello....");
    }
}
