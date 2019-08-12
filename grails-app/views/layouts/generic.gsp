<g:set var="orgNameLong" value="${grailsApplication.config.skin.orgNameLong}"/>
<g:set var="orgNameShort" value="${grailsApplication.config.skin.orgNameShort}"/>
<!DOCTYPE html>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
   <meta name="app.version" content="${g.meta(name:'app.version')}"/>
   <meta name="app.build" content="${g.meta(name:'app.build')}"/>
   <link rel="shortcut icon" type="image/x-icon" href="favicon.ico">
   <g:set var="fluidLayout" value="${pageProperty(name:'meta.fluidLayout')?:grailsApplication.config.skin?.fluidLayout}"/>
   <g:if test="${instance}">
        <meta name="description" content="${orgNameLong} description of the ${instance?.name}. ${instance?.makeAbstract(200)}"/>
   </g:if>
   <g:else>
        <meta name="description" content="Explore ${orgNameLong}'s Natural History Collections."/>
   </g:else>
   <title><g:layoutTitle /> | ${orgNameLong}</title>
   <g:render template="/layouts/global"/>
   <r:require modules="jquery_migration, jquery_i18n, bootstrap, application, collectory" />
    <r:layoutResources disposition="head"/>
    <g:layoutHead />
</head>
<body class="${pageProperty(name:'body.class')?:'nav-collections'}" id="${pageProperty(name:'body.id')}" onload="${pageProperty(name:'body.onload')}">

    <div class="navbar navbar-inverse navbar-static-top">
        <div class="navbar-inner">
            <div class="${fluidLayout ? 'container-fluid' : 'container'}">
                <button type="button" class="btn navbar-btn" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="glyphicon-bar"></span>
                    <span class="glyphicon-bar"></span>
                    <span class="glyphicon-bar"></span>
                </button>
                <a class="navbar-brand" href="#">${orgNameLong}</a>
                <div class="navbar-collapse collapse">
                    <p class="navbar-text pull-right">
                        %{--Logged in as <a href="#" class="navbar-link">Fred Bare</a>--}%
                    </p>
                    <ul class="nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="#about">About</a></li>
                        <li><a href="#contact">Contact</a></li>
                    </ul>
                </div><!--/.nav-collapse -->
            </div><!--/.container-fluid -->
        </div><!--/.navbar-inner -->
    </div><!--/.navbar -->

    <div class="${fluidLayout ? 'container-fluid' : 'container'} testing,,,,," id="main-content">
        <g:layoutBody />
    </div><!--/.container-->

    <div id="footer">
        <div class="container-fluid">
            <div class="row">
                <a href="https://creativecommons.org/licenses/by/3.0/au/" title="External link to Creative Commons"><img src="https://i.creativecommons.org/l/by/3.0/88x31.png" width="88" height="31" alt=""></a>
                This site is licensed under a <a href="https://creativecommons.org/licenses/by/3.0/au/" title="External link to Creative Commons" class="external">Creative Commons Attribution 3.0 Australia License</a>.
            Provider content may be covered by other <a href="#terms-of-use" title="Terms of Use">Terms of Use</a>.
            </div>
        </div>
    </div><!--/#footer -->

<!-- JS resources-->
<r:layoutResources disposition="defer"/>
</body>
</html>
