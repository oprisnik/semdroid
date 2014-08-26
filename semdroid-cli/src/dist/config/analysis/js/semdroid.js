function onSuccess(data) {
      $("#analyzing").css('display', 'none');
      if (data != null) {
      	$("#results").html(data);
      } else {
    	  $("#results").html("<h1>Could not analyze the application!</h1>");
      }
    }

    function analyze() {
    	$("#analyzing").css('display', 'block');
    	$("#main").css('display', 'none');
      $.ajax({
	type: 'POST',
	url: 'semdroid',
	data: new FormData(document.getElementById("my-form")),
      	processData: false,
      	contentType: false,
	success: onSuccess,
      });
    }

    $('#apkselect').click(function(){
                // Trigger the file browser dialog
		document.getElementById("android-apk").click();
		//main.css('top',(-100)+'%');
	});


	$('#main').on('change', '#android-apk', function(e){

		// Has a file been selected?

		if(e.target.files.length!=1){
			alert('Please select a file!');
			return false;
		}

		file = e.target.files[0];

		var ext = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase();
		if (ext != 'apk') {
			alert('Only APK files allowed!');
			return;
		}

		if(file.size > 1024*1024*30){
			alert('Please choose an application smaller than 30mb.');
			return;
		}
		analyze();
	});

	$( document ).ready(function() {
		$("#analyzing").css('display', 'none');
	});