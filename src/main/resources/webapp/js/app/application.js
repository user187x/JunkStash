var app = angular.module('app', ['ngRoute', 'ui.bootstrap','ngAnimate','ngWebsocket']);

app.config([ '$routeProvider', '$locationProvider' , function($routeProvider, $locationProvider) {
	
	$routeProvider.when('/', {
		
		templateUrl : '/js/app/views/homeView.html',
		controller : 'homeController'
	})
	.otherwise({
		redirectTo : '/'
	});
	
	//Turns on Pretty URL HTML5 
	$locationProvider.html5Mode(true).hashPrefix('!');
	
} ]);