app.controller('primaryController', ['$scope', 'primaryFactory' ,function($scope, primaryFactory) {
	
	$scope.inputData;
	$scope.result;
	$scope.messages = [];
	
	$scope.search = function postData(data) { 		
		
		primaryFactory.search($scope.inputData).success(function (data) {
			
			$scope.result = data;
		});
	}
	
	$scope.submit = function postData(data) { 		
		
		primaryFactory.submit($scope.inputData).success(function (data) {
			
			$scope.result = data;
			$scope.getAll();
		});
	}
	
	$scope.getAll = function postData(data) { 		
		
		$scope.messages = [];
		
		primaryFactory.getAll().success(function (data) {
			
		    angular.forEach(data, function(value, key) {
		        
		    	$scope.messages.push(
	    			{ 
	    				message : value.message,
	    				time : value.time
    				}
    			);
		      
		    });
		});
	}
	
}]);