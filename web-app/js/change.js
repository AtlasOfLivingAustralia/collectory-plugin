/*
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

/**
 * Created by markew
 * Date: 26/11/11
 * Time: 4:52 PM
 */
var validator = {
        tips: null,

        updateTips: function (t) {
            var $tips = this.tips; // for use in the timeout function where this is a different context
            $tips
                .text(t)
                .addClass("ui-state-highlight");
                setTimeout(function() {
                    $tips.removeClass("ui-state-highlight", 1500);
                }, 500 );
        },

        checkLength: function (o, n, min, max) {
            if (o.val().length > max || o.val().length < min) {
                o.addClass("ui-state-error");
                this.updateTips(jQuery.i18n.prop('change.js.lengthof') + " " + n + " " + jQuery.i18n.prop('change.js.mustbebetween') + " " + min + " and " + max + ".");
                return false;
            } else {
                return true;
            }
        },
        checkRegexp: function (o, regexp, n) {
            if (!( regexp.test(o.val()))) {
                o.addClass("ui-state-error");
                this.updateTips(n);
                return false;
            } else {
                return true;
            }
        },
        checkUnique: function (o) {
            var isUnique = true;
            // make a synchronous call to check existence of the name
            $.ajax({
                url: "${grailsApplication.config.grails.serverURL}/collection/nameExists?name=" + o.val(),
                dataType: 'json',
                async: false,
                success: function(data) {
                    if (data.found == 'true') {
                        o.addClass("ui-state-error");
                        this.updateTips(jQuery.i18n.prop('change.js.collectionwithname') + " (" + data.uid + ")");
                        isUnique = false;
                    }
                }
            });
            return isUnique;
        },
        checkDate: function (date, n) {
            //var regex = /\d\d\d\d(\-(0[1-9]|1[012])(\-((0[1-9])|1\d|2\d|3[01])(T(0\d|1\d|2[0-3])(:[0-5]\d){0,2})?)?)?|\-\-(0[1-9]|1[012])(\-(0[1-9]|1\d|2\d|3[01]))?|\-\-\-(0[1-9]|1\d|2\d|3[01])/;
            var regex = /^([\+-]?\d{4}(?!\d{2}\b))((-?)((0[1-9]|1[0-2])(\3([12]\d|0[1-9]|3[01]))?|W([0-4]\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\d|[12]\d{2}|3([0-5]\d|6[1-6])))([T\s]((([01]\d|2[0-3])((:?)[0-5]\d)?|24\:?00)([\.,]\d+(?!:))?)?(\17[0-5]\d([\.,]\d+)?)?([zZ]|([\+-])([01]\d|2[0-3]):?([0-5]\d)?)?)?)?$/;
            return validator.checkRegexp(date, regex, n);
        }
    },

    transformer = {
        temporalSpan: function (start, end) {
            var text;
            if (start && end) {
                text = jQuery.i18n.prop('change.js.transformer01') + " " + start + " " + jQuery.i18n.prop('change.js.transformer02') + " " + end + "."
            } else if (start) {
                text = jQuery.i18n.prop('change.js.transformer03') + " " + start + " " + jQuery.i18n.prop('change.js.transformer04')
            } else if (end) {
                text = jQuery.i18n.prop('change.js.transformer05') + " " + end + "."
            } else {
                text = jQuery.i18n.prop('change.js.transformer06')
            }
            return text;
        },
        lsidHtml: function (lsid) {
            var authority = lsid.substring(9,lsid.indexOf(':',10));
            return '<a target="_blank" rel="nofollow" class="external" href="http://' + authority +
                    '/' + lsid +  '">' + lsid + '</a>';
        },
        checkboxesToList: function (checkedInputs) {
            var kc = [];
            $.each(checkedInputs, function(i, obj) {
                kc.push($(obj).val());
            });
            return kc;
        },
        commaSeparatedToList: function (str) {
            if (str === "") { return [] }
            var list = [];
            $.each(str.split(','), function (i,obj) {
                list.push(obj.trim());
            });
            return list;
        },
        listToString: function (list) {
            var len = list.length;
            //alert(list.join(' ') + " (" + len + ")");
            switch (len) {
                case 0: return "";
                case 1: return list[0];
                default: return list.slice(0,list.length - 1).join(', ') + " and " + list[list.length - 1];
            }
        },
        states: function (str) {
            if (str === "") { return ""; }
            if ($.inArray(str.toLowerCase(), [jQuery.i18n.prop('change.js.transformer07'), jQuery.i18n.prop('change.js.transformer08'), jQuery.i18n.prop('change.js.transformer09')]) > -1) {
                return jQuery.i18n.prop('change.js.transformer10');
            } else {
                return jQuery.i18n.prop('change.js.transformer11') + " " + str + ".";
            }
        }
    },

    pageButtons = {
        changed: function (what) {
            var $save, $cancel, $buttons;
            // show save button if not already visible
            if ($('#pageButtons').length === 0) {
                $buttons = $('<span id="pageButtons"></span>').appendTo($('#breadcrumb'));
                $save = $('<button id="save">Save all changes</button>').appendTo($buttons);
                $cancel = $('<button id="cancel">Discard changes</button>').appendTo($buttons);
                $save.click(this.save);
                $cancel.click(this.cancel);
            }
            this.changes.push(what);
        },
        changes: [],
        save: function () {
            // save title
            var payload = {
                api_key: 'Venezuela',
                user: username
            };
            $.each(pageButtons.changes, function (i, name) {
                payload[name] = currentValue[name];
            });
            $.ajax({
                type: 'POST',
                url: baseUrl + '/ws/collection/' + uid,
                data: JSON.stringify(payload, function (key, value) {
                    if (value instanceof Array && value.length === 0) {
                        return "";
                    } else {
                        return value;
                    }
                }),
                contentType: 'application/json',
                success: function(data, textStatus, jqXHR) {
                    window,location.reload();
                },
                complete: function (jqXHR, textStatus) {
                    if (textStatus !== 'success') {
                        alert(textStatus + " (" + jqXHR.status + ") " + jqXHR.responseText);
                    }
                }
            });
        },
        cancel: function () {
            window.location.reload();
        }
    },

    dialogs = {
        name: {
            width: 500,
            title: jQuery.i18n.prop('change.js.dialogs01'),
            ok: function() {
                var bValid = true,
                    $input = $('#nameInput'),
                    $display = $('#name');

                $input.removeClass( "ui-state-error" );

                bValid = bValid && validator.checkLength( $input, "name", 3, 1024 );

                bValid = bValid && validator.checkRegexp( $input, /^[a-z]([0-9a-z_ ])+$/i,
                    jQuery.i18n.prop('change.js.dialogs02'));

                //bValid = bValid && validator.checkUnique($nameInput);

                if ( bValid ) {
                    dialogs.checkAndUpdateText('name');
                    $(this).dialog("close");
                }
            }},
        acronym: {
            width: 400,
            title: jQuery.i18n.prop('change.js.acronym01'),
            ok: function() {
                var bValid = true,
                    $input = $('#acronymInput');

                $input.removeClass( "ui-state-error" );

                bValid = bValid && validator.checkLength( $input, "acronym", 3, 45 );

                bValid = bValid && validator.checkRegexp( $input, /^[a-z]([0-9a-z_ ])+$/i,
                    jQuery.i18n.prop('change.js.acronym02'));

                if ( bValid ) {
                    dialogs.checkAndUpdateText('acronym');
                    $(this).dialog("close");
                }
            }
        },
        lsid: {
            pageStore: {selector: "#lsid", key: 'lsid'},
            width: 400,
            title: jQuery.i18n.prop('change.js.lsid01'),
            ok: function() {
                var bValid = true,
                    $input = $('#lsidInput'),
                    $display = $('#lsid');

                $input.removeClass( "ui-state-error" );

                bValid = bValid && validator.checkLength( $input, "lsid", 0, 45 );

                bValid = bValid && validator.checkRegexp( $input, /urn:lsid:([\w\-\.]+\.[\w\-\.]+)+:\w+:\S+/i,
                    jQuery.i18n.prop('change.js.lsid02') + " - URN:LSID:<Authority>:<Namespace>:<ObjectID>[:<Version>]");

                if ( bValid ) {
                    var val = $input.val();
                    if (val !== currentValue.guid) {
                        currentValue.guid = val;
                        pageButtons.changed('guid');
                        $display.html(transformer.lsidHtml(val));
                    }
                    $(this).dialog("close");
                }
            }
        },
        description: {
            width: 700,
            title: jQuery.i18n.prop('change.js.description01'),
            ok: function () {
                // update page
                var newContent = $('#descriptionInput').tinymce().getContent();
                currentValue.pubDescription = newContent;
                currentValue.techDescription = "";
                $('#description').html(newContent);
                pageButtons.changed('pubDescription');
                pageButtons.changed('techDescription');
                $(this).dialog("close");
            }
        },
        temporalSpan: {
            width: 400,
            title: jQuery.i18n.prop('change.js.temporalspan01'),
            ok: function () {
                var bValid = true,
                    sDate = $('#startDateInput'),
                    eDate = $('#endDateInput'),
                    sDateVal = sDate.val(),
                    eDateVal = eDate.val(),
                    $span = $('#temporalSpan');

                sDate.removeClass( "ui-state-error" );
                eDate.removeClass( "ui-state-error" );

                if (sDateVal !== "") {
                    bValid = validator.checkDate(sDate, jQuery.i18n.prop('change.js.temporalspan02'));
                }
                if (eDateVal !== "") {
                    bValid = bValid && validator.checkDate(eDate, jQuery.i18n.prop('change.js.temporalspan03'));
                }

                if (bValid) {
                    $span.html(transformer.temporalSpan(sDateVal, eDateVal));
                    if (sDateVal != currentValue.startDate) {
                        currentValue.startDate = sDateVal;
                        pageButtons.changed('startDate');
                    }
                    if (eDateVal != currentValue.endDate) {
                        currentValue.endDate = eDateVal;
                        pageButtons.changed('endDate');
                    }
                    $(this).dialog("close");
                }
            }
        },
        taxonomicRange: {
            title: jQuery.i18n.prop('change.js.taxonomicrange01'),
            width: 700,
            ok: function () {
                var $kingdomCoverageElement = $('#kingdomCoverage'),
                    $scientificNamesElement = $('#scientificNames'),
                    selectedKingdoms = transformer.checkboxesToList($('input:checked[name="kingdomCoverage"]')),
                    kingdomCoverageVal = selectedKingdoms.join(' '),
                    scientificNamesVal = transformer.commaSeparatedToList($('#scientificNamesInput').val());

                dialogs.checkAndUpdateText('focus');
                dialogs.checkAndUpdateText('uso');

                if (kingdomCoverageVal !== currentValue.kingdomCoverage) {
                    $kingdomCoverageElement.html("Kingdoms covered include: " + transformer.listToString(selectedKingdoms));
                    $kingdomCoverageElement.toggleClass('empty', kingdomCoverageVal === "");
                    currentValue.kingdomCoverage = kingdomCoverageVal;
                    pageButtons.changed('kingdomCoverage');
                }

                if (scientificNamesVal.join(',') !== currentValue.scientificNames.join(',')) {
                    $scientificNamesElement.html(transformer.listToString(scientificNamesVal));
                    $('#sciNames').toggleClass('empty', scientificNamesVal.length === 0);
                    currentValue.scientificNames = scientificNamesVal;
                    pageButtons.changed('scientificNames');
                }

                $(this).dialog("close");
            }
        },
        geographicRange: {
            title: jQuery.i18n.prop('change.js.geographicrange01'),
            width: 700,
            ok: function () {
                dialogs.checkAndUpdateText("geographicDescription");
                dialogs.checkAndUpdateText("states", "states");
                $(this).dialog("close");
            }
        },
        /** Handles ok processing for text properties.
         * @param f the name of the field
         * @param transform optional name of transformer method to use for display
         */
        checkAndUpdateText: function (f, transform) {
            var element, val;
            element = $('#' + f);
            val = $('#' + f + 'Input').val();
            if (val !== currentValue[f]) {
                if (transform !== undefined) {
                    element.html(transformer[transform](val)); // set transformed value on page
                } else {
                    element.html(val);  // set value on page
                }
                currentValue[f] = val;  // set as current value
                element.toggleClass('empty', val === "");  // toggle visibility of container in case there is extra text
                pageButtons.changed(f);  // mark as changed
            }
        }
    },

    fields = {
        name: {
            display: '#name',
            property: "name"
        },
        acronym: {
            display: '#acronym',
            property: "acronym"
        },
        guid: {
            property: "guid",
            pageStore: {selector: "#lsid a", key: 'lsid'}
        },
        pubDescription: {
            display: "#description",
            property: "pubDescription"
        },
        techDescription: {
            display: "#description",
            property: "techDescription"
        },
        startDate: {
            property: 'startDate',
            pageStore: {selector: "#temporalSpan", key: 'startDate'}
        },
        endDate: {
            property: 'endDate',
            pageStore: {selector: "#temporalSpan", key: 'endDate'}
        },
        focus: {
            property: 'focus'
        },
        uso: {
            property: 'uso'
        },
        kingdomCoverage: {
            property: "kingdomCoverage"
        },
        scientificNames: {
            display: "scientificNames",
            property: 'scientificNames'
        }
    },

    originalValues = {};

