app.service('socketService', ['$websocket', '$http', '$rootScope', function($websocket, $http, $rootScope) {
 
	//Private Variables
	var socketConnection = undefined;
    var socketUrl = undefined;
    var connected = false;
	  
    this.getSocketInfo = function(){
    	
    	$http.get('/socket').success(function(data) {
    		socketUrl = data;
    	});  
    }
    
    this.isConnected = function(){
    	
    	return connected;
    }
    
    this.closeSocketConnection = function () {
    	
    	socketConnection.$close();
    	connected = false;
    }
    
    this.sendMessage = function(payload){

    	socketConnection.$emit('message', payload);
    }
    
    this.openSocketConnection = function (userKey) {
    	
		socketConnection = $websocket.$new({url: socketUrl+"?userKey="+userKey});

        socketConnection.$on('$open', function () {
        	
        	connected = true;	   
        	console.log("Client Socket Connected ");
        	
        }).$on('$close', function () {
        	
        	connected = false;
        	console.log("Client Socket Closed");
        	
        }).$on('broadcast', function (data) {
        	
        	//Server Broadcast
        	console.log("<<<BROADCAST>>> : "+JSON.stringify(data));
        	
        }).$on('onlineUsers', function (data) {
        	
        	//Update active user list
        	$rootScope.$broadcast('online-users', {
        		
        		onlineUsers : data.users,
        		count : data.count
        	});
        	
        }).$on('fileUpdate', function (data) {
        	
        	//Notify File Updates
        	console.log("File Update Notification : "+JSON.stringify(data));
        	
        	//Angular Broadcast
        	$rootScope.$broadcast('file-update');
        	
        }).$on('userUpdate', function (data) {
        	
        	//Notify User Updates
        	console.log("User Update Notification : "+JSON.stringify(data));
        	
        	$rootScope.$broadcast('user-update');
        	
        }).$on('notification', function (data) {
        	
        	//Notification Alert
        	console.log("Client Recieved Notification : "+JSON.stringify(data));
        	
        	$rootScope.$broadcast('user-notification', {
        		
        		type : data.type,
        		count : data.count
        	});

        }).$on('message', function (message) {
        	
        	//Message Received
        	console.log('Client Recieved Message :'+message.message);
        	
        	$rootScope.$broadcast('message', {
        		
        		sender : message.sender,
        		message : message.message
        	});
        });
    }
	
}]);