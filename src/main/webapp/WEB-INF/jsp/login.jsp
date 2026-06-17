<%@ include file="common.jspf"%>

<html>
<head>
<e2i2:skin name="e2-modern" />
<e2i2:dhtml padsupport="yes" />
<e2i2:clientcache enabled="no" />
<e2ot:pcmSupport calendarSupport="false" ajaxSupport="true" />


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login</title>
</head>
<body>
	<e2o:errors />
	
	<center>
		<div class="display-case">
			<div class="eto-card" style="width: 400px">
				<section class="eto-card__body">
					<img style="padding: 1.5rem;"
						src="skins/e2-modern/images/main_logo_277x65.png" width="200px">
					<form action="authenticate"
						class="eto-form eto-form--horizontal" method="get">
						<input type="hidden" name="actionType" id="actionType" value=""/>
						<span style="float: left;"> Username </span>
						<div class="eto-input">
							<span class="eto-input__container"> <input
								style="padding: 1.0rem;" class="eto-input__field" type="text"
								id="username" aria-invalid="false" aria-describedby="username"
								tabindex="1" name="username" value=""> <span
								class="eto-input__message" role="alert" aria-live="polite"
								id="username"></span>
							</span>
						</div>
						<button style="display: block; width: 100%; padding: 2.0rem;"
							name="submit" id="submit" tabindex="3" value="" type="submit"
							class="eto-btn eto-btn--primary">Log In</button>
					</form>
					<div class="eto-italic">
						<p
							style="text-align: center; padding-top: 10px; font-size: 0.85em; width: 90%">
							By logging in to this Kumar learning project, you agree to use the local demo environment for practice and testing.
						</p>
					</div>
				</section>
			</div>
			<p style="margin-top: 20px; width: 400px;">
				© 2026 Kumar Learning Project. Local demo environment for learning and testing.
			</p>
			<br>
			<script type="text/javascript">
				var conextPath = window.location.pathname.substring(0,
						window.location.pathname.indexOf("/", 2));
				var url = window.location.protocol + "//"
						+ window.location.host + conextPath;
				url = encodeURI(url);
			</script>
			<div style="width: 400px" role="alert" class="eto-messageblock"
				data-message-type="warn">
				<div class="eto-messageblock__body">
						<b>Learning Project Login:</b> Use the local kumar account below.
						<p>
							<button type="button" class="eto-btn eto-btn--link">
								<script type="text/javascript">
									document.write('<a href="' + url
										+ '/authenticate?username=kumar'
										+ '">kumar</a>');
								</script>
							</button>
						</p>
				</div>
			</div>
		</div>
	</center>
	<div class="eto-modal" id="sessionExpiredModal">
  <div class="eto-modal__content col-xs-12 col-sm-8 col-lg-6 col-xl-4">
    <header class="eto-modal__header">
      <button class="eto-modal__close" data-modal-close></button>
    </header>
    <section class="eto-modal__body">
      <p>
        <fmt:message key="error.session.expired" />   </p>
    </section>
    <footer class="eto-modal__footer">
      <button class="eto-btn" type="button" data-modal-close>No</button>
      <a href="continueWithSameUID()" class="eto-btn eto-btn--primary">Yes</a>
    </footer>
  </div>
</div>
	<script>
	sessionExpiredModal = new eto.Modal({ el: document.querySelector('#sessionExpiredModal') });
	var mesgCheck = null;
   mesgCheck=  '<%=(String)session.getAttribute("msg") %>'
   if(mesgCheck!='null'){
	   sessionExpiredModal.open();
   }
    function continueWithSameUID()
    {
    	debugger;
    	window.location.href = 'loginWithSameUID';
    }
	 </script>
	 <script type="text/javascript" src="./js/menuModule.js"></script>
</body>
</html>
