(function () {
    'use strict';

    var directives = angular.module('callflows.directives', []);

    // Code mirror - This part is adapted from ETl-Lite Module
    // This provides a directive like <code-editor>
    directives.directive('codeEditor', function($timeout, $log) {
        function postLink(scope, element, attrs, ngModel) {
            var syntax = attrs.syntax || 'velocity',
                refresh = attrs.refresh,
                theme = 'default ' + (attrs.theme || ''),
                template = attrs.template,
                fullscreenOn = function(cm) {
                    if (attrs.fullscreen) {
                        cm.setOption("fullScreen", !cm.getOption("fullScreen"));
                    }
                },
                fullScreenOff = function(cm) {
                    if (cm.getOption("fullScreen")) { cm.setOption("fullScreen", false); }
                },
                editor = new CodeMirror(element[0], {
                    lineNumbers: true,
                    lineWrapping: true,
                    addModeClass: true,
                    tabSize: 2,
                    mode: syntax,
                    theme: theme,
                    extraKeys: {
                        "F11": fullscreenOn,
                        "Esc": fullScreenOff
                    }
                }),
                unregister,
                init = function() {
                    $timeout(function() {
                        editor.refresh();
                    }, 100);
                    // after first time, we unwatch the proceedings
                    unregister();
                };

            if (!ngModel) {
                return;
            }

            // CodeMirror expects a string, so make sure it gets one.
            // This does not change the model.
            ngModel.$formatters.push(function(value) {
                if (angular.isUndefined(value) || value === null) {
                    return '';
                } else if (angular.isObject(value) || angular.isArray(value)) {
                    throw new Error('ui-codemirror cannot use an object or an array as a model');
                }
                return value;
            });

            // Override the ngModelController $render method, which is what gets called when the model is updated.
            // This takes care of the synchronizing the codeMirror element with the underlying model, in the case that it is changed by something else.
            ngModel.$render = function() {
                //Code mirror expects a string so make sure it gets one
                //Although the formatter have already done this, it can be possible that another formatter returns undefined (for example the required directive)
                var safeViewValue = ngModel.$viewValue || '';
                editor.setValue(safeViewValue);
            };

            // Keep the ngModel in sync with changes from CodeMirror
            editor.on('change', function(instance) {
                var newValue = instance.getValue();
                if (newValue !== ngModel.$viewValue) {
                    scope.$evalAsync(function() {
                        ngModel.$setViewValue(newValue);
                    });
                }
            });

            // cursor activity
            editor.on('cursorActivity', function(instance) {
                if (scope.editor) {
                    if (scope.selection && instance.somethingSelected()) {
                        scope.$apply(function() {
                            scope.editor(editor);
                        });
                    }
                }
            });

            if (refresh) {
                unregister = scope.$watch(refresh, function(newValue, oldValue) {
                    if (newValue !== oldValue) {
                        init();
                    }
                });
            }
            $timeout(function() {
                editor.refresh();
            }, 100);
        }
        return {
            restrict: 'EA',
            require: '?ngModel',
            scope: {
                selection: '=',
                editor: '='
            },
            compile: function compile() {

                // Require CodeMirror
                if (angular.isUndefined(window.CodeMirror)) {
                    throw new Error('ui-codemirror need CodeMirror to work... (o rly?)');
                }
                return postLink;
            }
        };
    });

}());
