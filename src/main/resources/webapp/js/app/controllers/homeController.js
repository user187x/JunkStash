app.controller('homeController', ['$scope', 'homeFactory', '$timeout','$rootScope' ,function($scope, homeFactory, $timeout, $rootScope) {
	
	$scope.files = [];
	$scope.users = [];
	
	$scope.admin = false;
	
	$scope.fileSearch = undefined;
	$scope.userSearch = undefined;
	
	$scope.result = undefined;
	$scope.uploadFile = undefined;
	$scope.loading = false;
	
    $scope.showLoginModal = false;
    $scope.showShareModal = false;
    
    $scope.user = undefined;
    $scope.userKey = undefined;
    $scope.selectedFile = undefined;
    
    $scope.totalSpace = undefined;
    $scope.totalSpaceNormalized = undefined;
    $scope.maxSpaceNormalized = undefined;
    $scope.maxSpace = undefined;
    $scope.percentUsed = undefined;
    
    $scope.toggleLoginModal = function(){
    	
        $scope.showLoginModal = !$scope.showLoginModal;
    };
	
    $scope.toggleShareModal = function(file){
    	
    	$scope.selectedFile = file.id;
        $scope.showShareModal = !$scope.showShareModal;
    };
    
	$scope.upload = function(data) { 		
		
		$scope.loading = true;
		
		homeFactory.upload($scope.uploadFile, $scope.userKey)
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
		
		homeFactory.getFiles($scope.userKey).success(function (data) {
			
		    angular.forEach(data.payload, function(value, key) {
		        
		    	$scope.files.push({ 
    				
		    		message : value.message,
    				time : value.time,
    				id : value.id,
    				name : value.name,
    				type : value.type,
    				size : value.size,
    				shared : value.shared.shared,
    				sharer : value.shared.sharer,
    				shareDate : value.shared.shareDate,
    				owner : value.owner
				});
		    });
		});
	};
	
	$scope.listAllUsers = function(data) { 		
		
		$scope.users = [];
		
		homeFactory.getUsers($scope.userKey).success(function (data) {
			
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
		
		homeFactory.approve(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$scope.getTotalDiskSpace = function(data) { 		
		
		homeFactory.getTotalDiskSpace($scope.userKey).success(function (data) {
			
			$scope.totalSpace = data.payload.size;
			$scope.totalSpaceNormalized = data.payload.normalized;
			$scope.maxSpace = data.payload.maxSpace;
			$scope.maxSpaceNormalized = data.payload.maxSpaceNormalized;
			$scope.percentUsed = (($scope.totalSpace/$scope.maxSpace)*100);
			
		});
	};
	
	$scope.logout = function(){
		
		if($scope.userKey===undefined)
			return;
		
		var payload = {
			user : $scope.user,
			userKey : $scope.userKey
		}
		
		homeFactory.logout(payload).success(function (data) {
			
			$scope.result = data;
			
			$scope.user = undefined;
			$scope.userKey = undefined;
			$scope.files = undefined;
			$scope.admin = undefined;
			$scope.selectedFile = undefined;
			
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
		$scope.selectedFile = undefined;
	};
	
	$scope.removeFile = function(data){
		
		homeFactory.removeFile(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
		
	$scope.removeUser = function(data){
		
		homeFactory.removeUser(data, $scope.userKey).success(function (data) {
			$scope.result = data;
			$scope.refresh();
		});
	};
	
	$scope.approveUser = function(data){
		
		homeFactory.approveUser(data, $scope.userKey).success(function (data) {
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