$(document).ready(function() {


});
$(document).on('click','#clickDown',function(){
	$('.down').slideToggle(500);
});
$(document).on('click','.checkAll',function(){
	$( '.check' ).prop( 'checked', this.checked );
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

function FunLoadingBarStart() {
	const backHeight = $(document).height(); //뒷 배경의 상하 폭
	const backWidth = window.document.body.clientWidth; //뒷 배경의 좌우 폭
	const backGroundCover = "<div id='back'></div>"; //뒷 배경을 감쌀 커버
	let loadingBarImage = ''; //가운데 띄워 줄 이미지
	loadingBarImage += "<div id='loadingBar'>";
	loadingBarImage += " <img src='/admin/assets/image/loading.svg'/>"; //로딩 바 이미지
	loadingBarImage += "</div>";
	$('body').append(backGroundCover).append(loadingBarImage);
	$('#back').css({ 'width': backWidth, 'height': backHeight, 'opacity': '0.3' });
	$('#back').show();
	$('#loadingBar').show();
}

function FunLoadingBarEnd() {
	$('#back, #loadingBar').hide();
	$('#back, #loadingBar').remove();
}

function ajaxView(url,method,data,fragmentId){

	$.ajax({
		url: url,
		type :  method,
		data : data,
		beforeSend : function(xhr)
		{
			xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"),$("meta[name='_csrf']").attr("content"));
		},
		complete: function () {
		},
		error : function(error){
		}
	}).done(function (fragment) {
		$("#"+fragmentId).replaceWith(fragment);
	});
}

function resultTokenCompare(leftObject) {
	let allKeys = new Set();
	Object.keys(leftObject).forEach(key => {
		allKeys.add(key);
	});

	let result = [];
	allKeys.forEach((v1) => {
		result.push({
			to: v1,
			left: leftObject[v1],
			leftValue: leftObject[v1],
		});
	});

	return result;
}