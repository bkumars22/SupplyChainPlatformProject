<%@ include file="common.jspf"%>
<e2i2:doctype />
<e2i2:skin />
<e2i2:preferences />
<e2i2:clientcache />
<link rel="stylesheet" type="text/css" href="skins/edl/css/edl.min.css" />
<script type="text/javascript" src="skins/edl/js/edl.min.js"></script>
<style>
    body{
        overflow: hidden;
    }
    
    .eto-breadcrumbs {
	   margin-left: 2rem;
    }
</style>
<script>
	parent.mcmApp.removeBreadcrumbs = true;
</script>

<html>
<body>
<div style="margin: auto;width: 40%;">
<h2 class="margin-top-xs-3">
    <fmt:message key="welcome.page.heading"/></h2>
<br><div>
    <fmt:message key="welcome.page.message" />
    </div>
</div>
</body>
</html>
