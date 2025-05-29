package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Message;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class MessageDao {


	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public MessageDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}
	//messageServiceから受け取ったmessageの情報すべてをDBに流すメソッド
	public void insert(Connection connection, Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO messages ( ");
			sql.append("    user_id, ");
			sql.append("    text, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, ");                  // user_id
			sql.append("    ?, ");                  // text
			sql.append("    CURRENT_TIMESTAMP, ");  // created_date
			sql.append("    CURRENT_TIMESTAMP ");   // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());
			//バインド変数で動的な変える
			ps.setInt(1, message.getUserId());
			ps.setString(2, message.getText());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
	public void delete(Connection connection, int messageId) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" DELETE ");
			sql.append(" FROM messages ");
			sql.append(" WHERE ");
			sql.append("    id = ? ");

			ps = connection.prepareStatement(sql.toString());
			//バインド変数で動的な変える
			ps.setInt(1, messageId);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public void editMessage(Connection connection, Message message) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			int id = message.getId();
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE messages ");
			sql.append(" SET ");
			sql.append(" text = ?,");
			sql.append(" updated_date = CURRENT_TIMESTAMP ");
			sql.append(" WHERE ");
			sql.append("    id = ? ");

			ps = connection.prepareStatement(sql.toString());
			//バインド変数で動的な変える
			ps.setString(1, message.getText());
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}
	public Message editSelect(Connection connection, int editMessageId) {

		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" SELECT * ");
			sql.append("FROM messages ");
			sql.append("WHERE id = ? ");

			ps = connection.prepareStatement(sql.toString());
				ps.setInt(1, editMessageId);

			ResultSet rs = ps.executeQuery();
			Message message = toMessage(rs);
			return message;
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object(){}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	private Message toMessage(ResultSet rs) throws SQLException {
		log.info(new Object(){}.getClass().getEnclosingClass().getName() +
				" : " + new Object(){}.getClass().getEnclosingMethod().getName());

			try {
				Message message = new Message();
				while (rs.next()) {
				message.setId(rs.getInt("id"));
				message.setText(rs.getString("text"));
				message.setUserId(rs.getInt("user_id"));
				message.setCreatedDate(rs.getTimestamp("created_date"));
				message.setUpdatedDate(rs.getTimestamp("updated_date"));
				}
			return message;
		} finally {
			close(rs);
		}
	}

}