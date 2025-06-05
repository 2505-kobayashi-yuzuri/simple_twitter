package chapter6.filter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;
@WebFilter(urlPatterns = {"/edit", "/setting"})
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//ログイン情報をセッションで抜き取る
		HttpServletRequest filterRequest = (HttpServletRequest)request;
		HttpServletResponse filterResponse = (HttpServletResponse)response;
		HttpSession session = filterRequest.getSession();
		List<String> loginErrorMessages = new ArrayList<String>();
		User user = (User) session.getAttribute("loginUser");
		//ログイン情報がなければログイン画面へ
		if (user != null) {
			chain.doFilter(request, response);
		} else {
			loginErrorMessages.add("ログインしてください");
			session.setAttribute("loginErrorMessages", loginErrorMessages);
			filterResponse.sendRedirect("./login");
		}
	}
	@Override
	public void init(FilterConfig config) {
	}
	@Override
	public void destroy() {
	}
}