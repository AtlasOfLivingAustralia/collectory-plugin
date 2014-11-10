modules = {
    application {
        resource url:[dir:'js', file:'application.js', plugin:'collectory-plugin']
    }
    jquery {
        resource url:[dir:'js', file:'jquery.min.js', plugin:'collectory-plugin']
    }
    smoothness {
        resource url:[dir:'css/smoothness', file:'jquery-ui-1.8.16.custom.css', plugin:'collectory-plugin']
    }
    jquery_jsonp {
        //resource url:[dir:'js', file:'jquery.min.js', plugin:'collectory-plugin']
        resource url:[dir:'js', file:'jquery.jsonp-2.1.4.min.js', plugin:'collectory-plugin']
    }
    jquery_tools {
        resource url:[dir:'js', file:'jquery.tools.min.js', plugin:'collectory-plugin']
    }
    jquery_json {
        resource url:[dir:'js', file:'jquery.json-2.2.min.js', plugin:'collectory-plugin']
    }
    jquery_i18n {
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
        resource url:[dir:'js', file:'jquery-ui-1.8.16.custom.min.js', plugin:'collectory-plugin']
    }
    datadumper {
        resource url:[dir:'js', file:'datadumper.js', plugin:'collectory-plugin']
    }
    bbq {
        resource url:[dir:'js', file:'jquery.ba-bbq.min.js', plugin:'collectory-plugin']
    }
    openlayers {
        resource url:[file:'theme/default/style.css', plugin:'collectory-plugin']
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
        resource url:[dir:'js', file:'bootstrap-fileupload.min.js', plugin:'collectory-plugin']
        resource url:[dir:'css', file:'bootstrap-fileupload.min.css', plugin:'collectory-plugin']
    }
    collectory {
        //dependsOn 'bootstrap,jquery,jquery_i18n,jquery_json,jquery_tools,jquery_jsonp,map,fancybox,smoothness,jstree,jquery_ui_custom,openlayers'
        //dependsOn 'bootstrap,jquery,jquery_ui_custom,smoothness,jquery_i18n,jquery_json,jquery_tools,jquery_jsonp,fancybox,openlayers,map'
        dependsOn 'bootstrap,jquery_ui_custom,smoothness,jquery_i18n,jquery_json,jquery_tools,jquery_jsonp,fancybox,openlayers,map'
        resource url:[dir:'js', file:'collectory.js', plugin:'collectory-plugin']
        resource url:[dir:'css', file:'temp-style.css', plugin:'collectory-plugin']
    }
    charts {
        resource url:[dir:'js', file:'charts2.js', plugin:'collectory-plugin']
        resource url:[dir:'js', file:'charts.js', plugin:'collectory-plugin']
    }
    //jquery {
    //    resource url:[dir:'js', file:'jquery.min.js', plugin:'collectory-plugin']
    //    resource url:[file:'theme/default/style.css', plugin:'collectory-plugin']
    //}
//
//
//
//    application {
//        resource url:'js/application.js'
//
//        //resource url:[dir:'bootstrap/js', file:'bootstrap.js', plugin:'biocache-hubs'], disposition: 'head', exclude: '*'
//    }
//    smoothness {
//        resource url:'css/smoothness/jquery-ui-1.8.16.custom.css'
//    }
//    jquery_jsonp {
//        resource url:'js/jquery.jsonp-2.1.4.min.js'
//    }
//    jquery_tools {
//        resource url: 'js/jquery.tools.min.js'
//    }
//    jquery_json {
//        resource url:'js/jquery.json-2.2.min.js'
//    }
//    jquery_i18n {
//        resource url:'js/jquery.i18n.properties-1.0.9.min.js'
//    }
//    fancybox {
//        resource url: 'js/jquery.fancybox/fancybox/jquery.fancybox-1.3.1.css'
//        resource url: 'js/jquery.fancybox/fancybox/jquery.fancybox-1.3.1.pack.js'
//    }
//    jstree {
//        resource url: 'js/jquery.jstree.js'
//        resource url:[dir:'js/themes/classic', file:'style.css'], attrs:[media:'screen, projection, print']
//    }
//    jquery_ui_custom {
//       resource url: 'js/jquery-ui-1.8.16.custom.min.js'
//    }
//    datadumper {
//       resource url: 'js/datadumper.js'
//    }
//    bbq {
//        resource url: 'js/jquery.ba-bbq.min.js'
//    }
//    openlayers {
//        resource url: 'js/OpenLayers/OpenLayers.js'
//        resource url: 'js/OpenLayers/theme/default/style.css'
//    }
//    map {
//        resource url: 'js/map.js'
//    }
//    datasets {
//        resource url:'js/datasets.js'
//    }
//    rotate {
//        resource url:'js/jQueryRotateCompressed.2.1.js'
//    }
//    bigbuttons {
//        resource url:'css/temp-style.css'
//    }
//    debug {
//        resource url:'js/debug.js'
//    }
//    change {
//        resource url:'js/change.js'
//    }
//    json2 {
//        resource url:'js/json2.js'
//    }
//    fileupload {
//        resource url:[dir:'js', file:'bootstrap-fileupload.min.js']
//        resource url:[dir:'css', file:'bootstrap-fileupload.min.css']
//    }
//    collectory {
//        dependsOn 'bootstrap,jquery,jquery_i18n,jquery_json,jquery_tools, jquery_jsonp'
//        resource url:'js/collectory.js'
//        resource url:'css/temp-style.css'
//    }
//    charts {
//        resource url:'js/charts2.js'
//        resource url:'js/charts.js'
//    }
}
