package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 利用filter 过滤器进行拦截前台的所有请求 用于判断用户是否已经登录过，如果没有登录过就不能直接给进入到首页
 */
// webfilter注解 用于 springboot注入原生的filter的功能 urlPatterns表示拦截的路径 这样写完还需要在启动类中里扫描注解来扫描这里
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径比较，支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response =  (HttpServletResponse)servletResponse;
        //用户请求的地址
        String requestURI = request.getRequestURI();
        log.info("拦截到请求:{}",requestURI);
        //定义数组 用于判断用户可以直接访问的内容
        //例如下面我们这里有"/backend/**"这种匹配规则 但是实际请求过来的地址可能是/employee/index.html那么如何进行地址与通配符的比较呢 可以利用AntPathMatcher 类进行操作
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", //移动端用户发送短信
                "/user/login", //用户登录放行
        };
        //调用封装的判断路径方法
        Boolean check = check(requestURI, urls);

         filterChain.doFilter(request,response);
         return; //这里直接放行所有用于测试记得关闭
        //表示可以直接访问的地址 为true
//        if (check) {
//            log.info("本次请求不需要处理:{}",requestURI);
//            filterChain.doFilter(request,response);
//            return;
//        }
//        //4.判断登录状态如果已经登录的直接放行 后台登录的判断
//        if (request.getSession().getAttribute("employee") != null) {
//
//            //用户已经登录了从session中获取id 然后存储到我们自己封装的ThreadLocal里面
//            Long empId = (Long)request.getSession().getAttribute("employee");
//
//            //存储到threadLocal中
//            BaseContext.setCurrentId(empId);
//            //放行
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        //移动端用户登录的判断 如果登录过直接放行
//        if (request.getSession().getAttribute("user") != null) {
//            //用户已经登录了从session中获取id 然后存储到我们自己封装的ThreadLocal里面
//            Long userId = (Long)request.getSession().getAttribute("user");
//            //存储到threadLocal中
//            BaseContext.setCurrentId(userId);
//            //放行
//            filterChain.doFilter(request,response);
//            return;
//        }
//        log.info("用户没有登录");
//        //没有登录的情况响应未登录结果，通过输出流方式向客户端数据结果
//        //前台的判断res.data.code === 0 && res.data.msg === 'NOTLOGIN' 所以这里需要填写NOTLOGIN
//        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
//        return;
    }

    /**
     * 该类用于处理用户访问的地址 是否直接访问
     * @param requestUrl 用户请求的地址
     * @param urls  白名单数组 相当于用户可以直接访问的地址
     * @return
     */
    public Boolean check(String requestUrl,String[] urls) {
        for (String url : urls) {
            // 利用PATH_MATCHER的match方法第一个参数表示规则(要被匹配的规则),第二个参数表示进行比较的值
            boolean match = PATH_MATCHER.match(url, requestUrl);
            //如果命中匹配那么就要返回false 表示当前的请求是不能直接访问的
            if (match) {
                return true;
            }
        }
        return false;
    }
}
