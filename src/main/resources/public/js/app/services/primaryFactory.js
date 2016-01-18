app.factory('primaryFactory', ['$http', function($http) {
    
    var search = function (payload) {
    	return $http.post('/search', payload);
    };
    
    var submit = function (payload) {
    	return $http.post('/submit', payload);
    };
    
    var upload = function (payload){
    	
    	var formData = new FormData();
    	formData.append('file', payload);
    	
	    return $http.post('/upload', formData, {
	        headers: {'Content-Type': undefined },
	        transformRequest: angular.identity
	    });
    };
    
    var remove = function (payload) {
    	return $http.post('/remove', payload);
    };
    
    var getAll = function () {
    	return $http.get('/getAll');
    };
    
    var getTotalDiskSpace = function () {
    	return $http.get('/getTotalDiskSpace');
    };

    var login = function(payload){
    	return $http.post('/login', payload);
    };
    
    var logOut = function (payload) {
    	return $http.post('/logOut', payload);
    };
    
    var register = function(payload){
    	return $http.post('/register', payload);
    };
    
    return {
    	login    : login,
    	logOut   : logOut,
    	register : register,
    	search   : search,
    	submit   : submit,
    	upload   : upload,
    	remove   : remove,
    	getAll   : getAll,
    	getTotalDiskSpace : getTotalDiskSpace
    };
    
}]);