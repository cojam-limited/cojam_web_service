$(document).ready(function() {
	
	// 인쿠르트 //
	$("#header").load("/admin/include/header.html");
	//$("#footer").load("/admin/include/footer.html");
	$("#left-sidebar").load("/admin/include/left.html");

	$(document).on('click','#clickDown',function(){
			$('.down').slideToggle(500);
	});
	$(document).on('click','.checkAll',function(){
		$( '.check' ).prop( 'checked', this.checked );
});

	// 날짜 - 퀘스트 등록 //
	$(".date-icon").datetimepicker({
		dateFormat:'yy-mm-dd',
		// timepicker 설정
		timeFormat:'hh:mm:ss',
		//controlType:'select',
		//oneLine:true,
	});
	$('.date-icon').val($.datepicker.formatDate('yy-mm-dd', new Date()));



});