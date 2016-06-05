$(document).ready(function() {
	// alert("I am here ");
	 var id = window.location.href.slice(window.location.href.indexOf('?') + 1).split('=');
		
		if (typeof id[1] === "undefined") {
			//alert (" I am not defined");
		} else {
			startPoint = id[1];
		}
	 $.ajax({
		    url: 'cmad/blogs', // returns "[1,2,3,4,5,6]"
		    dataType: 'json', // jQuery will parse the response as JSON
		    success: function (data) {
		    	blogcount =  data.length; 
		    	
		    	 endPoint = (startPoint + PageSize);

		 	    if (endPoint > blogcount) {

		 	    endPoint = blogcount;

		 	    }
		        for (i = startPoint; i < endPoint ;i++){
		    		var 	brpoint = document.createElement('br'),
		    		ith = document.createElement('h2'),
		    		ita=document.createElement('a'),
		    		its=document.createElement('span'),
		    		ita=document.createTextNode(data[i].title),
		    		username = data[i].username,
		    		blogTime= dataFromTimestamp(data[i].postdate),
		    	     dateTxt = blogTime + "| Posted By " + username,
		    	     idn=document.createTextNode(dateTxt);
		    		
		    		 $(its).attr('class','small');
		    		 its.appendChild(idn);
		    		 ith.appendChild(ita);
		    		 ith.appendChild(brpoint);
		    		 ith.appendChild(its);
		    		
			    	 
			    	 var itp = document.createElement('p'),
			    	 ipt = document.createTextNode((data[i].content).substr(0, 255)),
		    	     itp2 = document.createElement('p'),
		    	     ita2=document.createElement('a'),
		    	     ipt2 = document.createTextNode("Read More"),
		    	     brpoint2 =document.createElement('br'),
		    	      blogid= data[i].blogid;
			    	 
			    	 itp.appendChild(ipt);
			    	  $(ita2).attr('href','#');
			    	  $(ita2).attr('class','mylink');
			    	  $(ita2).attr('id', blogid);
			    	  ita2.appendChild(ipt2);
			    	 itp2.appendChild(ita2);
			    	 
			    	
			    	  $("#left").append(ith).append(itp).append(brpoint2).append(itp2);
		    	
		        };
		        if(startPoint != 0){
	    			startPoint = endPoint - PageSize ;
	    			
	    			var  brpoint3 =document.createElement('br'),
			        preva= document.createElement('a');

	    			prevT = document.createTextNode("prev");
			        $(preva).attr('href','#');
			    	$(preva).attr('class','prev');
			       	$(preva).attr('id', "prev");
			       	preva.appendChild(prevT);
			       	$("#left").append(brpoint3).append(preva);
	    	}
		    	if(endPoint < blogcount){
		    			startPoint = endPoint ;
		    			
		    			var  brpoint3 =document.createElement('br'),
				        nexta= document.createElement('a');

				        nextT = document.createTextNode("next");
				        $(nexta).attr('href','#');
				    	$(nexta).attr('class','next');
				       	$(nexta).attr('id', "next");
				       	nexta.appendChild(nextT);
				       	$("#left").append(brpoint3).append(nexta);
		    	}
		       
	    		
		    }
		});
	
	 $(document).on("click", ".mylink", function(event) {
	       event.preventDefault();    //prevent default action of <a>
	       var id = $(this).attr('id');
	        window.location.href = 'viewblog.jsp?id='+id; 

	});
	 $(document).on("click", ".prev", function(event) {
	       event.preventDefault();    //prevent default action of <a>
	        window.location.href = 'viewnextblogs.jsp?id='+startPoint; 

	});
	 $(document).on("click", ".next", function(event) {
	       event.preventDefault();    //prevent default action of <a>
	        window.location.href = 'viewallblogs.jsp?id='+startPoint; 

	});
});
