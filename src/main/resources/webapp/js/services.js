(function () {
    'use strict';

    var services = angular.module('callflows.services', ['ngResource']);

    services.constant('REST_API', {
        'CALLFLOW'      : '../callflows/flows',
        'INBOUND_FLOW'  : '../callflows/in/{0}/flows/{1}.json',
        'CONFIG'        : '../callflows/configs',
        'RENDERER'      : '../callflows/renderers'
    });

    services.constant('CONFIG', {
        // Default renderer text for new renderers
        'RENDERER_TXT'      : 'This is the default renderer. It doesn\'t do much unless it is edited. :)\nYou can use underscore js syntax in this template.',
        // By no means a exhaustive list, this is anyways editable and just provided to give a head start to the user
        'MIME_TYPES'    : {
            'vxml'  : 'application/voicexml+xml',
            'ccxml' : 'application/ccxml+xml'
        },
        // If we don't know what the extension is
        'DEFAULT_MIME'  : 'text/plain'
    });

    // Settings Service - to manage both IVR configurations as well as renderers
    services.factory('settingsService', function($http, $log, REST_API, CONFIG) {
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
                        $log.log(config);
                        var testUsersMap = angular.fromJson(config.testUsersMap),
                            servicesMap = angular.fromJson(config.servicesMap),
                            services = [];

                        config.testUsers = [];
                        angular.forEach(testUsersMap, function(outbound, phone) {
                            config.testUsers.push({phone: phone, outbound: outbound});
                        });
                        angular.forEach(servicesMap, function(val, key) {
                            services.push(key + ':' + val);
                        });
                        config.servicesMapString = services.join(',');
                        $log.log(config);
                    });
                }
            },
            loadSettings    = function(rest_api_key, success, error) {
                $http.get(REST_API[rest_api_key])
                    .success(function(response) {
                        if (rest_api_key === 'CONFIG') {
                            settings.configs = response;
                            originalConfigs = angular.copy(response);
                            builders.CONFIG(settings.configs);
                        } else {
                            settings.renderers = response;
                            originalRenderers = angular.copy(response);
                        }
                        if (success) { success(response); }
                    }).error(function(response) {
                        $log.log('ERROR retrieving ' + rest_api_key + JSON.stringify(response));
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
            }, buildTestUsersMap = function(testUsers) {
                var testUsersMap = {};
                angular.forEach(testUsers, function(testUser) {
                    testUsersMap[testUser.phone] = testUser.outbound;
                });
                return testUsersMap;
            }, prepareConfigs = function(data) {
                var out = [],
                    servicesMap = {},
                    testUsersMap = {},
                    i, services;

                for (i = 0; i < data.length; i+=1) {
                    servicesMap = parseServicesMapString(data[i].servicesMapString);
                    testUsersMap = buildTestUsersMap(data[i].testUsers);

                    out.push({
                        name: data[i].name,
                        outgoingCallUriTemplate : data[i].outgoingCallUriTemplate,
                        outgoingCallMethod : data[i].outgoingCallMethod,
                        servicesMap : servicesMap,
                        testUsersMap : testUsersMap
                    });
                }
                $log.log(out);
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
                'statusFieldMapString'      :'',
                'servicesMapString'         :'',
                'outgoingCallLimit'         :0,
                'outgoingCallRetrySeconds'  :0,
                'outgoingCallRetryAttempts' :0,
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
                $log.log(settings.renderers);
                settings.renderers.push(renderer);
                $log.log(settings.renderers);
                return renderer;
            }
        };

        settings.saveConfigs = function(success, error) {
            $log.log(settings.configs);
            var configs = prepareConfigs(settings.configs);
            $http.post(REST_API.CONFIG, configs)
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
            $http.post(REST_API.RENDERER, settings.renderers)
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

}());
