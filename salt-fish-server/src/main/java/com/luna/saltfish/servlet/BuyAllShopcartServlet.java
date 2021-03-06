package com.luna.saltfish.servlet;

import com.luna.saltfish.constant.UserLoginConstant;
import com.luna.saltfish.dao.OrderHandle;
import com.luna.saltfish.dao.ShopCartHandle;
import com.luna.saltfish.entity.Goods;
import com.luna.saltfish.entity.Order;
import com.luna.saltfish.entity.User;
import com.luna.saltfish.tools.LoginVerify;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/BuyAllShopcartServlet")
/**
 * @author luna@mac
 */
public class BuyAllShopcartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public BuyAllShopcartServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        PrintWriter resOut = response.getWriter();
        Boolean isLogined = LoginVerify.isLogin(request);
        User user = null;
        if (isLogined) {
            user = (User)request.getSession().getAttribute(UserLoginConstant.LOGIN_USER);
        } else {
            request.getRequestDispatcher("user/login.jsp?login-info=" + java.net.URLEncoder.encode("你应该先登录", "UTF-8"))
                .forward(request, response);
        }
        ShopCartHandle shopCartHandle = new ShopCartHandle();
        List<Goods> list = null;
        try {
            list = shopCartHandle.findGoodsByUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && list.size() != 0) {
            OrderHandle orderHandle = new OrderHandle();
            Order order = new Order();
            Date date = new Date();
            // 使用两个list来收集失败和成功的物品
            List<Goods> listSuc = new ArrayList<Goods>();
            List<Goods> listErr = new ArrayList<Goods>();

            for (Goods goods : list) {
                order.setGoodsId(goods.getId());
                order.setUserId(user.getId());
                order.setMessage("此物品通过购物车批量购买");
                order.setDate(date);
                try {
                    if (orderHandle.doCreate(order)) {
                        listSuc.add(goods);
                        // 购买成功则移除
                        shopCartHandle.removeList(goods.getId(), user.getId());
                    } else {
                        listErr.add(goods);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    listErr.add(goods);
                }
            }

            if (listSuc.size() > 0) {
                resOut.println("<p>物品：</p><p>");
                for (int i = 0; i < listSuc.size(); i++) {
                    resOut.println("<a href=\"goods/info.jsp?goodsId=" + listSuc.get(i).getId() + "\">"
                        + listSuc.get(i).getName() + "</a>　");
                }
                resOut.println("</p><p>购买成功!</p>");
            }
            if (listErr.size() > 0) {
                resOut.println("<p>物品：</p><p>");
                for (int i = 0; i < listErr.size(); i++) {
                    resOut.println("<a href=\"goods/info.jsp?goodsId=" + listErr.get(i).getId() + "\">"
                        + listErr.get(i).getName() + "</a>　");
                }
                resOut.println("</p><p>购买失败</p>");
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doGet(request, response);
    }
}
