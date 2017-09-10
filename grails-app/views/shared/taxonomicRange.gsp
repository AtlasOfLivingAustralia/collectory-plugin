<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="collection.base.label" default="Edit taxonomy hints" /></title>
        <r:require modules="jstree, jquery_tools, debug"/>
    </head>
    <body>
        <div class="title-bar">
            <h1><g:message code="shared.title.editing" />: ${command.name}</h1>
        </div>
        <div id="baseForm" class="body">
            <g:if test="${message}">
            <div class="message">${message}</div>
            </g:if>
            <g:hasErrors bean="${command}">
            <div class="errors">
                <g:renderErrors bean="${command}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" name="baseForm" action="base">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <g:hiddenField name="range" value="${command.listTaxonomicRange() ? command.listTaxonomicRange().join(',') : ''}" />

                <div>
                  <h2><g:message code="shared.tr.title01" />.</h2>
                  <p class="lead"><g:message code="shared.tr.des01" />.</p>
                  %{--<p class="potential-problem">Note that IE<span> </span>&nbsp;has some minor problems with the list below. Nodes in the list will not close. You are still able to use the list to define your taxonomic scope.</p>--}%
                  <div class="row">
                      <div class=" col-md-4" id="selections">
                        <h3><g:message code="shared.tr.title02" /></h3>
                        %{--<div> </div>--}%
                        <div id="taxa-tree"></div>
                      </div>
                      <div class="col-md-4 well" id="selected-list">
                            <h3><g:message code="shared.tr.title03" /></h3>
                            <ul></ul>
                            <button type="button" class="btn btn-default" id="clear"><g:message code="shared.tr.btn.clearall" /></button>
                            <span class="button"><input type="submit" name="_action_updateTaxonomicRange" value="${message(code:"shared.button.update")}" class="save btn btn-default"></span>
                            <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.button.cancel")}" class="cancel btn btn-default"></span>
                      </div>
                   </div>
                </div>
            </g:form>
        </div>

        <div class="simple_overlay hide" id="help" >
          <div class="details">
            <h2><g:message code="shared.tr.help.title01" /></h2>
            <p><a href="#"><g:message code="shared.tr.help.link01" />?</a></p>
            <p><a href="#"><g:message code="shared.tr.help.link02" />?</a></p>
            <p><a href="#"><g:message code="shared.tr.help.link03" />?</a></p>
            <p><a href="#"><g:message code="shared.tr.help.link04" />?</a></p>
            <h2><g:message code="shared.tr.help.title02" /></h2>
            <p><a href="#"><g:message code="shared.tr.help.link05" />?</a></p>
            <p><a href="#"><g:message code="shared.tr.help.link06" />?</a></p>
            <p><a href="#"><g:message code="shared.tr.help.link07" />?</a></p>
            <p><a href="#"><g:message code="shared.tr.help.link08" />?</a></p>
          </div>
        </div>

    <script type="text/javascript">
      var baseUrl = "${grailsApplication.config.grails.serverURL}";

      function init() {
        // add tree
        var $tree = $('#taxa-tree');
        $tree.jstree({
          json_data: {
            /*data: sampleJson*/
            ajax: {
              url: baseUrl + "/data/taxa/taxa.json"
              //url: baseUrl + "/data/taxa-groups.json"
            }
          },
          core: { animation: 400, open_parents: true },
          themes:{
            theme: 'classic',
            icons: false
          },
          checkbox: {override_ui:true},
          plugins: ['json_data','themes','checkbox','ui']
        });
        // set initial state when tree has loaded
        $tree.bind('loaded.jstree', function() {
          var range = $('input#range').val();
          if (range != '') {
            var list = range.split(',');
            $.each(list, function(i,value) {
//              alert('checking ' + value + ': exists? ' + $('li#' + value).length);
              var hit = $('li#' + value);
              if (hit) {
                $tree.jstree('change_state', hit);
                // open the node
                if (hit.hasClass('jstree-leaf')) {
                  // open the parent
                  $tree.jstree('open_node', hit.parentsUntil('li'));
                }
                else {
                  $tree.jstree('open_node', hit);
                }
              }
            });
            if (jQuery.browser.msie) {
              var version = parseInt(jQuery.browser.version,10);
              if (version === 9 || version === 8 || version === 7) {
                $tree.jstree('open_all', -1, false);
              }
            }
            updateList();
          }
          // update on check
          $tree.bind('check_node.jstree',function(event, data){
            updateList();
          });
          // update on un-check
          $tree.bind('uncheck_node.jstree',function(event, data){
            updateList();
          });
        });
        // clear all
        $('#clear').click(function() {
          $('input#range').val('');
          $tree.jstree('uncheck_node', 'li');
        });
        // init help link
        $('a#helpLink').overlay();
        // flag possible problem in IE 7, 8, 9
        /*if (jQuery.browser.msie) {
          var version = parseInt(jQuery.browser.version,10);
          if (version === 9 || version === 8 || version === 7) {
            $('.potential-problem span').html(" " + version);
            $('.potential-problem').css('display','block');
            // open all nodes so it is at least consistent
          }
        }*/
      }
      function updateList() {
        $('#selected-list li').remove();
        $('input#range').val('');
        var $checked = $('#taxa-tree').jstree('get_checked');
        $.each($checked, function(i, obj) {
          addItem($(obj).attr('rank'),$(obj).attr('id'));
        })
      }
      function addItem(rank, name) {
        //var text = rank == undefined || rank == 'group' ? name : name + " (" + rank + ")";
        var text = name;
        var $item = $('<li></li>').appendTo('#selected-list ul');
        $item.append(text);
        // add to hidden field
        var str = $('input#range').val();
        str += name + ',';
        $('input#range').val(str);
      }
      $(function(){
        init();
      })
    </script>
    </body>
</html>
