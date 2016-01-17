app.directive('modal', ['primaryFactory', function (primaryFactory) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/login.html',	   
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
	        
	        scope.submit = function(userName, userPassword){
	        	
	        	scope.result = undefined;
	        	
	        	var payload = {user : userName, password : userPassword};
	        	primaryFactory.login(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			
	        	});
	        };
	    }
	};
}]);