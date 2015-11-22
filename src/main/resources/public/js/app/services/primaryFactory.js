app.factory('primaryFactory', ['$http', function($http) {

	var testGet = function () {
        return $http.get('/testGet');
    };
	
	var testGetAll = function () {
        return $http.get('/testGetAll');
    };
    
    var testPost = function (payload) {
    	return $http.post('/testPost', payload);
    };
    
    return {
	    testGet : testGet,
	    testGetAll : testGetAll,
	    testPost : testPost
    };
    
}]);