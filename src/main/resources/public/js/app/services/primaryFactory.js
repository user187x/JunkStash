app.factory('primaryFactory', ['$http', function($http) {
    
    var search = function (payload) {
    	return $http.post('/search', payload);
    };
    
    var getAll = function () {
    	return $http.get('/getAll');
    };
    
    return {
    	search : search,
    	getAll : getAll
    };
    
}]);