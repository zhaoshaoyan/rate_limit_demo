package com.shaoyan.rate_limiter.filter;

import com.shaoyan.rate_limiter.request.RepeatSubmitRequestWrapper;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: zsy
 * @date: 2023/4/5
 */
public class RepeatAbleRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = ((HttpServletRequest) servletRequest);
        if(StringUtils.startsWithIgnoreCase(request.getContentType(), "application/json")) {
            RepeatSubmitRequestWrapper repeatSubmitRequestWrapper = new RepeatSubmitRequestWrapper(request, (HttpServletResponse) servletResponse);
            filterChain.doFilter(repeatSubmitRequestWrapper, servletResponse);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}