app.directive('sharemodal', ['primaryFactory', '$timeout', '$rootScope', function (primaryFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/share.html',	   
	    link: function postLink(scope, element, attrs) {
    	    	
	    	scope.userKey;
	    	scope.user;
	    	scope.fileId;
	    	
	        scope.$watch(attrs.visible, function(value){
	          
	        	if(value == true)
	        		$(element).modal('show');
	        	else
	        		$(element).modal('hide');
	        });
	
	        $(element).on('shown.bs.modal', function(){
	        	
	        	scope.$apply(function(){
	        		clearForm();
	        		scope.$parent[attrs.visible] = true;
		        });
	        });
	
	        $(element).on('hidden.bs.modal', function(){
	        	
	        	scope.$apply(function(){
	        		scope.$parent[attrs.visible] = false;
		        });
	        });
	        
	        var autoCloseModal = function(success){
	            
	        	if(success===false)
	        		return;
	        	
	        	$timeout(function(){
	        		
	        		$(element).modal('hide');
	            }, 500);
	        };
	        
	        var autoCloseAlert = function(){
	            
	        	$timeout(function(){
	        		
	        		scope.result = undefined;
	            }, 1000);
	        };
	        
	        var acknowledge = function(success, user, userKey, admin){

	        	if(success===false)
	        		return;
	        	
	        	$rootScope.$broadcast('user-login', {
	        		
	        		shareUser : scope.shareUser,
        			userKey : scope.userKey
	        	});
	        };
	        
	        var clearForm = function(){
	        	
		    	scope.shareUser = undefined;
		    	scope.userKey = undefined;
		    	scope.result = undefined;
	        };
	        
	        scope.searchUser = function(){
	        	
	        	primaryFactory.searchUser(scope.shareUser).success(function (data) {
	    			
	    			scope.result = data;
	    			autoCloseAlert();
	        	});
	        };
	        
	        scope.shareFile = function(){
	        	
	        	var payload = {
        			
	        		user : scope.user,
        			fileId : scope.fileId
	        	};
	        	
	        	primaryFactory.shareFile(payload, userKey).success(function (data) {
	    			
	    			scope.result = data;
	    			
	    			autoCloseModal(data.success);
	    			acknowledge(data.success, scope.user, data.userKey, false);
	        	});
	        };
	    }
	};
}]);