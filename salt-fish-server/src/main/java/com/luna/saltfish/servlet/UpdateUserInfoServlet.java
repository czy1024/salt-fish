package com.luna.saltfish.servlet;

import com.luna.saltfish.constant.UserLoginConstant;
import com.luna.saltfish.dao.UserHandle;
import com.luna.saltfish.entity.User;
import com.luna.saltfish.tools.LoginVerify;
import com.luna.saltfish.tools.MD5;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet implementation class UpdateUserInfoServlet
 */
@WebServlet("/UpdateUserInfoServlet")
/**
 * @author luna@mac
 */
public class UpdateUserInfoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public UpdateUserInfoServlet() {
        super();
    }

    private String getNotNullParameter(HttpServletRequest request, String s) {
        return request.getParameter(s) == null ? "" : request.getParameter(s);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO：验证更新信息并写入数据库
        //之后重定向到个人页
        Boolean isLogin = LoginVerify.isLogin(request);
        String info = "";
        if (isLogin) {
            User user = (User)(request.getSession().getAttribute(UserLoginConstant.LOGIN_USER));
            String name = getNotNullParameter(request, "name");
            String phone = getNotNullParameter(request, "phone");
            String password = getNotNullParameter(request, "password");
            String verify = getNotNullParameter(request, "verify");
            if (password.length() > 0) {
                if (password.equals(verify) && password.matches("[A-Za-z0-9]{6,}")) {
                    user.setPwd(MD5.getMD5(password));
                } else {
                    info = "更新失败，两次密码不一致";
                    response.sendRedirect("user/personal.jsp?tab=info&info=" + java.net.URLEncoder.encode(info, "UTF-8"));
                    return;
                }
            }
            if (name.length() > 0 && (phone.matches("^[0-9]*$") || phone.length() == 0)) {
                user.setName(name);
                user.setPhone(phone);
                UserHandle userHandle = new UserHandle();
                try {
                    if (userHandle.doUpdate(user)) {
                        info = "更新信息成功";
                    } else {
                        info = "更新失败";
                    }
                } catch (Exception e) {
                    info = "更新失败，数据库错误";
                    e.printStackTrace();
                }
            } else {
                info = "更新失败，检查你的输入";
            }
            response.sendRedirect("user/personal.jsp?tab=info&info=" + java.net.URLEncoder.encode(info, "UTF-8"));
        } else {
            response.sendRedirect("user/login.jsp?login-info=" + java.net.URLEncoder.encode("你应该先登录", "UTF-8"));
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
