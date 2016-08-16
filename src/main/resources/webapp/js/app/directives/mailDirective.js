app.directive('mailmodal', ['homeFactory', '$timeout', '$rootScope', function (homeFactory, $timeout, $rootScope) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/mail.html',	   
	    link: function postLink(scope, element, attrs) {
	    	
	    	scope.user = scope.mailRecipient;
	    	scope.message = undefined;
	    	
	        scope.$watch(attrs.visible, function(value){
	        	
	        	if(value == true){ 
	        		$(element).modal('show');
	        	}
	        	else{
	        		$(element).modal('hide');
	        		clearForm();
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
	
	        var clearForm = function(){
		    	
	        	scope.message = undefined;
	        };
	        
	        scope.submitMail = function(keyEvent, message) {
	        	 
	        	if (keyEvent.which === 13){
	        		
	        		scope.sendMail(message);
	        	}
	        };
	        
	        scope.sendMail = function(message){
	        	
	        	scope.message = message;
	        	
	        	if(!scope.message || scope.message==='' || scope.message===undefined)
	        		return;
	        	
	        	var payload = {
	        		message : scope.message,
	        		recipient : scope.mailRecipient
	        	};
	        	
	    		homeFactory.sendMail(scope.userKey, payload)
	    		.success(function (data) {

	    			clearForm();
	    			$(element).modal('hide');
	    		});
	        };
	    }
	};
}]);