$(function() {
    var $showChangesLink = $('#showChangesLink');

    validator.tips = $(".validateTips");

    //var model = JSON.parse(modelJson);

    // toggle for recent changes
    $showChangesLink.click(function () {
        var $changes = $('#changes');
        if ($('#changes:visible').length > 0) {
            $changes.slideUp();
            $showChangesLink.html(jQuery.i18n.prop('change.js.showchangeslink01'));
        } else {
            $changes.slideDown();
            $showChangesLink.html(jQuery.i18n.prop('change.js.showchangeslink02'));
        }
    });

    // bind click handler to twisty in recent changes
    $('p.relatedFollows').rotate({
        bind:
            {
                click: function() {
                    var $target = $(this).parent().find('table'),
                        $twisty = $(this).find('img');
                    if ($target.css('display') == 'none') {
                        $twisty.rotate({animateTo:90,duration:350});
                        $target.slideDown();
                    }
                    else {
                        $twisty.rotate({animateTo:0,duration:350});
                        $target.slideUp();
                    }
                    return false;
                }
            }
    });

    // init dialogs
    for (var dialog in dialogs) {
        $('#' + dialog + '-dialog').dialog({
            autoOpen: false,
            width: dialogs[dialog].width,
            modal: true,
            title: dialogs[dialog].title,
            buttons: {
                "Ok": dialogs[dialog].ok,
                Cancel: function() {
                    $(this).dialog("close");
                }
            },
            close: function() {
                $('#' + dialog).removeClass( "ui-state-error" );
            }
        });
    }

    // handle change links - show dialogs
    $('img.changeLink').click(function() {
        switch (this.id) {
            case 'descriptionLink':
                // see whether tinymce has already been initialised
                var inited = $('textarea.tinymce').attr('aria-hidden');
                if (inited === undefined) {
                    $('textarea.tinymce').tinymce({
                        script_url : baseUrl + '/js/tinymce/jscripts/tiny_mce/tiny_mce.js',
                        theme : "advanced",
                        width: "675",
                        height: "400",
                        plugins : "fullscreen",
                        theme_advanced_toolbar_location : "top",
                        theme_advanced_buttons1: "bold,italic,underline,bullist,numlist,undo,redo,link,unlink,cleanup,code,sub,sup,charmap,fullscreen,autoresize",
                        theme_advanced_buttons2: "",
                        theme_advanced_buttons3: ""
                    });
                }
                // note that this is loaded from the page elements not the currentValue object
                //  because we are leveraging the formattedText tag where the text is still in old markup
                $('#descriptionInput').html($('#description').html());
                // TODO: clear undo list else undo will reverse the above text insertion
                $("#description-dialog").dialog("open");
                break;
            default:
                // follows the convention: '<name>Link' opens '<name>-dialog'
                $('#' + this.id.substr(0, this.id.length - 4) + "-dialog").dialog("open");
        }
    });
});
