app.directive('sharemodal', ['homeFactory', '$timeout', '$rootScope', function (homeFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/share.html',	   
	    link: function postLink(scope, element, attrs) {
	    	
	        scope.$watch(attrs.visible, function(value){
	          
	        	if(value == true)       		
	        		$(element).modal('show');
	        	else
	        		$(element).modal('hide');
	        });
	
	        $(element).on('shown.bs.modal', function(){
	        	
	        	scope.$apply(function(){
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
	            }, 800);
	        };
	        
	        var autoCloseAlert = function(){
	            
	        	$timeout(function(){
	        		scope.result = undefined;
	            }, 2000);
	        };
	        
	        var clearForm = function(){
	        	
		    	scope.shareUser = undefined;
		    	scope.userKey = undefined;
		    	scope.result = undefined;
	        };
	        
	        scope.setUser = function(user){
	        	
	        	scope.shareUser = user;
	        	scope.shareFile();
	        }
	        
	    	scope.findUsers = function() { 		
	    		
	    		if(scope.shareUser===undefined || scope.shareUser===''){
	    			scope.users = [];
	    			return;
	    		}
	    		
	    		scope.users = [];
	    		
	    		homeFactory.findUsers(scope.userKey, scope.shareUser).success(function (data) {
	    			
	    		    angular.forEach(data.payload, function(value, key) {
	    		        
	    		    	scope.users.push({ 
	    		    		
	    		    		user : value.user
	    				});
	    		    });
	    		});
	    	};
	        
	        scope.shareFile = function(){
	        	
	        	var payload = {
	        		user : scope.shareUser,
        			fileId : scope.selectedFile
	        	};
	        	
	        	homeFactory.shareFile(payload, scope.userKey).success(function (data) {
	    			
	    			scope.result = data;
	    			
	    			autoCloseModal(data.success);
	        	});
	        };
	    }
	};
}]);