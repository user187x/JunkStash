app.controller('primaryController', ['$scope', 'primaryFactory' ,function($scope, primaryFactory) {
  
	$scope.inputData;
	$scope.result;
	
	$scope.search = function postData(data) { 		
		
		primaryFactory.search($scope.inputData).success(function (data) {
			
			$scope.result = data;
			
		});
   }
	
}]);