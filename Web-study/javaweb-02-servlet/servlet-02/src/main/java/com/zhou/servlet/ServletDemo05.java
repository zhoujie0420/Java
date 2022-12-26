package com.zhou.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServletDemo05 extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream in = this.getServletContext().getResourceAsStream("/WEB-INF/classes/com/zhou/servlet/a.properties");

        Properties properties = new Properties();
        properties.load(in);
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        resp.getWriter().println(username + ":" + password);

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
