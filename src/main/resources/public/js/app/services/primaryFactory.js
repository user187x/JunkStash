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
        
        return $http.post(
        	'/upload', 
        	formData, 
        	{
	            transformRequest: angular.identity,
	            headers: {'Content-Type': undefined }
        	}
        );
    };
    
    var remove = function (payload) {
    	return $http.post('/remove', payload);
    };
    
    var getAll = function () {
    	return $http.get('/getAll');
    };
    
    return {
    	search : search,
    	submit : submit,
    	upload : upload,
    	remove : remove,
    	getAll : getAll
    };
    
}]);