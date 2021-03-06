<%/*
购买历史页，被/personal.jsp包含，查找所有自己已购买商品
*/%>
<%@ page
        import="java.text.SimpleDateFormat,java.sql.*,com.luna.saltfish.tools.*,javax.servlet.http.HttpSession,java.util.*,com.luna.saltfish.entity.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page
        import="com.luna.saltfish.dao.*,com.luna.saltfish.constant.*,com.luna.saltfish.entity.*,java.sql.*,java.util.*,java.text.SimpleDateFormat" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<%
    //findGoodsByUser
    OrderHandle orderHandle = new OrderHandle();

    User me = (User) session.getAttribute(UserLoginConstant.LOGIN_USER);
    UserHandle userHandle = new UserHandle();
    List<Goods> list = null;
    list = orderHandle.findGoodsByUser(me.getId());
%>
<div class="panel panel-info">
    <div class="panel-heading">
        我的购买记录
    </div>
    <div class="panel-body">

        <%
            if (list.size() != 0) {
                for (Goods good : list) {
                    if (good.getProducterId() == null) {
                        continue;
                    }
                    User user = userHandle.findById(good.getProducterId());
        %>

        <div class="list-group-item">
            <div class="row">
                <div class="goods-img col-md-2">
                    <img class="img-rounded img-item-goods"
                         src="<%=good.getImage()%>"/>
                </div>
                <div class="col-md-10">
                    <div class="row detail-goods lead">
                        <a href="goods/info.jsp?goodsId=<%=good.getId()%>"><%=good.getName()%>
                        </a>
                    </div>
                    <div class="row detail-goods">￥<span class="text-danger"><%=Math.round(good.getPrice()) %></span>
                        <span class="detail-goods text-muted">　发布者:<%if (user.getName() != null) {%><%=user.getName() %><%} else {%><%=user.getEmail()%><%}%> </span>
                        <span class="detail-goods text-muted">　时间：
                                <%
                                    java.util.Date date = good.getCreateDate();
                                    SimpleDateFormat myFmt = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                                    String dateStr = myFmt.format(date);
                                    out.print(dateStr);
                                %>
                                </span>

                    </div>

                </div>
            </div>
        </div>
        <%
            }
        } else {
        %>
        还没有购买过商品
        <%}%>
    </div>
</div>
<%


%>