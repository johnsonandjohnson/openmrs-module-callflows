(function () {
    'use strict';

    var services = angular.module('callflows.services', ['ngResource']);

    services.constant('CALLFLOW_API', {
        'CALLFLOW'          : '../callflows/flows',
        'CALLFLOW_BY_NAME'  : '../callflows/flows?lookup=By Name&term={0}',
        'INBOUND_FLOW'      : '../callflows/in/{0}/flows/{1}.json',
        'CONFIG'            : '../callflows/configs',
        'RENDERER'          : '../callflows/renderers'
    });

    services.constant('CONFIG', {
        // Default renderer text for new renderers
        'RENDERER_TXT'      : 'This is the default renderer. It doesn\'t do much unless it is edited. :)\nYou can use underscore js syntax in this template.',
        // By no means a exhaustive list, this is anyways editable and just provided to give a head start to the user
        'MIME_TYPES'    : {
            'vxml'  : 'application/voicexml+xml',
            'ccxml' : 'application/ccxml+xml'
        },
        'FIELD_TYPES'   : ['digits', 'date', 'boolean', 'currency', 'number', 'phone', 'time'],
        // If we don't know what the extension is
        'DEFAULT_MIME'  : 'text/plain'
    });

    /* Call flow Service */
    services.factory('callflows', ['$http', '$log', 'CALLFLOW_API', 'CONFIG', 'settingsService', function($http, $log, CALLFLOW_API, CONFIG, settingsService) {
        // container
        var flows = {},
            settings = settingsService;

        // Track the current flow here
        flows.current = null;

        flows.loadFlow = function(flow) {
            $http.get(CALLFLOW_API.CALLFLOW_BY_NAME.replace('{0}', flow))
                .success(function(response) {
                    if (response.results && response.results.length) {
                        flows.setCurrent(flows.deserialize(response.results[0]));
                        flows.refreshed = new Date();
                    }
                });
        };

        flows.deserialize = function(flow) {
            var raw = angular.fromJson(flow.raw),
                loadedFlow, nodes, i;
            // deserialize the meta field, which is provided for extensibility and hence a string
            raw.meta = angular.fromJson(raw.meta);
            loadedFlow = {
                id          : flow.id,
                name        : flow.name,
                description : null,
                raw         : raw,
                status      : flow.status
            };
            // We got the flow, we got back the object from the raw node, but each node also has it's own
            // currentBlock and currentElement which also have to be re-initialized
            nodes = loadedFlow.raw.nodes;
            for (i = 0; i < nodes.length; i+=1) {
                if (nodes[i].nodeType === 'user') {
                    // we reset to null, cause using json parse above would have created a brand new object,
                    // ideally we need a reference to an existing object
                    nodes[i].currentBlock = null;
                    nodes[i].currentElement = null;
                }
            }
            return loadedFlow;
        };

        flows.setCurrent = function(flow) {
            var renderers = settings.renderers, i, j, node;
            for (i = 0; i < flow.raw.nodes.length; i+=1) {
                node = flow.raw.nodes[i];
                if (node.nodeType === 'user') {
                    for (j = 0; j < renderers.length; j+=1) {
                        if (! node.templates[renderers[j].name]) {
                            node.templates[renderers[j].name] = {
                                dirty: false
                            };
                        }
                    }
                }
            }
            flows.current = flow;
        };

        // render a node in a specific format
        flows.renderNode = function(node, renderer) {
            return renderer.render(node, flows.current);
        };

        flows.addFlow = function() {
            var flow = {
                raw: { nodes: [] , audio: {}},
                status: 'DRAFT'
            };
            flows.current = flow;
            return flow;
        };

        flows.addInteraction = function() {
            var flow = flows.current, i,
                userNode = {
                    nodeType    : 'user',
                    templates : {},
                    blocks: []
                },
                systemNode = {
                    nodeType: 'system' ,
                    templates: {}
                };
            for (i = 0; i < settings.renderers.length; i+=1) {
                // each template has a content and a dirty attribute
                userNode.templates[settings.renderers[i].name] = {dirty: false};
            }
            // System Nodes have one handler for now
            systemNode.templates.velocity = {};

            flow.raw.nodes.push(userNode);
            flow.raw.nodes.push(systemNode);
        };

        flows.deleteNode = function(id) {
            var current = flows.current;
            current.raw.nodes.splice(id, 2);
        };

        /* Blocks */
        flows.addBlock = function(node, type) {
            var block = {type: type};
            if (type === 'form') {
                block.name = 'Form';
                block.elements = [];
            } else if (type === 'menu') {
                block.name = 'Menu';
                block.elements = [];
            }
            node.blocks.push(block);
            // This is the current block
            node.currentBlock = block;
            node.currentBlockId = node.blocks.length - 1;
            // reset current element
            node.currentElement = block.elements[0];
            node.currentElementId = 0;
        };

        flows.deleteBlock = function(node, blockId) {
            node.blocks.splice(blockId, 1);
            if (blockId > 0) {
                // set current block
                node.currentBlock = node.blocks[blockId - 1];
                node.currentBlockId = blockId - 1;
                node.currentElement = node.currentBlock.elements[0];
                node.currentElementId = 0;
            }  else {
                // no blocks. it's a sad world
                node.currentBlock = null;
                node.currentElement = null;
            }
        };

        flows.setCurrentBlock = function(node, blockId) {
            node.currentBlock = node.blocks[blockId];
            node.currentBlockId = blockId;
            node.currentElement = node.currentBlock.elements[0];
            node.currentElementId = 0;
        };

        flows.serialize = function() {
            var flow = angular.copy(flows.current);
            flow.raw.meta = angular.toJson(flow.raw.meta);
            // pack the name within the JSON as well for easy use
            flow.raw.name = flow.name;
            return {
                name        : flow.name,
                description : null,
                raw         : angular.toJson(flow.raw),
                status      : flow.status
            };
        };

        flows.saveFlow = function(success, error) {
            var flow = flows.current;
            // generic save, can either create or update flow
            if (flow.id) {
                flows.updateFlow(success, error);
            } else {
                flows.createFlow(success, error);
            }
        };

        // runs all renderers for a given flow
        flows.runRenderers = function(flow) {
            var nodes = flow.raw.nodes, i;
            // for each renderer format
            angular.forEach(settings.renderers, function(renderer) {
                var fmt = renderer.name;
                // for each user node
                for (i = 0; i < nodes.length; i+=1) {
                    // that is of type user
                    if (nodes[i].nodeType === 'user') {
                        // run the renderer template
                        if (! nodes[i].templates[fmt].dirty) {
                            nodes[i].templates[fmt].content = renderer.render(nodes[i], flow);
                        }
                    }
                }
            });
        };

        flows.createFlow = function(successCallback, errorCallback) {
            var flow = flows.current;
            // At the time of save, we need to run all the renderers
            flows.runRenderers(flow);

            $http.post(CALLFLOW_API.CALLFLOW, flows.serialize())
                .success(function(response) {
                    // Set the id to the saved object, so that next time we can do update
                    flow.id = response.id;
                    successCallback();
                })
                .error(function (response) {
                    errorCallback();
                });
        };

        flows.updateFlow = function(successCallback, errorCallback) {
            var flow = flows.current;
            // At the time of save, we need to run all the renderers
            flows.runRenderers(flow);

            $http.put(CALLFLOW_API.CALLFLOW + '/' + flow.id, flows.serialize())
                .success(function(response) {
                    successCallback();
                })
                .error(function (response) {
                    errorCallback();
                });
        };

        flows.run = function(successCallback, errorCallback) {
            var flow = flows.current,
                config = settings.configs[0].name,
                url = CALLFLOW_API.INBOUND_FLOW.replace('{0}', config).replace('{1}', flow.name);
            // Call runner URL
            $http({
                method: 'GET',
                url: url
            })
            .success(function (response) {
                successCallback(response);
            })
            .error (function (response) {
                errorCallback(response);
            });
        };

        /* Elements */
        flows.addElement = function(node, type) {

            var element = {type: type};
            // for fields, we will allow user to enter a nice name (the field name)
            if (type !== 'field') {
                element.name = type[0].toUpperCase() + type.slice(1);
            }
            if (type === 'field') {
                element.fieldType = 'digits';
                //element.txt = "please enter this field";
                //element.noInput = "Sorry I did not hear that input";
                //element.noMatch = "Sorry that did not match any recognized input";
                element.reprompt = 3;
                // sensible defaults
                element.bargeIn = true;
                element.dtmf = true;
                element.voice = false;
            }
            node.currentBlock.elements.push(element);
            // set current element
            node.currentElement = element;
            node.currentElementId = node.currentBlock.elements.length - 1;
        };

        flows.deleteElement = function(node, block, elementId) {
            var newIndex;
            block.elements.splice(elementId, 1);
            newIndex = block.elements[elementId - 1] ? elementId - 1 : 0;
            node.currentElement = block.elements[newIndex];
            node.currentElementId = newIndex;
        };

        flows.setCurrentElement = function(node, block, elementId) {
            node.currentElement = block.elements[elementId];
            node.currentElementId = elementId;
        };

        // Every controller that injects this Service, will assign this service to a scoped variable, say flows
        // Then current flows can be accessed as flows.current

        return flows;
    }]);

    // Settings Service - to manage both IVR configurations as well as renderers
    services.factory('settingsService', function($http, $log, CALLFLOW_API, CONFIG) {
        // settings = container object. This object is the service and methods set on this container are exposed
        // The rest of the functions are internal
        var settings        = {
                configs     : [],
                renderers   : []
            },
            originalConfigs = [],
            originalRenderers = [],
            builders        = {
                'CONFIG'    : function(data) {
                    angular.forEach(data, function(config) {
                        var testUsersMap = angular.fromJson(config.testUsersMap),
                            servicesMap = angular.fromJson(config.servicesMap),
                            services = [],
                            outgoingCallPostHeadersMap = angular.fromJson(config.outgoingCallPostHeadersMap),
                            outgoingCallPostHeaders = [];

                        config.testUsers = [];
                        angular.forEach(testUsersMap, function(outbound, phone) {
                            config.testUsers.push({phone: phone, outbound: outbound});
                        });

                        angular.forEach(servicesMap, function(val, key) {
                            services.push(key + ':' + val);
                        });
                        config.servicesMapString = services.join(',');

                        angular.forEach(outgoingCallPostHeadersMap, function(val, key) {
                            outgoingCallPostHeaders.push(key + ':' + val);
                        });
                        config.outgoingCallPostHeadersMapString = outgoingCallPostHeaders.join(',');
                    });
                },
                'RENDERER' : function(data) {
                        angular.forEach(data, function(renderer) {
                            renderer.render = function(node, flow) {
                                var tpl = _.template(renderer.template);
                                return tpl({node: node, flow: flow});
                            };
                        });
                }
            },
            loadSettings    = function(restApiKey, success, error) {
                $http.get(CALLFLOW_API[restApiKey])
                    .success(function(response) {
                        if (restApiKey === 'CONFIG') {
                            settings.configs = response;
                            originalConfigs = angular.copy(response);
                            builders.CONFIG(settings.configs);
                        } else {
                            settings.renderers = response;
                            originalRenderers = angular.copy(response);
														builders.RENDERER(settings.renderers);
                        }
                        if (success) { success(response); }
                    }).error(function(response) {
                        if (error) { error(response); }
                    });
            }, parseServicesMapString = function(str) {
                var services = str.split(/\s*,\s*/),
                    out = {};
                angular.forEach(services, function(service) {
                    var keyVal = service.split(/\s*:\s*/);
                    out[keyVal[0]] = keyVal[1];
                });
                return out;
            }, parseOutgoingCallPostHeadersMapString = function(str) {
                var outgoingCallPostHeaders = str.split(/\s*,\s*/),
                    out = {};
                angular.forEach(outgoingCallPostHeaders, function(header) {
                    var keyVal = header.split(/\s*:\s*/);
                    out[keyVal[0]] = keyVal[1];
                });
                return out;
            }, buildTestUsersMap = function(testUsers) {
                var testUsersMap = {};
                angular.forEach(testUsers, function(testUser) {
                    testUsersMap[testUser.phone] = testUser.outbound;
                });
                return testUsersMap;
            }, prepareConfigs = function(data) {
                var out = [],
                    servicesMap = {},
                    outgoingCallPostHeadersMap = {},
                    testUsersMap = {},
                    i, services, outgoingCallPostHeaders;

                for (i = 0; i < data.length; i+=1) {
                    servicesMap = parseServicesMapString(data[i].servicesMapString);
                    outgoingCallPostHeadersMap = parseOutgoingCallPostHeadersMapString(data[i].outgoingCallPostHeadersMapString);
                    testUsersMap = buildTestUsersMap(data[i].testUsers);
                    out.push({
                        name: data[i].name,
                        outgoingCallUriTemplate : data[i].outgoingCallUriTemplate,
                        outgoingCallMethod : data[i].outgoingCallMethod,
                        outgoingCallPostParams : data[i].outgoingCallPostParams,
                        outboundCallLimit : data[i].outboundCallLimit,
                        outboundCallRetryAttempts : data[i].outboundCallRetryAttempts,
                        outboundCallRetrySeconds : data[i].outboundCallRetrySeconds,
                        callAllowed : data[i].callAllowed,
                        servicesMap : servicesMap,
                        outgoingCallPostHeadersMap : outgoingCallPostHeadersMap,
                        testUsersMap : testUsersMap
                    });
                }
                return out;
            };

        settings.loadConfigs = function(success, error) {
            loadSettings('CONFIG', success, error);
        };

        settings.loadRenderers = function(success, error) {
            loadSettings('RENDERER', success, error);
        };

        // Create a new Configuration
        settings.newConfig = function() {
            var config = {
                'name'                      :'',
                'outgoingCallUriTemplate'   :'',
                'outgoingCallMethod'        :'GET',
                'ignoredStatusFields'       :[],
                'outgoingCallPostHeadersMapString' :'',
                'outgoingCallPostParams'    :'',
                'statusFieldMapString'      :'',
                'servicesMapString'         :'',
                'outboundCallLimit'         :0,
                'outboundCallRetrySeconds'  :0,
                'outboundCallRetryAttempts' :0,
                'callAllowed'               :true,
                'testUsersMapString'        : '',
                'testUsers'                 : []
            };
            settings.configs.push(config);
            return config;
        };

        // delete a configuration
        settings.deleteConfig = function(index) {
            settings.configs.splice(index, 1);
        };

        // create a new renderer
        settings.newRenderer = function(name) {
            if (! _.findWhere(settings.renderers, {name: name})) {
                var mime = CONFIG.MIME_TYPES[name.toLowerCase()],
                    renderer = {
                        name: name,
                        template: CONFIG.RENDERER_TXT,
                        mimeType: mime || CONFIG.DEFAULT_MIME
                    };
                // Each renderer has a render function defined on it, which is the crux of the whole system
                // This renderer expects a template in underscore.js syntax
                // To each render, we can pass the current node object and the template can do something intelligent with it
                renderer.render = function(node) {
                    var tpl = _.template(renderer.template);
                    return tpl({node: node});
                };
                settings.renderers.push(renderer);
                return renderer;
            }
        };

        settings.saveConfigs = function(success, error) {
            var configs = prepareConfigs(settings.configs);
            $http.post(CALLFLOW_API.CONFIG, configs)
                .success(function(response) {
                    if (success) { success(response);}
                })
                .error (function(response) {
                    if (error) { error(response);}
                });
        };

        settings.resetRenderers = function() {
            settings.renderers = angular.copy(originalRenderers);
            return settings.renderers;
        };
        settings.saveRenderers = function(success, error) {
            $http.post(CALLFLOW_API.RENDERER, settings.renderers)
                .success(function(response) {
                    if (success) { success(response);}
                })
                .error (function(response) {
                    if (error) { error(response);}
                });
        };
        settings.deleteRenderer = function(name, handler) {
            var id = _.indexOf(settings.renderers, 'name', name);
            if (id > -1) {
                settings.renderers.splice(id, 1);
                settings.saveRenderers();
            }
            handler(id > -1);
        };

        return settings;
    });


    /* Audio Service */
    services.factory('audioService', ['$http', '$log', 'CALLFLOW_API', 'CONFIG', function($http, $log, CALLFLOW_API, CONFIG) {
        var audio = {
            language: 'en'
        };

        return audio;
    }]);

    services.factory('transformRequestToFormPost', function() {
        /**
         * Helper method to serialize data
         */

        var serialize = function(data) {
            var buffer = [], name, value, source;
            // If this is not an object, defer to native mechanism to create strings
            if (! angular.isObject(data)) {
                return(data === null ? "" : data.toString());
            }

            // Serialize each key in the object.
            for (name in data) {
                if (data.hasOwnProperty(name)) {
                    value = data[name];
                    buffer.push(encodeURIComponent(name) + "=" + encodeURIComponent(value === null ? "" : value));
                }
            }

            // Serialize the buffer and clean it up for transportation.
            source = buffer.join( "&" ).replace( /%20/g, "+" );
            return(source);
        },
        transform = function(data, headersFn) {
            var headers = headersFn();
            headers['Content-Type'] = "application/x-www-form-urlencoded; charset=utf-8";
            return serialize(data);
        };

        return transform;
    });

}());
