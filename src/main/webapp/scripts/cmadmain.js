(function(){
	var app = angular.module('myApp',['ngRoute']);
	
	
	app.config(function($routeProvider) {
        $routeProvider
        // route for the home page
        // route for the login page
        .when('/', {
            templateUrl : 'login.html',
            controller  : 'UserController'
        })
        .when('/viewallblogs', {
            templateUrl : 'viewallblogs.html',
            controller  : 'BlogController'
        })
        .when('/viewnextblogs', {
            templateUrl : 'viewnextblogs.html',
            controller  : 'BlogController'
        })
          .when('/viewnextblogs/:id', {
        	  templateUrl : 'viewnextblogs.html',
              controller  : 'detailBlogController'
    })
    .when('/user', {
            templateUrl : 'user.html',
            controller  : 'UserController'
        })
            // route for the signup page
            .when('/signup', {
                templateUrl : 'signup.html',
                controller  : 'UserController'
            })

            // route for the login page
            .when('/login', {
                templateUrl : 'login.html',
                controller  : 'UserController'
            })

             .when('/addblog', {
                templateUrl : 'addblog.html',
                controller  : 'BlogController'
            })
            .otherwise({
            	redirectTo : '/',
            })
    });
	
	
	app.controller('UserController',function($http, $log, $scope, $rootScope){
		$rootScope.mypage = 0
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
		
		$scope.getUserOnLogin = function (user) {
			$log.debug(user);
			$scope.showEditForm=false;
			$scope.showAddForm=true;
			 var postlogin =  $http.post('cmad/user/login', user);
	         postlogin.success(function (data) {
	        	 console.log("Success");
	        	 console.log(data)
	        	 $log.debug(data);
	        	 
	         })
	         .error(function (data) {
	        	 $log.debug(data);
	         });
	    };
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
			$http.put('rest/user',user).
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
	app.controller('detailBlogController',function($http, $log, $scope, $location,$rootScope,  $routeParams){
		var controller = this;
		$scope.blogs=[];
		$scope.allblogs=[];
		$scope.count= 0;
		$scope.loading = true;
		$scope.shownext = true;
		$scope.showprev = true;
		$scope.prevpage = 0;
		$scope.nextpage = 0;
		$scope.lastDataPoint = 3;
		$scope.pagesize= 3;
		$scope.currentPage = this.n;
	
		 $scope.startpage = parseInt( $routeParams.id);
		 $scope.nextpage = parseInt( $routeParams.id) + $scope.pagesize;
		$log.debug($routeParams.id  + "Getting Blogs..." +  $scope.nextpage);
		$log.debug($scope.showprev  + "Getting Prev ..." +  $scope.prevpage);
		$http.get('rest/question').
		  success(function(data, status, headers, config) {
			  $scope.count= data.length;
			  $scope.allblogs = data;
			  if($routeParams.id != undefined && $routeParams.id <=  3) {
				  $scope.prevpage = 0;
				  $scope.showprev = true;
			  } else {
				  $scope.showprev = true;
				  $scope.prevpage = $routeParams.id - $scope.pagesize;
			  }
			 if($scope.nextpage >=  $scope.count) {
				 $scope.shownext = false;
				  $scope.lastDataPoint = $scope.count;
			  } else {
				  $scope.lastDataPoint = $scope.nextpage;
			  }
			  $log.debug($rootScope.mypage + "Getting Blogs..." + $scope.startpage + "last=" + $scope.lastDataPoint);
			  for (var i =  $scope.startpage, j= 0; i < $scope.lastDataPoint; i++, j++) {
				  $scope.blogs[j] = $scope.allblogs[i];
			  }
			 // $scope.blogs[0] = data[$routeParams.id];
			  //$scope.blogs = data;
			  $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
			  $scope.loading = false;
			  $scope.error = status;
		  });
	
	$scope.addBlog = function (blog) {
		$log.debug(blog);
		$scope.showEditForm=false;
		$scope.showAddForm=true;
		$log.debug("Add Blogs...");
         var postData =  $http.post('cmad/blog', blog);
         postData.success(function (data) {
        	 $log.debug(data);
        	 $scope.blogs.push(blog);
         })
         .error(function (data) {
        	 $log.debug(data);
         });
    };
    $scope.setPage = function () {
    	
    	$log.debug("setPagecunt" +  $scope.nextpage);
    
    	$scope.currentPage++;
    	$scope.startpage =  $scope.lastDataPoint  ;
    	$location.path('/viewnextblogs/'+ $scope.nextpage);
    	    };
    $scope.setPrevPage = function () {
    	    	
    	    	$log.debug("setPrev" +  $scope.prevpage);
    	    	if(($scope.prevpage) === 0) {
    	    		$log.debug("0 matched" );
    	    		$location.path('/viewallblogs');
    	    	} else {
    	    		$log.debug("not matched" );
    	    		$location.path('/viewnextblogs/'+ $scope.prevpage);
    	    	}
    	    	
    	    	    };

	});
	app.controller('BlogController',function($http, $log, $scope, $location,$rootScope){
		var controller = this;
		$scope.blogs=[];
		$scope.allblogs=[];
		$scope.count= 0;
		$scope.loading = true;
		$scope.shownext = true;
		$scope.showprev = false;
		$scope.lastDataPoint = 3;
		$scope.pagesize= 3;
		$scope.currentPage = this.n;
		$scope.nextpage = 0 + $scope.pagesize;
		 $scope.startpage = 0;
		$log.debug("Getting Blogs...");
		$http.get('rest/question').
		  success(function(data, status, headers, config) {
			  $scope.count= data.length;
			  $scope.allblogs = data;
			  if($scope.lastDataPoint >  $scope.count) {
				  $scope.lastDataPoint = $scope.count;
			  }
			  $log.debug($scope.nextpage + "Getting Blogs..." + $scope.startpage + "last=" + $scope.lastDataPoint);
			  for (var i = $scope.startpage, j= 0; i < $scope.lastDataPoint; i++, j++) {
				  $scope.blogs[j] = $scope.allblogs[i];
			  }
			  //$scope.blogs = data;
			  $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
			  $scope.loading = false;
			  $scope.error = status;
		  });
	
	$scope.addBlog = function (blog) {
		$log.debug(blog);
		$scope.showEditForm=false;
		$scope.showAddForm=true;
		$log.debug("Add Blogs...");
         var postData =  $http.post('rest/question', blog);
         postData.success(function (data) {
        	 $log.debug(data);
        	 $scope.blogs.push(blog);
         })
         .error(function (data) {
        	 $log.debug(data);
         });
    };
    $scope.setPage = function () {
    	
    	$log.debug("Change Path" +  $scope.nextpage);
    	$location.path('/viewnextblogs/'+ $scope.nextpage);
    	    };

	});
})();
