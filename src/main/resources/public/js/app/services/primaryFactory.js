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

    var login = function(payload){
    	return $http.post('/login', payload);
    }
    
    return {
    	login  : login,
    	search : search,
    	submit : submit,
    	upload : upload,
    	remove : remove,
    	getAll : getAll
    };
    
}]);