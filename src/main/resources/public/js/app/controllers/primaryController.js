app.controller('primaryController', ['$scope', 'primaryFactory', '$timeout','$rootScope' ,function($scope, primaryFactory, $timeout, $rootScope) {
	
	$scope.messages = [];
	$scope.inputData = undefined;
	$scope.result = undefined;
	$scope.uploadFile = undefined;
	$scope.loading = false;
    $scope.showModal = false;
    $scope.user = undefined;
    $scope.userKey = undefined;
    
    $scope.totalSpace = undefined;
    $scope.totalSpaceNormalized = undefined;
    $scope.maxSpaceNormalized = undefined;
    $scope.maxSpace = undefined;
    $scope.percentUsed = undefined;
    
    $scope.toggleModal = function(){
    	
        $scope.showModal = !$scope.showModal;
    };
	
	$scope.upload = function(data) { 		
		
		$scope.loading = true;
		
		primaryFactory.upload($scope.uploadFile, $scope.userKey)
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
	
	$scope.listAllFiles = function(data) { 		
		
		$scope.messages = [];
		
		primaryFactory.getFiles($scope.userKey).success(function (data) {
			
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
	
	$scope.getTotalDiskSpace = function(data) { 		
		
		primaryFactory.getTotalDiskSpace($scope.userKey).success(function (data) {
			
			$scope.totalSpace = data.payload.size;
			$scope.totalSpaceNormalized = data.payload.normalized;
			$scope.maxSpace = data.payload.maxSpace;
			$scope.maxSpaceNormalized = data.payload.maxSpaceNormalized;
			$scope.percentUsed = ($scope.totalSpace/$scope.maxSpace)*100;
			
		});
	};
	
	$scope.logout = function(){
		
		if($scope.userKey===undefined)
			return;
		
		var payload = {
			user : $scope.user,
			userKey : $scope.userKey
		}
		
		primaryFactory.logout(payload).success(function (data) {
			
			$scope.result = data;
			
			$scope.user = undefined;
			$scope.userKey = undefined;
			$scope.messages = undefined;
			
			$rootScope.$broadcast('user-logout', function (event, args) {});
			
			$scope.refresh();
		});
	};
	
	$scope.refresh = function(){
		
		if($scope.userKey===undefined)
			return;
		
		$scope.listAllFiles();
		$scope.getTotalDiskSpace();
		
		autoCloseAlert();
	};
	
	$scope.clear = function(){
		
		$scope.inputData = undefined;
		$scope.result = undefined;
	};
	
	$scope.remove = function(data){
		
		primaryFactory.remove(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$rootScope.$on('user-login', function (event, args) {
		
		$scope.user = args.user;
		$scope.userKey = args.userKey;
		
		$scope.refresh();
	});
	
	var autoCloseAlert = function(){
        
    	$timeout(function(){
    		$scope.result.success = undefined;
    	}, 1000);
    };
	
}]);