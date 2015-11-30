<%--
  Created by IntelliJ IDEA.
  User: dos009@csiro.au
  Date: 27/11/2015
  Time: 4:54 PM
  To change this template use File | Settings | File Templates.
--%><%@ expressionCodec="xml" %><%@ page contentType="text/html;charset=UTF-8" %><?xml version="1.0" encoding="UTF-8" ?>
<rss xmlns:ipt="http://ipt.gbif.org/" version="2.0">
    <channel>
        <title>${feed.title?.encodeAsXML()}</title>
        <link>${feed.link?.encodeAsXML()}</link>
        <description>${feed.description?.encodeAsXML()}</description>
        <g:each in="${items}" var="item">
            <item>
                <title>${item.title?.encodeAsXML()}</title>
                <link>${item.link?.encodeAsXML()}</link>
                <pubDate><g:formatDate date="${item.date}" format="E, dd MMM yyyy HH:mm:ss Z"/></pubDate>
                <description><![CDATA[ ${item.description} ]]></description>
                <guid isPermaLink="false">${item.guid?.encodeAsXML()}</guid>
                <ipt:eml>${item.emlLink?.encodeAsXML()}</ipt:eml>
            </item>
        </g:each>
    </channel>
</rss>