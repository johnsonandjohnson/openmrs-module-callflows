(function () {
    'use strict';

    /* App Module */

    var callflows = angular.module('callflows',  [
    'motech-dashboard', 'callflows.controllers', 'callflows.directives', 'callflows.services', 'ngCookies', 'ngFileUpload'
    ])
    .config(['$stateProvider', function ($stateProvider) {
        $stateProvider
            .state('callflows', {
                url: "/callflows",
                abstract: true,
                views: {
                    "moduleToLoad": {
                        templateUrl: '../callflows/resources/index.html'
                    }
                }
            })
            .state('callflows.designer', {
                url: "/designer",
                parent: "callflows",
                views: {
                    "callflowsView": {
                        templateUrl: '../callflows/resources/partials/designer.html',
                        controller: 'DesignerController'
                    }
                }
            })
            .state('callflows.providers', {
                url: "/providers",
                parent: "callflows",
                views: {
                    "callflowsView": {
                        templateUrl: '../callflows/resources/partials/providers.html',
                        controller: 'ProviderController'
                    }
                }
            })
            .state('callflows.renderers', {
                url: "/renderers",
                parent: "callflows",
                views: {
                    "callflowsView": {
                        templateUrl: '../callflows/resources/partials/renderers.html',
                        controller: 'RendererController'
                    }
                }
            });
    }]);

    // some utils, esp things that are not there in this version of _
    _.mixin({
        indexOf: function(arr, prop, val) {
            var i, x;
            for (i = 0; i < arr.length; i+=1) {
                x = arr[i];
                if (x[prop] === val) {
                    return i;
                }
            }
            return -1;
        }
    });

}());


