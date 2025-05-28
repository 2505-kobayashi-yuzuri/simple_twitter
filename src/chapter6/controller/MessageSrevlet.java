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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/message" })
public class MessageSrevlet extends HttpServlet {


	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public MessageSrevlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}
	//メッセージをPOSTから受け取り処理するメソッド
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		//セッションスコープの宣言（セッションを閉じない限り保持）
		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();
		//isVAlidがfalseならif文で処理を終える
		String text = request.getParameter("text");
		if (!isValid(text, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}
		//Messageクラスからメッセージ情報を受け取るインスタンスを生成
		Message message = new Message();
		message.setText(text);
		//Userからユーザー情報（loginUser）を引数で保持してuser変数に格納
		User user = (User) session.getAttribute("loginUser");
		message.setUserId(user.getId());
		//messageServiceクラスのInsertメソッドを呼び出し（messageを渡す）
		new MessageService().insert(message);
		response.sendRedirect("./");//top.jspに戻る
	}
	//エラー処理のバリデーションとエラーlistを作成メソッド
	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}
