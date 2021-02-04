$(document).ready(function() {
	// 인쿠르트 //
	$("#header").load("/include/header.html")
	$("#footer").load("/include/footer.html")


	// 상단영역 //
	$(window).scroll(function() {
		if ($(".header").offset().top > 50) {
			$('.header').css({ borderBottom : '#f9f9f9 0.5px solid', background : '#fff' });
			$('.header > dl > dt > h2 > a img').attr("src", '/assets/image/common/logo_black.png');
			$('.header > dl > dt > div').stop().animate({ padding : '15px 0' }, 350, 'easeOutQuad');
			$('.header > dl > dt > div > a').css({ color : '#222' });
			$('.header > dl > dt > div > a:hover').css({ background : '#edf3f8' });
			$('.header > dl > dt > div > a.active').css({ background : '#edf3f8' });
			$('.header > dl > dt > div > div').stop().animate({ top : '65px' }, 350, 'easeOutQuad');
			$('.header > dl > dd > h2').css({ color : '#222' });
			$('.header > dl > dd > h2 > span').css({ color : '#222' });
			$('.header > dl > dd > div > a').css({ color : '#222', background : '#edf3f8' });
			$('.header > dl > dd > div > a:hover').css({ color : '#222', background : '#222' });
		} else {
			$('.header').css({ borderBottom : 'rgba(255,255,255,0.1) 1px solid', background : 'none' });
			$('.header > dl > dt > h2 > a img').attr("src", '/assets/image/common/logo_white.png');
			$('.header > dl > dt > div').stop().animate({ padding : '20px 0' }, 350, 'easeOutQuad');
			$('.header > dl > dt > div > a').css({ color : '#fff' });
			$('.header > dl > dt > div > a:hover').css({ background : 'rgba(255,255,255,0.2)' });
			$('.header > dl > dt > div > a.active').css({ background : 'rgba(255,255,255,0.2)' });
			$('.header > dl > dt > div > div').stop().animate({ top : '75px' }, 350, 'easeOutQuad');
			$('.header > dl > dd > h2').css({ color : '#fff' });
			$('.header > dl > dd > h2 > span').css({ color : '#fff' });
			$('.header > dl > dd > div > a').css({ color : '#fff', background : 'rgba(255,255,255,0.3)' });
			$('.header > dl > dd > div > a:hover').css({ color : '#fff', background : 'rgba(255,255,255,0.3)' });
		}
	});

	$('.header').addClass("active");
	// URL에서 파일명 추출하기
	// var totalUrl = document.URL;
	// if (totalUrl.indexOf('/main') !== -1) {
	// 	alert ('quest');
	// 	$('.header > dl > dt > div > a').removeClass('active');
	// 	$('.header > dl > dt > div > a:nth-child(1)').addClass('active');
	// } else if (totalUrl.indexOf('/quest') !== -1) {
	// 	alert ('quest');
	// 	$('.header > dl > dt > div > a').removeClass('active');
	// 	$('.header > dl > dt > div > a:nth-child(2)').addClass('active');
	// } else if (totalUrl.indexOf('/service') !== -1) {
	// 	alert ('service');
	// 	$('.header > dl > dt > div > a').removeClass('active');
	// 	$('.header > dl > dt > div > a:nth-child(3)').addClass('active');
	// } else if (totalUrl.indexOf('/about') !== -1) {
	// 	alert ('about');
	// 	$('.header > dl > dt > div > a').removeClass('active');
	// 	$('.header > dl > dt > div > a:nth-child(4)').addClass('active');
	// } else if (totalUrl.indexOf('/notice') !== -1) {
	// 	alert ('notice');
	// 	$('.header > dl > dt > div > a').removeClass('active');
	// 	$('.header > dl > dt > div > a:nth-child(5)').addClass('active');
	// }


	// 카타고리 상단 고정 //
	var categoryOffset = $( '.category-section' ).offset();
	$( window ).scroll( function() {
		if ($(window).width() < 991) {
			if ( $( document ).scrollTop() > categoryOffset.top ) {
				$('.header-mobile').css({ boxShadow : 'none' });
				$('.category-section').css({ position : 'fixed', left : '0', top : '53px', width : '100%', borderBottom : '#e5e5e5 0.5px solid', background : '#fff' });
				$('.category-section > dl').css({ width : '100%', margin : '0 auto', border : 'none', borderRadius : '0', boxShadow : 'none' });
				$('.category-section > dl').stop().animate({ padding : '5px 20px 12px 20px' }, 350, 'easeOutQuad');
			}
			else {
				$('.header-mobile').css({ boxShadow : '0 0 10px 0 rgba(0,0,0,0.1)' });
				$('.category-section').css({ position : 'relative', left : '0', top : '0', width : '100%', borderBottom : 'none', background : 'none' });
				$('.category-section > dl').css({ width : '100%', margin : '0 0 17px 0', border : '#e5e5e5 0.5px solid', borderRadius : '10px', boxShadow : '0 0 10px 0 rgba(0,0,0,0.1)' });
				$('.category-section > dl').stop().animate({ padding : '13px' }, 350, 'easeOutQuad');
			}
		} else {
			if ( $( document ).scrollTop() > categoryOffset.top ) {
				$('.category-section').css({ position : 'fixed', left : '0', top : '68px', width : '100%', borderBottom : '#e5e5e5 0.5px solid', background : '#fff' });
				$('.category-section > dl').css({ width : '1300px', margin : '0 auto', border : 'none', borderRadius : '0', boxShadow : 'none' });
				$('.category-section > dl').stop().animate({ padding : '15px 0' }, 350, 'easeOutQuad');
			}
			else {
				$('.category-section').css({ position : 'relative', left : '0', top : '0', width : '100%', borderBottom : 'none', background : 'none' });
				$('.category-section > dl').css({ width : '100%', margin : '0 0 30px 0', border : '#e5e5e5 0.5px solid', borderRadius : '10px', boxShadow : '0 0 10px 0 rgba(0,0,0,0.1)' });
				$('.category-section > dl').stop().animate({ padding : '20px 30px' }, 350, 'easeOutQuad');
			}
		}
	});


	// 카타고리 Swiper //
	var swiper = new Swiper('.category-section > dl > dt', {
		slidesPerView: 'auto',
		spaceBetween: 10,
		//freeMode: true,
	});


	// 상세 - 탭버튼 //
	$('.vs-tab01').click(function(){ 
		$('.view-section > dl > dd > ul li').removeClass('active');
		$(this).addClass('active');

		$('.vs-chart').css({ display : 'block' });
		$('.vs-vol').css({ display : 'none' });
		$('.vs-info').css({ display : 'none' });
	});
	$('.vs-tab02').click(function(){ 
		$('.view-section > dl > dd > ul li').removeClass('active');
		$(this).addClass('active');

		$('.vs-chart').css({ display : 'none' });
		$('.vs-vol').css({ display : 'block' });
		$('.vs-info').css({ display : 'none' });
	});
	$('.vs-tab03').click(function(){ 
		$('.view-section > dl > dd > ul li').removeClass('active');
		$(this).addClass('active');

		$('.vs-chart').css({ display : 'none' });
		$('.vs-vol').css({ display : 'none' });
		$('.vs-info').css({ display : 'block' });
	});


	// 모달 - 퀘스트 등록 //
	$('#modalAdd, .add-btn a').on('click',function(event){
		event.preventDefault();
		$(".modal-bg").fadeIn(300);
		$('.modal-add').fadeIn(300);
		$('.modal-area').stop().animate({ marginTop : '0' }, 300, 'easeOutQuad');
	});


	// 모달 - 퀘스트 시즌 //
	$('#modalSeason').on('click',function(event){
		event.preventDefault();
		$(".modal-bg").fadeIn(300);
		$('.modal-season').fadeIn(300);
		$('.modal-area').stop().animate({ marginTop : '0' }, 300, 'easeOutQuad');
	});


	// 모달 - 닫기 //
	$(".modal-bg, .modal-close").on('click',function(){
		$('.modal-area').stop().animate({ marginTop : '-15px' }, 300, 'easeOutQuad');
		$(".modal-bg").fadeOut(300);
		$(".modal-area").fadeOut(300);
	});


	// 날짜 - 퀘스트 등록 //
	$("#schDate").datetimepicker({
		dateFormat:'yy-mm-dd',
		// timepicker 설정
		timeFormat:'hh:mm:ss',
		//controlType:'select',
		//oneLine:true,
	});
	$('#schDate').val($.datepicker.formatDate('yy-mm-dd', new Date()));
	
});