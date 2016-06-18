(function(){
	var app = angular.module('myApp',['ngRoute','ngCookies','ngDialog']);


	app.config(function($routeProvider) {
		$routeProvider
		// route for the home page
		// route for the login page
		.when('/', {
			templateUrl : 'viewallblogs.html',
			controller  : 'BlogController'
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
//		$rootScope.loginstatus = false;
		var controller = this;
		$scope.users=[];
		$scope.loading = true;
		if($window.localStorage.getItem("loggedin") === "true"){
			$rootScope.loginstatus = true;
		} else {
			$rootScope.loginstatus = false;
		}
		$log.debug("loginstatus value after refresh and if cond'n: "+ $rootScope.loginstatus);
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
				$cookies['username'] = user.username;
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
			$http.post('rest/users', user).success(function (data) {
				$log.debug(data);
				$scope.users.push(user);
				$location.path("login");
//				$window.location.href= "#login";
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
			$http.put('rest/users',user).
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
		$scope.editanswer = true;
		$scope.oldText = "";
		$scope.oldAnsText = "";
		$scope.answerText = "";
		$log.debug($routeParams.id  + "Getting New Detail Blogs..." + $scope.questionid);

		$http.get('rest/questions/' + $scope.questionid).then(function(response) {
			$log.debug("detail blog="+ response.data);
			$scope.currentquestion = response.data;
			$scope.oldText = $scope.currentquestion.text;
			
			if($scope.currentquestion.answers != undefined && $scope.currentquestion.answers.length > 0) {
				$scope.showanswers = true;
			} else {
				$scope.showanswers = false;
			}
			$scope.username= $window.localStorage.getItem("currentUser");
			if($scope.currentquestion.username != undefined && ($scope.username == $scope.currentquestion.username)) {
				$scope.edittext = true;
			} else {
				$scope.edittext = false;
			}
			$scope.showansbtn = true;
			$scope.dataloading = false;
			
			$http.post('rest/questions/' + $scope.questionid + '/view_incrementer');
		}, function(response) {

			$scope.error = response.status;
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
			$scope.updatedquestion.text = updatedtext;
			$scope.updatedquestion.title = $scope.currentquestion.title;
			$scope.updateBlog(changedques);
		}

		$scope.openEditQuestion = function() {
			
			ngDialog.openConfirm({template: 'editQuestionDialog.html',
			scope: $scope, //Pass the scope object if you need to access in the template,
			className: 'ngdialog-theme-default custom-width-400'
			}).then(
				function(value) {
					$scope.questionupdate(value)
			        
				},
				function(value) {
					  $scope.currentquestion.text =  $scope.oldText;
				}
			);
		};

		$scope.deletequestion = function () {
			$scope.openDelConfirmForm();

		}
		
		$scope.openDelConfirmForm = function() {

			ngDialog.openConfirm({template: 'deleteform.html',
				scope: $scope, //Pass the scope object if you need to access in the template,
				className: 'ngdialog-theme-default custom-width-100'
			}).then(
					function(value) {
						$log.debug("delete blog=" + $scope.currentquestion.id + "===" + $cookies['token']);
						$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];

						$http({ url: 'rest/questions/' +  $scope.currentquestion.id, 
							method: 'DELETE'
						}).then(function(res) {
							console.log("del Successful" +res.data);
							$location.path('/viewallblogs');
						}, function(error) {
							console.log("del UnSuccessful" + error);
						});

					},
					function(value) {
						$log.debug("Dont delete blog=" + $scope.currentquestion.id);
					}
			);
		};

		$scope.updateBlog = function(data) {
			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];

			$http.put('rest/questions', data)
			.then(
					function(response){
						$log.debug(response.data);
						//$route.reload();
						if(response.status == 200 || response.status == 201) {
							$scope.currentquestion = response.data;
							$scope.oldText = $scope.currentquestion.text;
							console.log("posting the question " );
						} else if(response.status == 401) {
							$log.debug("ERROR..." );
							$cookies['token'] = "";
							$scope.username = "";
							$window.localStorage.setItem("loggedin", false);
							$rootScope.loginstatus = $window.localStorage.getItem("loggedin");

						} else {
							console.log("Comment posted respomse was " + response.status  );
						}
						//$location.path('/viewdetailblog/' + $scope.questionid);

					}
			);

		};

		$scope.questionUpvote = function() {
			$log.debug("=== token " + $cookies['token']);
			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
			$http.post('rest/questions/'+$scope.questionid +"/vote_up")
			.then(function(response){
				$log.debug(response.data);
				// $route.reload();
				if(response.status == 200) {
					$scope.currentquestion.totalVotes = response.data;
				} else if(response.status == 401) {
					$log.debug("ERROR..." );
					$cookies['token'] = "";
					$scope.username = "";
					$window.localStorage.setItem("loggedin", false);
					$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
				} else {
					console.log("Comment posted respomse was " + response.status  );
				}
			});
		};

		$scope.openQAnswerForm = function() {
			$log.debug('answer.html');
			ngDialog.openConfirm({template: 'answerdialog.html',
				scope: $scope 
			}).then(
					function(value) {
						$log.debug(value);
						var answer = {
								"text": value
						};
						$scope.addYourAnswer(answer);

					},
					function(value) {
						//Cancel or do nothing
					}
			);
		};
		
$scope.openEditAnswer = function(answerTxt) {
		$scope.answerText = answerTxt;
		$scope.oldAnsText = answerTxt;
			ngDialog.openConfirm({template: 'editAnswerDialog.html',
			scope: $scope, //Pass the scope object if you need to access in the template,
			className: 'ngdialog-theme-default custom-width-400'
			}).then(
				function(value) {
					$log.debug("ansTxt=" + value);
					$log.debug("scope.oldAnsText=" + $scope.oldAnsText);
					var answer = {
							"text": value
					};
					$scope.addYourAnswer(answer)
			        
				},
				function(value) {
					$log.debug("Can ansTxt=" + $scope.answerText);
					$log.debug("value=" + $scope.oldAnsText);
					  //$scope.currentquestion.answer.text =  $scope.oldAnsText;
				}
			);
		};
		
		$scope.answerupdate = function (updatedtext) {
			$log.debug(updatedtext);
			var changedques= {
					"title":  $scope.currentquestion.title,
					"text": updatedtext
			};
			$log.debug("edit text" + changedques);
			$scope.updatedquestion.text = updatedtext;
			$scope.updatedquestion.title = $scope.currentquestion.title;
			$scope.updateBlog(changedques);
		}
		$scope.addYourAnswer = function (answer) {
			$log.debug(answer);
			$log.debug("=====" + $cookies['token']);
			$log.debug("Add Answers...");
		
			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
			$http.put('rest/answers/'+$scope.questionid, answer)
			.then(
					function(response){
						$log.debug(response.data);
						if(response.status == 200 || response.status == 201) {
							$scope.currentquestion = response.data;
							console.log("posting the question " );
						} else if(response.status == 401) {
							$log.debug("ERROR..." );
							$cookies['token'] = "";
							$scope.username = "";
							$window.localStorage.setItem("loggedin", false);
							$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
						}  else {
							console.log("Comment posted respomse was " + response.status  );
						}
					}
			);
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
    
    $scope.questionUpvote = function() {
    	$log.debug("=== token " + $cookies['token']);
		$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
	    $http.post('rest/questions/'+$scope.questionid +"/vote_up")
         .then(function(response){
        	 $log.debug(response.data);
        	 if(response.status == 200 || response.status == 201) {
         		 $scope.currentquestion.totalVotes = response.data;
         	 } else if(response.status == 401) {
         		 $log.debug("ERROR..." );
         		 $cookies['token'] = "";
         		 $scope.username = "";
                 $window.localStorage.setItem("loggedin", false);
                 $rootScope.loginstatus = $window.localStorage.getItem("loggedin");
         	} else {
				console.log("Comment posted respomse was " + response.status  );
			}
         });
	};
	
	$scope.questionDownvote = function() {
    	$log.debug("=== token " + $cookies['token']);
		$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
	    $http.post('rest/questions/'+$scope.questionid +"/vote_down")
         .then(function(response){
        	 $log.debug(response.data);
	    	        	 // $route.reload();
         	 if(response.status == 200) {
         		 $scope.currentquestion.totalVotes = response.data;
         	 } else if(response.status == 401) {
         		 $log.debug("ERROR..." );
         		 $cookies['token'] = "";
         		 $scope.username = "";
                 $window.localStorage.setItem("loggedin", false);
                 $rootScope.loginstatus = $window.localStorage.getItem("loggedin");
             }
         });
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

			$http.put('rest/questions/'+$scope.questionid +"/comments" , comment)
			.then(
					function(response){
						$log.debug(response.data);
						if(response.status == 200 || response.status == 201) {
							$scope.currentquestion = response.data;
							console.log("posting the question " );
						} else if(response.status == 401) {
							$log.debug("ERROR..." );
							$cookies['token'] = "";
							$scope.username = "";
							$window.localStorage.setItem("loggedin", false);
							$rootScope.loginstatus = $window.localStorage.getItem("loggedin");

						} else {
							console.log("Comment posted respomse was " + response.status  );
						}
					}
			);
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
						$scope.postAComment(comment, ans.id);

					},
					function(value) {
						//Cancel or do nothing
					}
			);
		};

		$scope.postAComment = function(comment, ansid, ans){
			$log.debug("edit blog=" + comment + "=== token " + $cookies['token']);
			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
			$http.put('rest/answers/'+ansid +"/comments" , comment)
				.then(
			function(response){
				$log.debug(response.data);
				if(response.status == 200 || response.status == 201) {
					$scope.currentquestion = response.data;
					console.log("Answer Comment posted successfully" );
				} else if(response.status == 401) {
					$log.debug("ERROR..." );
					$cookies['token'] = "";
					$scope.username = "";
					$window.localStorage.setItem("loggedin", false);
					$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
				} else {
					console.log("Comment posted respomse was " + response.status  );
				}
				//$location.path('/viewdetailblog/' + $scope.questionid);
			});
		};
		$scope.deleteanswer = function () {
			$scope.openAnswerDelConfirmForm();

		}
		$scope.openAnswerDelConfirmForm = function() {

			ngDialog.openConfirm({template: 'deleteform.html',
				scope: $scope, //Pass the scope object if you need to access in the template,
				className: 'ngdialog-theme-default custom-width-100'
			}).then(
					function(value) {
						$log.debug("delete answernow =" + $scope.currentquestion.id + "===" + $cookies['token']);
						$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];

						$http({ url: 'rest/answers/' +  $scope.currentquestion.id, 
							method: 'DELETE'
						}).then(function(response) {
							console.log("del Successful" +response.data);
							if(response.status == 200 || response.status == 201) {
								$scope.currentquestion = response.data;
							
							}
						}, function(error) {
							console.log("del answer UnSuccessful" + error);
						});

					},
					function(value) {
						$log.debug("Dont delete answer=" + $scope.currentquestion.id);
					}
			);
		};
	});

	app.controller('BlogController',function($http, $log, $scope,$window, $location,$rootScope,$cookies, $routeParams){
		var controller = this;
		$scope.maxLengthPerPage=5;
		$rootScope.blogLength;
		$scope.loading = true;
		$scope.shownext = false;
		$scope.showprev = false;
		 $scope.addOper= false;

//		$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
		console.log("Getting Blogs token========="  + $location.url() + " "  + $location.url().indexOf('addblog')  );
		if($location.url().indexOf('addblog') > -1){    
			$scope.addOper= true;
	           
	    }
		if(!($scope.addOper) && $routeParams.id == undefined  ) {
			$http({ method: 'GET',
				url:'rest/questions/length' 
			}).then(function(response) {
				console.log("got length " + response.data); 
				$rootScope.blogLength = (parseInt(response.data)) ;
				$routeParams.id = 1;
				$location.path('/viewallblogs/'+ $routeParams.id);
			})
		} else 	if(!($scope.addOper) && $routeParams.id != undefined  ) {
			$scope.blogCurrentOffset = ($routeParams.id-1) * $scope.maxLengthPerPage;
			$scope.blogCurrentLength = $scope.maxLengthPerPage;
			if ( ($rootScope.blogLength - $scope.blogCurrentOffset )    <  $scope.maxLengthPerPage) {
				$scope.blogCurrentLength = $rootScope.blogLength;
			} else {
				$scope.blogCurrentLength = $scope.maxLengthPerPage;
			}
			console.log("id is  "+ $routeParams.id + " " + $scope.blogCurrentOffset + " " + $scope.blogCurrentLength  );
			console.log("length  is " + $scope.blogLength); 
			$http({
				method: 'GET',
				url: 'rest/questions',
				params: {
					offset : $scope.blogCurrentOffset ,
					length : $scope.blogCurrentLength 
				}
			}).then(function(response) {
				console.log("received  successfully" + response.status);
				$scope.count= response.data.length;
				$scope.blogs = response.data;

				if ($routeParams.id  > 1 ) {
					$scope.showprev = true;
					$scope.prevpage =  (parseInt($routeParams.id))  - 1;
				}
				if ( ($rootScope.blogLength - $scope.blogCurrentOffset )    >   $scope.maxLengthPerPage) {
					$scope.shownext = true;
					$scope.nextpage = (parseInt($routeParams.id)) + 1;
				}			


			}, function(response) {
				console.log("Error message");   
			});
		}

		$scope.addBlog = function (data) {
			$log.debug("Add Blogs..." + data + "=====" + $cookies['token']);
			$http.defaults.headers.common.Authorization = 'Bearer ' + $cookies['token'];
			$http.put('rest/questions', data)
			.then(
					function(response){
						$log.debug("Question added .."+ response.data.id);

						$location.path('/viewdetailblog/' + response.data.id);
					}, 
					function(response){
						$log.debug("ERROR..." + response.status);  
						if(response.status == 401) {
							$log.debug("ERROR..." );
							$cookies['token'] = "";
							$scope.username = "";
							$window.localStorage.setItem("loggedin", false);
							$rootScope.loginstatus = $window.localStorage.getItem("loggedin");

						}
					}
			);
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
		$scope.maxLengthPerPage =5;
		$rootScope.quesLength;
		$scope.count= 0;
		$scope.loading = true;
		$scope.shownext = false;
		$scope.showprev = false;
		$scope.searchkey = "";
		
		$scope.prevpage = 1;

		$log.debug("Getting  Search Results..." + $rootScope.loginstatus);
//		$rootScope.loginstatus = $window.localStorage.getItem("loggedin");
		console.log("printing token=========" + $window.localStorage.getItem("loggedin"));

		if($routeParams.searchkey == undefined || $routeParams.searchkey == "") {
			// DO Nothing
		} else {
			if($routeParams.id == undefined) {
				$scope.showprev = false;
				$scope.nextpage = 2;
				$scope.prevpage = 0;
				$scope.searchkey = $routeParams.searchkey; 
				console.log("Search key=" + $scope.searchkey);
				$http({
					method: 'POST',
					url: 'rest/questions/search/length',
					data:  $scope.searchkey ,
					headers: {
						'Content-Type': 'text/plain'

					}
				}).success(function(response) {
					$rootScope.quesLength = (parseInt(response)) ;
					
					console.log(response + "Search length" + $rootScope.quesLength);
					$routeParams.id = 1;
					$location.path('/viewallblogs/tagged/'+ $routeParams.searchkey+ "/" + $routeParams.id );

				}).error(function(response) {
					console.log("Search Error message");   
					$scope.shownext= false;
				});
			} else if($routeParams.id != undefined  ) {
				$scope.blogCurrentOffset = ($routeParams.id-1) * $scope.maxLengthPerPage;
				$scope.blogCurrentLength = $scope.maxLengthPerPage;
				if ( ($rootScope.quesLength - $scope.blogCurrentOffset )    <  $scope.maxLengthPerPage) {
					$scope.blogCurrentLength = $rootScope.quesLength;
				} else {
					$scope.blogCurrentLength = $scope.maxLengthPerPage;
				}
				console.log("route id is  "+ $routeParams.id + " " + $scope.blogCurrentOffset + " " + $scope.blogCurrentLength  );
				console.log("ques length  is " + $rootScope.quesLength); 
				$scope.nextpage = (parseInt( $routeParams.id) + 1);
				$scope.prevpage =  (parseInt( $routeParams.id) - 1);;
				$scope.searchkey = $routeParams.searchkey; 
				console.log("printing key=" + $scope.searchkey); 
				$http({
					method: 'POST',
					url: 'rest/questions/search',
					data:  $scope.searchkey,
					headers: {
						'Content-Type': 'text/plain'

					},
					params: {
						offset : $scope.blogCurrentOffset,
						length : $scope.blogCurrentLength
					}
				}).then(function(response) {
					console.log("searched successfully" + response.status);

					$scope.count= response.data.length;
					$scope.questions = response.data;

					if ($routeParams.id  > 1 ) {
						$scope.showprev = true;
						$scope.prevpage =  (parseInt($routeParams.id))  - 1;
					}
					if ( ($rootScope.quesLength - $scope.blogCurrentOffset )    >   $scope.maxLengthPerPage) {
						$scope.shownext = true;
						$scope.nextpage = (parseInt($routeParams.id)) + 1;
					}

				}, function(response) {
					console.log("Search Error message");   
				});
			}
			

		}

		
		$scope.showsearchresult = function(searchkey) {
			$location.path('/viewallblogs/tagged/'+ searchkey);
		}


		$scope.setPage = function () {

			$log.debug("Change search Path" +  $routeParams.searchkey);
			$location.path('/viewallblogs/tagged/'+ $routeParams.searchkey+ "/" + $scope.nextpage );

		};

		$scope.setPrevPage = function () {
			$log.debug("setPrev" +  $scope.prevpage);
			if(($scope.prevpage) === 1) {
				$log.debug("1 matched" );
				$location.path('/viewallblogs/tagged/'+ $routeParams.searchkey);
			} else {
				$log.debug("not matched" );
				$location.path('/viewallblogs/tagged/'+ $routeParams.searchkey + "/" + $scope.prevpage);
			}
		};
		$scope.showBlogDetails = function (id) {

			$log.debug("id" +  id);
			$location.path('/viewdetailblog/'+ id);
		};

	})
})();
