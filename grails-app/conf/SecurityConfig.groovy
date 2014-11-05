import au.org.ala.collectory.ActivityLog

security {

	// see DefaultSecurityConfig.groovy for all settable/overridable properties

	active = false

	loginUserDomainClass = "au.org.ala.security.Logon"
	authorityDomainClass = "au.org.ala.security.Role"
	requestMapClass = "au.org.ala.security.SecRequestMap"

    useSecurityEventListener = true

    /*onInteractiveAuthenticationSuccessEvent = { e, appCtx ->
        def user = e.authentication.principal.domainClass //Logon.get(e.authentication.principle.domainClass.id)
        ActivityLog.withTransaction {
            ActivityLog.log(user.username, au.org.ala.collectory.Action.LOGIN)
        }
   }*/

}
