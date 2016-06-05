$(document).ready(function() {
	// alert("I am here ");
	 var id = window.location.href.slice(window.location.href.indexOf('?') + 1).split('=');

	 var data = null;
	 var url = "cmad/blogs/" + id[1];


	 $.get(url, function(data){

	     var ith=document.createElement('h2'),
	           its=document.createElement('span'),
	         ita=document.createElement('a'),
	         blogTimeRaw= dataFromTimestamp(data.postdate),
	         dateTxt = blogTimeRaw + "| Posted By",
	         iat=document.createTextNode(data.title),
	         idn=document.createTextNode(dateTxt),
	         brpoint = document.createElement('br'),
	         itp = document.createElement('p'),
	      ipt = document.createTextNode(data.content),
	      itp2 = document.createElement('p'),
	      ita2=document.createElement('a'),
	      ipt2 = document.createTextNode("Read More"),
	      brpoint2 =document.createElement('br'),
	       blogid= data.blogId;

	   ith.appendChild(iat);
	   ith.appendChild(brpoint);
	   its.appendChild(idn);
	   ith.appendChild(its);
	   itp.appendChild(ipt);
	   $(ita2).attr('href','#');
	   $(ita2).attr('class','mylink');
	   $(ita2).attr('id', blogid);
	   ita2.appendChild(ipt2);
	  itp2.appendChild(ita2);

	   $("#left").append(ith).append(itp).append(brpoint2).append(itp2);


	 });

});
