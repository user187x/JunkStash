app.directive('notificationmodal', ['homeFactory', '$timeout', '$rootScope', function (homeFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/notification.html',	   
	    link: function postLink(scope, element, attrs) {
	    	
	    	scope.user = scope.userKey;
	    	scope.notifications = undefined;
	    	
	        scope.$watch(attrs.visible, function(value){
	        	
	        	if(value == true){ 
	        		$(element).modal('show');
	        		
	    			homeFactory.getNotifications(scope.userKey).success(function (data) {
	    				
	    				scope.notifications = data;
	    			});
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
	    }
	};
}]);