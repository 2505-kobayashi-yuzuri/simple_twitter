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
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {

	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		//listにして出力
		List<String> errorMessageId = new ArrayList<String>();
		errorMessageId.add("不正なパラメータが入力されました");
		String strMessageId = request.getParameter("editMessage_id");
		HttpSession session = request.getSession();

		if (!strMessageId.matches("^[0-9]+$") || StringUtils.isBlank(strMessageId)) {
			session.setAttribute("errorMessageId", errorMessageId);
			response.sendRedirect("./");
			return;
		}
		int intMessageId = Integer.parseInt(strMessageId);
		//レコードが存在しなかったらエラー処理
		Message message = new MessageService().editSelect(intMessageId);
		if (message == null) {
			session.setAttribute("errorMessageId", errorMessageId);
			response.sendRedirect("./");
			return;
		}
		request.setAttribute("editMessage", message);
		//編集画面に遷移
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		String text = request.getParameter("text");
		String strMessageId = request.getParameter("editMessage_id");
		Message message = new Message();
		message.setText(text);
		int intMessageId = Integer.parseInt(strMessageId);
		message.setId(intMessageId);
		if (!isValid(text, errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("editMessage", message);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}
		//Messageクラスからメッセージ情報を受け取るインスタンスを生成
		new MessageService().editMessage(message);
		response.sendRedirect("./");
	}

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