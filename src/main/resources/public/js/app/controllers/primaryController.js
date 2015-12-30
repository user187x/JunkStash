app.controller('primaryController', ['$scope', 'primaryFactory' ,function($scope, primaryFactory) {
	
	$scope.messages = [];
	$scope.inputData = null;
	$scope.result = null;
	$scope.uploadFile = null;
	$scope.loading = false;
	
	$scope.search = function postData(data) { 		
		
		primaryFactory.search($scope.inputData).success(function (data) {
			$scope.result = data;
		});
	}
	
	$scope.submit = function postData(data) { 		
		
		primaryFactory.submit($scope.inputData).success(function (data) {
			
			$scope.result = data;
			
			$scope.clear();
			$scope.refresh();
		});
	}
	
	$scope.upload = function postData() { 		
		
		$scope.loading = true;
		
		primaryFactory.upload($scope.uploadFile)
			.success(function (data) {
			
				$scope.result = data;
				$scope.loading = false;
			})
			.error(function (data) {
			
				$scope.result = data;
				$scope.loading = false;
			});
	}
	
	$scope.getAll = function postData(data) { 		
		
		$scope.messages = [];
		
		primaryFactory.getAll().success(function (data) {
			
		    angular.forEach(data.payload, function(value, key) {
		        
		    	$scope.messages.push({ 
    				
		    		message : value.message,
    				time : value.time,
    				id : value.id
				});
		    });
		});
	}
	
	$scope.refresh = function(){
		$scope.getAll();
	}
	
	$scope.clear = function(){
		$scope.inputData = undefined;
		$scope.result = undefined;
	}
	
	$scope.remove = function remove(data){
		
		primaryFactory.remove(data).success(function (data) {
			
			$scope.result = data;
			$scope.refresh();
		});
	}
	
}]);