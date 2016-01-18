app.factory('primaryFactory', ['$http', function($http) {
    
    var upload = function (payload, userKey){
    	
    	var formData = new FormData();
    	formData.append('file', payload);
    	
	    return $http.post('/upload/'+userKey, formData, {
	        headers: {'Content-Type': undefined },
	        transformRequest: angular.identity
	    });
    };
    
    var remove = function (payload, userKey) {
    	return $http.post('/remove/'+userKey, payload);
    };
    
    var getFiles = function (userKey) {
    	return $http.get('/getFiles/'+userKey);
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
    
    var register = function(payload){
    	return $http.post('/register', payload);
    };
    
    return {
    	login : login,
    	logout : logout,
    	register : register,
    	upload : upload,
    	remove : remove,
    	getFiles : getFiles,
    	getTotalDiskSpace : getTotalDiskSpace
    };
    
}]);