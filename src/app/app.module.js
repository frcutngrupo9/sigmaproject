(function() {
  var app = angular.module('woodShop', []);

  app.controller('shopController', function() {

  });

  app.directive("menuBar", function() {
    return {
      restrict: "E",
      templateUrl: "shared/menubar/menu-bar.html"
    };
  });

  app.directive("miDirectiva", function() {
    return {
      restrict: "E",
      templateUrl: "mi-directiva.html"
    };
  });


})();