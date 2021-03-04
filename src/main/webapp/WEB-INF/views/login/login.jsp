<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<meta name="title" content="COJAM " />
		<meta name="description" content="" />
		<meta name="keywords" content="" />
		<meta name="robots" content="index, follow">
		<link rel="canonical" href="https://www.cojam.io/">
		<meta property="og:type" content="article" />
		<meta property="og:site_name" content="COJAM">
		<meta property="og:type" content="article">
		<meta property="og:url" content="https://www.cojam.io/">
		<meta property="og:title" content="COJAM">
		<meta property="og:image" content="/assets/image/common/meta_img.jpg">
		<meta property="og:description" content="" />
		<title>COJAM</title>
		<link href="/assets/css/style.css" rel="stylesheet">
		<link rel="shortcut icon" href="/assets/image/common/favicon.ico">
		<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    	<![endif]-->
    	<script src="/assets/js/jquery-3.3.1.js"></script>
		<script src="/assets/js/jquery-ui.js"></script>
		<script src="/assets/js/jquery-ui-timepicker-addon.js"></script>
    	<script src="/assets/js/easing.js"></script>
    	<script src="/assets/js/fontawesome.js"></script>
		<script src="/assets/js/swiper.js"></script>
		<script src="/assets/js/imagesloaded.pkgd.min.js"></script>
		<script src="/assets/js/isotope.pkgd.min.js"></script>
		<script src="/assets/js/vegas.js"></script>
		<script src="/assets/js/fittext.js"></script>
		<script src="/assets/js/lettering.js"></script>
		<script src="/assets/js/textillate.js"></script>
		<script src="/assets/js/countup.js"></script>
    	<script src="/assets/js/common.js"></script>
	</head>
	<body>
		<div class="wrap">
			





			<!-- 로그인 -->
			<div class="login-area">
				<div>
					<form name="loginForm" method="post" action="/login/auth">
						<fieldset>
							<legend>로그인</legend>
							<h2><a href="/"><img src="/assets/image/common/logo_black.png" alt="" title="" /></a></h2>
							<div>
								<a href="/user/login" class="active">Sing In</a>
								<a href="/user/join">Sing up</a>
							</div>
							<ul>
								<li>
									<input type="text" name="userIdText" title="ID" maxlength="35" class="id-icn" placeholder="ID" value="${userIdText}"/>
								</li>
								<li>
									<input type="password" name="userPassText" title="Password" maxlength="20" class="pw-icn" placeholder="PASSWORD" autocomplete="off"/>
								</li>
							</ul>
							<h3>
								<input type="checkbox" name="" value="" id="idSaveCheck" title="Stay signed in" checked="checked" />
								<label for="idSaveCheck">Stay signed in</label>
							</h3>
							<p>
								<a href="javascript:fnLogin();">Login</a>
							</p>
							<dl>
								<dt><a href="idFind.html">Find ID</a></dt>
								<dd><a href="pwEmail.html">Reset Password</a></dd>
							</dl>
						</fieldset>
						<input type="hidden" name="userId" id="userId" value="" />
						<input type="hidden" name="userPass" id="userPass" value="" />
					</form>
				</div>
			</div>
			<!-- 로그인 끝 -->
		</div>
	<script>

		function fnLogin(){

			const id = $("input[name=userIdText]");
			const pw = $("input[name=userPassText]");

			if(id.val() == ""){
				alert("아이디를 입력 해주세요.");
				id.focus();
				return false;
			}

			if(pw.val() == ""){
				alert("비밀번호를 입력 해주세요.");
				pw.focus();
				return false;
			}
			$.ajax({
				url:"/login",
				type :  "POST",
				dataType : "json",
				data : {
					memberId : id.val(),
					memberPassword : pw.val()
				},
				beforeSend : function(xhr)
				{
					//이거 안하면 403 error
					//데이터를 전송하기 전에 헤더에 csrf값을 설정한다
					//var $token = $("#token");
					//xhr.setRequestHeader($token.data("token-name"), $token.val());
				},
				success : function(response){
					if(response.code == "200"){
						// 정상 처리 된 경우
						console.debug('response',response);
						//window.location = response.item.url;	//이전페이지로 돌아가기
					} else {
						alert(response.message);
					}
				},
				error : function(a,b,c){
					console.log(a,b,c);
				}

			});
		}
	</script>
	</body>
</html>