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
	    	scope.selected = undefined;
	    	
	    	var removeMySelf = function(userArray){
	    		
	    		for (var i=userArray.length-1; i>=0; i--) {
	    		    if (userArray[i] === scope.user)
	    		    	userArray.splice(i, 1);
	    		}
	    	};
	    	
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
				        	scope.onlineUsers = data.users;
				        	
				        	removeMySelf(scope.onlineUsers);
				        	
				        	scope.$apply();
				        })
			        	.$on('fileUpdate', function (data) {
				        	
				        	//Update active user list
				        	console.log("Client Recieved Notification : "+JSON.stringify(data));
				        	
				        	//Angular Broadcast
				        	$rootScope.$broadcast('file-update');
				        	
				        	scope.$apply();
				        })
				        .$on('notification', function (data) {
				        	
				        	//Update active user list
				        	console.log("Client Recieved Notification : "+JSON.stringify(data));
				        	
				        	//Angular Broadcast
				        	$rootScope.$broadcast('user-notification', {
				        		
				        		type : data.type,
				        		count : data.count
				        	});
				        	
				        	scope.$apply();
				        })
				        .$on('message', function (message) {
				        	
				        	scope.serverMessage = message.sender+" : "+message.message;
				        	console.log('Client Recieved Message :'+message.message);
				        	
				        	appendToMessage(message.sender, message.message);
				        	
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
	        
	        var appendFromMessage = function(user, incomingMessage) {
	            
	        	var msgArea = angular.element(document.querySelector('#messageArea'));

	        	msgArea.append(
	        		'<div class="alert alert-info alert-dismissible" role="alert">'
	        		+'<button type="button" class="close" data-dismiss="alert" aria-label="Close">'
	        		+'<span aria-hidden="true">&times;</span>'
	        		+'</button><strong>'+user+'</strong>&nbsp;'+incomingMessage+'</div>'
	        		+'<div><small>'+new Date()+'</small></div>'
	        	);
	        };
	        
	        var appendToMessage = function(user, incomingMessage) {
	            
	        	var msgArea = angular.element(document.querySelector('#messageArea'));
	        	var alertType = 'success';
	        	
	        	if(user==='Server')
	        		alertType = 'danger';
	        	
	        	msgArea.append(
	        		'<div class="alert alert-'+alertType+' alert-dismissible" role="alert">'
	        		+'<button type="button" class="close" data-dismiss="alert" aria-label="Close">'
	        		+'<span aria-hidden="true">&times;</span>'
	        		+'</button><strong>'+user+'</strong>&nbsp;'+incomingMessage+'</div>'
	        		+'<div><small>'+new Date()+'</small></div>'
	        	);
	        };
	        
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
	        	
	        	scope.selected = user;
	        	scope.messageUser = user;
	        	scope.users = [];
	        };
	        
	        scope.isEnabled = function(){
	        	
	        	var messageCheck = scope.message!==undefined && scope.message!=='' && scope.message;
	        	var userCheck = scope.messageUser!==undefined && scope.messageUser!=='' && scope.messageUser;
	        	var inputNameValid = nameExists(scope.messageUser);
	        	var selectedCheck = scope.selected!==undefined && scope.selected!=='' && scope.selected;
	        	
	        	scope.enabled = messageCheck && userCheck && scope.connected && inputNameValid && selectedCheck;
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
	        	
	        	if(!scope.selected || scope.selected==='' || scope.selected===undefined)
	        		return;
	        	
	        	var payload = {
	        		message : scope.message,
	        		recipient : scope.messageUser
	        	};
	        	
	        	if(scope.connected){
	        		
	        		appendFromMessage(scope.user, scope.message)
	        		
	        		scope.webSocket.$emit('message', payload);
	        		scope.message = undefined;
	        	}
	        };
	    }
	};
}]);