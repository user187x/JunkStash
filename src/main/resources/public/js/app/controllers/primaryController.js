app.controller('primaryController', ['$scope', 'primaryFactory' ,function($scope, primaryFactory) {
  
	$scope.greetings = 'Variable Passed From Primary Controller Successfully';

	$scope.getData = function getData() { 
		
		primaryFactory.testGet().success(function (data) {
			$scope.getResponse = data;
        })
        .error(function (error) {
        	$scope.getResponse = 'Failure Retreiving Data';
        });
   }
	
	$scope.postData = function postData(data) { 
		
		primaryFactory.testPost().success(function (data) {
			$scope.postResponse = data;
        })
        .error(function (error) {
        	$scope.postResponse = 'Failure Posting Data';
        });
   }
	
}]);