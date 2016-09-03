var app = angular.module('app', ['ngRoute', 'ngCookies', 'ui.bootstrap', 'ngAnimate', 'ngWebsocket', 'angular-loading-bar']);

app.config([ '$routeProvider', '$locationProvider' , function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/', {
		
		templateUrl : '/js/app/views/home.html',
		controller : 'homeController'
	})
	.otherwise({
		redirectTo : '/'
	});
	
	//Turns on Pretty URL HTML5 
	$locationProvider.html5Mode(true).hashPrefix('!');
	
}]).
run(['socketService', function(socketService) {
	  
	//Get Socket URL From Server
	socketService.getSocketInfo();
	
}]);