(function() {
    'use strict';

    /* Controllers */
    var controllers = angular.module('callflows.controllers', []);

    /* Designer Controller for modelling callflows */
    controllers.controller('DesignerController', function() {

    });

    /* Provider Controller for managing IVR provider data */
    controllers.controller('ProviderController', [
        '$scope', '$http', '$log', '$timeout', 'settingsService', 'REST_API', function(
        $scope, $http, $log, $timeout, settingsService, REST_API) {

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
            for (key in $scope.accordions) {
                $scope.accordions[key] = false;
            }
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
            if ($scope.originalConfigs === null || $scope.configs === null) {
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

