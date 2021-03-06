package com.luna.saltfish.servlet;

import com.luna.saltfish.constant.UserLoginConstant;
import com.luna.saltfish.dao.GoodsHandle;
import com.luna.saltfish.entity.Goods;
import com.luna.saltfish.entity.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Servlet implementation class GoodsCheckServlet
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 10)
@WebServlet("/GoodsCheckServlet")
/**
 * @author luna@mac
 */
public class GoodsCheckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoodsCheckServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    // 使用part获取表单域
    public String getForm(HttpServletRequest req, String formName)
            throws IllegalStateException, IOException, ServletException {
        Part part = req.getPart(formName);
        byte[] tmp = new byte[(int) part.getSize()];
        part.getInputStream().read(tmp);
        return new String(tmp, "UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nameCheck = "";
        String quantityCheck = "";
        String contentCheck = "";
        String fileCheck = "";
        response.setContentType("text/html;charset=UTF-8");
        request.setAttribute("isCheck", false);
        // 存储路径
        String savePath = request.getServletContext().getRealPath(
                "static/goods_img");
        // 处理表单
        String goodsName = getForm(request, "name-goods");
        String goodsContent = getForm(request, "content-goods");

        String goodsQuantitystr = getForm(request, "quantity-goods");
        // 先判断再转换成int防止直接抛出异常
        Float goodsQuantity = null;

        // 处理文件
        Part part = request.getPart("file");
        String type_id = getForm(request, "type_id-goods");
        String[] typeId = {"book", "live", "clothes", "eletronic", "sport", "other"};
        // id对应123456
        if (goodsName != null && (!goodsName.equals(""))) {
            if ((goodsQuantitystr != null && !goodsQuantitystr.equals(""))) {
                if (goodsContent != null && (!goodsContent.equals(""))) {
                    if (part.getSize() != 0) {
                        int type = 0;
                        for (int i = 0; i < typeId.length; i++) {
                            if (type_id.equals(typeId[i])) {
                                type = i + 1;
                            }
                        }
                        User user = (User)request.getSession().getAttribute(UserLoginConstant.LOGIN_USER);
                        Goods good = new Goods();
                        good.setTypeId(type);
                        good.setNum(1);
                        good.setProducterId(user.getId());
                        goodsQuantity = Float.parseFloat(getForm(request, "quantity-goods"));
                        good.setName(goodsName);
                        good.setContent(goodsContent);
                        good.setPrice(goodsQuantity);
                        request.setAttribute("isCheck", true);
                        int maxid = 0;
                        FileOutputStream fos = null;
                        GoodsHandle goodsHandle = new GoodsHandle();
                        try {
                            maxid = goodsHandle.getMaxId();
                            good.setId(maxid + 1);
                            int id = good.getId();
                            byte[] bt = new byte[(int) part.getSize()];
                            String imagePathName1 = savePath + "\\" + id + ".jpg";
                            String imagePathName = imagePathName1.replace('\\', '/');
                            good.setImage("static/goods_img/" + id + ".jpg");
                            fos = new FileOutputStream(imagePathName);
                            part.getInputStream().read(bt);
                            fos.write(bt);
                            Date date = new Date();
                            good.setCreateDate(date);
                            goodsHandle.doCreate(good);
                            response
                                .sendRedirect("user/personal.jsp?tab=push&userId=" + user.getId() + "&success=1&info="
                                    + java.net.URLEncoder.encode("添加成功", "UTF-8"));
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            response.sendRedirect("user/personal.jsp?tab=push&info="
                                    + java.net.URLEncoder.encode("数据库异常", "UTF-8"));
                            return;
                        } finally {
                            if (fos != null) {
                                fos.close();
                            }
                        }
                    } else {
                        fileCheck = "需要上传物品图片";
                        request.setAttribute("fileCheck", fileCheck);
                        request.getRequestDispatcher("user/personal.jsp?tab=push&success=0").forward(request, response);
                    }
                } else {
                    contentCheck = "物品简介不能为空";
                    request.setAttribute("contentCheck", contentCheck);
                    request.getRequestDispatcher("user/personal.jsp?tab=push&success=0")
                            .forward(request, response);
                }

            } else {
                quantityCheck = "物品价格不能为空且要大于0";
            }
            request.setAttribute("quantityCheck", quantityCheck);
            request.getRequestDispatcher("user/personal.jsp?tab=push&success=0").forward(request, response);
        } else {
            nameCheck = "物品名称不能为空";
            request.setAttribute("nameCheck", nameCheck);
            request.getRequestDispatcher("user/personal.jsp?tab=push&success=0").forward(request, response);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
