<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix='fmt' %>


<script src="js/index.js"></script>

<header class="index-top">
    <a href="./" class="logo">
        <img src="img/logo.png">
    </a>
    <div class="search">
        <form action="search" >
        <input type="text" placeholder="搜索 ${website_name} 商品/品牌/店铺" name="keyword">
        <button class="search-button" type="submit">搜索</button>
        </form>

    </div>

</header>