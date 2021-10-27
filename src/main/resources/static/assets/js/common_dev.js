$(document).ready(function() {
	// 인쿠르트 //
	//$("#header").load("/include/header.html")
	//$("#footer").load("/include/footer.html")
	window.addEventListener("message", function(event) {
		const pushUrl = event.data;

		if(typeof pushUrl =='string' && pushUrl.valueOf().indexOf('/user/result/view?') > -1){
			location.href = event.data;
		}
	}, false);

	// 상단영역 //
	$(window).scroll(function() {
		if($(".header") && $(".header").length > 0){
			if ($(".header").offset().top > 50) {
				$('.header').css({ borderBottom : '#f9f9f9 0.5px solid', background : '#fff' });
				$('.header > dl > dt > h2 > a img').attr("src", '/assets/image/common/logo_black.png');
				$('.header > dl > dt > div').stop().animate({ padding : '15px 0' }, 350, 'easeOutQuad');
				$('.header > dl > dt > div > a').css({ color : '#222' });
				$('.headeår > dl > dt > div > a:hover').css({ background : '#edf3f8' });
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
		}
	});

	//$('.header').addClass("active");
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


	// 메인 - 비주얼 //
	$(".main-vegas").vegas({
		slides: [
			{ src: "/assets/image/content/main_visual_img00.jpg" },
			{ src: "/assets/image/content/main_visual_img01.jpg" },
			{ src: "/assets/image/content/main_visual_img02.jpg" },
			{ src: "/assets/image/content/main_visual_img03.jpg" },
		],
		animation: 'kenburns',
		delay: 7000
		//overlay: '../../images/btnIcn/overlay_07.png'
	});

	var mainBtmOffset = $('.main-quest').offset();
	$('.mv-btm').click(function(){
		$('html, body').animate({ scrollTop : mainBtmOffset.top-50 }, 900, 'easeOutQuad');
	});


	// 메인 - 카피 //
	$('.mv-copy > h2').textillate({
		in: { effect: 'flash', delay: 200, shuffle: true },
		out: { effect: 'flash', delay: 200, shuffle: true },
		loop: true
	});
	$('.mv-copy > h3').textillate({
		in: { effect: 'bounceIn', delay: 30, shuffle: true },
		out: { effect: 'bounceOut', delay: 30, shuffle: true },
		loop: true
	});

	setTimeout(function() {
		$('.mv-copy div').animate({ opacity : '1.0' }, 350, 'easeOutQuad');
	}, 2500);


	// 메인 - 시즌 - 카운터 //
	//$('.main-season > div > ul > li > div > h2').countUp();


	// 메인 - 공지사항 - 오버효과 //
	$('.main-notice > div > ul > li').hover(function (event) {
		$(this).find('p span').stop().animate({ width : '105%', margin : '0 0 0 -2.5%', opacity: '0.5' }, 300, 'easeOutQuad');
	}, function (event) {
		$(this).find('p span').stop().animate({ width : '100%', margin : '0', opacity: '1.0' }, 300, 'easeOutQuad');
	});


	// 메인 - Q&A - 업다운 //
	$('.qna_list > div').each(function (index) {
		var updownChk = 0+String(index);
		$('.qna_list > div').eq(index).click(function(){
			if (updownChk == 0+String(index)) {
				$('.qna_list > div > dl').eq(index).slideDown(300, 'easeOutQuad');
				//$('.qna_list > div').eq(index).css({ border : '#333 2px solid' });
				$('.qna_list > div > ul').eq(index).css({ background : '#fff3f3' });
				$('.qna_list > div').eq(index).find('.xi-plus').removeClass('xi-plus').addClass('xi-minus')
				updownChk = 1+String(index);
			} else {
				$('.qna_list > div > dl').eq(index).slideUp(300, 'easeOutQuad');
				//$('.qna_list > div').eq(index).css({ border : 'none' });
				$('.qna_list > div > ul').eq(index).css({ background : '#fff' });
				$('.qna_list > div').eq(index).find('.xi-minus').removeClass('xi-minus').addClass('xi-plus')
				updownChk = 0+String(index);
			}
		});
	});


	// 카타고리 - 상단 고정 //
	var categoryOffset = $( '.category-section' ).offset();
	$( window ).scroll( function() {
		if($( '.category-section' ) && $( '.category-section' ).length > 0){
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
		}
	});


	// 카타고리 - Swiper //
	var swiper = new Swiper('.category-section > dl > dt', {
		slidesPerView: 'auto',
		spaceBetween: 10,
		//freeMode: true,
	});


	// 퀘스트 - 오버효과 //
	$('.quest-list-columns > ul > li').hover(function (event) {
		$(this).find('p span').stop().animate({ width : '105%', margin : '0 0 0 -2.5%', opacity: '0.5' }, 300, 'easeOutQuad');
	}, function (event) {
		$(this).find('p span').stop().animate({ width : '100%', margin : '0', opacity: '1.0' }, 300, 'easeOutQuad');
	});


	// 퀘스트 - 상세 - 탭버튼 //
	$('.qv-tab01').click(function(){
		$('.quest-view > dl > dd > ul li').removeClass('active');
		$(this).addClass('active');

		$('.qv-chart').css({ display : 'block' });
		$('.qv-vol').css({ display : 'none' });
		$('.qv-info').css({ display : 'none' });
	});
	$('.qv-tab02').click(function(){
		$('.quest-view > dl > dd > ul li').removeClass('active');
		$(this).addClass('active');

		$('.qv-chart').css({ display : 'none' });
		$('.qv-vol').css({ display : 'block' });
		$('.qv-info').css({ display : 'none' });
	});
	$('.qv-tab03').click(function(){
		$('.quest-view > dl > dd > ul li').removeClass('active');
		$(this).addClass('active');

		$('.qv-chart').css({ display : 'none' });
		$('.qv-vol').css({ display : 'none' });
		$('.qv-info').css({ display : 'block' });
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


	// 공지사항 - 오버효과 //
	$('.notice-list > ul > li').hover(function (event) {
		$(this).find('p span').stop().animate({ width : '105%', margin : '0 0 0 -2.5%', opacity: '0.5' }, 300, 'easeOutQuad');
	}, function (event) {
		$(this).find('p span').stop().animate({ width : '100%', margin : '0', opacity: '1.0' }, 300, 'easeOutQuad');
	});


	// 공지사항 - 리스트 //
	var $noticeList = $('.notice-list > ul').isotope({
		itemSelector: '.item',
		layoutMode: 'masonry'
	});

	$noticeList.imagesLoaded().progress( function() {
		$noticeList.isotope('layout');
	});

	$('#noticefilters').on( 'click', 'a', function() {
		var filterValue = $(this).attr('data-filter');
		$noticeList.isotope({ filter: filterValue });
		$('#noticefilters > a').removeClass('active');
		$(this).addClass('active');
	});


	// 공지사항 - 상세 - 오버효과 //
	$('.notice-view > dl > dd > ul li').hover(function (event) {
		$(this).find('p span').stop().animate({ width : '105%', margin : '0 0 0 -2.5%', opacity: '0.5' }, 300, 'easeOutQuad');
	}, function (event) {
		$(this).find('p span').stop().animate({ width : '100%', margin : '0', opacity: '1.0' }, 300, 'easeOutQuad');
	});


	// 마이페이지 - 탭버튼 //
	$('.mt-tab01').click(function(){
		$('.mypage-tab li').removeClass('active');
		$(this).addClass('active');

		$('.mc-votings').css({ display : 'block' });
		$('.mc-grounds').css({ display : 'none' });
		$('.mc-transfer').css({ display : 'none' });
	});
	$('.mt-tab02').click(function(){
		$('.mypage-tab li').removeClass('active');
		$(this).addClass('active');

		$('.mc-votings').css({ display : 'none' });
		$('.mc-grounds').css({ display : 'block' });
		$('.mc-transfer').css({ display : 'none' });
	});
	$('.mt-tab03').click(function(){
		$('.mypage-tab li').removeClass('active');
		$(this).addClass('active');

		$('.mc-votings').css({ display : 'none' });
		$('.mc-grounds').css({ display : 'none' });
		$('.mc-transfer').css({ display : 'block' });
	});


	// 마이페이지 날짜 - Transfer 등록 //
	$("#schDateTransfer").datetimepicker({
		dateFormat:'yy-mm-dd',
		// timepicker 설정
		timeFormat:'hh:mm:ss',
		//controlType:'select',
		//oneLine:true,
	});
	$('#schDateTransfer').val($.datepicker.formatDate('yy-mm-dd', new Date()));

});


function fnLogOut(type) {

	$.ajax({
		url:"/logout",
		type :  "POST",
		dataType : "json",
		beforeSend : function(xhr)
		{
			xhr.setRequestHeader($("meta[name='_csrf_header']").attr("content"),$("meta[name='_csrf']").attr("content"));
		},
		success : function(response){
			console.debug(response)
			if(response.code == "200"){
				try {
					const data = JSON.stringify({type: "logout"});
					window.ReactNativeWebView.postMessage(data);
				} catch (error) {

				}finally {
					if(type=="C"){
						window.location = '/loginP'
					}else{
						window.location = response.item.url;
					}
				}
			} else {
				alert(response.message);
			}
		}
	});
}

function FunLoadingBarStart() {
	const backHeight = $(document).height(); //뒷 배경의 상하 폭
	const backWidth = window.document.body.clientWidth; //뒷 배경의 좌우 폭
	const backGroundCover = "<div id='back'></div>"; //뒷 배경을 감쌀 커버
	let loadingBarImage = ''; //가운데 띄워 줄 이미지
	loadingBarImage += "<div id='loadingBar'>";
	loadingBarImage += " <img src='/admin/assets/image/loading.svg'/>"; //로딩 바 이미지
	loadingBarImage += "</div>";
	$('body').append(backGroundCover).append(loadingBarImage);
	$('#back').css({ 'width': backWidth, 'height': backHeight, 'opacity': '0.3','z-index': '999999' });
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
		if(fragmentId=='seasonInfo'){
			$('.main-season > div > ul > li > div > h2').countUp();
		}
	});
}


function calculateWinToken(token, myAnswerTotalToken, otherTotalToken) {
	const currentRate = (token / myAnswerTotalToken);
	const returnToken = otherTotalToken * currentRate;
	const receiveToken = token + returnToken;
	const rate = (receiveToken / token);


	return {rate: rate, receiveToken: receiveToken};
}


function toEther(wei) {
	return wei * 0.000000000000000001;
}

function toWei(ether) {
	return ether * 1000000000000000000;
}

//천단위마다 콤마 생성
function addComma(data) {
	return data.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

//모든 콤마 제거 방법
function removeCommas(data) {
	if(!data || data.length == 0){
		return "";
	}else{
		return data.split(",").join("");
	}
}

function changeUtcToLocal(utcDate,type){
	let localTime = moment.utc(utcDate).toDate();
	localTime = moment(localTime).format(type);

	return localTime
}


function changeLocalToUtc(localTime,type){
	let utcTime = moment(localTime).toDate();
	utcTime = moment(utcTime).utc().format(type);

	return utcTime
}

let Platform = '';

function setPlatform(os){
	Platform = os;
}

function getPlatform(){
	return Platform;
}




