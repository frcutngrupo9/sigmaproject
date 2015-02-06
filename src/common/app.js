(function() {
  var app = angular.module('furnitureFactory', []);

  app.controller('factoryController', function() {
    this.products = furniture;
  });

  app.controller("TabController", function() {
    this.tab = 1;

    this.isSet = function(checkTab) {
      return this.tab === checkTab;
    };

    this.setTab = function(setTab) {
      this.tab = setTab;
    };
  });
})();