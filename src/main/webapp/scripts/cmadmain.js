(function(){
	var app = angular.module('myApp',['ngRoute','ngCookies','ngDialog']);
	
	
	app.config(function($routeProvider) {
        $routeProvider
        // route for the home page
        // route for the login page
        .when('/', {
            templateUrl : 'login.html',
            controller  : 'UserController'
        }).when('/viewallblogs', {
            templateUrl : 'viewallblogs.html',
            controller  : 'BlogController'
        }).when('/viewallblogs/:id', {
            templateUrl : 'viewallblogs.html',
            controller  : 'BlogController'
        }).when('/viewallblogs/tagged/:searchkey', {
            templateUrl : 'viewsearchquestions.html',
            controller  : 'searchCtrl'
        }).when('/viewallblogs/tagged/:searchkey/:id', {
            templateUrl : 'viewsearchquestions.html',
            controller  : 'searchCtrl'
        }).when('/viewdetailblog/:id', {
            templateUrl : 'viewdetailblog.html',
            controller  : 'detailBlogController'
        }).when('/viewnextblogs', {
            templateUrl : 'viewnextblogs.html',
            controller  : 'BlogController'
        }).when('/viewnextblogs/:id', {
        	  templateUrl : 'viewnextblogs.html',
              controller  : 'detailBlogController'
        }).when('/user', {
            templateUrl : 'user.html',
            controller  : 'UserController'
        }).when('/signup', {
        	// route for the signup page
                templateUrl : 'signup.html',
                controller  : 'UserController'
        }).when('/login', {
            	// route for the login page
                templateUrl : 'login.html',
                controller  : 'UserController'
        }).when('/addblog', {
                templateUrl : 'addblog.html',
                controller  : 'BlogController'
        }).otherwise({
            	redirectTo : '/',
        })
    });
	
	
	app.controller('UserController',function($http, $log, $scope, $window, $rootScope, $location, $cookies){
		$rootScope.mypage = 0
		$rootScope.loginstatus = false;
		var controller = this;
		$scope.users=[];
		$scope.loading = true;
		$log.debug("Getting users..." + $window.localStorage.getItem("loggedin"));
		$http.get('rest/user').
		  success(function(data, status, headers, config) {
			  $scope.users = data;
			  $scope.loading = false;
		  }).
		  error(function(data, status, headers, config) {
			  $scope.loading = false;
			  $scope.error = status;
		  });
		
		$scope.getUserOnLogin = function(user) {
			$log.debug(user);
			$scope.showEditForm=false;
			$scope.showAddForm=true;
			 var postlogin =  $http.post('rest/authentication', user);
	         postlogin.success(function(data) {
	        	 $rootScope.loginstatus = true;
	        	 console.log("Success");
	        	 $log.debug(data);
	        	 $cookies['token'] = data;
	        	 $window.localStorage.setItem("currentUser", user.username);
	        	 $window.localStorage.setItem("loggedin", true);
	        	 console.log("printing token=========" + $window.localStorage.getItem("loggedin"));
	        	 $log.debug($cookies['token']);
	        	 $location.path("viewallblogs");
	         })
	         .error(function (data) {
	        	 $log.debug("Login failed");
	        	 $log.debug(data);
	         });
	    };
	    $scope.getUserLogout = function() {
	    	$log.debug("Inside logout function");
			$scope.showEditForm=false;
			$scope.showAddForm=true;
			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
			var postlogin =  $http.delete('rest/authentication');
	        postlogin.success(function(data) {
	        	 $rootScope.loginstatus = false;
	        	 console.log("Success");
	        	 $log.debug(data);
	        	 $cookies['token'] = "";
	        	 $window.localStorage.setItem("currentUser", "");
	        	 $window.localStorage.setItem("loggedin", false);
	        	 console.log("printing token=========" + $window.localStorage.getItem("loggedin"));
	        	
	        	 console.log("printing token=========");
	        	 $log.debug($cookies['token']);
	        	 $location.path("login");
	         })
	         .error(function (data) {
	        	 $log.debug("Logout failed");
	        	 $log.debug(data);
	         });
	    };
	    
		$scope.addUser = function (user) {
			$log.debug("Inside user creation");
			$log.debug(user);
			$scope.showEditForm=false;
			$scope.showAddForm=true;
	         $http.post('rest/user', user).success(function (data) {
	        	 $log.debug(data);
	        	 $scope.users.push(user);
	        	 $location.path("login");
//	        	 $window.location.href= "#login";
	         }).error(function (data) {
	        	 $log.debug("Failed to create user");
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
	app.controller('detailBlogController',function($http, $log, $scope, $window, $route, $location,$rootScope, $cookies, $routeParams, ngDialog){
		var controller = this;
		$scope.question=[];
		$scope.currentquestion=[];
		$scope.updatedquestion = [];
		$scope.changequestion = [];
		$scope.addanswers = false;
		$scope.showansbtn = false;
		$scope.showanswers = false;
		 $scope.questionid = $routeParams.id;
		 $scope.dataloading = true;
		 $scope.edittext = false;
		
		$log.debug($routeParams.id  + "Getting Detail Blogs..." + $scope.questionid);
		$log.debug( $scope.edittext);
		$http.get('rest/question/' + $scope.questionid).
		  success(function(data, status, headers, config) {
			  $log.debug("detail blog="+ data);
			  $scope.currentquestion = data;
			  
			  $log.debug("detail blog="+ $scope.currentquestion.username + "-" + $scope.currentquestion.title);
			  $log.debug("Successful data retrieved:" +   $scope.currentquestion.answers.length);
			  if($scope.currentquestion.answers.length > 0) {
				  $scope.showanswers = true;
			  } else {
				  $scope.showanswers = false;
			  }
			  $scope.showansbtn = true;
			  $scope.dataloading = false;
			  $scope.edittext =false;
		  }).
		  error(function(data, status, headers, config) {
			
			  $scope.error = status;
		  });
		$scope.addanswer = function () {
			$scope.showansbtn = false;
			$scope.addanswers = true;
		}
		
		$scope.questionupdate = function (updatedtext) {
			$log.debug(updatedtext);
			var changedques= {
					"title":  $scope.currentquestion.title,
					"text": updatedtext
			};
			$log.debug("edit text" + changedques);
			//$scope.updatedquestion.username = $scope.currentquestion.username;
			$scope.updatedquestion.text = updatedtext;
			$scope.updatedquestion.title = $scope.currentquestion.title;
			$scope.updateBlog(changedques);
		}
		
	$scope.editText = function () {
			$log.debug("edit text");
			 $scope.username= $window.localStorage.getItem("currentUser");
			if($scope.username == $scope.currentquestion.username) {
				$scope.edittext= true;
			} else {
				$log.debug("No Previledge");
				$window.alert("No Previledge to edit the Post");
                return;
				
			}
			
			$log.debug("edit text=" + $scope.edittext);
		}
	$scope.updateBlog = function(blog) {
		$log.debug("edit blog=" + blog + "===" + $cookies['token']);
		$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
         var postData =  $http.post('rest/question', blog);
         postData.success(function (data) {
        	 $log.debug(data);
        	 $scope.edittext= false;
        	 $location.path('/viewdetailblog/' + data);
        	 
         })
         .error(function (data) {
        	 $log.debug("ERROR..." + blog);
        	 $log.debug(data);
         });
    };
    
    $scope.addYourAnswer = function (answer) {
		$log.debug(answer);
		$log.debug("=====" + $cookies['token']);
		$log.debug("Add Answers...");
		$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
        var postData =  $http.post('rest/answer/'+$scope.questionid, answer);
         postData.success(function (data) {
        	 $log.debug(data);
        	 $log.debug("Add Answers Success change the path...");
        	 $route.reload();
        	 $location.path('/viewdetailblog/' + $scope.questionid);
         })
         .error(function (data) {
        	 $log.debug(data);
         });
    };
    $scope.setPage = function () {
    	
    	$log.debug("setPagecunt" +  $scope.nextpage);
    	$location.path('/viewallblogs/'+ $scope.prevpage);
    	    };
    $scope.setPrevPage = function () {
    	    	
    	    	$log.debug("setPrev" +  $scope.prevpage);
    	    	if(($scope.prevpage) === 0) {
    	    		$log.debug("0 matched" );
    	    		$location.path('/viewallblogs');
    	    	} else {
    	    		$log.debug("not matched" );
    	    		$location.path('/viewallblogs/'+ $scope.prevpage);
    	    	}
    	    	
    	    	    };
    $scope.openQCommentsForm = function() {
    	    			ngDialog.openConfirm({template: 'comments.html',
    	    			  scope: $scope //Pass the scope object if you need to access in the template
    	    			}).then(
    	    				function(value) {
    	    					//save the contact form
    	    					$log.debug(value);
    	    					var comment= {
    	    							"text": value
    	    					};
    	    					//$scope.updatedquestion.username = $scope.currentquestion.username;
//    	    					$scope.updatedquestion.text = updatedtext;
//    	    					$scope.updatedquestion.comments = $scope.currentquestion.comments;
    	    					$scope.postQComment(comment);
    	    					
    	    				},
    	    				function(value) {
    	    					//Cancel or do nothing
    	    				}
    	    			);
    	    		};
    	    	    $scope.postQComment = function(comment){
    	    	    	$log.debug("edit blog=" + comment + "=== token " + $cookies['token']);
    	    			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
    	    	         var postData =  $http.post('rest/question/'+$scope.questionid +"/comment" , comment);
    	    	         postData.success(function (data) {
    	    	        	 $log.debug(data);
//    	    	        	 $scope.edittext= false;
//    	    	        	 $location.path('/viewdetailblog/' + data);
    	    	        	 
    	    	         })
    	    	         .error(function (data) {
    	    	        	 $log.debug("ERROR..." + comment);
    	    	        	 $log.debug(data);
    	    	         });
    	    	    };
    	    		
    	    		$scope.openACommentsForm = function(ans) {
    	    			ngDialog.openConfirm({template: 'comments.html',
    	    			  scope: $scope //Pass the scope object if you need to access in the template
    	    			}).then(
    	    				function(value) {
    	    					//save the contact form
    	    					$log.debug("comment is:" + value);
    	    					$log.debug("answer id: " + ans.id);
    	    					var comment= {
    	    							"text": value
    	    					};
    	    					//$scope.updatedquestion.username = $scope.currentquestion.username;
//    	    					$scope.updatedquestion.text = updatedtext;
//    	    					$scope.updatedquestion.comments = $scope.currentquestion.comments;
    	    					$scope.postAComment(comment, ans.id);
    	    					
    	    				},
    	    				function(value) {
    	    					//Cancel or do nothing
    	    				}
    	    			);
    	    		};
    	    		
    	    		$scope.postAComment = function(comment, ansid){
    	    	    	$log.debug("edit blog=" + comment + "=== token " + $cookies['token']);
    	    			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
    	    	         var postData =  $http.post('rest/answer/'+ansid +"/comment" , comment);
    	    	         postData.success(function (data) {
    	    	        	 $log.debug(data);
//    	    	        	 $scope.edittext= false;
//    	    	        	 $location.path('/viewdetailblog/' + data);
    	    	        	 
    	    	         })
    	    	         .error(function (data) {
    	    	        	 $log.debug("ERROR..." + comment);
    	    	        	 $log.debug(data);
    	    	         });
    	    	    };
    	    		
    	    		

	});
	
	app.controller('BlogController',function($http, $log, $scope,$window, $location,$rootScope,$cookies, $routeParams){
		var controller = this;
		
		$scope.blogs=[];
		$scope.allblogs=[];
		$scope.count= 0;
		$scope.loading = true;
		$scope.shownext = false;
		$scope.showprev = false;
		 $scope.nextpage = 2;
		$scope.lastDataPoint = $scope.pagesize;
		$scope.pagesize= 3;
		$scope.offset = 0;
		$scope.prevpage = 1;
		
		 $scope.startpage = 0;
		$log.debug("Getting Blogs..." + $window.localStorage.getItem("currentUser"));
		$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
    	 console.log("printing token=========" + $window.localStorage.getItem("loggedin"));
    	
		if($routeParams.id == undefined) {
			//$scope.showprev = false;
			$scope.nextpage = 2;
			$scope.offset = 0;
			$scope.prevpage = 0;
			$log.debug("MainPage/" + "$scope.nextpage" + $scope.nextpage + "$scope.offset" + $scope.offset);
		} else {
			//$scope.showprev = true;
			$scope.nextpage = (parseInt( $routeParams.id) + 1);
			$scope.offset = (parseInt( $routeParams.id) -1) * 3;
			$log.debug("$scope.nextpage" + $scope.nextpage + "$scope.offset" + $scope.offset);
			$scope.prevpage =  (parseInt( $routeParams.id) - 1);;
		}
		
		$http.get('rest/question/'+$scope.offset +"-"+3 ).
		  success(function(data, status, headers, config) {
			  $scope.count= data.length;
			  //$scope.allblogs = data;
			  //$scope.shownext= true;
			  if($scope.pagesize >  $scope.count) {
				  $scope.shownext= false;
			  } 
			  $log.debug($scope.nextpage + "Getting Blogs..." +  $scope.count + "last=" +$scope.pagesize);
			 
			 $scope.blogs = data;
			 $scope.getnextquestion();
			 if($routeParams.id == undefined) {
					$scope.showprev = false;
			 } else {
				 $scope.showprev = true;
			 }
		  }).
		  error(function(data, status, headers, config) {
			  $scope.loading = false;
			  $scope.error = status;
		  });
		$scope.getnextquestion = function() {
			$http.get('rest/question/'+($scope.offset+$scope.pagesize) +"-"+1 ).
			  success(function(data, status, headers, config) {
				  if(data.length != 0) {
					  $log.debug("Next Data:" + data.length);
					  $scope.foundnext = true;
					  $scope.shownext= true;
				  } else {
					  $log.debug("No Next Data:" + data.length);
					  $scope.foundnext = true;
					  $scope.shownext= false;
				  }
				 
				 
				  $log.debug("Next Data:" + data.length);
				
				
				
			  }).
			  error(function(data, status, headers, config) {
				  $scope.shownext= false;
				  $scope.error = status;
			  });
		}
	
	$scope.addBlog = function (blog) {
		$log.debug(blog);
		$scope.showEditForm=false;
		$scope.showAddForm=true;
		$log.debug("=====" + $cookies['token']);
		$log.debug("Add Blogs...");
		$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
        var postData =  $http.post('rest/question', blog);
         postData.success(function (data) {
        	 $log.debug(data);
        	 $scope.question = data;
        	 $location.path('/viewallblogs');
         })
         .error(function (data) {
        	 $log.debug(data);
         });
    };
$scope.setPage = function () {
    	
    	$log.debug("Change Path" +  $scope.nextpage);
    	$location.path('/viewallblogs/'+ $scope.nextpage);
    	
    	    };
   
    	    $scope.setPrevPage = function () {
    	    	$log.debug("setPrev" +  $scope.prevpage);
    	    	if(($scope.prevpage) === 1) {
    	    		$log.debug("1 matched" );
    	    		$location.path('/viewallblogs');
    	    	} else {
    	    		$log.debug("not matched" );
    	    		$location.path('/viewallblogs/'+ $scope.prevpage);
    	    	}
    	    	    };
   $scope.showBlogDetails = function (id) {
    	    	
    	    	$log.debug("id" +  id);
    	    	$location.path('/viewdetailblog/'+ id);
    	    	    };

	});
	app.controller('searchCtrl',function($http, $log, $scope, $window, $location,$rootScope,$cookies, $routeParams){
		var controller = this;
		$scope.questions=[];
	
		$scope.count= 0;
		$scope.loading = true;
		$scope.shownext = false;
		$scope.showprev = false;
		 $scope.searchkey = "";
		$scope.lastDataPoint = $scope.pagesize;
		$scope.pagesize= 1;
		$scope.offset = 0;
		$scope.prevpage = 1;
		
		 $scope.startpage = 0;
		
		$log.debug("Getting Search Blogs..." + $rootScope.loginstatus);
		$log.debug("Getting Blogs..." + $window.localStorage.getItem("currentUser"));
		$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
    	 console.log("printing token=========" + $window.localStorage.getItem("loggedin"));
    	
		if($routeParams.searchkey == undefined || $routeParams.searchkey == "") {
			//$location.path('/viewallblogs/');
		} else {
			/*$scope.showprev = true;
			$scope.nextpage = (parseInt( $routeParams.id) + 1);
			$scope.offset = (parseInt( $routeParams.id) -1) * 3;
			$log.debug("$scope.nextpage" + $scope.nextpage + "$scope.offset" + $scope.offset);
			$scope.prevpage =  (parseInt( $routeParams.id) - 1);;*/
			if($routeParams.id == undefined) {
				$scope.showprev = false;
				$scope.nextpage = 2;
				$scope.foundnext = false;
				$scope.offset = 0;
				$scope.prevpage = 0;
				$log.debug("$scope.nextpage" + $scope.nextpage + "$scope.offset" + $scope.offset);
			} else {
				//$scope.showprev = true;
				$scope.nextpage = (parseInt( $routeParams.id) + 1);
				$scope.offset = (parseInt( $routeParams.id) -1) * $scope.pagesize;
				$log.debug("$scope.nextpage" + $scope.nextpage + "$scope.offset" + $scope.offset);
				$scope.prevpage =  (parseInt( $routeParams.id) - 1);;
			}
			$scope.searchkey = "\"" +$routeParams.searchkey +"\""; 
			
			$scope.url = 'rest/question/search/'+$scope.offset +"-"+$scope.pagesize;
			$log.debug("Getting Search results for..." + $routeParams.searchkey + "-" + $scope.url);
			$http({
		          method: 'POST', 
		          url: $scope.url,
		          data:  $routeParams.searchkey,
		          headers: {
		        	  'Content-Type': 'text/plain'
		            
		          },
		        }).
		        success(function(data, status, headers) {
		          if (status == 200 || status == 201) {
		        	  $scope.questions = data;
		              $scope.searchcontact = {};
		              $scope.getnextsearchresult();
		          }
		          if($routeParams.id == undefined) {
		        	  $scope.showprev = false;
		          } else {
		        	  $scope.showprev = true;
		          }
		        }).
		        error(function(data, status) {
		        	$scope.shownext= false;
		          if (status == 401) {
		            notify('Forbidden', 'Authentication required to create new resource.');
		          } else if (status == 403) {
		            notify('Forbidden', 'You are not allowed to create new resource.');
		          } else {
		            notify('Failed '+ status + data);
		          }
		        });
		}
		
		$scope.getnextsearchresult = function() {
			$scope.offset = $scope.offset + 1;
			$scope.url = 'rest/question/search/'+$scope.offset +"-"+$scope.pagesize;
			$log.debug("Getting next Search results for..." + $routeParams.searchkey + "-" + $scope.url);
			$http({
		          method: 'POST', 
		          url: $scope.url,
		          data:  $routeParams.searchkey,
		          headers: {
		        	  'Content-Type': 'text/plain'
		            
		          },
		        }).
		        success(function(data, status, headers) {
		        	if(data.length != 0) {
						  $log.debug("Next Data:" + data.length);
						
						  $scope.shownext= true;
					  } else {
						  $log.debug("No Next Data:" + data.length);
						  $scope.foundnext = true;
						  $scope.shownext= false;
					  }
		        }).
		        error(function(data, status) {
		        	$scope.shownext= false;
		          if (status == 401) {
		        	  
		            notify('Forbidden', 'Authentication required to create new resource.');
		          } else if (status == 403) {
		            notify('Forbidden', 'You are not allowed to create new resource.');
		          } else {
		            notify('Failed '+ status + data);
		          }
		        });
			
			
		}
		$scope.showsearchresult = function(searchkey) {
			$location.path('/viewallblogs/tagged/'+ searchkey);
		}
         
	
$scope.setPage = function () {
    	
    	$log.debug("Change Path" +  $scope.nextpage);
    	$location.path('/viewallblogs/tagged/'+ $scope.searchkey+ "/" + $scope.nextpage );
    	
    	    };
   
    	    $scope.setPrevPage = function () {
    	    	$log.debug("setPrev" +  $scope.prevpage);
    	    	if(($scope.prevpage) === 1) {
    	    		$log.debug("1 matched" );
    	    		$location.path('/viewallblogs/tagged/'+ $scope.searchkey);
    	    	} else {
    	    		$log.debug("not matched" );
    	    		$location.path('/viewallblogs/tagged/'+ $scope.searchkey + "/" + $scope.prevpage);
    	    	}
    	    	    };
   $scope.showBlogDetails = function (id) {
    	    	
    	    	$log.debug("id" +  id);
    	    	$location.path('/viewdetailblog/'+ id);
    	    	  };

	})
})();