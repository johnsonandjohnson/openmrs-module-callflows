(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('callflows.controllers', []),
        makeLayout = function(view) {
            if (view === 'narrow') {
                innerLayout({ 'spacing_closed': 30, 'east__minSize': 200, 'east__maxSize': 650 }, { show: true });
            } else {
                // initialize innerLayout to wider angle
                innerLayout({ 'spacing_closed': 30, 'east__minSize': 850, 'east__maxSize': 1400 }, { show: true });
            }
        };

    /* Search Controller */
    controllers.controller('SearchController', ['$scope', '$http', '$log', 'callflows', 'CALLFLOW_API', 'CONFIG', 'settingsService',
    function($scope, $http, $log, callflows, CALLFLOW_API, CONFIG, settingsService) {

        var renderers = settingsService.renderers,
            selectConfig = {
                ajax: {
                    url: CALLFLOW_API.CALLFLOW,
                    dataType: 'json',
                    quietMillis: 100,
                    data: function (term, page) {
                        return {
                            lookup: 'By Name',
                            term: term,
                            pageLimit: 5,
                            page: page
                        };
                    },
                    results: function (data) {
                        return data;
                    },
                    minimumInputLength: CONFIG.MIN_SEARCH_LEN
                },
                formatSelection: function (flow) {
                    var name = flow && flow.name ? flow.name : '';

                    return name ? name : $scope.msg('mds.error');
                },
                formatResult: function (flow) {
                    var result = (flow && flow.name) ? angular.element('<strong>').text(flow.name) : undefined;

                    return result || $scope.msg('mds.error');
                },
                containerCssClass: "form-control-select2",
                escapeMarkup: function (markup) {
                    return markup;
                },
                minimumInputLength: CONFIG.MIN_SEARCH_LEN
            };

        // This is the configuration required for the directive ui-select2 to work
        $scope.SELECT_FLOWS_CONFIG = selectConfig;

        // select the flow that we searched for and set it in the service for other controllers to use
        $scope.selectFlow = function() {
            // when we get the flow from the server, the raw is a property with value as string
            // so we need to deserialize it
            var raw, flow;
            if ($scope.selectedFlow) {
                flow = callflows.deserialize($scope.selectedFlow);
                // make this available in the service
                callflows.setCurrent(flow);
            }
        };
    }]);

    /* Designer Controller */
    controllers.controller('DesignerController', ['$scope', '$http', '$timeout', '$log', 'settingsService', 'callflows', 'CONFIG', 'audioService', 'Upload',
    function($scope, $http, $timeout, $log, settingsService, callflows, CONFIG, audioService, Upload) {

        $scope.flows = callflows;
        // current flow can now be accessed as flows.current, which references callflows.current
        // directly assigning callflows.current doesn't force the scope to get updated in certain circumstances
        $scope.audio = audioService;

        $scope.settings = settingsService;

        // if settings is not yet loaded
        if (! $scope.settings.renderers.length) {
            $scope.settings.loadConfigs();
            $scope.settings.loadRenderers(function(renderers) {
                $scope.currentRenderer = renderers.length ? renderers[0] : null;
            });
        } else {
            // current renderer
            $scope.currentRenderer = $scope.settings.renderers[0];
        }

        // tracks the state of each node, whether open/closed in the UI
        $scope.accordions = [];
        // a bunch of errors
        $scope.errors = [];
        // a bunch of messages that's not errors
        $scope.messages = [];

        $scope.codeMirrorLoaded = function(_editor) {
            _editor.focus();
        };

        $scope.editorOptions = {
            lineWrapping: true,
            lineNumbers: true,
            indentUnit: 2,
            mode: 'velocity'
        };

        // Accordions
        var setAccordions = function(flow) {
            var i, nodes;
            if (flow && flow.nodes) {
                nodes = flow.nodes;
                $scope.accordions = [];
                for (i = 0; i < nodes.length; i+=1) {
                    $scope.accordions.push(true);
                }
            }
        },
        setNodeStates = function(val) {
            var flow = callflows.current,
                nodes = flow.raw.nodes,
                output = [],
                i;
            for (i = 0; i < nodes.length; i+=1) {
                output.push(val);
                if (val) { $scope.openNode(nodes[i]); }
            }
            $scope.accordions = output;
        },
        hideMsgLater = function(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        },
        initAudioCounts = function() {
            $scope.uploadSuccess = 0;
            $scope.uploadFailed = 0;
        },
        init = function() {
            // initialize accordions
            // setAccordions($scope.flow);
            // Whether mappings must be revealed
            audioService.revealOn = false;
            // Whether mapping names must be auto-generated
            audioService.generateName = true;
            // initialize the narrow forcibly
            makeLayout('narrow');
        };

        // open a node
        $scope.openNode = function(node) {
            if (node.nodeType === 'user') {
                // initialize to a nice state
                if (node.blocks.length && ! node.currentBlock) {
                    node.currentBlock = node.blocks[0];
                    node.currentBlockId = 0;
                }
                if (node.currentBlock && ! node.currentElement) {
                    node.currentElement = node.currentBlock.elements[0];
                    node.currentElementId = 0;
                }
            }
        };

        // toggle node panel
        $scope.togglePanel = function(node, index) {
            $scope.accordions[index] = !$scope.accordions[index];
            // if open
            if ($scope.accordions[index]) {
                $scope.openNode(node);
            }
        };

        // expand all
        $scope.expandNodes = function() {
            setNodeStates(true);
        };

        // collapse all
        $scope.collapseNodes = function() {
            setNodeStates(false);
        };


        // Add a flow
        $scope.addFlow = function() {
            $scope.currentFlow = callflows.addFlow();
        };

        // Add a interaction (2 nodes, user-node and system-node)
        $scope.addInteraction = function(type) {
            callflows.addInteraction();
            // default state is open for the new user node
            $scope.accordions.push(true);
            // default state is closed for the new system node
            $scope.accordions.push(false);
        };

        // delete node
        $scope.deleteNode = function(node, id) {
            callflows.deleteNode(id);
        };

        // add a element
        $scope.addElement = function(node, type) {
            callflows.addElement(node, type);
        };

        // set current element
        $scope.showElement = function(node, block, elementId) {
            callflows.setCurrentElement(node, block, elementId);
        };

        // delete element
        $scope.deleteElement = function(node, block, elementId) {
            callflows.deleteElement(node, block, elementId);
            // re-render node
            // runRender(node);
        };

        // list of field types
        $scope.fieldTypes = CONFIG.FIELD_TYPES;

        // set field type
        $scope.setFieldType = function(element, fieldType) {
            element.fieldType = fieldType;
        };

        $scope.toggleDirty = function(event, tpl) {
            tpl.dirty = !tpl.dirty;
            event.stopPropagation();
            return false;
        };

        // toggle dtmf, bargeIn, voice
        $scope.toggle = function(node, obj, prop) {
            obj[prop] = ! obj[prop];
            $scope.runRender(node);
        };
        $scope.setProperty = function(prop) {
            $scope.currentProperty = prop;
        };

        // add block
        $scope.addBlock = function(node, type) {
            callflows.addBlock(node, type);
        };

        $scope.showBlock = function(node, blockId) {
            callflows.setCurrentBlock(node, blockId);
        };

        $scope.deleteBlock = function(node, blockId) {
            callflows.deleteBlock(node, blockId);
        };

        // run render for a specific node
        $scope.runRender = function(node) {
            $scope.currentNode = node;

            var fmt = $scope.currentRenderer.name;
            // Run render only if the template has not been manually edited or marked so
            if (node.templates[fmt] && ! node.templates[fmt].dirty) {
                node.templates[fmt].content = callflows.renderNode(node, $scope.currentRenderer);
            }
        };

        // set a current render, not dependent on callflows service
        $scope.setRenderer = function(renderer, node) {
            $scope.currentRenderer = renderer;
            $scope.runRender(node);
        };

        $scope.saveFlow = function() {
            var flow = callflows.current;

            $log.log(flow);
            // proceed to save
            callflows.saveFlow(
                function() {
                    var index = $scope.messages.push('Callflow ' + flow.name + ' saved successfully!');
                    hideMsgLater(index - 1);
                }, function() {
                    handleWithStackTrace('flows.error.header', 'flows.error.body');
                });
        };

        // Toggles [used in title attributes via ng-attr-title]
        $scope.getRevealTitle = function() {
            return $scope.audio.revealOn ? $scope.msg('callflows.toggle.revealOn') : $scope.msg('callflows.toggle.revealOff');
        };

        $scope.getGenerateNameTitle = function() {
            return $scope.audio.generateName ? $scope.msg('callflows.toggle.mappingOn') : $scope.msg('callflows.toggle.mappingOff');
        };

        $scope.getRendererTitle = function(tpl) {
            return tpl.dirty ? $scope.msg('callflows.toggle.rendererOff') : $scope.msg('callflows.toggle.rendererOn');
        };

        // Audio Support
        // =============

        $scope.selectedText = function(selections, target) {
            var selection = selections[0];
            // replace backquotes in the selection, cause we put them!
            $scope.audio.current = selection.replace(/`/g, '');
            // currently we are targetting this
            $scope.audio.target = target;
            // Do we want to auto-generate a file name or not
            $scope.audio.text = $scope.audio.generateName ? $scope.prettifyAudioName(selection) : '';
        };

        $scope.getTarget = function(node, element, property) {
            var target = {
                node: node.step,
                block: node.currentBlockId,
                element: node.currentElementId,
                name: element.name,
                property: property
            };
            return target;
        };

        $scope.getNode = function(target) {
            var obj = angular.fromJson(target);
            if (obj) {
                return obj.node;
            }
        };

        $scope.editorHandler = function(editor, target) {
            $scope.currentEditor = editor;
            $scope.$parent.currentEditor = editor;
            $scope.selectedText(editor.getSelections(), target);
        };

        $scope.toggleGenerateName = function() {
            $scope.audio.generateName = ! $scope.audio.generateName;
            $scope.audio.text = $scope.audio.generateName ? $scope.prettifyAudioName($scope.audio.current) : '';
        };

        $scope.deleteMapping = function(text, mappingIndex) {
            var flow = $scope.flows.current;
            flow.raw.audio[text].splice(mappingIndex, 1);
            if (! flow.raw.audio[text].length) {
                delete flow.raw.audio[text];
            }
            // At the time of removing a mapping, let's re-render everything
            callflows.runRenderers(flow);
        };

        $scope.deleteAllMappings = function() {
            var flow = $scope.flows.current;
            flow.raw.audio = {};
            // At the time of removing all mappings, the templates would have changed, so let's re-render everything
            callflows.runRenderers(flow);
        };

        // Remove all characters other than text and numbers
        // Replace spaces with one underscore to make intent clearer
        $scope.prettifyAudioName = function(name) {
            if (! name) {
                return '';
            }
            // replace multiple spaces with single underscore
            // remove anything that doesn't look like either alphabet, number or underscore
            return name.toLowerCase().replace(/\s+/g, '_').replace(/[^a-zA-Z0-9_]/g, '');
        };

        // Cleans up the part including the file extension and then cleans up special characters and spaces by using underscores
        $scope.prettifyFileName = function(name) {
            var prettyName = name;
            if (! prettyName) {
                return '';
            }
            // anything that starts with a dot followed by characters that doesn't look like dots until termination
            prettyName = prettyName.replace(/\.[^.]*$/, '');
            return $scope.prettifyAudioName(prettyName);
        };

        $scope.buildUrl = function(language, mapping) {
            return '/motech-platform-server/module/cmsliteapi/stream/' + language + '/' + mapping.mapping;
        };

        // Map a particular text to a particular cms key (mapping) for a particular language
        // and that which is linked to a specific  audio file
        $scope.mapTextToAudioHandler = function(text, mapping, language, file) {
            var flow = $scope.flows.current, selections;
            if (! flow.raw.audio) {
                flow.raw.audio = {};
            }
            if (text && mapping && language && flow) {
                if (! flow.raw.audio[text]) {
                    flow.raw.audio[text] = [];
                }
                // store mapping against each text
                // since the same text can be mapped to different audio files at different points, we use an array
                // We expect that the mapping is same across different languages, as it's easier to work with uploading
                // language specific audio files against the same text, but with a different language
                // For easy debugging, we also need to track the node in which current mapping is registered
                flow.raw.audio[text].push({
                    mapping: mapping,
                    target: $scope.audio.target
                });
                // flow.audio[$scope.audio.language] = cmsMappingName;
                // If any text is selected, replace it with our special delimiters
                // we use backquote for pure aesthetics, it conveys special meaning plus is not very jarring when read alongside text
                if ($scope.currentEditor) {
                    selections = $scope.currentEditor.getSelections();
                    if (selections.length && selections[0] && selections[0].length) {
                        $scope.currentEditor.replaceSelections($scope.currentEditor.getSelections().map(function(text) {
                            // people are interesting, they will try all sorts of things :)
                            // this is still not foolproof, as backquotes might still be outside the selection area
                            return '`' + text.replace(/`/g, '') + '`';
                        }));
                        // We are done with this text, no more
                        $scope.audio.text = null;
                        $scope.audio.current = null;
                    }
                }
            }
        };

        $scope.mapTextToAudio = function(text, mapping, language, file) {
            // If file is not uploaded, first upload and then do mapping
            initAudioCounts();
            // If no mapping was supplied, use file name
            var mappingName = mapping || $scope.prettifyFileName(file.name);
            if (! file.uploadSuccess) {
                // The CMS Lite API doesn't need to know the text, just the mapping which is the equiv of the CMS Key
                $scope.uploadToServer(mappingName, language, file, function() {
                    $scope.mapTextToAudioHandler(text, mappingName, language, file);
                });
            } else {
                $scope.mapTextToAudioHandler(text, mappingName, language, file);
            }
        };

        // Upload single file to CMS-Lite.
        $scope.doUpload = function(file, callback) {
            file.upload.then(
                function (response) {
                    // successfully uploaded
                    file.uploadSuccess = true;
                    $scope.uploadSuccess += 1;
                    if (callback) {
                        callback(file, true);
                    }
                }, function (response) {
                    // error in upload
                    $scope.uploadFailed += 1;
                    file.uploadError = (response.status || 0) + ': unknown error. Most likely due to file size.';
                    if (callback) {
                        callback(file, false);
                    }
                });
        };

        // Upload multiple files to CMS-Lite
        $scope.uploadAllToServer = function(language, files) {
            // since we upload multiple here, we will use the file name as the key to store the file against
            var name;
            // No processed
            initAudioCounts();
            // one by one upload
            if (files && files.length) {
                angular.forEach(files, function(file) {
                    name = $scope.audio.generateName ? $scope.prettifyFileName(file.name) : file.name.replace(/\.[^.]*$/, '');
                    $scope.uploadToServer(name, language, file);
                });
            }
        };

        // Upload single file to CMS-Lite. Since we are mostly interested in a create/update situation that always works
        // irrespective of whether the file exists or not and CMS-Lite adherence to strict REST standards
        // We first check if CMS-Lite throws 404 for a GET, and then determine whether to use POST or PUT
        // so that our operation always works [well unless there is a file size issue]
        $scope.uploadToServer = function(name, language, file, callback) {
            file.uploaded = false;
            name = name || $scope.prettifyAudioName(file.name);
            var url = '/motech-platform-server/module/cmsliteapi/resource/stream/' + language + '/' + name,
                createUrl = '/motech-platform-server/module/cmsliteapi/resource?type=stream&language=' + language + '&name=' + name,
                params = {
                    method: 'POST',
                    file: {'file': file}
                };
            $http.get(url)
                .then(function(response) {
                    file.upload = Upload.upload(angular.extend(params, {'url' : url}));
                    $scope.doUpload(file, callback);
                }, function(errResponse) {
                    file.upload = Upload.upload(angular.extend(params, {'url' : createUrl}));
                    $scope.doUpload(file, callback);
                });
        };

        // initialize everything
        init();
    }]);

    controllers.controller('FlowRunnerController', function($scope, $http, $log, callflows, transformRequestToFormPost) {
        var getMessage = function(element) {
            return element.txt ? element.txt : '<no message set>';
        }, handleResponse = function(response) {
            if (response.error) {
                $scope.messages = [{error: true, txt: response.body}];
            } else {
                try {
                    $scope.messages = angular.fromJson(response.body);
                } catch (err) {
                    $scope.messages = [{error: true, txt: response.body}];
                }
                $scope.current.node = response.node;
                $scope.current.callId = response.callId;
                $scope.current.continueNode = response.continueNode;
            }
            process($scope.messages, $scope.current.continueNode);
        };

        $scope.flows = callflows;
        // current flow can now be accessed as flows.current, which references callflows.current
        // directly assigning callflows.current doesn't force the scope to get updated in certain circumstances

        $scope.userInput = {};

        function addToChat(txt, error, who) {
            var node, callId;
            if (! who) {
                who = 'system';
            }
            if ($scope.current) {
                node = $scope.current.node;
                callId = $scope.current.callId;
            }
            $scope.chats.push(angular.extend({}, {
                node        : node,
                callId      : callId,
                time        : new Date(),
                type        : who,
                body        : txt,
                error       : error ? true : false
            }));
        }

        // validate current input
        function process(messages, continueNode) {
            var msg, txt, noFieldFoundYet = true;
            $scope.terminated = true;
            // multiple text prompts and first field message needs to be dispalyed at one go
            while (messages.length && noFieldFoundYet) {
                // shrink messages, get the detached element
                if (messages[0].field) {
                    $scope.terminated = false;
                    noFieldFoundYet = false;
                    msg = messages[0];
                } else {
                    // we can remove the top message and proceed to find a field
                    msg = messages.splice(0, 1)[0];
                }
                txt = getMessage(msg);
                if (txt) {
                    addToChat(txt, msg.error);
                }
            }
            if (messages.length && messages[0].field) {
                $scope.terminated = false;
            } else if (continueNode && !msg.error) {
                // not yet
                $scope.terminated = false;
                $scope.sendDataToFlow('');
            }
        }

        $scope.$watch('flows.current.mode', function(mode) {
            var newView = mode === 'visualize' ? 'wide' : (!mode && callflows.menuView) ? callflows.menuView : 'narrow';
            callflows.mode = mode;
            if (callflows.menuView !== newView) {
                callflows.menuView = newView;
                makeLayout(newView);
            }
        });

        // running a flow
        $scope.runFlow = function() {
            var flow = callflows.current;
            // set current mode
            $scope.flows.current.mode = 'test';
            // reset chats
            $scope.chats = [];
            // current properties
            $scope.current = {};
            // runner is not running
            $scope.terminated = true;
            if (flow) {
                callflows.run(
                    function(response) {
                        handleResponse(response);
                    }, function(response) {
                        handleResponse(response);
                    });
            } else {
                addToChat('No Flow loaded!', true);
            }
        };
        $scope.sendDataToFlow = function(data) {
            var fieldName, fieldType,
                flow = callflows.current,
                callId = $scope.current.callId,
                url = '../callflows/calls/' + callId + '.json';
            // we might not have fields to start with
            if ($scope.messages.length) {
                fieldName = $scope.messages[0].field;
                fieldType = $scope.messages[0].fieldType;

                $scope.userInput[fieldName] = data;
                // reset the message area
                $scope.userResponse = '';
                addToChat(data, false, 'user');
                $scope.messages.splice(0, 1);
            }
            // and then we might have had just one field
            if ($scope.messages.length) {
                // There might be more fields to process. Not yet time to send. Not yet
                process($scope.messages);
                return;
            }
            $http({
                method: 'post',
                url: url,
                transformRequest: transformRequestToFormPost,
                data: $scope.userInput
            })
            .success(function(response) {
                $scope.current.node = response.node;
                // refresh messages again
                // if there is a error, the body contains the error response
                handleResponse(response);
            })
            .error(function(response) {
                handleResponse(response);
            });

            //reset the message area again!
            $scope.userResponse = '';
            // to terminate, set this
            // $scope.terminated = true;
        };
    });

    /* Controller to visualize callflows */
    controllers.controller('VisualizeController', ['$scope', '$http', 'callflows', '$timeout', '$log',
    function($scope, $http, callflows, $timeout, $log) {
        var stepRegex = /^\s*\|\s*([-a-zA-Z0-9.]+)\s*\|\s*$/, instance, buildCondition, buildConditionPath, buildValue, buildOperator;

        $scope.$watch('flows.current.mode', function(newMode) {
            if (newMode === 'visualize') {
                $scope.visualize();
            }
        });

        $scope.$watch('flows.refreshed', function(state) {
            $scope.visualize();
        });

        $scope.loadFlow = function(name) {
            callflows.loadFlow(name.replace(/\..*$/, ''));
        };

        buildValue = function(node) {
            var output = '';
            if (node.type === "string") {
                output = '"' + node.value + '"';
            } else if (node.type === "integer" || node.type === "decimal") {
                // simple value
                output = node.value;
            } else if (node.type === "references") {
                // referenced value
                if (node.id) {
                    output = node.id;
                }
                if (node.path && node.path.length) {
                    output += ".";
                    output = node.path[0].id;
                }
            } else if (node.type === "math") {
                // complex conditional
                if (node.operator === "parenthesis") {
                    output = "(" + buildCondition(node.expression[0], output) + ')';
                } else {
                    output = buildCondition(node);
                }
            }
            return output;
        };

        buildOperator = function(op) {
            switch(op) {
                case "not" : return "not ";
                case "||" : return " or ";
                case "&&" : return " and ";
                case "==" : return " = ";
                case "!=" : return " not = ";
                default: return ' ' + op + ' ';
            }
        };

        buildCondition = function(condition, output) {
            var expr = condition.expression,
            operator = condition.operator;
            output = output || '';

            if (expr) {
                if (operator === 'not') {
                    output = buildOperator(operator) + buildValue(expr[0]);
                } else {
                    output = buildValue(expr[0]) + buildOperator(operator) + buildValue(expr[1]);
                }
            }
            return output;
        };

        buildConditionPath = function(condition, conditions) {
            var output = '',
                delimiter = "/";
            if (conditions.length > 1) {
                for (var i = 0; i < conditions.length; i++) {
                    output += conditions[i];
                    output += delimiter;
                }
            }
            output += condition;
            return output;
        };


        /* parse a velocity template string using the JISON generated parser
           look for targets matching the correct step regex */
		$scope.parse = function(input) {
            var internal = [], // internal targets - jumps to points in same flow
                external = [], // external targets - jumps to points in another flow
                output = {input : input, internal: internal, external : external},
                i, parsed, condition, conditions = [], path, level, conditionText;

            if (input) {
                parsed = velocity.parse(input);
                // tracks nested levels
                level = 0;
                for (i = 0; i < parsed.length; i++) {
                    if (typeof(parsed[i]) === "string") {
                        if (stepRegex.test(parsed[i])) {
                            path = buildConditionPath(condition, conditions);
                            if (parsed[i].indexOf('.') === -1) {
                                internal.push({step: parsed[i].match(stepRegex)[1], condition: condition});
                            } else {
                                external.push({step: parsed[i].match(stepRegex)[1], condition: condition});
                            }
                        }
                    } else if (typeof[parsed[i]] === "object") {
                        if (parsed[i].type === "if") {
                            condition = buildCondition(parsed[i].condition);
                            // we are tracking nested conditions using this array
                            conditions.push(condition);
                            level += 1;
                        } else if (parsed[i].type === "elseif") {
                            condition = buildCondition(parsed[i].condition);
                        } else if (parsed[i].type === "else") {
                            condition = "default";  // default makes more sense visually than an else
                        } else if (parsed[i].type === "end") {
                            condition = null;
                            // end of condition
                            conditions.pop(condition);
                            level -= 1;
                        }
                    }
                }
            }
            return output;
        };

        $scope.useLines = function() {
            if ($scope.model) {
                $scope.model.meta.graphType = "Flowchart";
            }
            $scope.connect();
        };

        $scope.useCurves = function() {
            if ($scope.model) {
                $scope.model.meta.graphType = "StateMachine";
            }
            $scope.connect();
        };

        /* connect nodes using jsplumb
           There are three types of connections, connections from user-system nodes, system-system nodes and system-user nodes */
        $scope.connect = function() {

            var userToSystem = { anchor: "Continuous", paintStyle: {strokeStyle: 'red', lineWidth: 1} },
                systemToSystem = { anchor: "Continuous", paintStyle: {strokeStyle: '#073863', lineWidth: 2, dashstyle: '2 2 0 2'} },
                systemToUser = { anchor: "Continuous", paintStyle: {strokeStyle: '#073863', lineWidth: 1} },
                systemToExternal = { anchor: "Continuous", paintStyle: {strokeStyle: '#419641', lineWidth: 1} },
                defaults = {
                    Endpoint: ["Blank", {}],
                    Connector: [$scope.model.meta.graphType, {curviness: 40}],
                    HoverPaintStyle: {strokeStyle: "#1e8151", lineWidth: 1},
                    ConnectionOverlays: [
                        [ "Arrow", { width: 6, location: 1, id: "arrow", length: 12, foldback: 0.8 }]
                    ]
                },
                linkToSameFlow = function(srcIndex, target, instance) {
                    // same flow
                    var targetNode = $scope.findNode(target.step);
                    if (targetNode.node) {
                        if (targetNode.node.nodeType === 'system') {
                            // we are connecting a system node to another system node
                            // so ideally it should be right to right using a dotted pattern
                            instance.connect({ source: 'node' + srcIndex, target: 'node' + targetNode.index}, systemToSystem);
                        } else {
                            // we are connecting a system node to a user node again
                            // to differentiate, we go from bottom to right
                            instance.connect({
                                source: 'node' + srcIndex,
                                target: 'node' + targetNode.index,
                                label: target.condition ? target.condition : null
                            }, systemToUser);
                        }
                    }
                },
                linkToAnotherFlow = function(srcIndex, target, instance) {
                    // another flow
                    var targetNode = {};
                    $timeout(function() {
                        _.find($scope.external , function(item, index) {
                            if (item.step === target.step) {
                                targetNode.index = index;
                                targetNode.node = item;
                                return true;
                            }
                        });
                        // connect to external flow
                        instance.connect({
                            source: 'node' + srcIndex,
                            target: 'external-node' + targetNode.index,
                            label: target.condition ? target.condition : null
                        }, systemToExternal);
                    });
                },
                processExternalNode = function(id, externalTargets) {
                    var j;
                    // make the new external nodes draggable
                    $timeout(function() {
                        instance.draggable($('.external-node'), {
                            drag: function(event) {
                                var el = event.el,
                                    pos = event.pos,
                                    re = /^external-node(\d+)$/,
                                    index = el.id.match(re)[1],
                                    step = $scope.external[index].step;

                                $scope.$apply(function() {
                                    $scope.external[index].xpos = pos[0];
                                    $scope.external[index].ypos = pos[1];
                                    $scope.model.meta.positions[step] = {xpos: pos[0], ypos: pos[1]};
                                });
                            }
                        });
                        for (j = 0; j < externalTargets.length; j++) {
                            linkToAnotherFlow(id, externalTargets[j], instance);
                        }
                    });
                };

            jsPlumb.ready(function() {
                var i, j, nodes, target, targetNode, index, obj;
                nodes = $scope.model.nodes;
                // get the same instance if available to avoid leaving a trail of instances around each with their own connections!
                instance = instance || jsPlumb.getInstance();
                instance.importDefaults(defaults);
                // first remove every connection
                instance.detachEveryConnection();
                instance.deleteEveryEndpoint();
                instance.repaintEverything();

                instance.registerConnectionType("basic", { anchor:"Continuous", connector: $scope.model.meta.graphType });
                // make all node objects draggable
                instance.draggable($('.node'), {
                    drag: function(event) {
                        var el = event.el,
                            pos = event.pos,
                            re = /^node(\d+)$/,
                            index = el.id.match(re)[1];

                        $scope.$apply(function() {
                            $scope.model.nodes[index].xpos = pos[0];
                            $scope.model.nodes[index].ypos = pos[1];
                        });
                    }
                });
                // maintain a list of external flows linked to from all the nodes of this flow
                $scope.external = [];
                // now we have access to both internal and external nodes in order to link them
                for (i = 0; i < nodes.length; i++) {
                    // we want odd nodes here - i.e. system nodes
                    if (i % 2 !== 0) {
                        if ($scope.isNodeShown(nodes[i])) {
                            target = $scope.parse(nodes[i].templates.velocity.content);
                            for (j = 0; j < target.internal.length; j++) {
                                linkToSameFlow(i, target.internal[j], instance);
                            }
                            // set up the external nodes first, as we want to link to them
                            for (j = 0; j < target.external.length; j++) {
                                if (! _.findWhere($scope.external, {step: target.external[j]})) {
                                    obj = angular.extend({}, target.external[j], {index: $scope.external.length});
                                    if ($scope.model.meta.positions[target.external[j].step]) {
                                        obj.xpos = $scope.model.meta.positions[target.external[j].step].xpos;
                                        obj.ypos = $scope.model.meta.positions[target.external[j].step].ypos;
                                    }
                                    $scope.external.push(obj);
                                }
                            }
                            if (target.external.length) {
                                processExternalNode(i, target.external);
                            }
                        }
                    } else {
                        // if server node is a valid enough node to be shown, then connect the user node with it's server node
                        if ($scope.isNodeShown(nodes[i+1])) {
                            // user nodes always link to the corresponding server node
                            // this can therefore by default link user-node:right to system-node:left
                            instance.connect({ source: 'node' + i, target: 'node' + (i+1) }, userToSystem);
                        }
                    }
                }
            });
        };

        $scope.isNodeShown = function(node) {
            if (node.nodeType === 'system') {
                return node.step && node.templates.velocity && node.templates.velocity.content;
            }
            return true;
        };
        $scope.findNode = function(name) {
            var i, node;
            for (i = 0; i < $scope.model.nodes.length; i++) {
                node = $scope.model.nodes[i];
                if (node.step === name) {
                    return {node: node, index: i};
                }
            }
            return {node: null, index: -1};
        };

        $scope.findFields = function(node) {
            var fields = [], i, j;
            if (node.nodeType === 'user') {
                for (i = 0; i < node.blocks.length; i++) {
                    for (j = 0; j < node.blocks[i].elements.length; j++) {
                        if (node.blocks[i].elements[j].type === 'field') {
                            fields.push(node.blocks[i].elements[j]);
                        }
                    }
                }
            }
            return fields;
        };

        $scope.exportDiagram = function() {
            var flow = $scope.model.name;
            domtoimage.toBlob(document.getElementById('callflowDiagram'))
                .then(function(blob) {
                    window.saveAs(blob, flow + '.png');
                });
        };

        $scope.visualize = function() {
            var nodes, i;
            if (callflows.current && callflows.current.raw) {
                $scope.model = callflows.current.raw;
                // include meta object if not present
                if (! $scope.model.meta) {
                    $scope.model.meta = {};
                }
                if (! $scope.model.meta.positions) {
                    $scope.model.meta.positions = {};
                }
                nodes = $scope.model.nodes;
                for (i = 0; i < nodes.length; i++) {
                    // If currently there are no x and y positions, set some sensible defaults
                    if (! nodes[i].xpos && ! nodes[i].ypos) {
                        nodes[i].ypos = (i * 25) + 40;
                        nodes[i].xpos = nodes[i].nodeType === 'user' ? 100 : 420;
                    }
                }
                // and make the connections
                // This needs to be in a timeout cause we want the previous digest to complete and the DOM to be ready
                // otherwise jsplumb won't be able to find the DOM nodes to connect
                $timeout(function() {
                    $scope.model.meta.graphType = $scope.model.meta.graphType || "StateMachine";
                    $scope.connect();
                }, 1);
            }
        };

    }]);

    /* Provider Controller for managing IVR provider data */
    controllers.controller('ProviderController', [
        '$scope', '$http', '$log', '$timeout', 'settingsService', 'CALLFLOW_API', function(
        $scope, $http, $log, $timeout, settingsService, CALLFLOW_API) {

        var hideMsgLater = function(index) {
                return $timeout(function() {
                    $scope.messages.splice(index, 1);
                }, 5000);
            },
            prepare = function(configs) {
                angular.forEach(configs, function(config) {
                    var testUsersMap = {};
                    angular.forEach(config.testUsers, function(user) {
                        testUsersMap[user.phone] = user.outbound ? user.outbound : "";
                    });
                    config.testUsersMapString = angular.toJson(testUsersMap);
                });
                return configs;
            },
            autoExpandSingleAccordion = function() {
                if ($scope.accordions.length === 1) {
                    $scope.accordions[0] = true;
                }
            },
            setAccordions = function(configs) {
                var i;
                $scope.accordions = [];
                $scope.dupeNames = [];
                for (i = 0 ; i < configs.length ; i = i + 1) {
                    $scope.accordions.push(false);
                    $scope.dupeNames.push(false);
                }
                autoExpandSingleAccordion();
            };

        // to track errors and messages
        $scope.errors = [];
        $scope.messages = [];
        $scope.dupeNames = [];
        $scope.testUsers = [];

        $scope.checkForDuplicateNames = function(index) {
            var i;
            for (i = 0 ; i < $scope.configs.length ; i = i + 1) {
                if (i!==index && $scope.configs[i].name === $scope.configs[index].name) {
                    $scope.dupeNames[index] = true;
                    return;
                }
            }
            $scope.dupeNames[index] = false;
        };

        $scope.anyDuplicateNames = function() {
            var i;
            for (i = 0 ; i < $scope.dupeNames.length ; i += 1) {
                if ($scope.dupeNames[i]) { return true; }
            }
            return false;
        };

        settingsService.loadConfigs(
            function(response) {
                $scope.configs = response;
                setAccordions($scope.configs);
            },
            function(response) {
                $scope.configs = [];
                $scope.errors.push($scope.msg('callflows.error.no-config', response));
                setAccordions([]);
            });

        setAccordions([]);

        $scope.collapseAccordions = function () {
            var key;
            angular.forEach($scope.accordions, function(key) {
                $scope.accordions[key] = false;
            });
            autoExpandSingleAccordion();
        };

        $scope.addTestUser = function(config) {
            config.testUsers.push({});
        };

        $scope.deleteTestUser = function(config, index) {
            config.testUsers.splice(index, 1);
        };

        $scope.deleteTestUsers = function(config) {
            config.testUsers = [];
        };

        $scope.addConfig = function () {
            var config = settingsService.newConfig();
            $scope.configs = settingsService.configs;
            $scope.accordions.push(true);
            autoExpandSingleAccordion();
            $scope.dupeNames.push(false);
        };

        $scope.deleteConfig = function(index) {
            settingsService.deleteConfig(index);
            $scope.configs = settingsService.configs;
            $scope.accordions.splice(index, 1);
            $scope.dupeNames.splice(index, 1);
            autoExpandSingleAccordion();
        };

        $scope.addRenderer = function(name) {
            var renderer = settingsService.newRenderer(name);
            // We don't want to disturb the UI
            if (renderer) {
                $scope.renderers = settingsService.renderers;
                $scope.currentRenderer = renderer;
            }
        };

        $scope.setRenderer = function(renderer) {
            $scope.currentRenderer = renderer;
        };

        $scope.isDirty = function () {
            if (!$scope.originalConfigs || !$scope.configs) {
                return false;
            }
            return !angular.equals($scope.originalConfigs, $scope.configs);
        };

        $scope.reset = function () {
            $scope.configs = angular.copy($scope.originalConfigs);
            setAccordions($scope.configs);
        };

        $scope.submit = function () {
            settingsService.saveConfigs(
                function(data) {
                    setAccordions($scope.configs);
                    var index = $scope.messages.push($scope.msg('callflows.ok.providersSaved'));
                    hideMsgLater(index-1);
                }, function(response) {
                    handleWithStackTrace('callflows.error.header', 'callflows.error.body', response);
                });
        };

    }]);

    /* Renderer Controller */
    controllers.controller('RendererController', ['$scope', 'settingsService', '$timeout',
        function($scope, settingsService, $timeout) {

        var hideMsgLater = function(index) {
            return $timeout(function() {
                $scope.messages.splice(index, 1);
            }, 5000);
        };

        $scope.settings = settingsService;

        $scope.messages = [];

        // load data at the beginning
        $scope.settings.loadRenderers(
            function(response) {
                $scope.renderers = response;
                if (response.length) {
                    $scope.currentRenderer = response[0];
                }
            },
            function(response) {
                $scope.renderers = [];
                $scope.errors.push($scope.msg('callflows.error.noRenderer', response));
            }
        );

        // Adding a new renderer
        $scope.addRenderer = function(name) {
            var renderer = $scope.settings.newRenderer(name);
            // We don't want to disturb the UI, if no name was provided
            if (renderer) {
                $scope.currentRenderer = renderer;
            }
        };

        $scope.setRenderer = function(renderer) {
            $scope.currentRenderer = renderer;
        };

        $scope.reset = function() {
            $scope.renderers = $scope.settings.resetRenderers();
        };
        $scope.save = function() {
            settingsService.saveRenderers(
            function(response) {
                var index = $scope.messages.push($scope.msg('callflows.ok.renderersSaved'));
                hideMsgLater(index-1);
            },
            function(response) {
                handleWithStackTrace('callflows.error.header', 'callflows.error.body', response);
            });
        };
        $scope.del = function(name) {
            settingsService.deleteRenderer(name, function(deleted) {
                if (deleted) {
                    $scope.currentRenderer = $scope.settings.renderers.length ? $scope.settings.renderers[0] : null;
                }
            });
        };
    }]);

}());

