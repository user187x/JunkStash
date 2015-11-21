app.factory('primaryFactory', ['$http', function($http) {

	var testGet = function () {
        return $http.get('/testGet');
    };
	
    var testPost = function () {
        return $http.post('/testPost');
    };
    
    return {
	    testGet : testGet,
	    testPost : testPost
    };
    
}]);