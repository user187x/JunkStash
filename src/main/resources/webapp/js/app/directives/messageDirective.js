app.directive('messagemodal', ['homeFactory', '$timeout', '$rootScope', '$websocket', 
       function (homeFactory, $timeout, $rootScope, $websocket) {
	
	return {
	    
		restrict: 'E',
	    transclude: true,
	    replace:true,
	    scope:true,
	    templateUrl: '/js/app/views/message.html',	   
	    link: function postLink(scope, element, attrs) {
	    	
	    	scope.enabled = false;
	    	scope.textSearchBoxEnabled = true;
	    	scope.connected = false;
	    	scope.serverMessage = undefined;
	    	
	    	scope.webSocket = $websocket.$new({url: 'ws://localhost:8888/chat'});

	        scope.webSocket.$on('$open', function () {
	        	scope.connected = true;	        	
	        })
	        .$on('$close', function () {
	        	scope.connected = false;
	        })
	        .$on('broadcast', function (data) {
	        	scope.serverMessage = data;
	        });
	    	
	        scope.$watch(attrs.visible, function(value){
	          
	        	if(value == true){ 
	        		
	        		scope.visible = true;
	        		
	        		$(element).modal('show');
	        		
	        		scope.messageUser = scope.messageRecipient;
	        		
	        		if(scope.messageRecipient && scope.messageRecipient!==undefined && scope.messageRecipient!=='')
	        			scope.textSearchBoxEnabled = false;
	        		else
	        			scope.textSearchBoxEnabled = true;
	    	        
	    	        if(scope.connected===false)
	    	        	scope.webSocket.$open();
	        	}
	        	else{
	        		
	        		scope.visible = false;
	        		
	        		$(element).modal('hide');
	        		clearForm();
	        		
	        		if(scope.connected)
	        			scope.webSocket.$close();
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
	        	
		    	scope.messageUser = undefined;
		    	scope.result = undefined;
		    	scope.users = [];
		    	scope.textSearchBoxEnabled = true;
		    	scope.message = undefined;
	        };
	        
	        scope.setUser = function(user){
	        	
	        	scope.messageUser = user;
	        	scope.users = [];
	        }
	        
	        scope.isEnabled = function(){
	        	
	        	var messageCheck = scope.message!==undefined && scope.message!=='' && scope.message;
	        	var userCheck = scope.messageUser!==undefined && scope.messageUser!=='' && scope.messageUser;
	        	
	        	scope.enabled = messageCheck && userCheck;
	        }
	        
	    	scope.findUsers = function() { 		
	    		
	    		if(scope.messageUser===undefined || scope.messageUser===''){
	    			scope.users = [];
	    			return;
	    		}
	    		
	    		scope.users = [];
	    		
	    		homeFactory.findUsers(scope.userKey, scope.messageUser).success(function (data) {
	    			
	    		    angular.forEach(data.payload, function(value, key) {
	    		        
	    		    	scope.users.push({ 
	    		    		
	    		    		user : value.user
	    				});
	    		    });
	    		});
	    	};
	        
	        scope.sendMessage = function(message){
	        	
	        	scope.message = message;
	        	
	        	if(!scope.message || scope.message==='' || scope.message===undefined)
	        		return;
	        	
	        	var payload = {
	        		message : scope.message,
	        		user : scope.userKey,
	        		recipient : scope.messageUser
	        	};
	        	
	        	if(scope.connected){
	        		scope.webSocket.$emit('message', payload);
	        	}
	        };
	    }
	};
}]);