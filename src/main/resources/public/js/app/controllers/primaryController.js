app.controller('primaryController', ['$scope', 'primaryFactory', '$timeout','$rootScope' ,function($scope, primaryFactory, $timeout, $rootScope) {
	
	$scope.files = [];
	$scope.users = [];
	$scope.admin = false;
	
	$scope.fileSearch = undefined;
	$scope.userSearch = undefined;
	
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
		
		$scope.files = [];
		
		primaryFactory.getFiles($scope.userKey).success(function (data) {
			
		    angular.forEach(data.payload, function(value, key) {
		        
		    	$scope.files.push({ 
    				
		    		message : value.message,
    				time : value.time,
    				id : value.id,
    				name : value.name,
    				type : value.type,
    				size : value.size,
    				owner : value.owner
				});
		    });
		});
	};
	
	$scope.listAllUsers = function(data) { 		
		
		$scope.users = [];
		
		primaryFactory.getUsers($scope.userKey).success(function (data) {
			
		    angular.forEach(data.payload, function(value, key) {
		        
		    	$scope.users.push({ 
    				
		    		user : value.user,
    				status : value.status,
    				added : value.added,
    				space : value.space
				});
		    });
		});
	};
	
	$scope.approve = function(date){
		
		primaryFactory.approve(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
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
			$scope.files = undefined;
			$scope.admin = undefined;
			
			$rootScope.$broadcast('user-logout', function (event, args) {});
			
			$scope.refresh();
			autoCloseAlert();
			
		});
	};
	
	$scope.refresh = function(){
		
		if($scope.userKey===undefined)
			return;
		
		$scope.listAllFiles();
		$scope.listAllUsers();
		$scope.getTotalDiskSpace();
		
		autoCloseAlert();
	};
	
	$scope.clear = function(){
		
		$scope.fileSearch = undefined;
		$scope.userSearch = undefined;
		$scope.result = undefined;
	};
	
	$scope.removeFile = function(data){
		
		primaryFactory.removeFile(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$scope.removeUser = function(data){
		
		console.log('Here...'+data+' here again '+JSON.stringify(data));
		
		primaryFactory.removeUser(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$scope.approve = function(data){
		
		primaryFactory.approve(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$rootScope.$on('user-login', function (event, args) {
		
		$scope.user = args.user;
		$scope.userKey = args.userKey;
		$scope.admin = args.admin;
		
		$scope.refresh();
	});
	
	var autoCloseAlert = function(){
        
    	$timeout(function(){
    		$scope.result = undefined;
    	}, 1000);
    };
	
}]);