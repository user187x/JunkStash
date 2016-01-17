app.controller('primaryController', ['$scope', 'primaryFactory', '$rootScope' ,function($scope, primaryFactory, $rootScope) {
	
	$scope.messages = [];
	$scope.inputData = undefined;
	$scope.result = undefined;
	$scope.uploadFile = undefined;
	$scope.loading = false;
    $scope.showModal = false;
    $scope.user = undefined;
    
    $scope.totalSpace = undefined;
    $scope.totalSpaceNormalized = undefined;
    $scope.maxSpaceNormalized = undefined;
    $scope.maxSpace = undefined;
    $scope.percentUsed = undefined;
    
    $scope.toggleModal = function(){
    	
        $scope.showModal = !$scope.showModal;
    };
	
	$scope.search = function postData(data) { 		
		
		primaryFactory.search($scope.inputData).success(function (data) {
			$scope.result = data;
		});
	};
	
	$scope.submit = function postData(data) { 		
		
		primaryFactory.submit($scope.inputData).success(function (data) {
			
			$scope.result = data;
			
			$scope.clear();
			$scope.refresh();
		});
	};
	
	$scope.upload = function postData() { 		
		
		$scope.loading = true;
		
		primaryFactory.upload($scope.uploadFile)
			.success(function (data) {
			
				$scope.result = data;
				$scope.loading = false;
				$scope.uploadFile = undefined;
				
				$scope.refresh();
			})
			.error(function (data) {
			
				$scope.result = data;
				$scope.loading = false;
				$scope.uploadFile = undefined;
			});
	};
	
	$scope.getAll = function postData(data) { 		
		
		$scope.messages = [];
		
		primaryFactory.getAll().success(function (data) {
			
		    angular.forEach(data.payload, function(value, key) {
		        
		    	$scope.messages.push({ 
    				
		    		message : value.message,
    				time : value.time,
    				id : value.id,
    				name : value.name,
    				type : value.type,
    				size : value.size
				});
		    });
		});
	};
	
	$scope.getTotalDiskSpace = function postData(data) { 		
		
		primaryFactory.getTotalDiskSpace().success(function (data) {
			
			$scope.totalSpace = data.payload.size;
			$scope.totalSpaceNormalized = data.payload.normalized;
			$scope.maxSpace = data.payload.maxSpace;
			$scope.maxSpaceNormalized = data.payload.maxSpaceNormalized;
			$scope.percentUsed = ($scope.totalSpace/$scope.maxSpace)*100;
			
		});
	};
	
	
	$scope.refresh = function(){
		$scope.getAll();
		$scope.getTotalDiskSpace();
	};
	
	$scope.clear = function(){
		$scope.inputData = undefined;
		$scope.result = undefined;
	};
	
	$scope.remove = function remove(data){
		
		primaryFactory.remove(data).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$rootScope.$on('user-login', function (event, args) {
		$scope.user = args.user;
	});
	
}]);