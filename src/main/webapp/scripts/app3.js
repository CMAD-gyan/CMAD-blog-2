(function(){
	var app = angular.module('Airlines',[]);
	app.controller('UserController',function($http, $log, $scope){
		var controller = this;
		$scope.users=[];
		$scope.loading = true;
		$log.debug("Getting users...");
		$http.get('cmad/user').
		  success(function(data, status, headers, config) {
			  $scope.users = data;
			  $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
			  $scope.loading = false;
			  $scope.error = status;
		  });
		
		
		$scope.addUser = function (user) {
			$log.debug(user);
			$scope.showEditForm=false;
			$scope.showAddForm=true;
	         var postData =  $http.post('cmad/user', user);
	         postData.success(function (data) {
	        	 $log.debug(data);
	        	 $scope.users.push(user);
	         })
	         .error(function (data) {
	        	 $log.debug(data);
	         });
	    };
		$scope.editUser = function(user){
			console.log(user);
			$scope.user = user;
			$scope.showEditForm=true;
			$scope.showAddForm=false;
		}
		
		$scope.updateUser = function(user){
			$log.debug(user);
			$http.put('cmad/user',user).
			  success(function(data, status, headers, config) {
				  console.log(data);
				  $scope.showEditForm=false;
			  }).
			  error(function(data, status, headers, config) {
				  $scope.error = status;
				  $scope.showEditForm=false;
			  });

		}
	});
	app.controller('ProductController',function($http, $log, $scope){
		var controller = this;
		$scope.products=[];
		$scope.loading = true;
		$log.debug("Getting Products...");
		$http.get('Product.json').
		  success(function(data, status, headers, config) {
			  $scope.products = data;
			  $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
			  $scope.loading = false;
			  $scope.error = status;
		  });
	});
})();
