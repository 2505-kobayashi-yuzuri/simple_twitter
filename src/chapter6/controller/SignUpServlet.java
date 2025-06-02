package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/signup" })
public class SignUpServlet extends HttpServlet {


	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public SignUpServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		//ユーザー登録画面JSPを呼び出し、登録画面に遷移
		request.getRequestDispatcher("signup.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		//エラーがでたときの処理
		User user = getUser(request);
		if (!isValid(user, errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("signup.jsp").forward(request, response);
			return;
		}
		//ユーザー登録完了後にトップ画面を再度表示するリダイレクト
		//トップ画面のServletを呼びたし
		//リダイレクトなのは、topServletからtiojspを呼び出さなければいけないから
		//このservletからJSPを呼ぶならフォワードでいい
		new UserService().insert(user);
		response.sendRedirect("./");
	}
	//引数requestでサーブレットから受け取った値をUserに格納
	private User getUser(HttpServletRequest request) throws IOException, ServletException {
		//ログファイルに書き込む
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		//JSPから送られてきたrequest+nameをuserに格納、userを戻す
		User user = new User();
		user.setName(request.getParameter("name"));
		user.setAccount(request.getParameter("account"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("email"));
		user.setDescription(request.getParameter("description"));
		return user;
	}

	private boolean isValid(User user, List<String> errorMessages) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		//入力された値がtureかfolseか。falseならエラー処理
		String name = user.getName();
		String account = user.getAccount();
		String password = user.getPassword();
		String email = user.getEmail();
		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}
		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}
		User userAccount = new UserService().select(user.getAccount());
		if (userAccount != null) {
			errorMessages.add("そのアカウントは既に使われています");
		}
		if (StringUtils.isEmpty(password)) {
			errorMessages.add("パスワードを入力してください");
		}
		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}
		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}