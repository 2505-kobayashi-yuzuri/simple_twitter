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

import chapter6.beans.Comment;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;

@WebServlet(urlPatterns = { "/comment" })
public class CommentServlet extends HttpServlet {


	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public CommentServlet() {
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
		Comment comment = new Comment();
		//isVAlidがfalseならif文で処理を終える
		String text = request.getParameter("comment");
		if (!isValid(text, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}
		//UserのIDを引き出すためにUserオブジェクトを呼び出し
		//MessageのIDを引き出すためにMessageオブジェクトを呼び出し
		String strMessageId = request.getParameter("commentMessage_id");
		int messageId = Integer.parseInt(strMessageId);
		User user = (User) session.getAttribute("loginUser");
		//UserIdとMessageIdを格納
		comment.setText(text);
		comment.setUserId(user.getId());
		comment.setMessageId(messageId);
		//commentServiceクラスのInsertメソッドを呼び出し（commentを渡す）
		new CommentService().insert(comment);
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