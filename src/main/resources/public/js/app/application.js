var app = angular.module('app', ['ngRoute']);

app.config([ '$routeProvider', '$locationProvider' , function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/', {
		
		templateUrl : '/js/app/views/primaryView.html',
		controller : 'primaryController'
	})
	.when('/2', {
		
		templateUrl : '/js/app/views/secondaryView.html',
		controller : 'secondaryController'
	})
	.when('/3', {
		
		templateUrl : '/js/app/views/ternaryView.html',
		controller : 'ternaryController'
	})
	.otherwise({
		
		redirectTo : '/'
	});
	
	// 	Turns on Pretty URL HTML5 
	//	$locationProvider.html5Mode(true).hashPrefix('!');
	
} ]);