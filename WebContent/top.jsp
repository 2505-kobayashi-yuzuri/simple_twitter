<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>簡易Twitter</title>
<link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div class="main-contents">
		<div class="header">
		<!--servletのloginUser=Userに値がないif文-->
			<c:if test="${ empty loginUser }">
				<a href="login">ログイン</a>
				<a href="signup">登録する</a>
			</c:if>
			<!--servletのloginUser=Userに値があるif文-->
			<c:if test="${ not empty loginUser }">
				<a href="./">ホーム</a>
				<a href="setting">設定</a>
				<a href="logout">ログアウト</a>
			</c:if>
		</div>
		<!--値がある場合は名前とアカウント名と説明が表示-->
		<c:if test="${ not empty loginUser }">
			<div class="profile">
				<div class="name">
					<h2>
						<c:out value="${loginUser.name}" />
					</h2>
				</div>
				<div class="account">
					@
					<c:out value="${loginUser.account}" />
				</div>
				<div class="description">
					<c:out value="${loginUser.description}" />
				</div>
			</div>
		</c:if>
		<!--仮-->
		<c:if test="${ not empty errorMessages }">
			<div class="errorMessages">
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" />
					</c:forEach>
				</ul>
			</div>
			<c:remove var="errorMessages" scope="session" />
		</c:if>
		<!-- メッセージのIDが不正だったときのエラー処理 -->
		<c:if test="${ not empty errorMessageId }">
			<div class="errorMessages">
				<c:out value="${errorMessageId}" />
			</div>
		</c:if>

		<!-- 日付の絞り込み -->
		<form action="./" method="get">
			日付：
		 	<input type="date"  name="startDate" value="${startDate}" min="2020-01-01" max="2100-12-31" />
		 	～
		 	<input type="date"  name="endDate" value="${endDate}" min="2020-01-01" max="2100-12-31" />
		 	<input type="submit" value="絞り込み">
		 </form>
		<!-- ログイン中はつぶやくテキストボックスとボタンを表示 -->
		<div class="form-area">
			<c:if test="${ isShowMessageForm }">
				<form action="message" method="post">
					いま、どうしてる？<br />
					<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
					<br /> <input type="submit" value="つぶやく">（140文字まで）
				</form>
			</c:if>
		</div>
		<!-- メッセージを表示する -->
		<div class="messages">
			<!-- メッセージの数だけ繰り返す -->
			<c:forEach items="${messages}" var="message">
				<div class="message">
					<div class="account-name">
						<span class="account">
							<a href="./?user_id=<c:out value="${message.userId}"/> ">
								<c:out value="${message.account}" />
							</a>
						</span> <span class="name"><c:out value="${message.name}" /> </span>
					</div>
					<div class="text">
						<pre><c:out value="${message.text}" /></pre>
					</div>
					<div class="date">
						<fmt:formatDate value="${message.createdDate}"
							pattern="yyyy/MM/dd HH:mm:ss" />
					</div>

					<!-- 削除・編集ボタンの追加 -->
					<c:if test="${loginUser.id == message.userId}">
						<div class = "submitMassage">
							<form action="deleteMessage" method="post">
								<input name="deleteMessage_id" value="${message.id}" id="deleteMessage_id" type="hidden"/>
								<input type="submit" value="削除">
							</form>
							<form action="edit" method="get">
								<input name="editMessage_id" value="${message.id}" id="editMessage_id" type="hidden"/>
								<input type="submit" value="編集">
							</form>
						</div>
					</c:if>

					<!-- 返信の表示 -->
					<c:forEach items="${comments}" var="comment">
						<c:if test="${message.id == comment.messageId}">
							<div class="account-name">
								<span class="account">
									<c:out value="${comment.account}" />
								</span>
								<span class="name">
									<c:out value="${comment.name}" />
								</span>
							</div>
							<div class="text">
								<pre><c:out value="${comment.text}" /></pre>
							</div>
							<div class="date">
								<fmt:formatDate value="${comment.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
							</div>
						</c:if>
					</c:forEach>
					<!-- 返信テキストボックス -->
					<c:if test="${ isShowMessageForm }">
						<form action="comment" method="post">
							<br /><input name="commentMessage_id" value="${message.id}" id="commentMessage_id" type="hidden" />
							<textarea name="comment" cols="100" rows="5" class="tweet-box"></textarea>
							<br /> <input type="submit" value="返信">（140文字まで）
						</form>
					</c:if>
					<!--</div> -->
				</div>
			</c:forEach>
		</div>
		<div class="copyright">Copyright(c)kobayashi yuzuri</div>
	</div>
</body>
</html>