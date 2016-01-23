app.directive('loginmodal', ['homeFactory', '$timeout', '$rootScope', function (homeFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/login.html',	   
	    link: function postLink(scope, element, attrs) {
    
	    	scope.user = undefined;
	    	scope.password1 = undefined;
	    	scope.password2 = undefined;
	    	scope.admin = undefined;
	    	scope.userKey = undefined;
	    	scope.result = undefined;
	    	scope.registeringUser = false;
	    	scope.enabled = false;
	    	scope.userExists = false;
	    	
	        scope.toggleRegister = function(){
	            scope.registeringUser = !scope.registeringUser;
	        };
	    	
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
	        
	        scope.submit = function(){
	        	
	        	var payload = {
        			user : scope.user, 
        			password1 : scope.password1,
        			password2 : scope.password2
	        	};
	        	
	        	homeFactory.login(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			
	    			autoCloseModal(data.success);
	    			autoCloseAlert();
	    			
	    			acknowledge(data.success, scope.user, data.userKey, data.admin);
	        	});
	        };
	        
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
	        		
	        		user : scope.user,
	        		userKey : userKey,
	        		admin : admin
	        	});
	        };
	        
	        var clearForm = function(){
	        	
		    	scope.user = undefined;
		    	scope.password1 = undefined;
		    	scope.password2 = undefined;
		    	scope.userKey = undefined;
		    	scope.result = undefined;
		    	scope.registeringUser = false;
		    	scope.userExists = false;
	        };
	        
	        scope.isEnabled = function(){
	        	
	        	if(scope.password1===undefined || scope.password1==='')
	        		scope.enabled = false;
	        	
	        	if(scope.registeringUser===true)
		        	scope.enabled = scope.password1===scope.password2 && !scope.userExists
	        };
	        
	        scope.checkUser = function(user){

	        	if(user===undefined || user===''){
	        		scope.enabled = false;
	        		return;
	        	}
	        		
	        	homeFactory.userExists(user).success(function (data) {
	    			scope.userExists = data.exists;
	    			scope.result = data;
	    			
	    			scope.isEnabled();
	        	});
	        };
	        
	    	$rootScope.$on('user-logout', function (event, args) {
	    		clearForm();
	    	});
	    
	        scope.register = function(){
	        	
	        	var payload = {
        			user : scope.user, 
        			password : scope.password
	        	};
	        	
	        	homeFactory.register(payload).success(function (data) {
	    			
	    			scope.result = data;
	    			
	    			autoCloseModal(data.success);
	    			acknowledge(data.success, scope.user, data.userKey, false);
	        	});
	        };
	    }
	};
}]);