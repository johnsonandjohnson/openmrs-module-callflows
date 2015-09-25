(function () {
    'use strict';

    /* App Module */

    angular.module('callflows', [
        'motech-dashboard',
        'callflows.controllers',
        'callflows.directives',
        'callflows.services',
        'ngCookies'])
        .config(['$routeProvider',
        function ($routeProvider) {

        }]);
}());


