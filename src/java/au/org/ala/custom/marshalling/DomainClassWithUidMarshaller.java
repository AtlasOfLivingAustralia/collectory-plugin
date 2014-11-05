package au.org.ala.custom.marshalling;

import au.org.ala.collectory.ProviderGroup;
import grails.converters.JSON;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsDomainClass;
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty;
import org.codehaus.groovy.grails.support.proxy.ProxyHandler;
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException;
import org.codehaus.groovy.grails.web.converters.marshaller.json.DomainClassMarshaller;
import org.codehaus.groovy.grails.web.json.JSONWriter;

/**
 * Overrides the way associations are marshalled as JSON to include UID for ProviderGroup entities.
 *
 * Created by markew
 * Date: Sep 13, 2010
 * Time: 9:52:48 AM
 */
public class DomainClassWithUidMarshaller extends DomainClassMarshaller {

    public DomainClassWithUidMarshaller(boolean includeVersion, GrailsApplication application) {
        super(includeVersion, application);
    }

    public DomainClassWithUidMarshaller(boolean includeVersion, ProxyHandler proxyHandler, GrailsApplication application) {
        super(includeVersion, proxyHandler, application);
    }

    @Override
    protected void asShortObject(Object refObj, JSON json, GrailsDomainClassProperty idProperty, GrailsDomainClass referencedDomainClass) throws ConverterException {
//        GrailsDomainClassProperty uidProperty = referencedDomainClass.getPropertyByName("uid");
//        if (uidProperty != null) {
//            System.out.println("UID = " + extractValue(refObj, uidProperty));
//        }
        if (referencedDomainClass.getShortName().equals("Institution") ||
            referencedDomainClass.getShortName().equals("DataProvider")) {
            JSONWriter writer = json.getWriter();
            writer.object();
            writer.key("class").value(referencedDomainClass.getName());
            writer.key("uid").value(extractValue(refObj, referencedDomainClass.getPropertyByName("uid")));
            writer.endObject();
        } else {
            super.asShortObject(refObj, json, idProperty, referencedDomainClass);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }
}
