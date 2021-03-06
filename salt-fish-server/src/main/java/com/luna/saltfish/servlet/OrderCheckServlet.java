package com.luna.saltfish.servlet;


import com.luna.saltfish.dao.OrderHandle;
import com.luna.saltfish.entity.Order;
import com.luna.saltfish.tools.LoginVerify;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author luna@mac
 */
@WebServlet("/OrderCheckServlet")
public class OrderCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public OrderCheckServlet() {
        super();
    }

    /**
     * 订单验证
     * 
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取发送消息
        String messageToSeller = request.getParameter("message-to-seller");
        Boolean isLogined = LoginVerify.isLogin(request);
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        Integer goodsId = Integer.parseInt(request.getParameter("goodsId"));
        OrderHandle orderHandle = new OrderHandle();
        Order order = new Order();
        // 获取来源url，只保留第一个参数goodsId
        String[] fromUrl = request.getHeader("Referer").split("&");
        if (isLogined != null && isLogined == true && userId != null && goodsId != null) {
            try {
                order.setGoodsId(goodsId);
                order.setUserId(userId);
                order.setMessage(messageToSeller);
                order.setDate(new Date());
                if (orderHandle.doCreate(order)) {
                    response.sendRedirect(fromUrl[0] + "&info=" + java.net.URLEncoder.encode("购买成功，消息已发送至卖家", "UTF-8"));
                    return;
                }
            } catch (Exception e) {
                response.sendRedirect(fromUrl[0] + "&info=" + java.net.URLEncoder.encode("购买失败", "UTF-8"));
                e.printStackTrace();
                return;
            }
        } else {
            response.sendRedirect(fromUrl[0] + "&info=" + java.net.URLEncoder.encode("购买失败", "UTF-8"));
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
