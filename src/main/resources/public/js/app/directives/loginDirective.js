app.directive('modal', ['primaryFactory', '$timeout', function (primaryFactory, $timeout) {
	
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
	        
	        scope.submit = function(userName, userPassword){
	        	
	        	scope.result = undefined;
	        	
	        	var payload = {user : userName, password : userPassword};
	        	primaryFactory.login(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			
	        	});
	        };
	        
	        var autoClose = function(success){
	            
	        	if(success===false)
	        		return;
	        	
	        	$timeout(function(){
	        		$(element).modal('hide');
	            }, 1000);
	        };
	        
	        var clearForm = function(){
	        	
	        	scope.userId = undefined;
	        	scope.userPassword = undefined;
	        };
	        
	        scope.register = function(userName, userPassword){
	        	
	        	scope.result = undefined;
	        	
	        	var payload = {user : userName, password : userPassword};
	        	primaryFactory.register(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			
	    			autoClose(data.success);
	        	});
	        };
	    }
	};
}]);