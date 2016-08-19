app.directive('notificationmodal', ['homeFactory', '$timeout', '$rootScope', function (homeFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/notification.html',	   
	    link: function postLink(scope, element, attrs) {
	    	
	    	scope.success = undefined;
	    	scope.selectedId = undefined;
	    	scope.notifications = [];
	    	
	        scope.$watch(attrs.visible, function(value){
	        	
	        	if(value == true){ 
	        		$(element).modal('show');

	        		scope.refreshNotifications();
	        	}
	        	else{
	        		$(element).modal('hide');
	        	}
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
	    	
	    	scope.refreshNotifications = function(){
        		
        		homeFactory.getNotifications(scope.userKey).success(function (data) {
    				
    				scope.notifications = [];
    				
    			    angular.forEach(data.payload, function(value, key) {
    			        
    			    	scope.notifications.push({ 
    	    				
    			    		id : value.id,
    			    		message : value.message,
    	    				timeStamp : value.timeStamp,
    	    				from : value.from
    					});
    			    });
    			});
	    	};
	    	
	    	scope.setSelected = function(value){
	    		scope.selectedId = value;
	    	};
	    	
	    	scope.markAcknowledged = function(){
	    		
	    		if(scope.userKey === undefined || scope.selectedId === undefined){
	    			console.log("Userkey Or Selected Mail ID Not Found");
	    			return;
	    		}
	    		
    			homeFactory.markAcknowledged(scope.userKey, scope.selectedId).success(function (data) {
    				scope.success = data.success;
    			});
    			
   				scope.refreshNotifications();
	    	};
	    }
	};
}]);