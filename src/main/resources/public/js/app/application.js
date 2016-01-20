var app = angular.module('app', ['ngRoute']);

app.config([ '$routeProvider', '$locationProvider' , function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/', {
		
		templateUrl : '/js/app/views/primaryView.html',
		controller : 'primaryController'
	})
	.otherwise({
		redirectTo : '/'
	});
	
	//Turns on Pretty URL HTML5 
	$locationProvider.html5Mode(true).hashPrefix('!');
	
} ]);