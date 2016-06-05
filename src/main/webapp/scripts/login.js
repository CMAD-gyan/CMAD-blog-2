 $(document).ready(function() {
    //alert (" login");
    $("form").submit(function (e) {
      //alert ("call submit");
      var user = document.getElementById("username").value;
      var pwd = document.getElementById("password").value;
      var data = JSON.stringify({ email: user, password: pwd});
      //alert( data);
      event.preventDefault(); 
 
      $.ajax({
          type : "POST",
          url : 'cmad/users/login',
          dataType :"json",
          contentType: "application/json",
          data : data,
          success : function(xhr, status, error) {
        	  if(xhr != null ){
        		 
        		  if (status === "success") {alert (status);}
        		  
        		  window.location="viewallblogs.jsp";
        	  } else {
        		  if (status === "nocontent") {
        			 
						$('#addResultDiv').html( "User Credendial Invalid.");
        		  }
        	  }
        	
	},
	failure : function(result) {
		//alert (result);

}

});
});
});
 
 
 
 
 
 
 
 /* $(document).ready(function() {
    alert (" login");
    $("form").submit(function (e) {
      alert ("call submit");
      var user = document.getElementById("username").value;
      var pwd = document.getElementById("password").value;
      var data = JSON.stringify({ email: user, password: pwd});
      alert( data);
      event.preventDefault(); 
 
      $.ajax({
          type : "POST",
          url : 'cmad/users/login',
          dataType :"json",
          contentType: "application/json",
          data : data,
          success : function(result) {
        	  if(result != null){
        		  alert ("I am authorized");
        		  window.location="viewallblogs.jsp";
        	  } else {
        		  alert ("I am not authorized");
        	  }
        
},

});
});
});*/
