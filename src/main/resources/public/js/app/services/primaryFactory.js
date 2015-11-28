app.factory('primaryFactory', ['$http', function($http) {
    
    var search = function (payload) {
    	return $http.post('/search', payload);
    };
    
    var submit = function (payload) {
    	return $http.post('/submit', payload);
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
    	remove : remove,
    	getAll : getAll
    };
    
}]);