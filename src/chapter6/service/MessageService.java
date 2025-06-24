package chapter6.service;

import static chapter6.utils.CloseableUtil.*;
import static chapter6.utils.DBUtil.*;

import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.dbunit.Assertion;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Test;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.dao.MessageDao;
import chapter6.dao.UserMessageDao;
import chapter6.logging.InitApplication;
import chapter6.utils.DBUtil;

public class MessageService {


	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public MessageService() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}
	//message情報すべてをmessageServletから受け取り、Daoに流すメソッド
	//ここを参考にdeleteメソッドを作成
	public void insert(Message message) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			//受け取った値をmessageDaoに受け渡す
			new MessageDao().insert(connection, message);
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}
	//つぶやき情報の表示を受け渡すメソッド
	public List<UserMessage> select(String userId, String startDate ,String endDate) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		final int LIMIT_NUM = 1000;
		Connection connection = null;
		try {
			Integer id = null;
			if(!StringUtils.isEmpty(userId)) {
				id = Integer.parseInt(userId);
			}
			String setStartDate = null;
			String setEndDate = null;
			//入力があれば入力値を格納、なければディフォルト値を格納
			if(StringUtils.isBlank(startDate)) {
				setStartDate = "2020-01-01 00:00:00";
			} else {
				setStartDate = startDate + " 00:00:00";
			}

			if(StringUtils.isBlank(endDate)) {
				Date nowDate = new Date();
				SimpleDateFormat simpleDefaultEnd= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				setEndDate = simpleDefaultEnd.format(nowDate);
			} else {
				setEndDate = endDate + " 23:59:59";
			}
			connection = getConnection();
			List<UserMessage> messages = new UserMessageDao().select(connection, id, LIMIT_NUM, setStartDate, setEndDate);
			commit(connection);
			return messages;
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	public void delete(int deleteMessageId) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().delete(connection, deleteMessageId);
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	public Message editSelect(int editMessageId) {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());
		Connection connection = null;
		try {
			connection = getConnection();
			Message message = new MessageDao().editSelect(connection, editMessageId);
			commit(connection);
			return message;
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}

	public void editMessage(Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().editMessage(connection, message);
			commit(connection);
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			close(connection);
		}
	}


	@Test
	public void testInsertMessage() throws Exception {

		List<Message> insertMessagesList = new ArrayList<>();

		//テストのインスタンスを生成
		Message message001 = new Message();
		message001.setText("テストデータ004");
		message001.setUserId(1);
		insertMessagesList.add(message001);

		Message message002 = new Message();
		message002.setText("テストデータ005");
		message002.setUserId(2);
		insertMessagesList.add(message002);

		Message message003 = new Message();
		message003.setText("テストデータ006");
		message003.setUserId(3);
		insertMessagesList.add(message003);

		MessageService messageService = new MessageService();

		for(int i = 0; i < insertMessagesList.size(); i++) {
			messageService.insert(insertMessagesList.get(i));
		}

		//データ
		IDatabaseConnection connection = null;
		try {
			Connection conn = DBUtil.getConnection();
			connection = new DatabaseConnection(conn);
			//メソッド実行した実際のテーブル
			IDataSet databaseDataSet = connection.createDataSet();
			ITable actualTable = databaseDataSet.getTable("messages");
			// テスト結果として期待されるべきテーブルデータを表すITableインスタンス
			IDataSet expectedDataSet = new FlatXmlDataSet(new
					File("messages_data_insert.xml"));

			ITable expectedTable = expectedDataSet.getTable("messages");

			//期待されるITableと実際のITableの比較
			//id、created_date、updated_dateを除いたデータを確認
			Assertion.assertEqualsIgnoreCols(actualTable, expectedTable, new
					String[] {"id",  "created_date", "updated_date"});

		} finally {
			if (connection != null)
				connection.close();
		}
	}
}