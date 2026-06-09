package com.my.blog.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WebUtils {
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
