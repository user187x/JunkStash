app.factory('homeFactory', ['$http', function($http) {
    
    var upload = function (payload, userKey){
    	
    	var formData = new FormData();
    	formData.append('file', payload);
    	
	    return $http.post('/upload/'+userKey, formData, {
	        headers: {'Content-Type': undefined },
	        transformRequest: angular.identity
	    });
    };
    
    var shareFile = function (payload, userKey) {
    	return $http.post('/shareFile/'+userKey, payload);
    };
    
    var removeFile = function (payload, userKey) {
    	return $http.post('/removeFile/'+userKey, payload);
    };
    
    var removeUser = function (payload, userKey) {
    	return $http.post('/removeUser/'+userKey, payload);
    };
    
    var getFiles = function (userKey) {
    	return $http.get('/getFiles/'+userKey);
    };
    
    var searchUser = function (userId) {
    	return $http.get('/whoIs/'+userId);
    };
    
    var findUsers = function (userKey, searchUser) {
    	return $http.get('/findUsers/'+userKey+'/'+searchUser);
    };
    
    var userExists = function (userId) {
    	return $http.get('/userExists/'+userId);
    };
    
    var getUsers = function (userKey) {
    	return $http.get('/getUsers/'+userKey);
    };
    
    var getTotalDiskSpace = function (userKey) {
    	return $http.get('/getTotalDiskSpace/'+userKey);
    };
    
    var sendMail = function (userKey, payload) {
    	return $http.post('/sendMail/'+userKey, payload);
    };

    var login = function(payload){
    	return $http.post('/login', payload);
    };
    
    var logout = function (payload) {
    	return $http.post('/logout', payload);
    };
    
    var approveUser = function(payload, userKey) {
    	return $http.post('/approveUser/'+userKey, payload);
    };
    
    var register = function(payload){
    	return $http.post('/register', payload);
    };
    
    var getNotifications = function (userKey) {
    	return $http.get('/getNotifications/'+userKey);
    };
    
    var getSocketInfo = function (userKey) {
    	return $http.get('/socket/'+userKey);
    };
    
    return {
    	
    	login : login,
    	logout : logout,
    	register : register,
    	upload : upload,
    	removeFile : removeFile,
    	shareFile : shareFile,
    	removeUser : removeUser,
    	approveUser : approveUser,
    	getFiles : getFiles,
    	getUsers : getUsers,
    	userExists : userExists,
    	searchUser : searchUser,
    	sendMail : sendMail,
    	findUsers : findUsers,
    	getSocketInfo : getSocketInfo,
    	getNotifications : getNotifications,
    	getTotalDiskSpace : getTotalDiskSpace
    };
    
}]);