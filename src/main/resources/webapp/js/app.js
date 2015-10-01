(function () {
    'use strict';

    /* App Module */

    angular.module('callflows',  [
    'motech-dashboard', 'callflows.controllers', 'callflows.directives', 'callflows.services', 'ngCookies'
    ])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/designer', {
                templateUrl: '../callflows/resources/partials/designer.html',
                controller: 'DesignerController'
            })
            .when('/providers', {
                templateUrl: '../callflows/resources/partials/providers.html',
                controller: 'ProviderController'
            })
            .when('/renderers', {
                templateUrl: '../callflows/resources/partials/renderers.html',
                controller: 'RendererController'
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


