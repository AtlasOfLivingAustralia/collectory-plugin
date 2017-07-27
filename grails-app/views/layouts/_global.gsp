<script type="text/javascript">
    var COLLECTORY_CONF = {
        contextPath: "${request.contextPath}",
        locale: "${(org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).toString())?:request.locale}",
        cartodbPattern: "${grailsApplication.config.cartodb.pattern}"
    };
</script>