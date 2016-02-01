app.directive('messagemodal', ['homeFactory', '$timeout', '$rootScope', '$websocket', function (homeFactory, $timeout, $rootScope, $websocket) {
	
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
	    	scope.onlineUsers = [];
	    	
	    	$rootScope.$on('user-login', function (event, args) {
	    		
	    		scope.userKey = args.userKey;
	    		
	    		homeFactory.getSocketInfo(scope.userKey).success(function (data) {
	    			
	    			scope.url = data;
	    			
		    		if(scope.userKey!==undefined && data!==undefined){
			    		
			    		scope.wsUrl = scope.url+"?userKey="+scope.userKey;
			    		scope.webSocket = $websocket.$new({url: scope.wsUrl});

				        scope.webSocket.$on('$open', function () {
				        	
				        	scope.connected = true;	   
				        	console.log("Client Socket Connected ");
				        })
				        .$on('$close', function () {
				        	
				        	scope.connected = false;
				        	console.log("Client Socket Closed");
				        })
				        .$on('broadcast', function (data) {
				        	
				        	//Update active user list
				        	console.log("<<<Broadcast>>> : "+JSON.stringify(data));
				        	scope.onlineUsers = data.users
				        	
				        	scope.$apply();
				        })
				        .$on('message', function (message) {
				        	
				        	scope.serverMessage = message.sender+" : "+message.message;
				        	console.log('Client Recieved Message :'+message.message);
				        	
				        	scope.$apply();
				        });
			    	}
	    		});
	    	});
	    	
	    	$rootScope.$on('user-logout', function (event, args) {
	    		
	    		scope.webSocket.$close();
	    	});
	    	
	        scope.$watch(attrs.visible, function(value){
	        	
	        	if(value == true){ 
	        		
	        		$(element).modal('show');
	        		
	        		scope.messageUser = scope.messageRecipient;
	        		
	        		if(scope.messageRecipient && scope.messageRecipient!==undefined && scope.messageRecipient!=='')
	        			scope.textSearchBoxEnabled = false;
	        		else
	        			scope.textSearchBoxEnabled = true;
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
	        
	        var autoCloseModal = function(success){
	            
	        	if(success===false)
	        		return;
	        	
	        	$timeout(function(){
	        		$(element).modal('hide');
	            }, 800);
	        };
	        
	        var nameExists = function(name){
	        	
	        	if(scope.users===undefined)
	        		return false;
	        	
	        	if(name in scope.users)
	        		return true;
	        	else
	        		return false;
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
	        };
	        
	        scope.isEnabled = function(){
	        	
	        	var messageCheck = scope.message!==undefined && scope.message!=='' && scope.message;
	        	var userCheck = scope.messageUser!==undefined && scope.messageUser!=='' && scope.messageUser;
	        	var inputNameValid = nameExists(scope.messageUser);
	        	
	        	scope.enabled = messageCheck && userCheck && scope.connected && inputNameValid;
	        };
	        
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
	        	
	        	if(!scope.message || scope.message==='' || scope.message===undefined || scope.connected===false)
	        		return;
	        	
	        	var payload = {
	        		message : scope.message,
	        		recipient : scope.messageUser
	        	};
	        	
	        	if(scope.connected){
	        		
	        		scope.webSocket.$emit('message', payload);
	        		scope.message = undefined;
	        	}
	        };
	    }
	};
}]);