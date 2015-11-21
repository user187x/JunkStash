app.factory('primaryFactory', ['$http', function($http) {

	var testGet = function () {
        return $http.get('/testGet');
    };
	
    var testPost = function (payload) {
    	return $http.post('/testPost', payload);
    };
    
    return {
	    testGet : testGet,
	    testPost : testPost
    };
    
}]);