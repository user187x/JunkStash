app.factory('primaryFactory', ['$http', function($http) {
    
    var upload = function (payload, userKey){
    	
    	var formData = new FormData();
    	formData.append('file', payload);
    	
	    return $http.post('/upload/'+userKey, formData, {
	        headers: {'Content-Type': undefined },
	        transformRequest: angular.identity
	    });
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
    
    var getUsers = function (userKey) {
    	return $http.get('/getUsers/'+userKey);
    };
    
    var getTotalDiskSpace = function (userKey) {
    	return $http.get('/getTotalDiskSpace/'+userKey);
    };

    var login = function(payload){
    	return $http.post('/login', payload);
    };
    
    var logout = function (payload) {
    	return $http.post('/logout', payload);
    };
    
    var approve = function(payload, userKey) {
    	return $http.post('/approve/'+userKey, payload);
    };
    
    var register = function(payload){
    	return $http.post('/register', payload);
    };
    
    return {
    	login : login,
    	logout : logout,
    	register : register,
    	upload : upload,
    	removeFile : removeFile,
    	removeUser : removeUser,
    	approve : approve,
    	getFiles : getFiles,
    	getUsers : getUsers,
    	getTotalDiskSpace : getTotalDiskSpace
    };
    
}]);