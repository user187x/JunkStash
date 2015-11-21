app.controller('primaryController', ['$scope', 'primaryFactory' ,function($scope, primaryFactory) {
  
	$scope.greetings = 'Variable Passed From Primary Controller Successfully';
	$scope.inputData;
	
	$scope.getData = function getData() { 
		
		primaryFactory.testGet().success(function (data) {
			$scope.getResponse = data;
        })
        .error(function (error) {
        	$scope.getResponse = 'Failure Retreiving Data';
        });
   }
	
	$scope.postData = function postData(data) { 
		
		console.log('Input data from JS : '+$scope.inputData);
		
		primaryFactory.testPost($scope.inputData);
   }
	
}]);