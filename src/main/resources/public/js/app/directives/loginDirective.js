app.directive('modal', ['primaryFactory', '$timeout', '$rootScope', function (primaryFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/login.html',	   
	    link: function postLink(scope, element, attrs) {
    
	        scope.$watch(attrs.visible, function(value){
	          
	        	if(value == true){
	        		clearForm();
	        		$(element).modal('show');
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
	        
	        scope.submit = function(user, password){
	        	
	        	clearForm();
	        	
	        	scope.userId = user;
	        	scope.userPassword = password;
	        	
	        	var payload = {user : scope.userId, password : scope.userPassword};
	        	primaryFactory.login(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			autoClose(data.success);
	    			acknowledge(data.success);
	        	});
	        };
	        
	        var autoClose = function(success){
	            
	        	if(success===false)
	        		return;
	        	
	        	$timeout(function(){
	        		$(element).modal('hide');
	            }, 500);
	        };
	        
	        var acknowledge = function(success){

	        	if(success===false)
	        		return;
	        	
	        	$rootScope.$broadcast('user-login', {
	        		user : scope.userId
	        	});
	        };
	        
	        var clearForm = function(){
	        	scope.userId = undefined;
	        	scope.userPassword = undefined;
	        };
	    
	        scope.register = function(user, password){
	        	
	        	clearForm();
	        	
	        	scope.userId = user;
	        	scope.userPassword = password;
	        	
	        	var payload = {user : scope.userId, password : scope.userPassword};
	        	primaryFactory.register(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			
	    			
	    			autoClose(data.success);
	    			acknowledge(data.success);
	        	});
	        };
	    }
	};
}]);