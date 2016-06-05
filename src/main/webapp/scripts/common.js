var blogcount = 0;
	 var PageSize = 5;
	    var CurrentPageIndex = 0;
	    var startPoint = 0;
	    var endPoint = CurrentPageIndex + PageSize;
	    
	    
function dataFromTimestamp(timestamp){
	
	  var d = new Date(timestamp);

	    // Time
	    var h = addZero(d.getHours());              //hours
	    var m = addZero(d.getMinutes());            //minutes
	    var s = addZero(d.getSeconds());            //seconds

	    // Date
	    var da = d.getDate();                       //day
	    var mon = d.getMonth() + 1;                 //month
	    var yr = d.getFullYear();                   //year
	    var dw = d.getDay();                        //day in week

	    // Readable feilds
	    months = ["jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"];
	    var monName = months[d.getMonth()];         //month Name
	    var time = h + ":" + m + ":" + s;           //full time show
	    var thisDay = da + "/" + mon + "/" + yr;    //full date show
	    var fullTimeValue = thisDay + " " + h +":" + m + ":" + s;
	    var dateTime = {
	        seconds : s,
	        minutes : m,
	        hours : h,
	        dayInMonth : da,
	        month : mon,
	        year : yr,
	        dayInTheWeek : dw,
	        monthName : monName,
	        fullTime : time,
	        fullDate : thisDay
	    };
	    return fullTimeValue;

	    function addZero(i) {
	        if (i < 10) {
	            i = "0" + i;
	        }
	        return i;
	    }
}
