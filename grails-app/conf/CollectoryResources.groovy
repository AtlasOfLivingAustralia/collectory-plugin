modules = {
    'google-maps-api' {
        resource  url: 'https://maps.google.com/maps/api/js?v=3&sensor=true', attrs: [type: "js"], disposition: 'head'
    }
    application {
        resource url:[dir:'js', file:'application.js', plugin:'collectory-plugin']
    }
    smoothness {
        resource url:[dir:'css/smoothness', file:'jquery-ui-1.8.16.custom.css', plugin:'collectory-plugin']
    }
    jquery_jsonp {
        dependsOn 'jquery_migration'
        resource url:[dir:'js', file:'jquery.jsonp-2.1.4.min.js', plugin:'collectory-plugin']
    }
    jquery_tools {
        dependsOn 'jquery_migration'
        resource url:[dir:'js', file:'jquery.tools.min.js', plugin:'collectory-plugin']
    }
    jquery_json {
        dependsOn 'jquery_migration'
        resource url:[dir:'js', file:'jquery.json-2.2.min.js', plugin:'collectory-plugin']
    }
    jquery_i18n {
        dependsOn 'jquery_migration'
        resource url:[dir:'js', file:'jquery.i18n.properties-1.0.9.min.js', plugin:'collectory-plugin']
    }
    fancybox {
        resource url:[dir:'js/jquery.fancybox/fancybox', file:'jquery.fancybox-1.3.1.css', plugin:'collectory-plugin']
        resource url:[dir:'js/jquery.fancybox/fancybox', file:'jquery.fancybox-1.3.1.pack.js', plugin:'collectory-plugin']
    }
    jstree {
        resource url:[dir:'js', file:'jquery.jstree.js', plugin:'collectory-plugin']
        resource url:[dir:'js/themes/classic', file:'style.css', plugin:'collectory-plugin'], attrs:[media:'screen, projection, print']
    }
    jquery_ui_custom {
        dependsOn 'jquery_migration'
        resource url:[dir:'js', file:'jquery-ui-1.8.16.custom.min.js', plugin:'collectory-plugin']
    }
    datadumper {
        resource url:[dir:'js', file:'datadumper.js', plugin:'collectory-plugin']
    }
    bbq {
        dependsOn 'jquery_migration'
        resource url:[dir:'js', file:'jquery.ba-bbq.min.js', plugin:'collectory-plugin']
    }
    openlayers {
        resource url:[dir:'js', file:'OpenLayers/OpenLayers.js', plugin:'collectory-plugin']
        resource url:[dir:'js', file:'OpenLayers/theme/default/style.css', plugin:'collectory-plugin']
    }
    map {
        resource url:[dir:'js', file:'map.js', plugin:'collectory-plugin']
    }
    datasets {
        resource url:[dir:'js', file:'datasets.js', plugin:'collectory-plugin']
    }
    rotate {
        resource url:[dir:'js', file:'jQueryRotateCompressed.2.1.js', plugin:'collectory-plugin']
    }
    bigbuttons {
        resource url:[dir:'css', file:'temp-style.css', plugin:'collectory-plugin']
    }
    debug {
        resource url:[dir:'js', file:'debug.js', plugin:'collectory-plugin']
    }
    change {
        resource url:[dir:'js', file:'change.js', plugin:'collectory-plugin']
    }
    json2 {
        resource url:[dir:'js', file:'json2.js', plugin:'collectory-plugin']
    }
    fileupload {
        dependsOn('bootstrap')
        resource url:[dir:'js', file:'bootstrap-fileupload.min.js', plugin:'collectory-plugin']
        resource url:[dir:'css', file:'bootstrap-fileupload.min.css', plugin:'collectory-plugin']
    }
    charts {
        dependsOn 'jquery_i18n'
        resource url:[dir:'js', file:'charts2.js', plugin:'collectory-plugin']
        resource url:[dir:'js', file:'charts.js', plugin:'collectory-plugin']
    }
    collectory {
        dependsOn 'jquery_ui_custom,smoothness,jquery_i18n,jquery_json,jquery_tools,jquery_jsonp,fancybox,openlayers,map'
        resource url:[dir:'js', file:'collectory.js', plugin:'collectory-plugin']
        resource url:[dir:'css', file:'temp-style.css', plugin:'collectory-plugin']
    }
    jquery_migration{
        // Needed to support legacy js components that do not work with latest versions of jQuery
        dependsOn 'jquery'
        resource url:[ dir: 'js',file:'jquery-migrate-1.2.1.min.js', plugin:'collectory-plugin']
    }
}
