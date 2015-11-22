app.controller('primaryController', ['$scope', 'primaryFactory' ,function($scope, primaryFactory) {
  
	$scope.greetings = 'Variable Passed From Primary Controller Successfully';
	$scope.inputData;
	
	$scope.allData;
	
	$scope.getData = function getData() { 
		
		primaryFactory.testGet().success(function (data) {
			$scope.getResponse = data;
        })
        .error(function (error) {
        	console.log(error);
        });
   }
	
	$scope.getAllData = function getAllData() { 
		
		primaryFactory.testGetAll().success(function (data) {
			$scope.allData = data;
			
			console.log('Data...'+data);
        })
        .error(function (error) {
        	console.log(error);
        });
   }
	
	$scope.postData = function postData(data) { 		
		primaryFactory.testPost($scope.inputData);
   }
	
}]);